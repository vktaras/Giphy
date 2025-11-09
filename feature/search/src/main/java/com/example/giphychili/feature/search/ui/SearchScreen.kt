package com.example.giphychili.feature.search.ui

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.giphichili.core.ui.components.OfflineBanner
import com.example.giphichili.core.ui.components.ErrorRow
import com.example.giphychili.domain.giphy.entity.Gif
import com.example.giphychili.feature.detail. DetailActivity
import com.example.giphychili.feature.search.SearchViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchRoute(vm: SearchViewModel) {
    val online = vm.isOnline.collectAsState().value
    val q = vm.query.collectAsState().value

    Column(Modifier.fillMaxSize()) {
        if (!online) OfflineBanner()
        SearchBar(q, vm::onQueryChange)
        GifGrid(pagingFlow = vm.items)
    }
}

@Composable
fun SearchBar(query: String, onChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onChange,
        singleLine = true,
        placeholder = { Text("Search GIFs…") },
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GifGrid(pagingFlow: Flow<PagingData<Gif>>) {
    val lazyItems = pagingFlow.collectAsLazyPagingItems()
    val cfg = LocalConfiguration.current
    val columns = if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2
    val ctx = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(lazyItems.itemCount) { i ->
            val gif = lazyItems[i] ?: return@items
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable {
                        ctx.startActivity(
                            Intent(ctx, DetailActivity::class.java).apply {
                                putExtra(DetailActivity.EXTRA_URL, gif.originalUrl)
                                putExtra(DetailActivity.EXTRA_TITLE, gif.title.orEmpty())
                            }
                        )
                    }
            ) {
                AsyncImage(
                    model = gif.previewUrl,
                    contentDescription = gif.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (lazyItems.loadState.refresh is LoadState.Loading ||
            lazyItems.loadState.append is LoadState.Loading
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(Modifier.fillMaxWidth().padding(16.dp)) { CircularProgressIndicator() }
            }
        }
        if (lazyItems.loadState.refresh is LoadState.Error) {
            val e = (lazyItems.loadState.refresh as LoadState.Error).error
            item(span = { GridItemSpan(maxLineSpan) }) {
                ErrorRow(e.message ?: "Error", onRetry = { lazyItems.retry() })
            }
        }
        if (lazyItems.loadState.append is LoadState.Error) {
            val e = (lazyItems.loadState.append as LoadState.Error).error
            item(span = { GridItemSpan(maxLineSpan) }) {
                ErrorRow(e.message ?: "Error", onRetry = { lazyItems.retry() })
            }
        }
    }
}
