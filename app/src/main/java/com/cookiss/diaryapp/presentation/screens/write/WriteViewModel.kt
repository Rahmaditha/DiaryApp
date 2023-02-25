package com.cookiss.diaryapp.presentation.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookiss.diaryapp.data.repository.MongoDB
import com.cookiss.diaryapp.domain.model.Diary
import com.cookiss.diaryapp.domain.model.Mood
import com.cookiss.diaryapp.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.cookiss.diaryapp.util.RequestState
import com.cookiss.diaryapp.util.toRealmInstant
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    init {
        getDiaryIdArgument()
        fetchSelectedDiary()
    }

    private fun getDiaryIdArgument(){
        uiState = uiState.copy(
            selectedDiaryId = savedStateHandle.get<String>(
                key = WRITE_SCREEN_ARGUMENT_KEY
            )
        )
    }

    private fun fetchSelectedDiary(){
        if(uiState.selectedDiaryId != null){
            viewModelScope.launch(Dispatchers.Main) {
                MongoDB.getSelectedDiary(
                    diaryId = ObjectId.Companion.from(uiState.selectedDiaryId!!)
                ).collect{ diary ->
                    if(diary is RequestState.Success){
                        setSelectedDiary(diary = diary.data)
                        setTitle(title = diary.data.title)
                        setDescription(description = diary.data.description)
                        setMood(mood = Mood.valueOf(diary.data.mood))
                    }
                }
            }
        }
    }

    fun setTitle(title: String){
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String){
        uiState = uiState.copy(description = description)
    }

    private fun setMood(mood: Mood){
        uiState = uiState.copy(mood = mood)
    }

    fun updateDateTime(zonedDateTime: ZonedDateTime){
        uiState = uiState.copy(updateDateTime = zonedDateTime.toInstant().toRealmInstant())

    }

    private fun setSelectedDiary(diary: Diary){
        uiState = uiState.copy(selectedDiary = diary)
    }

    fun upsertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            if(uiState.selectedDiaryId != null){
                updateDiary(
                    diary = diary,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }else{
                insertDiary(diary = diary, onSuccess = onSuccess, onError = onError)
            }
        }
    }

    private suspend fun insertDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        val result = MongoDB.insertDiary(diary = diary.apply {
            if(uiState.updateDateTime != null){
                date = uiState.updateDateTime!!
            }
        })
        if(result is RequestState.Success){
            withContext(Dispatchers.Main){
                onSuccess()
            }
        }else if(result is RequestState.Error){
            withContext(Dispatchers.Main){
                onError(result.error.message.toString())
            }
        }
    }

    private suspend fun updateDiary(
        diary: Diary,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        val result = MongoDB.updateDiary(diary = diary.apply {
            _id = ObjectId.Companion.from(uiState.selectedDiaryId!!)
            date = if(uiState.updateDateTime != null){
                uiState.updateDateTime!!
            }else{
                uiState.selectedDiary!!.date
            }
        })
        if(result is RequestState.Success){
            withContext(Dispatchers.Main){
                onSuccess()
            }
        }else if(result is RequestState.Error){
            onError(result.error.message.toString())
        }
    }
}

data class UiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val updateDateTime: RealmInstant? = null
)