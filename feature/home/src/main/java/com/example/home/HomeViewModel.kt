package com.example.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mongo.local.ImageToDeleteDao
import com.example.mongo.local.entity.ImageToDeleteEntity
import com.example.mongo.remote.MongoRepositoryImpl
import com.example.ui.util.MessageBarUi
import com.example.util.Constants.APP_ID
import com.example.util.RequestState
import com.example.util.connectivity.ConnectivityObserver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val connectivityObserver: ConnectivityObserver,
    private val imageToDeleteDao: ImageToDeleteDao
) : ViewModel() {

    private val app = App.create(APP_ID)

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private var filterDiariesJob: Job? = null
    private var getAllDiariesJob: Job? = null

    init {
        observeNetworkChanges()
        observeAllDiaries()
    }

    fun signOut() {
        viewModelScope.launch {
            app.currentUser?.logOut()
        }
    }

    fun observeFilteredOrNonFilterDiaries(localDate: LocalDate) {
        _state.update {
            it.copy(
                filterSelectedDate = localDate
            )
        }
        if (state.value.filterSelectedDate != null) {
            getFilteredDiariesByDayOfMonth(localDate)
        } else {
            observeAllDiaries()
        }
    }

    fun observeAllDiaries() {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        if (filterDiariesJob?.isActive == true) {
            filterDiariesJob?.cancel()
        }
        getAllDiariesJob?.cancel()
        getAllDiariesJob = viewModelScope.launch(Dispatchers.IO) {
            MongoRepositoryImpl.getAllDiaries().collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is RequestState.Success -> {
                            _state.update {
                                it.copy(
                                    diaries = result.data,
                                    isLoading = false
                                )
                            }
                        }

                        is RequestState.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    messageBarUi = MessageBarUi(exception = result.data as Exception)
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    fun deleteAllDiaries(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        if (state.value.network == ConnectivityObserver.Status.Available) {
            // delete all images as well
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val imagesDirectory = "images/$userId"
            val storage = FirebaseStorage.getInstance().reference
            // dont delete all "images" list only the images for this user
            storage.child(imagesDirectory).listAll()
                .addOnSuccessListener { result ->
                    result.items.forEach { ref ->
                        val imagePath = "$imagesDirectory${ref.name}"
                        storage.child(imagePath).delete().addOnFailureListener {
                            viewModelScope.launch(Dispatchers.IO) {
                                imageToDeleteDao.addImageToDelete(ImageToDeleteEntity(remoteImagePath = imagePath))
                            }
                        }
                    }
                }.addOnFailureListener {
                    onError(it)
                }

            viewModelScope.launch(Dispatchers.IO) {
                val result = MongoRepositoryImpl.deleteAllDiaries()

                withContext(Dispatchers.Main) {
                    if (result is RequestState.Success) {
                        onSuccess()
                    } else if (result is RequestState.Error) {
                        onError(result.data as Exception)
                    }
                }
            }
        } else {
            onError(Exception("Network not available."))
        }
    }

    private fun getFilteredDiariesByDayOfMonth(localDate: LocalDate) {
        _state.update {
            it.copy(
                isLoading = true,
            )
        }
        if (getAllDiariesJob?.isActive == true) {
            getAllDiariesJob?.cancel()
        }
        filterDiariesJob?.cancel()
        filterDiariesJob = viewModelScope.launch(Dispatchers.IO) {
            MongoRepositoryImpl.filterDiariesByDayOfMonth(localDate).collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is RequestState.Success -> {
                            _state.update {
                                it.copy(
                                    diaries = result.data,
                                )
                            }
                        }

                        is RequestState.Error -> {
                            _state.update {
                                it.copy(
                                    messageBarUi = it.messageBarUi.copy(exception = result.data as Exception)
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeNetworkChanges() {
        viewModelScope.launch {
            connectivityObserver.observe().collect { status ->
                _state.update {
                    it.copy(
                        network = status
                    )
                }
            }
        }
    }
}