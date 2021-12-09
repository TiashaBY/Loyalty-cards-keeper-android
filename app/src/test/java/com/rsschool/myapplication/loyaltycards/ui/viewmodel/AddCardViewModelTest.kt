package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.google.zxing.BarcodeFormat
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.AddCardUseCase
import com.rsschool.myapplication.loyaltycards.domain.usecase.DeleteCardImagesUseCase
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
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
class AddCardViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    lateinit var viewModel: AddCardViewModel

    @MockK(relaxed = true)
    lateinit var addCardUseCase: AddCardUseCase

    @MockK
    lateinit var deleteImagesUseCase: DeleteCardImagesUseCase

    @MockK
    lateinit var savedStateHandle: SavedStateHandle

    @MockK
    lateinit var frontUri: Uri

    @MockK
    lateinit var backUri: Uri

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        every { savedStateHandle.get<Barcode>("result") } returns null
        every { savedStateHandle.get<Uri>("frontImageUri") } returns frontUri
        every { savedStateHandle.get<Uri>("backImageUri") } returns backUri
        every { savedStateHandle.get<String>("name") } returns null
        every { savedStateHandle.get<BarcodeFormat?>("barcodeFormat") } returns null
        every { savedStateHandle.get<String>("number") } returns null
    }

    @Test
    fun givenUserCapturedCard_whenUserDiscardChanges_thenCapturedImagesAreDeleted() {
        every { viewModel.frontImageUri.value } returns frontUri
        every { viewModel.backImageUri.value } returns backUri
        coEvery { deleteImagesUseCase.invoke(frontUri) } returns MyResult.Success(frontUri)
        coEvery { deleteImagesUseCase.invoke(backUri) } returns MyResult.Success(backUri)
        viewModel = spyk(AddCardViewModel(savedStateHandle, addCardUseCase, deleteImagesUseCase))

        viewModel.onLeave()

        coVerify { deleteImagesUseCase.invoke(frontUri) }
        coVerify { deleteImagesUseCase.invoke(backUri) }
    }

    @Test
    fun onSaveClick() {
        val successResult = 1L
        viewModel = spyk(AddCardViewModel(savedStateHandle, addCardUseCase, deleteImagesUseCase))
        every { viewModel.name.value } returns ""
        every { viewModel.number.value } returns ""
        every { viewModel.barcodeFormat.value } returns null
        val card =
            spyk(LoyaltyCard(null, false, viewModel.name.value, viewModel.number.value, null))
        coEvery { addCardUseCase.invoke(card) } returns MyResult.Success(successResult)

        viewModel.onSaveClick()

        coVerify { addCardUseCase.invoke(card) }
    }

    @After
    internal fun tearDown() {
        clearAllMocks()
    }
}