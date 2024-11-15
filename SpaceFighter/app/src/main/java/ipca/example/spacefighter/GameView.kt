package ipca.example.spacefighter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView : SurfaceView, Runnable {

    var playing = false
    var gameThread : Thread? = null
    lateinit var surfaceHolder : SurfaceHolder
    lateinit var canvas : Canvas
    lateinit var paint : Paint
    var stars = arrayListOf<Star>()
    var enemies = arrayListOf<Enemy>()
    var lastBulletAddTime: Long = 0 // Timestamp when bullets were last added
    var addedBulletsThisPeriod = false // Flag to check if we've added bullets recently
    lateinit var player : Player
    lateinit var boom : Boom
    private lateinit var shootButtonBitmap: Bitmap

    // Score variable
    private var score = 0

    interface GameEventListener {
        fun onPlayerOutOfLives()
    }

    var gameEventListener: GameEventListener? = null

    // Define button areas
    private var leftButtonRect = Rect()
    private var rightButtonRect = Rect()
    private var shootButtonRect = Rect()

    //bullets numbers
    var activeBulletsCount = 11

    private fun init(context: Context, width: Int, height: Int){
        surfaceHolder = holder
        paint = Paint()

        for (i in 0..100){
            stars.add(Star(width, height))
        }

        for (i in 0..2){
            enemies.add(Enemy(context,width, height))
        }

        player = Player(context, width, height)
        boom = Boom(context, width, height)

        // Initialize bullets
        for (i in 0..10) { // A large number to ensure enough bullets are available
            bullets.add(Bullet(-100, -100)) // Position off-screen initially
        }

        shootButtonBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.aim)
    }

    constructor(context: Context?, width: Int, height: Int) : super(context) {
        init(context!!, width, height)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init(context!!, 0, 0)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        init(context!!, 0, 0)
    }

    // Bullet class
    class Bullet(var x: Int, var y: Int, val width: Int = 60, val height: Int = 10) {
        var isActive = false

        fun update() {
            if (isActive) {
                x += 45 // Bullet moves horizontally to the right
            }
        }

        fun reset() {
            isActive = false
            x = -100 // Move the bullet off-screen when not active
            y = -100
        }
    }

    // List to store bullets
    var bullets = ArrayList<Bullet>()
    var isShootButtonPressed = false

    // Function to handle shooting
    fun shoot() {
        // Only shoot if there is room for more bullets
        if (activeBulletsCount > 0) {
            for (bullet in bullets) {
                if (!bullet.isActive) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        bullet.x = player.x + player.width
                        bullet.y = player.y + player.height / 2
                        bullet.isActive = true
                        activeBulletsCount-- // Decrement active bullet count when a bullet is fired
                    }, 500) // Delay of 500 milliseconds before firing the bullet
                    break
                }
            }
        }
    }

    // Add bullets after a time period
    fun addBulletsAfterTime() {
        val currentTime = System.currentTimeMillis()
        val timeElapsed = currentTime - lastBulletAddTime

        if (timeElapsed >= 15000 && !addedBulletsThisPeriod) {
            if (activeBulletsCount == 0) {
                val bulletsToAdd = 10
                for (i in 0 until bulletsToAdd) {
                    if (bullets.size < 20) {
                        bullets.add(Bullet(-100, -100)) // Add new bullets off-screen
                    }
                }
                activeBulletsCount += bulletsToAdd
            }
            addedBulletsThisPeriod = true
            lastBulletAddTime = currentTime
        }

        if (timeElapsed >= 15000 && addedBulletsThisPeriod) {
            addedBulletsThisPeriod = false
        }
    }

    fun resume() {
        playing = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    fun pause() {
        playing = false
        gameThread?.join()
    }

    override fun run() {
        while (playing){
            update()
            draw()
            control()
        }
    }

    fun update() {
        val movementDistance = 10

        addBulletsAfterTime()

        if (isLeftButtonPressed) {
            player.y -= movementDistance
        }
        if (isRightButtonPressed) {
            player.y += movementDistance
        }

        for (bullet in bullets) {
            bullet.update()

            if (bullet.x > width || bullet.x < 0) {
                bullet.reset()
                if (bullet.isActive) {
                    activeBulletsCount = maxOf(0, activeBulletsCount - 1) // Ensure activeBulletsCount does not go negative
                }
            }

            for (e in enemies) {
                if (bullet.isActive && Rect.intersects(bulletRect(bullet), e.detectCollision)) {
                    bullet.reset()
                    activeBulletsCount = maxOf(0, activeBulletsCount - 1) // Ensure activeBulletsCount does not go negative
                    boom.x = e.x
                    boom.y = e.y
                    e.x = -300
                    Handler(Looper.getMainLooper()).postDelayed({
                        boom.x = -300
                        boom.y = -300
                    }, 500) // Delay to show the boom animation
                }
            }
        }

        for (s in stars) {
            s.update(player.speed)
        }
        for (e in enemies) {
            e.update(player.speed)

            if (Rect.intersects(player.detectCollision, e.detectCollision)) {
                boom.x = e.x
                boom.y = e.y
                e.x = -300
                player.lives--
                Handler(Looper.getMainLooper()).postDelayed({
                    boom.x = -300
                    boom.y = -300
                }, 500) // Delay to show the boom animation
            }

            if (player.isOutOfLives()) {
                Handler(Looper.getMainLooper()).post {
                    gameEventListener?.onPlayerOutOfLives()
                }
                pause()
                return
            }
        }

        player.update()

        // Increment score on each update
        score++
    }

    fun bulletRect(bullet: Bullet): Rect {
        return Rect(bullet.x, bullet.y, bullet.x + bullet.width, bullet.y + bullet.height)
    }

    fun draw() {
        if (surfaceHolder.surface.isValid) {
            canvas = surfaceHolder.lockCanvas()
            canvas.drawColor(Color.BLACK)

            paint.color = Color.YELLOW

            for (star in stars) {
                paint.strokeWidth = star.starWidth.toFloat()
                canvas.drawPoint(star.x.toFloat(), star.y.toFloat(), paint)
            }

            canvas.drawBitmap(player.bitmap, player.x.toFloat(), player.y.toFloat(), paint)

            for (e in enemies) {
                canvas.drawBitmap(e.bitmap, e.x.toFloat(), e.y.toFloat(), paint)
            }
            canvas.drawBitmap(boom.bitmap, boom.x.toFloat(), boom.y.toFloat(), paint)

            paint.color = Color.RED
            for (bullet in bullets) {
                if (bullet.isActive) {
                    canvas.drawRect(bullet.x.toFloat(), bullet.y.toFloat(), (bullet.x + bullet.width).toFloat(), (bullet.y + bullet.height).toFloat(), paint)
                }
            }

            val buttonSize = 200
            leftButtonRect = Rect(0, height - buttonSize, buttonSize, height)
            rightButtonRect = Rect(width - buttonSize, height - buttonSize, width, height)
            shootButtonRect = Rect(width - buttonSize, height - buttonSize * 2, width, height - buttonSize)

            paint.color = Color.TRANSPARENT
            canvas.drawRect(leftButtonRect, paint)
            canvas.drawRect(rightButtonRect, paint)
            canvas.drawRect(shootButtonRect, paint)

            paint.color = Color.WHITE
            paint.style = Paint.Style.FILL
            val upArrowPath = android.graphics.Path().apply {
                moveTo(leftButtonRect.centerX().toFloat(), (leftButtonRect.centerY() - 50).toFloat())
                lineTo((leftButtonRect.centerX() - 50).toFloat(), (leftButtonRect.centerY() + 50).toFloat())
                lineTo((leftButtonRect.centerX() + 50).toFloat(), (leftButtonRect.centerY() + 50).toFloat())
                close()
            }
            canvas.drawPath(upArrowPath, paint)

            val downArrowPath = android.graphics.Path().apply {
                moveTo(rightButtonRect.centerX().toFloat(), (rightButtonRect.centerY() + 50).toFloat())
                lineTo((rightButtonRect.centerX() - 50).toFloat(), (rightButtonRect.centerY() - 50).toFloat())
                lineTo((rightButtonRect.centerX() + 50).toFloat(), (rightButtonRect.centerY() - 50).toFloat())
                close()
            }
            canvas.drawPath(downArrowPath, paint)

            canvas.drawBitmap(shootButtonBitmap, null, shootButtonRect, paint)

            paint.color = Color.WHITE
            paint.textSize = 50f
            canvas.drawText("Bullets: $activeBulletsCount", 20f, 90f, paint)

            // Draw score in the top-right corner
            canvas.drawText("Score: $score", width - 200f, 90f, paint)

            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    fun control(){
        Thread.sleep(17)
    }

    private var isLeftButtonPressed = false
    private var isRightButtonPressed = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (leftButtonRect.contains(event.x.toInt(), event.y.toInt())) {
                    isLeftButtonPressed = true
                } else if (rightButtonRect.contains(event.x.toInt(), event.y.toInt())) {
                    isRightButtonPressed = true
                } else if (shootButtonRect.contains(event.x.toInt(), event.y.toInt())) {
                    isShootButtonPressed = true
                    shoot()
                }
            }
            MotionEvent.ACTION_UP -> {
                isLeftButtonPressed = false
                isRightButtonPressed = false
                isShootButtonPressed = false
            }
        }
        return true
    }
}
