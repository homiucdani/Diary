package com.example.diary.data.remote

import com.example.diary.domain.model.Diary
import com.example.diary.domain.repository.MongoRepository
import com.example.diary.util.Constants.APP_ID
import com.example.diary.util.RequestState
import com.example.diary.util.toInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.ZoneId

object MongoRepositoryImpl : MongoRepository {

    private val app = App.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureRealm()
    }

    override fun configureRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions { sub ->
                    add(
                        query = sub.query<Diary>(query = "ownerId == $0", user.id),
                        name = "User's Diaries"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    override fun getAllDiaries(): Flow<RequestState<Map<LocalDate, List<Diary>>>> {
        return if (user != null) {
            try {
                realm.query<Diary>(query = "ownerId == $0", user.id)
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        RequestState.Success(
                            result.list.groupBy {
                                it.date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                        )
                    }
            } catch (e: Exception) {
                flow {
                    emit(RequestState.Error(e))
                }
            }
        } else {
            flow {
                emit(RequestState.Error(UserNotAuthenticatedException()))
            }
        }
    }

    override fun getDiaryById(diaryId: ObjectId): Flow<RequestState<Diary>> {
        return if (user != null) {
            try {
                realm.query<Diary>(query = "_id == $0", diaryId).asFlow().map {
                    RequestState.Success(data = it.list.first())
                }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override suspend fun insertDiary(diary: Diary) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(
                        instance = diary.apply {// user needs an owner id into constructor, it can be applied here
                            ownerId = user.id
                        }
                    )
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        }
    }

    override suspend fun updateDiary(diary: Diary) {
        if (user != null) {
            realm.write {
                val queriedDiary = query<Diary>(query = "_id == $0", diary._id).first().find()
                if (queriedDiary != null) {
                    queriedDiary.title = diary.title
                    queriedDiary.description = diary.description
                    queriedDiary.mood = diary.mood
                    queriedDiary.images = diary.images
                    queriedDiary.date = diary.date
                    RequestState.Success(data = queriedDiary)
                }
            }
        }
    }

    override suspend fun deleteDiary(diaryId: ObjectId): RequestState<String> {
        return if (user != null) {
            realm.write {
                try {
                    val diary =
                        query<Diary>(query = "_id == $0 AND ownerId == $1", diaryId, user.id).find()
                            .first()
                    delete(diary)
                    RequestState.Success("Deleted successfully.")
                } catch (e: Exception) {
                    e.printStackTrace()
                    RequestState.Error(Exception(e))
                }
            }
        } else {
            RequestState.Error(Exception("Something went wrong."))
        }
    }
}

private class UserNotAuthenticatedException : Exception("User is not logged in.")

