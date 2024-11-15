package ipca.example.spacefighter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

class Player {

    var x = 0
    var y = 0
    var speed = 0
    var maxX = 0
    var maxY = 0
    var minX = 0
    var minY = 0
    var lives = 1


    var bitmap : Bitmap

    //tamanho imagem
    var width = 128
    var height = 92

    val aceleration = 0.1f
    private val MAX_SPEED = 500
    private val MIN_SPEED = 1

    var detectCollision : Rect

    constructor(context: Context, width: Int, height: Int){
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.player)

        minX = 0
        maxX = width

        maxY = height - bitmap.height
        minY = 0

        x = 75
        y = 50

        speed = 1

        detectCollision = Rect(x, y, width, bitmap.height)
    }


    fun update() {
        // Gradually increase the speed, ensuring it doesn't exceed the maximum
        if (speed < MAX_SPEED) {
            speed += (aceleration).toInt() // Gradual integer increment
        }

        // Clamp the speed to be within the min and max bounds
        if (speed > MAX_SPEED) speed = MAX_SPEED
        if (speed < MIN_SPEED) speed = MIN_SPEED

        // Apply the movement logic for y, clamping to the min and max bounds
        if (y < minY) y = minY
        if (y > maxY) y = maxY

        // Update the collision detection boundaries
        detectCollision.left = x
        detectCollision.top = y
        detectCollision.right = x + bitmap.width
        detectCollision.bottom = y + bitmap.height
    }

    // Check if player is out of lives
    fun isOutOfLives(): Boolean {
        return lives == 0
    }

}