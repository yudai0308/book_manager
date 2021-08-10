package io.github.yudai0308.honma.viewmodels

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import io.github.yudai0308.honma.models.Item
import io.github.yudai0308.honma.models.SearchResult
import io.github.yudai0308.honma.setBodyFromFileName
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [28])
@RunWith(AndroidJUnit4::class)
class BookResultViewModelTest {

    lateinit var viewModel: BookResultViewModel

    private val server = MockWebServer()

    @Before
    fun setUp() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        viewModel = BookResultViewModel(application)

        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(400)
                println(path)
                return when {
                    path.isBlank() -> MockResponse().setResponseCode(400)
                    path.matches(Regex("/books/v1/volumes\\?.*")) -> {
                        MockResponse()
                            .addHeader("Content-Type", "application/json; charset=UTF-8")
                            .setBodyFromFileName("response_sample.json")
                    }
                    else -> MockResponse().setResponseCode(400)
                }
            }
        }
        server.dispatcher = dispatcher
        server.start()
    }

    @Test
    fun test() {
        val path = "/books/v1/volumes"
        val params = "?q=夏目漱石"
        val req = Request.Builder().url(server.url(path + params)).build()
        val client = OkHttpClient.Builder().build()
        val call = client.newCall(req)
        val res = call.execute()
        assertThat(res.code).isEqualTo(200)
        val body = res.body?.string() ?: ""
        val adapter = Moshi.Builder().build().adapter(SearchResult::class.java)
        val searchResult = adapter.fromJson(body)
        val count = searchResult?.totalItems ?: 0
        val items = searchResult?.items ?: emptyList()
        assertThat(count).isGreaterThan(0)
        assertThat(items.first()).isInstanceOf(Item::class.java)
        res.close()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }
}