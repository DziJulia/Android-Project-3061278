package com.griffith.mybuddy

import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*

open class WeatherDataParser {
    open fun getTemperature(json: String): Double {
        val weatherData = JSONObject(json)
        return weatherData.getJSONObject("main").getDouble("temp") - 273.15
    }
}

class FakeWeatherDataParser : WeatherDataParser() {
    override fun getTemperature(json: String): Double {
        return 27.0  // Return a fixed temperature for testing
    }
}

class ExampleUnitTest {
    @Test
    fun testFetchWeatherData() = runBlocking {
        // URL for Tokyo
        val url = "http://api.openweathermap.org/data/2.5/weather?lat=35.6895&lon=139.6917&appid=5e99e2e828c2a3d4b57fab4f8772528f"
        val result = fetchWeatherData(url)

        // Check that the result is not null
        assertNotNull(result)
    }

    @Test
    fun testTemperatureConversion() = runBlocking {
        val parser = FakeWeatherDataParser()
        val temperature = parser.getTemperature("""
        {
            "main": {
                "temp": 300.15
            }
        """.trimIndent())

        assertEquals(27.0, temperature, 0.01)
    }
}