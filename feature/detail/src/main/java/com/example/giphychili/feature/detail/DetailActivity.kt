package com.example.giphychili.feature.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

class DetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_URL = "gif_url"
        const val EXTRA_TITLE = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(EXTRA_URL)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "GIF"

        setContent {
            MaterialTheme { DetailScreen(title, url) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(title: String, url: String?) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(title) }) }
    ) { padding ->
        Box(Modifier.fillMaxWidth().padding(padding)) {
            if (url == null) {
                Text("No URL", color = MaterialTheme.colorScheme.error)
            } else {
                AsyncImage(
                    model = url,
                    contentDescription = title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
