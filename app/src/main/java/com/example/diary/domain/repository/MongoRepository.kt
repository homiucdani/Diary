package com.example.diary.domain.repository

import com.example.diary.domain.model.Diary
import com.example.diary.util.RequestState
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

interface MongoRepository {
    fun configureRealm()
    fun getAllDiaries(): Flow<RequestState<Map<LocalDate, List<Diary>>>>
    fun getDiaryById(diaryId: ObjectId): Flow<RequestState<Diary>>
    fun filterDiariesByDayOfMonth(localDate: LocalDate): Flow<RequestState<Map<LocalDate, List<Diary>>>>

    suspend fun insertDiary(diary: Diary): RequestState<Boolean>
    suspend fun updateDiary(diary: Diary): RequestState<Boolean>
    suspend fun deleteDiary(diaryId: ObjectId): RequestState<String>
    suspend fun deleteAllDiaries(): RequestState<Boolean>

}