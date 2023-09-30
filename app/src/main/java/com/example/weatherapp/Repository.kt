import androidx.annotation.WorkerThread
import com.example.weatherapp.WeatherDao
import com.example.weatherapp.WeatherData

class WeatherRepository(private val weatherDao: WeatherDao) {
    val allWeatherData = weatherDao.getAllWeatherData()

    @WorkerThread
    suspend fun insert(weatherData: WeatherData) {
        weatherDao.insert(weatherData)
    }
}
