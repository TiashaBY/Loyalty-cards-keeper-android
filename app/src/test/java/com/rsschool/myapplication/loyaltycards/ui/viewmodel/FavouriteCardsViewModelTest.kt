package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import com.rsschool.myapplication.loyaltycards.ui.dashboard.DashboardEvent
import com.rsschool.myapplication.loyaltycards.ui.dashboard.DashboardUIState
import com.rsschool.myapplication.loyaltycards.ui.dashboard.FavouriteCardsViewModel
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class FavouriteCardsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var loyaltyCardUseCases: LoyaltyCardUseCases

    @MockK
    lateinit var card: LoyaltyCard

    @InjectMockKs
    lateinit var viewModel: FavouriteCardsViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun givenUserWithoutFavorites_whenGetFavoritesUseCaseExecuted_thenUiStateIsEmpty() = runBlockingTest {
        coEvery { loyaltyCardUseCases.getFavoriteCards() } returns flow {
            emit(ResultContainer.Success(listOf<LoyaltyCard>()))
        }
        val job = launch {
            viewModel.uiState.collect()
        }
        assert(viewModel.uiState.value == DashboardUIState.Empty)
        job.cancel()
    }

    @Test
    fun givenUserWithFavourites_whenGetFavoritesUseCaseExecuted_thenUiStateIsSuccess() =
        runBlockingTest {
            coEvery { loyaltyCardUseCases.getFavoriteCards() } returns flow {
                emit(ResultContainer.Success(listOf(card)))
            }
            val job = launch {
                viewModel.uiState.collect()
            }
            assert(viewModel.uiState.value == DashboardUIState.Success(listOf(card)))
            job.cancel()
        }

    @Test
    fun givenUserWithFavourites_thenUiStateInitialStateIsLoading() {
        coEvery { loyaltyCardUseCases.getFavoriteCards() } returns flow {
            emit(ResultContainer.Success(listOf(card)))
        }
            assert(viewModel.uiState.value is DashboardUIState.Loading)
        }

    @Test
    fun givenUserWithFavourites_whenUserClicksFavItem_thenNavigateToDetailsViewEventIsTriggered() =
        runBlockingTest {
            coEvery { loyaltyCardUseCases.getFavoriteCards() } returns flow {
                emit(ResultContainer.Success(listOf(card)))
            }
            viewModel.onItemDetailsClick(card)
            viewModel.dashboardEvent.map {
                assert(it == DashboardEvent.NavigateToDetailsView(card))
            }
        }

    @After
    internal fun tearDown() {
        clearAllMocks()
    }
}
