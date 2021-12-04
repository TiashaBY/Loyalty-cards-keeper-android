package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {

    private val _cameraEventFlow = MutableStateFlow<CameraActionsRequest?>(null)
    val event = _cameraEventFlow

    fun onLoad() {
        state.get<CameraActionsRequest>("cameraAction")?.let {
            _cameraEventFlow.value = it
        }
    }
}

/*
sealed class CameraResultEvent : Serializable {
    data class BarcodeScanned(val barcode: Barcode) : CameraResultEvent()
    data class ImageSaved(val type: CardImageType, val imageUri: Uri?) : CameraResultEvent()
}

sealed class CameraActionsRequest : Serializable {
    object ScanBarcodeAction : CameraActionsRequest()
    data class CaptureImageAction(val type: CardImageType) : CameraActionsRequest()
}

enum class CardImageType {
    FRONT, BACK
}*/
