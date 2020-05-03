package pozzo.apps.travelweather.map.overlay

//TODO definitively not a good solution, needs rethinking
enum class LastRunKey(val key: String) {
  DRAG_THE_FLAG("fullTutorial"),
  DRAG_AGAIN("routeCreatedTutorial"),
  DAY_SELECTION("daySelection"),
  RATE_DIALOG("rateDialog"),
  FORECAST_DETAILS("forecastDetails")
}
