package com.example.diary.domain.repository

import com.example.diary.domain.model.Diary
import com.example.diary.util.RequestState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MongoRepository {
    fun configureRealm()
    fun getAllDiaries(): Flow<RequestState<Map<LocalDate, List<Diary>>>>
}