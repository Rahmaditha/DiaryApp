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
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                val diary = MongoDB.getSelectedDiary(
                    diaryId = ObjectId.Companion.from(uiState.selectedDiaryId!!)
                )
                if(diary is RequestState.Success){
                    setSelectedDiary(diary = diary.data)
                    setTitle(title = diary.data.title)
                    setDescription(description = diary.data.description)
                    setMood(mood = Mood.valueOf(diary.data.mood))
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

    fun setMood(mood: Mood){
        uiState = uiState.copy(mood = mood)
    }

    fun setSelectedDiary(diary: Diary){
        uiState = uiState.copy(selectedDiary = diary)
    }
}

data class UiState(
    val selectedDiaryId: String? = null,
    val selectedDiary: Diary? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral
)