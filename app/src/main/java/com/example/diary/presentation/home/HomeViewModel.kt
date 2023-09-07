package com.example.diary.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diary.core.presentation.util.MessageBarUi
import com.example.diary.data.remote.MongoRepositoryImpl
import com.example.diary.util.Constants.APP_ID
import com.example.diary.util.RequestState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val app = App.create(APP_ID)

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        observeAllDiaries()
    }

    fun signOut() {
        viewModelScope.launch {
            app.currentUser?.logOut()
        }
    }

    private fun observeAllDiaries() {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            MongoRepositoryImpl.getAllDiaries().collect { result ->
                when (result) {
                    is RequestState.Success -> {
                        withContext(Dispatchers.Main) {
                            _state.update {
                                it.copy(
                                    diaries = result.data,
                                    isLoading = false
                                )
                            }
                        }
                    }

                    is RequestState.Error -> {
                        withContext(Dispatchers.Main) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    messageBarUi = MessageBarUi(exception = result.data as Exception)
                                )
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}