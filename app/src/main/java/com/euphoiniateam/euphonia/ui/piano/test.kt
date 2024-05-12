import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomSliderExample() {
    // Значение слайдера
    var sliderValue by remember { mutableStateOf(0f) }

    // Обновление значения слайдера
    Slider(
        value = sliderValue,
        onValueChange = { newValue -> sliderValue = newValue },
        valueRange = 0f..100f, // Диапазон значений слайдера
        modifier = Modifier.fillMaxWidth() // Занимает всю ширину родительского элемента
    )

    // Отображение текущего значения слайдера
    Text("Текущее значение: ${sliderValue.toInt()}%", modifier = Modifier.padding(16.dp))
}

@Preview
@Composable
private fun Test() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomSliderExample()
        }
    }
}
