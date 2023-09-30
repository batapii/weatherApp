import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.WeatherData
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    val weatherDataList = repository.allWeatherData

    fun insert(weatherData: WeatherData) = viewModelScope.launch {
        repository.insert(weatherData)
    }
}
