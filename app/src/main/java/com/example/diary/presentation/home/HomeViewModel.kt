package com.example.diary.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diary.core.presentation.util.MessageBarUi
import com.example.diary.domain.repository.MongoRepository
import com.example.diary.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mongoDb: App,
    private val mongoRepository: MongoRepository
) : ViewModel() {


    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        observeAllDiaries()
    }

    fun signOut() {
        viewModelScope.launch {
            mongoDb.currentUser?.logOut()
        }
    }

    fun observeAllDiaries() {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            mongoRepository.getAllDiaries().collect { result ->
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