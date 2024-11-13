package ipca.examples.dailynews.models

import ipca.examples.dailynews.parseDate
import org.json.JSONObject
import java.util.Date

class Article(
    var title: String? = null,
    var description: String? = null,
    var urlToImage: String? = null,
    var url: String? = null,
    var publishedAt: Date? = null,
    id: Int,
    imageUrl: String
) {

    companion object {
        fun fromJson(json: JSONObject): Article {
            return Article(
                title = json.getString("title"),
                description = json.getString("description"),
                urlToImage = json.getString("urlToImage"),
                url = json.getString("url"),
                publishedAt = json.getString("publishedAt").parseDate(),
                id = json.getInt("id"),
                imageUrl = json.getString("multimediaPrincipal")
            )
        }
    }

}

