package com.example.giphychili

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import coil.ImageLoader
import com.example.giphychili.feature.search.SearchViewModel
import com.example.giphychili.feature.search.ui.SearchRoute
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm: SearchViewModel by viewModels()

        setContent {
            SearchRoute(vm = vm, imageLoader = imageLoader)
        }
    }
}
