package com.example.giphychili.feature.search

import app.cash.turbine.test
import com.example.giphychili.core.connectivity.ConnectivityObserver
import com.example.giphychili.domain.giphy.GiphyRepository
import com.example.giphychili.domain.giphy.entity.Gif
import com.example.giphychili.domain.giphy.usecase.SearchGifsPagedUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy

private class FakeRepo: GiphyRepository {
    override fun searchPaged(query: String, pageSize: Int): Pager<Int, Gif> =
        Pager(PagingConfig(pageSize)) { object: PagingSource<Int, Gif>() {
            override fun getRefreshKey(state: androidx.paging.PagingState<Int, Gif>) = null
            override suspend fun load(
                params: LoadParams<Int>
            ): LoadResult<Int, Gif> {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        }}
}

private class FakeConn(val online: Boolean): ConnectivityObserver {
    override val status = flowOf(online)
}

private class FakeConnFlow(initial: Boolean) : ConnectivityObserver {
    private val _state = MutableStateFlow(initial)
    override val status: Flow<Boolean> = _state
    fun set(value: Boolean) { _state.value = value }
}

class SearchViewModelTest {
    @Test
    fun `debounce emits`() = runTest {
        val vm = SearchViewModel(
            SearchGifsPagedUseCase(FakeRepo()),
            FakeConn(true)
        )
        vm.items.test {
            awaitItem() // initial empty
            vm.onQueryChange("c")
            vm.onQueryChange("cat")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `debounce - only last query after 400ms emits`() = runTest {
        val vm = SearchViewModel(SearchGifsPagedUseCase(FakeRepo()), FakeConnFlow(true))

        vm.items.test {
            awaitItem() // initial empty PagingData

            vm.onQueryChange("c")
            advanceTimeBy(200)
            expectNoEvents()

            vm.onQueryChange("ca")
            advanceTimeBy(200)
            expectNoEvents()

            vm.onQueryChange("cat")
            advanceTimeBy(399)
            expectNoEvents()

            advanceTimeBy(1)
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `distinctUntilChanged - same query does not re-emit`() = runTest {
        val vm = SearchViewModel(SearchGifsPagedUseCase(FakeRepo()), FakeConnFlow(true))

        vm.items.test {
            awaitItem() // initial
            vm.onQueryChange("cat")
            advanceTimeBy(400)
            awaitItem()

            vm.onQueryChange("cat")
            advanceTimeBy(500)
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `connectivity state mirrors ConnectivityObserver`() = runTest {
        val conn = FakeConnFlow(false)
        val vm = SearchViewModel(SearchGifsPagedUseCase(FakeRepo()), conn)

        vm.isOnline.test {
            assert(!awaitItem())

            conn.set(true)
            assert(awaitItem())

            conn.set(false)
            assert(!awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
