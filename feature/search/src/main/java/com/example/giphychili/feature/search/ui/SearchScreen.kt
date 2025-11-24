package com.example.giphychili.feature.search.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.giphichili.core.ui.components.OfflineBanner
import com.example.giphichili.core.ui.components.ErrorRow
import com.example.giphychili.domain.giphy.entity.Gif
import com.example.giphychili.feature.detail. DetailActivity
import com.example.giphychili.feature.search.SearchViewModel

@Composable
fun SearchRoute(
    vm: SearchViewModel,
    imageLoader: ImageLoader
) {
    val online by vm.isOnline.collectAsState()
    val query by vm.query.collectAsState()
    val items = vm.items.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            Column {
                SearchBar(
                    query = query,
                    onChange = vm::onQueryChange,
                    onSearch = { items.refresh() }
                )
                AnimatedVisibility(visible = !online) {
                    OfflineBanner()
                }
            }
        }
    ) { padding ->
        GifGrid(
            items = items,
            imageLoader = imageLoader,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onChange,
        singleLine = true,
        placeholder = { Text("Search GIFs…") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() })
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GifGrid(
    items: androidx.paging.compose.LazyPagingItems<Gif>,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier
) {
    val cfg = LocalConfiguration.current
    val columns = if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2
    val ctx = LocalContext.current

    Box(modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(8.dp)
        ) {
            if (items.itemCount == 0 &&
                items.loadState.refresh !is LoadState.Loading &&
                items.loadState.refresh !is LoadState.Error
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState(modifier = Modifier.fillMaxWidth().padding(24.dp))
                }
            }

            items(
                count = items.itemCount,
                key = { i ->
                    val p = items.peek(i)
                    when {
                        p != null -> "${p.id}:${p.previewUrl}"
                        else -> "placeholder:$i"
                    }
                },
                contentType = { "gif" }
            ) { index ->
                val gif = items[index] ?: return@items
                GifCard(
                    gif = gif,
                    onClick = {
                        ctx.startActivity(
                            Intent(ctx, DetailActivity::class.java).apply {
                                putExtra(DetailActivity.EXTRA_URL, gif.originalUrl)
                                putExtra(DetailActivity.EXTRA_TITLE, gif.title.orEmpty())
                            }
                        )
                    },
                    modifier = Modifier
                        .padding(4.dp),
                    imageLoader = imageLoader
                )
            }

            if (items.loadState.append is LoadState.Loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    GridLoading(modifier = Modifier.fillMaxWidth().padding(16.dp))
                }
            }

            if (items.loadState.append is LoadState.Error) {
                val e = (items.loadState.append as LoadState.Error).error
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ErrorRow(
                        message = e.message ?: "Error",
                        onRetry = { items.retry() }
                    )
                }
            }

            if (items.loadState.refresh is LoadState.Error) {
                val e = (items.loadState.refresh as LoadState.Error).error
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ErrorRow(
                        message = e.message ?: "Error",
                        onRetry = { items.retry() }
                    )
                }
            }
        }

        if (items.loadState.refresh is LoadState.Loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun GifCard(
    gif: Gif,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader
) {
    val ctx = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        AsyncImage(
            model = ImageRequest.Builder(ctx)
                .data(gif.previewUrl)
                .crossfade(true)
                .build(),
            imageLoader = imageLoader,
            contentDescription = gif.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun GridLoading(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text("Nothing has been found")
    }
}

