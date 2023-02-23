package com.cookiss.diaryapp.data.repository

import com.cookiss.diaryapp.domain.model.Diary
import com.cookiss.diaryapp.util.RequestState
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

typealias Diaries = RequestState<Map<LocalDate, List<Diary>>>

interface MongoRepository {
    fun configureTheRealm()
    fun getAllDiaries(): Flow<Diaries>
    fun getSelectedDiary(diaryId: ObjectId): RequestState<Diary>
}