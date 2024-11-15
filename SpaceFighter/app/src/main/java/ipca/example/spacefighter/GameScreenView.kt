package ipca.example.spacefighter

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@Composable
fun GameScreenView(navController: NavController) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    val density = configuration.densityDpi / 160f
    val screenWidthPx = screenWidth * density
    val screenHeightPx = screenHeight * density

    AndroidView(
        factory = { context ->
            // Initialize GameView with width and height
            GameView(context = context, width = screenWidthPx.toInt(), height = screenHeightPx.toInt()).apply {
                // Set the gameEventListener to handle player out-of-lives event
                gameEventListener = object : GameView.GameEventListener {
                    override fun onPlayerOutOfLives() {
                        navController.navigate("game_over")  // Navigate to game over screen when lives are zero
                    }
                }
            }
        },
        update = {
            it.resume()  // Start the game loop
        }
    )
}
