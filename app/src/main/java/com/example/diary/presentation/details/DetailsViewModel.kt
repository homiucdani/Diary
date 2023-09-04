package com.example.diary.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diary.data.remote.MongoRepositoryImpl
import com.example.diary.domain.model.Diary
import com.example.diary.domain.model.Mood
import com.example.diary.util.RequestState
import com.example.diary.util.toInstant
import com.example.diary.util.toRealmInstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        observeDiaryId()
    }

    fun onTitleChange(title: String) {
        _state.update {
            it.copy(
                title = title
            )
        }
    }

    fun onDescriptionChange(description: String) {
        _state.update {
            it.copy(
                description = description
            )
        }
    }

    fun onMoodChange(mood: Mood) {
        _state.update {
            it.copy(
                mood = mood
            )
        }
    }

    private fun observeDiaryId() {
        val diaryId = savedStateHandle.get<String>("diaryId")
        if (diaryId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                MongoRepositoryImpl.getDiaryById(ObjectId.invoke(diaryId))
                    .catch {
                        emit(RequestState.Error(Exception("Diary does not exist.")))
                    }
                    .collect { result ->
                        withContext(Dispatchers.Main) {
                            when (result) {
                                is RequestState.Success -> {
                                    _state.update {
                                        it.copy(
                                            diaryId = diaryId,
                                            title = result.data.title,
                                            description = result.data.description,
                                            mood = Mood.valueOf(result.data.mood),
                                            date = result.data.date.toInstant(),
                                            images = result.data.images
                                        )
                                    }
                                }

                                is RequestState.Error -> {
                                    _state.update {
                                        it.copy(
                                            messageBarUi = it.messageBarUi.copy(
                                                exception = result.data as Exception
                                            )
                                        )
                                    }
                                }

                                else -> Unit
                            }
                        }
                    }
            }
        }
    }

    fun insertDiaryIntoDb() {
        if (
            state.value.title.isNotEmpty() && state.value.description.isNotEmpty()
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                MongoRepositoryImpl.insertDiary(
                    Diary().apply {
                        title = state.value.title
                        description = state.value.description
                        mood = state.value.mood.name
                        date = state.value.date.toRealmInstant()
                    }
                )
            }
        } else {
            viewModelScope.launch {
                _uiEvent.emit("Title or description is empty.")
            }
        }
    }

    fun updateDiaryIntoDb() {
        viewModelScope.launch(Dispatchers.IO) {
            MongoRepositoryImpl.updateDiary(
                Diary().apply {
                    _id = ObjectId.invoke(state.value.diaryId)
                    title = state.value.title
                    description = state.value.description
                    mood = state.value.mood.name
                    images = state.value.images
                    date = state.value.date.toRealmInstant()
                }
            )
        }
    }

    fun deleteDiary() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = MongoRepositoryImpl.deleteDiary(ObjectId.invoke(state.value.diaryId))
            withContext(Dispatchers.Main) {
                when (result) {
                    is RequestState.Success -> {
                        _state.update {
                            it.copy(
                                messageBarUi = it.messageBarUi.copy(
                                    message = result.data
                                )
                            )
                        }
                    }

                    is RequestState.Error -> {
                        _state.update {
                            it.copy(
                                messageBarUi = it.messageBarUi.copy(
                                    exception = result.data as Exception
                                )
                            )
                        }
                    }

                    else -> Unit
                }
            }
        }
    }

    fun updateDateTime(zonedDateTime: ZonedDateTime) {
        _state.update {
            it.copy(
                date = zonedDateTime.toInstant()
            )
        }
    }
}