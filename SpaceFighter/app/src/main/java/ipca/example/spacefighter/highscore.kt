package ipca.example.spacefighter


import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.io.File

@Composable
fun HighScoresView(navController: NavController, context: Context) {
    val highScores = readHighScores(context)

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "High Scores", style = androidx.compose.material3.MaterialTheme.typography.displayLarge)
            highScores.forEach { highScore ->
                Text(text = "${highScore.name}: ${highScore.score}")
            }
            androidx.compose.material3.Button(
                onClick = {
                    navController.navigate("game_start") // Navigate back to main menu
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Return to Main Menu")
            }
        }
    }
}

data class HighScore(val name: String, val score: Int)

fun saveHighScore(context: Context, highScore: HighScore) {
    val file = File(context.filesDir, "high_scores.txt")
    file.appendText("${highScore.name},${highScore.score}\n")
}

fun readHighScores(context: Context): List<HighScore> {
    val file = File(context.filesDir, "high_scores.txt")
    if (!file.exists()) return emptyList()
    return file.readLines().map {
        val parts = it.split(",")
        HighScore(parts[0], parts[1].toInt())
    }.sortedByDescending { it.score }
}