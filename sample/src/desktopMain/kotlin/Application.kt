import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.juul.krayon.color.black
import com.juul.krayon.color.cornflowerBlue
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.compose.Kanvas
import com.juul.krayon.kanvas.sansSerif

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            Kanvas(Modifier.fillMaxSize()) {
                drawColor(cornflowerBlue)
                drawLine(0f, 0f, width, height, Paint.Stroke(black, 1f))
                drawText("Hello, world", width / 2f, height / 2f, Paint.Text(black.copy(alpha = 0x80), 12f, Paint.Text.Alignment.Center, Font(sansSerif)))
            }
        }
    }
}
