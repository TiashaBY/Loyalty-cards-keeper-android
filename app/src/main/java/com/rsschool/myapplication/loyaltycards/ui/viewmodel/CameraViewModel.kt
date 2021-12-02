package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(state: SavedStateHandle): ViewModel() {

    private val cameraEventChannel = Channel<CameraActionsRequest>()
    val event = cameraEventChannel.receiveAsFlow().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            state.get<CameraActionsRequest>("cameraAction")?.let { cameraEventChannel.send(it) }
        }
    }
}

sealed class CameraResultEvent : Serializable{
    class BarcodeScanned(val barcode: Barcode) : CameraResultEvent()
    class ImageSaved(val type :CardImageType, val imageUri: Uri?) : CameraResultEvent()
}

sealed class CameraActionsRequest() : Serializable {
    class ScanBarcodeAction() : CameraActionsRequest()
    class CaptureImageAction(val type :CardImageType) : CameraActionsRequest()
}

enum class CardImageType {
    FRONT, BACK
}