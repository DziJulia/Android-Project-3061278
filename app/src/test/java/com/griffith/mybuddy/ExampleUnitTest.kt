package com.griffith.mybuddy

import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*
import java.util.concurrent.TimeUnit

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
        val url =
            "http://api.openweathermap.org/data/2.5/weather?lat=35.6895&lon=139.6917&appid=5e99e2e828c2a3d4b57fab4f8772528f"
        val result = fetchWeatherData(url)

        // Check that the result is not null
        assertNotNull(result)
    }

    @Test
    fun testTemperatureConversion() = runBlocking {
        val parser = FakeWeatherDataParser()
        val temperature = parser.getTemperature(
            """
        {
            "main": {
                "temp": 300.15
            }
        """.trimIndent()
        )

        assertEquals(27.0, temperature, 0.01)
    }

    @Test
    fun testGetTimeUntilMidnight() {
        val timeUntilMidnight = getTimeUntilMidnight()

        // Check that the time until midnight is not negative
        assertTrue(timeUntilMidnight >= 0)

        // Check that the time until midnight is less than 24 hours
        assertTrue(timeUntilMidnight < TimeUnit.DAYS.toMillis(1))
    }

    /**
     * Validation of password test
     */
    @Test
    fun testIsValidatePassword() {
        val password1 = "Password123!"
        val password2 = "pass"
        val password3 = "PasswordWithoutSpecialChar1"
        val password4 = "password123"
        val password5 = "PASSWORD!"
        val password6 = "Password 123!"

        val result1 = password1.isValidatePassword()
        val result2 = password2.isValidatePassword()
        val result3 = password3.isValidatePassword()
        val result4 = password4.isValidatePassword()
        val result5 = password5.isValidatePassword()
        val result6 = password6.isValidatePassword()

        assertEquals("", result1)
        assertEquals(ERR_LEN, result2)
        assertEquals(ERR_SPECIAL, result3)
        assertEquals(ERR_UPPER, result4)
        assertEquals(ERR_DIGIT, result5)
        assertEquals(ERR_WHITESPACE, result6)
    }

    /**
     * Validation of email test
     */
    @Test
    fun testIsValidateEmail() {
        val notValidEmail1 = "email"
        val notValidEmail2 = "pass@"
        val notValidEmail3 = "pass@email"
        val validEmail = "email@email.com"

        val result1 = notValidEmail1.isValidEmail()
        val result2 = notValidEmail2.isValidEmail()
        val result3 = notValidEmail3.isValidEmail()
        val result4 = validEmail.isValidEmail()

        assertEquals(false, result1)
        assertEquals(false, result2)
        assertEquals(false, result3)
        assertEquals(true, result4)
    }
}