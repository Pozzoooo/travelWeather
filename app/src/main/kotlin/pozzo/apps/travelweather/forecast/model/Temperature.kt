package pozzo.apps.travelweather.forecast.model

class Temperature {
    private var fahrenheit: Double = .0

    fun setCelsius(value: Double) {
        fahrenheit = convertCelsiusToFahrenheit(value)
    }

    fun setFahrenheit(value: Double) {
        fahrenheit = value
    }

    fun getFahrenheit(): Double = fahrenheit

    fun getCelsius(): Double = convertFahrenheitToCelsius(fahrenheit)

    private fun convertCelsiusToFahrenheit(celsius: Double) = 9.0 / 5.0 * celsius + 32.0

    private fun convertFahrenheitToCelsius(fahrenheit: Double) = (fahrenheit - 32.0) * 5.0 / 9.0
}
