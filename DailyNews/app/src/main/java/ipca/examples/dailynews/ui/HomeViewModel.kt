package ipca.examples.dailynews.ui

import androidx.lifecycle.ViewModel
import ipca.examples.dailynews.models.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

data class ArticlesState(
    val articles: ArrayList<Article> = arrayListOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ArticlesState())
    val uiState: StateFlow<ArticlesState> = _uiState.asStateFlow()

    fun fetchArticles() {
        // Set loading state
        _uiState.value = ArticlesState(isLoading = true, error = null)

        val client = OkHttpClient()

        // Build the request to the Publico API
        val request = Request.Builder()
            .url("https://www.publico.pt/api/list/ultimas")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                _uiState.value = ArticlesState(isLoading = false, error = e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        _uiState.value = ArticlesState(
                            isLoading = false,
                            error = "Unexpected response: $response"
                        )
                        return
                    }

                    val articlesResult = arrayListOf<Article>()
                    val result = response.body?.string()
                    result?.let {
                        val jsonResult = JSONObject(it)
                        val articlesJson = jsonResult.getJSONArray("items")  // Assuming 'items' contains the articles

                        // Parse the JSON response and map to the Article model
                        for (index in 0 until articlesJson.length()) {
                            val articleJson = articlesJson.getJSONObject(index)

                            // Extract relevant fields for the Article
                            val article = Article(
                                title = articleJson.optString("title", "No Title"),      // Default "No Title" if title is absent
                                description = articleJson.optString("lead", ""),         // Use "lead" for description
                                content = articleJson.optString("body", ""),             // Use "body" for the content
                                url = articleJson.optString("link", ""),                 // Use "link" for the URL
                                publishedAt = articleJson.optString("pubDate", "")       // Use "pubDate" for publication date
                            )

                            articlesResult.add(article)
                        }
                    }

                    // Update UI state with the fetched articles
                    _uiState.value = ArticlesState(
                        articles = articlesResult,
                        isLoading = false,
                        error = null
                    )
                }
            }
        })
    }
}
