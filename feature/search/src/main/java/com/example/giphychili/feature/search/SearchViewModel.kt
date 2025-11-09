package com.example.giphychili.feature.search


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.giphychili.core.connectivity.ConnectivityObserver
import com.example.giphychili.domain.giphy.entity.Gif
import com.example.giphychili.domain.giphy.usecase.SearchGifsPagedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchPaged: SearchGifsPagedUseCase,
    connectivity: ConnectivityObserver
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val isOnline: StateFlow<Boolean> =
        connectivity.status.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val items: StateFlow<PagingData<Gif>> =
        query
            .debounce(400)
            .map { it.trim() }
            .distinctUntilChanged()
            .flatMapLatest { q ->
                if (q.isBlank()) flowOf(PagingData.empty())
                else searchPaged(q, 25).flow
            }
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    fun onQueryChange(new: String) { _query.value = new }
}