package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CardDetailsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var card: LoyaltyCard

    @MockK(relaxed = true)
    lateinit var savedStateHandle: SavedStateHandle


    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun givenDashboardFragmentIsDestroyed_whenUserOpensItBack_thenSearchQueryIsRecovered() {
        every { savedStateHandle.get<LoyaltyCard>("card") } returns card
        val viewModel = CardDetailsViewModel(savedStateHandle)
        assert(viewModel.card == card)
    }

    @After
    internal fun tearDown() {
        clearAllMocks()
    }


}