package ipca.examples.dailynews.ui

import ArticlesState
import HomeViewModel
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ipca.examples.dailynews.Screen
import ipca.examples.dailynews.encodeURL
import ipca.examples.dailynews.models.Article
import ipca.examples.dailynews.ui.theme.DailyNewsTheme

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()) {

    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    HomeViewContent(modifier = modifier,
        navController = navController,
        uiState = uiState)

    LaunchedEffect(Unit) {
        viewModel.fetchArticles()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeViewContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    uiState: ArticlesState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    "News",
                    color = androidx.compose.ui.graphics.Color(0xFFDDE9F5),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            modifier = Modifier
                .height(90.dp)
                .clip(RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = androidx.compose.ui.graphics.Color(0xFF0D47A1)
            )
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> Text("Loading articles...")
                uiState.error != null -> Text("Error: ${uiState.error}")
                uiState.articles.isEmpty() -> Text("No articles found!")
                else -> LazyColumn(modifier = modifier.fillMaxSize()) {
                    itemsIndexed(uiState.articles) { _, article ->
                        RowArticle(
                            modifier = Modifier.clickable {
                                Log.d("dailynews", article.url ?: "none")
                                navController.navigate(
                                    Screen.ArticleDetail.route
                                        .replace("{articleUrl}", article.url?.encodeURL() ?: "")
                                )
                            },
                            article = article
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    val navController = rememberNavController()  // Initialize NavController

    val articles = arrayListOf(
        Article(
            id = 1,
            title = "Sample Article 1",
            description = "lorem ipsum dolor sit amet",
            url = "https://example.com/article1",
            imageUrl = "https://example.com/image1.jpg"
        ),
        Article(
            id = 2,
            title = "Sample Article 2",
            description = "lorem ipsum dolor sit amet",
            url = "https://example.com/article2",
            imageUrl = "https://example.com/image2.jpg"
        )
    )

    DailyNewsTheme {
        HomeViewContent(uiState = ArticlesState(
            articles = articles,
            isLoading = false,
            error = null
        ), navController = navController)  // Pass NavController here
    }
}

