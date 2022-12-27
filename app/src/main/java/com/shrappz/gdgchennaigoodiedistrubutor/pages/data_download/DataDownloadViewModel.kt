package com.shrappz.gdgchennaigoodiedistrubutor.pages.data_download

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.shrappz.gdgchennaigoodiedistrubutor.EVENT_DAY_ID
import com.shrappz.gdgchennaigoodiedistrubutor.csv.CSVHelper
import com.shrappz.gdgchennaigoodiedistrubutor.repository.GdgEventDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DataDownloadViewModel @Inject constructor(
    private val repository: GdgEventDataRepository,
    private val csvHelper: CSVHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventDay = requireNotNull(savedStateHandle.get<String>(EVENT_DAY_ID))

    internal var currentUiData by mutableStateOf(
        DataDownloadUiData(
            showDownloadButton = true,
            showShareButton = false,
            uiAction = null,
        )
    )
        private set

    private var downloadedFile: File? = null

    private fun downloadAndShareData() {

        viewModelScope.launch {
            try {
                val list = repository.fetchAllCheckedInUsers(eventDay)
                downloadedFile = csvHelper.convertToCSVAndSave("Day 2 Check In users", list)
                currentUiData = DataDownloadUiData(
                    showDownloadButton = false,
                    showShareButton = true,
                    uiAction = UiAction.ShowMessage("Day 2 data downloaded")
                )
            } catch (e: IOException) {
                Firebase.crashlytics.recordException(e)
                currentUiData = DataDownloadUiData(
                    showDownloadButton = true,
                    showShareButton = false,
                    uiAction = UiAction.ShowMessage("Data Download Error Occurred")
                )

            }
        }
    }

    fun uiActionCompleted() {
        currentUiData = currentUiData.copy(uiAction = null)
    }

    fun onDownloadDataClicked() {
        downloadAndShareData()
    }

    fun onShareClicked() {
        val file = requireNotNull(downloadedFile) {
            "Cannot share without file"
        }
        currentUiData = currentUiData.copy(uiAction = UiAction.ShareFile(file))
    }

    sealed interface UiAction {
        data class ShowMessage(val message: String) : UiAction
        data class ShareFile(val file: File) : UiAction
    }
}