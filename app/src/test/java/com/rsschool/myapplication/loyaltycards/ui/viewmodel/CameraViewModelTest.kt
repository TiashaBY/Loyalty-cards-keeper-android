package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.camera.core.ImageProxy
import androidx.lifecycle.SavedStateHandle
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.domain.usecase.TakeCardPictureUseCase
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import com.rsschool.myapplication.loyaltycards.ui.CameraMode
import com.rsschool.myapplication.loyaltycards.ui.CardSide
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class CameraViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var savePictureUseCase: TakeCardPictureUseCase

    @MockK
    lateinit var barcode: Barcode

    @MockK
    lateinit var savedStateHandle: SavedStateHandle

    @MockK
    lateinit var imageProxy: ImageProxy

    @MockK(relaxed = true)
    lateinit var viewModel: CameraViewModel

    @MockK(relaxed = true)
    lateinit var uri: Uri

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        every { savedStateHandle.get<CameraEvents>("cameraMode") } returns null
        every { savedStateHandle.get<CameraEvents>("frontImageUri") } returns null
        every { savedStateHandle.get<CameraEvents>("backImageUri") } returns null
    }

    @Test
    fun givenUserScannedBarcode_whenTheResultIsSuccess_thenCameraEventIsBarcodeScanned() {
        every { savedStateHandle.get<CameraMode>("mode") } returns CameraMode.SCANNER
        viewModel = spyk(CameraViewModel(savedStateHandle, savePictureUseCase))
        val result = ResultContainer.Success(barcode)

        viewModel.onBarcodeScanned(result)

        assert(viewModel.cameraEvent.value == CameraEvents.BarcodeScanned(barcode))
    }

    @Test
    fun givenUserScannedBarcode_whenTheResultIsFailure_thenCameraEventIsError() {
        every { savedStateHandle.get<CameraMode>("mode") } returns CameraMode.SCANNER
        viewModel = spyk(CameraViewModel(savedStateHandle, savePictureUseCase))
        val result = ResultContainer.Failure(Exception(""))

        viewModel.onBarcodeScanned(result)

        assert(viewModel.cameraEvent.value is CameraEvents.CameraError)
    }

    @Test
    fun givenCardCaptureFlowStarted_whenOnCardCapturedMethodCalled_thenSavePictureUseCaseRun() {
        every { savedStateHandle.get<CameraMode>("mode") } returns CameraMode.PHOTO
        viewModel = spyk(CameraViewModel(savedStateHandle, savePictureUseCase))

        viewModel.onCardCaptured(CardSide.BACK, imageProxy)

        coVerify { savePictureUseCase(imageProxy) }
    }

    @Test
    fun givenCardCaptureFlowStarted_whenOnCardCapturedFromFront_thenCaptureBackImageEventIsTriggered() {
        every { savedStateHandle.get<CameraMode>("mode") } returns CameraMode.PHOTO
        viewModel = spyk(CameraViewModel(savedStateHandle, savePictureUseCase))
        coEvery { savePictureUseCase.invoke(imageProxy) } returns ResultContainer.Success(uri)

        viewModel.onCardCaptured(CardSide.FRONT, imageProxy)

        assert(viewModel.cameraEvent.value is CameraEvents.CaptureBackImage)
    }

    @Test
    fun givenCardCaptureFlowStarted_whenOnCardCapturedFromBack_thenCameraFinishedCapturingEventIsTriggered() {
        every { savedStateHandle.get<CameraMode>("mode") } returns CameraMode.PHOTO
        viewModel = spyk(CameraViewModel(savedStateHandle, savePictureUseCase))
        coEvery { savePictureUseCase.invoke(imageProxy) } returns ResultContainer.Success(uri)

        viewModel.onCardCaptured(CardSide.BACK, imageProxy)

        assert(viewModel.cameraEvent.value is CameraEvents.CameraFinishedCapturing)
    }

    @After
    internal fun tearDown() {
        clearAllMocks()
    }
}
