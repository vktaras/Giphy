package com.example.giphychili.data.giphy

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.giphychili.data.giphy.paging.GiphyPagingSource
import com.example.giphychili.data.giphy.remote.GiphyApi
import com.example.giphychili.domain.giphy.entity.Gif
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.SocketPolicy


class GiphyPagingSourceTest {
    private lateinit var server: MockWebServer
    private lateinit var api: GiphyApi

    @Before fun setup() {
        server = MockWebServer().apply { start() }
        val json = Json { ignoreUnknownKeys = true }
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(GiphyApi::class.java)
    }

    @After fun tearDown() { server.shutdown() }

    @Test
    fun `first page nextKey uses count`() = runTest {
        // offset=0, count=25 -> nextKey=25
        val body = """
    {
      "data": [
        {
          "id": "1",
          "title": "cat 1",
          "images": {
            "original": { "url": "https://x/1o.gif", "width": "480", "height": "360" },
            "preview_gif": { "url": "https://x/1p.gif" },
            "fixed_width_small": { "url": "https://x/1s.gif" }
          }
        },
        {
          "id": "2",
          "title": "cat 2",
          "images": {
            "original": { "url": "https://x/2o.gif", "width": "480", "height": "360" },
            "preview_gif": { "url": "https://x/2p.gif" },
            "fixed_width_small": { "url": "https://x/2s.gif" }
          }
        }
      ],
      "pagination": { "total_count": 100, "count": 25, "offset": 0 }
    }
    """.trimIndent()

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(body)
        )

        val src = GiphyPagingSource(api, apiKey = "KEY", query = "cats", pageSize = 25)
        val res = src.load(PagingSource.LoadParams.Refresh(null, 25, false))

        assertTrue(res is PagingSource.LoadResult.Page<*, *>)
        val page = res as PagingSource.LoadResult.Page<Int, Gif>
        assertNull(page.prevKey)       // offset==0 -> null
        assertEquals(25, page.nextKey) // 0 + count(25)
    }

    @Test
    fun `append shortPage advances by count not pageSize`() = runTest {
        // offset=25, count=7 -> nextKey=32 (не 50)
        val body = """
    {
      "data": [
        {
          "id": "3",
          "title": "cat 3",
          "images": {
            "original": { "url": "https://x/3o.gif", "width": "200", "height": "150" },
            "preview_gif": { "url": "https://x/3p.gif" },
            "fixed_width_small": { "url": "https://x/3s.gif" }
          }
        },
        {
          "id": "4",
          "title": "cat 4",
          "images": {
            "original": { "url": "https://x/4o.gif", "width": "200", "height": "150" },
            "preview_gif": { "url": "https://x/4p.gif" },
            "fixed_width_small": { "url": "https://x/4s.gif" }
          }
        }
      ],
      "pagination": { "total_count": 100, "count": 7, "offset": 25 }
    }
    """.trimIndent()

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(body)
        )

        val src = GiphyPagingSource(api, "KEY", "cats", pageSize = 25)
        val res = src.load(PagingSource.LoadParams.Append(25, 25, false))

        assertTrue(res is PagingSource.LoadResult.Page<*, *>)
        val page = res as PagingSource.LoadResult.Page<Int, Gif>
        assertEquals(0, page.prevKey)    // 25 - 25 -> 0
        assertEquals(32, page.nextKey)   // 25 + 7
    }


    @Test
    fun `lastPage count0 nextKey null`() = runTest {
        val body = """{"data":[],"pagination":{"total_count":32,"count":0,"offset":32}}"""
        server.enqueue(MockResponse().setBody(body).setResponseCode(200))

        val src = GiphyPagingSource(api, "KEY", "cats", 25)
        val res = src.load(PagingSource.LoadParams.Append(32, 25, false))

        assertTrue(res is PagingSource.LoadResult.Page)
        val page = res as PagingSource.LoadResult.Page
        assertNull(page.nextKey)
    }

    @Test
    fun `smallOffset prevKey clamped to0`() = runTest {
        // offset = 10, count = 1 -> prevKey=0, nextKey=11
        val body = """
    {
      "data": [
        {
          "id": "10",
          "title": "odd offset",
          "images": {
            "original": { "url": "https://x/10o.gif", "width": "320", "height": "240" },
            "preview_gif": { "url": "https://x/10p.gif" },
            "fixed_width_small": { "url": "https://x/10s.gif" }
          }
        }
      ],
      "pagination": { "total_count": 100, "count": 1, "offset": 10 }
    }
    """.trimIndent()

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(body)
        )

        val src = GiphyPagingSource(api, "KEY", "cats", pageSize = 25)
        val res = src.load(PagingSource.LoadParams.Append(10, 25, false))

        assertTrue(res is PagingSource.LoadResult.Page<*, *>)
        val page = res as PagingSource.LoadResult.Page<Int, Gif>
        assertEquals(0, page.prevKey)   // (10 - 25).coerceAtLeast(0) -> 0
        assertEquals(11, page.nextKey)  // 10 + count(1)
    }

    @Test
    fun `http500 returns Error`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))
        val src = GiphyPagingSource(api, "KEY", "cats", 25)

        val res = src.load(PagingSource.LoadParams.Refresh(null, 25, false))
        assertTrue(res is PagingSource.LoadResult.Error)
    }

    @Test
    fun `malformed Json returns Error`() = runTest {
        server.enqueue(MockResponse().setBody("{not json").setResponseCode(200))
        val src = GiphyPagingSource(api, "KEY", "cats", 25)

        val res = src.load(PagingSource.LoadParams.Refresh(null, 25, false))
        assertTrue(res is PagingSource.LoadResult.Error)
    }

    @Test
    fun `network Disconnect returns Error`() = runTest {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))
        val src = GiphyPagingSource(api, "KEY", "cats", 25)

        val res = src.load(PagingSource.LoadParams.Refresh(null, 25, false))
        assertTrue(res is PagingSource.LoadResult.Error)
    }

    @Test
    fun `getRefreshKey uses prev plus pageSize or next minus page Size`() {
        val src = GiphyPagingSource(api, "KEY", "cats", pageSize = 25)

        fun mk(n: Int, start: Int = 0) =
            List(n) { i ->
                Gif(
                    id = "id${start + i}",
                    title = "t${start + i}",
                    previewUrl = "https://x/${start + i}p.gif",
                    originalUrl = "https://x/${start + i}o.gif",
                    width = 200, height = 150
                )
            }

        val state = PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = mk(25, 0),
                    prevKey = null,
                    nextKey = 25
                ),
                PagingSource.LoadResult.Page(
                    data = mk(25, 25),
                    prevKey = 0,
                    nextKey = 50
                )
            ),
            anchorPosition = 30,
            config = PagingConfig(pageSize = 25),
            leadingPlaceholderCount = 0
        )

        val key = src.getRefreshKey(state)
        assertEquals(25, key)
    }
}
