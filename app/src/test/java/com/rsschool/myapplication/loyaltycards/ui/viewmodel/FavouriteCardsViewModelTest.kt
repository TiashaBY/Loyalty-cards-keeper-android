package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mock
import java.lang.Exception


@RunWith(JUnit4::class)
class FavouriteCardsViewModelTest {

    @MockK
    lateinit var loyaltyCardUseCases: LoyaltyCardUseCases
    val dispatcher = Dispatchers.Unconfined

    @InjectMockKs
    lateinit var viewModel: FavouriteCardsViewModel

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)
    }
    @Test
    fun givenUserWithoutFavorites_whenGetFavoritesUseCaseExecuted_thenUiStateIsEmpty() {
        // given
        coEvery { loyaltyCardUseCases.getFavoriteCards() } returns flow {
            emit(listOf<LoyaltyCard>())
        }
        // when
        viewModel.onLoad()
        // then
        assert(viewModel.uiState.value is DBResult.Empty)
    }
    @Test
    fun givenUserWithFavorites_whenGetFavoritesUseCaseExecuted_thenUiStateIsSuccess() {
        val card = mockk<LoyaltyCard>()
        // given
        coEvery { loyaltyCardUseCases.getFavoriteCards() } returns flow {
            emit(listOf(card))
        }
        // when
        viewModel.onLoad()
        // then
        assert(viewModel.uiState.value == DBResult.Success(listOf(card)))
    }

    @Test
    fun givenDbError_whenGetFavoritesUseCaseExecuted_thenUiStateIsFailure() {
        val ex = Exception()
        // given
        coEvery { loyaltyCardUseCases.getFavoriteCards() } throws ex
        // when
        viewModel.onLoad()
        // then
        assert(viewModel.uiState.value is DBResult.Failure)
    }

    @After
    internal fun tearDown() {
        clearAllMocks()
    }
}