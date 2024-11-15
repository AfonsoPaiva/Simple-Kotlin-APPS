package ipca.example.spacefighter

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun GameOverView(navController: NavController, context: Context, score: Int) {
    var playerName by remember { mutableStateOf("") }

    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Game Over", style = androidx.compose.material3.MaterialTheme.typography.displayLarge)
            TextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Enter your name") }
            )
            androidx.compose.material3.Button(
                onClick = {
                    saveHighScore(context, HighScore(playerName, score))
                    navController.navigate("high_scores")
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Save Score")
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
