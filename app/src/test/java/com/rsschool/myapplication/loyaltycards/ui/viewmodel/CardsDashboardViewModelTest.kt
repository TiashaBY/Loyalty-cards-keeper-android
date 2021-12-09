package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.DashboardEvent
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.DashboardUIState
import io.mockk.*
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
class CardsDashboardViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var loyaltyCardUseCases: LoyaltyCardUseCases
    @MockK
    lateinit var card: LoyaltyCard
    @MockK
    lateinit var cardForSearch: LoyaltyCard
    @MockK(relaxed = true)
    lateinit var savedStateHandle: SavedStateHandle
    @MockK
    lateinit var viewModel: CardsDashboardViewModel


    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        every { savedStateHandle.get<String>("searchQuery") } returns null
        viewModel = spyk(
            CardsDashboardViewModel(loyaltyCardUseCases, savedStateHandle),
            recordPrivateCalls = true
        )
    }

    @Test
    fun givenDashboardFragmentIsDestroyed_whenUserOpensItBack_thenSearchQueryIsRecovered() {
        every { savedStateHandle.get<String>("searchQuery") } returns "query"
        val recoveredViewModel = CardsDashboardViewModel(loyaltyCardUseCases, savedStateHandle)
        assert(recoveredViewModel.searchQuery.value == "query")
    }


    @Test
    fun givenUserWithoutAnyCards_whenGetCardsUseCaseExecuted_thenUiStateIsEmpty() =
        runBlockingTest {
            coEvery { loyaltyCardUseCases.getCards("") } returns flow {
                emit(MyResult.Success(listOf<LoyaltyCard>()))
            }

            val job = launch {
                viewModel.uiState.collect()
            }

            assert(viewModel.uiState.value == DashboardUIState.Empty)
            job.cancel()
        }

    @Test
    fun givenUserWitSomeCards_whenUserGetsCardsWithEmptySearch_thenUiStateSuccessAndAllResultsAreShown() =
        runBlockingTest {
            coEvery { loyaltyCardUseCases.getCards("") } returns flow {
                emit(MyResult.Success(listOf(card)))
            }

            val job = launch {
                viewModel.uiState.collect()
            }

            assert(viewModel.uiState.value == DashboardUIState.Success(listOf(card)))
            job.cancel()
        }

    @Test
    fun givenUserWitSomeCards_whenUserGetsCardsWithSearchQuery_thenUiStateIsSuccessAndFilteredResultIsShown() =
        runBlockingTest {
            coEvery { loyaltyCardUseCases.getCards("query") } returns flow {
                emit(MyResult.Success(listOf(cardForSearch)))
            }

            every { viewModel.searchQuery } answers { fieldValue }
            viewModel.onSearchQueryChange("query")
            val job = launch {
                viewModel.uiState.collect()
            }

            assert(viewModel.uiState.value == DashboardUIState.Success(listOf(cardForSearch)))
            job.cancel()
        }

    @Test
    fun givenUserWithCards_whenUserClicksItem_thenNavigateToDetailsViewEventIsTriggered() {
        coEvery { loyaltyCardUseCases.getCards("") } returns flow {
            emit(MyResult.Success(listOf(card)))
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
