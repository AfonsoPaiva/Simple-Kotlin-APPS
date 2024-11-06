package ipca.examples.dailynews.models

import ipca.examples.dailynews.parseDate
import org.json.JSONObject

class Article(
    var title: String? = null,
    var description: String? = null,
    var urlToImage: String? = null,
    var url: String? = null,
    var publishedAt: String? = null // Mark as nullable
) {
    companion object {
        fun fromJson(json: JSONObject): Article {
            return Article(
                title = json.optString("title", "No Title"), // Use optString for safety
                description = json.optString("lead", ""), // Adjust based on the API response
                urlToImage = json.optString("image", ""), // Adjust based on the API response
                url = json.optString("link", ""), // Adjust based on the API response
                publishedAt = json.optString("pubDate", "").parseDate()?.toString() // Use optString for safety
            )
        }
    }
}
