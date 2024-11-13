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
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import android.os.Handler
import android.os.Looper

data class ArticlesState (
    val articles: ArrayList<Article> = arrayListOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ArticlesState())
    val uiState: StateFlow<ArticlesState> = _uiState.asStateFlow()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun fetchArticles() {
        _uiState.value = ArticlesState(isLoading = true, error = null)
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://www.publico.pt/api/list/ultimas")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                mainHandler.post {
                    _uiState.value = ArticlesState(isLoading = false, error = e.message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        mainHandler.post {
                            _uiState.value = ArticlesState(isLoading = false, error = "Unexpected response code: ${response.code}")
                        }
                        return
                    }
                    val articlesResult = arrayListOf<Article>()
                    val result = response.body?.string().orEmpty()


                    try {
                        // Assuming the response is a JSON array of articles
                        val articlesJson = JSONArray(result)
                        for (index in 0 until articlesJson.length()) {
                            val articleJson = articlesJson.getJSONObject(index)
                            val article = Article(
                                id = articleJson.optInt("id"),
                                title = articleJson.optString("titulo"),
                                description = articleJson.optString("descricao"),
                                url = articleJson.optString("url"),
                                imageUrl = articleJson.optString("multimediaPrincipal")
                            )
                            articlesResult.add(article)
                        }
                        mainHandler.post {
                            _uiState.value = ArticlesState(
                                articles = articlesResult,
                                isLoading = false,
                                error = null
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        mainHandler.post {
                            _uiState.value = ArticlesState(isLoading = false, error = "Error parsing JSON")
                        }
                    }
                }
            }
        })
    }
}
