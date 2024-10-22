package ipca.examples.dailynews.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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

    val viewModel : HomeViewModel = viewModel()
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
    navController: NavController = rememberNavController(),
    uiState: ArticlesState
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxSize()
    ) {
        androidx.compose.material3.TopAppBar(
            title = {
                androidx.compose.material3.Text(
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
            if (uiState.isLoading) {
                Text("Loading articles...")
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}")
            } else if (uiState.articles.isEmpty()) {
                Text("No articles found!")
            } else {
                LazyColumn(modifier = modifier.fillMaxSize()) {
                    itemsIndexed(
                        items = uiState.articles,
                    ) { index, article ->
                        RowArticle(
                            modifier = Modifier
                                .clickable {
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

    val articles = arrayListOf(
        Article("Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin lobortis augue in erat scelerisque, vitae fringilla nisi tempus. Sed finibus tellus porttitor dignissim eleifend. Etiam sed neque libero. Integer auctor turpis est. Nunc ac auctor velit. Nunc et mi sollicitudin, iaculis nunc et, congue neque. Suspendisse potenti. Vestibulum finibus justo sed eleifend commodo. Phasellus vestibulum ligula nisi, convallis rhoncus quam placerat id. Donec eu lobortis lacus, quis porta tortor. Suspendisse quis dolor sapien. Maecenas finibus purus at orci aliquam eleifend. Nam venenatis sapien ac enim efficitur pretium. Praesent sagittis risus vitae feugiat blandit. Etiam non neque arcu. Cras a mauris eu erat sodales iaculis non a lorem.",
            urlToImage = "https://media.istockphoto.com/id/1166633394/pt/foto/victorian-british-army-gymnastic-team-aldershot-19th-century.jpg?s=1024x1024&w=is&k=20&c=fIfqysdzOinu8hNJG6ZXOhl8ghQHA7ySl8BZZYWrxyQ="),
        Article("Lorem Ipsum is simply dummy text of the printing", "description"))

    //val articles = arrayListOf<Article>()
    DailyNewsTheme {
        HomeViewContent(uiState = ArticlesState(
            articles = articles,
            isLoading = false,
            error = null
        ))
    }
}