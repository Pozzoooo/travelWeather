package pozzo.apps.travelweather.core.injection

import android.app.Application
import com.google.gson.Gson
import dagger.Component
import okhttp3.OkHttpClient
import pozzo.apps.travelweather.analytics.AnalyticsModule
import pozzo.apps.travelweather.analytics.MapAnalytics
import pozzo.apps.travelweather.common.CommonModule
import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.direction.DirectionBusiness
import pozzo.apps.travelweather.direction.DirectionLineBusiness
import pozzo.apps.travelweather.direction.DirectionModule
import pozzo.apps.travelweather.direction.DirectionWeatherFilter
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastModule
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import pozzo.apps.travelweather.location.LocationBusiness
import pozzo.apps.travelweather.location.LocationModule
import pozzo.apps.travelweather.location.helper.GMapV2Direction
import pozzo.apps.travelweather.map.MapModule
import pozzo.apps.travelweather.map.parser.MapPointCreator
import pozzo.apps.travelweather.map.parser.WeatherToMapPointParser
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * todo re organize dependencies, seems to be getting a bit messy
 *
 * Root component.
 */
@Singleton
@Component(modules = [
    AppModule::class,
    NetworkModule::class,
    ForecastModule::class,
    DirectionModule::class,
    AnalyticsModule::class,
    CommonModule::class,
    LocationModule::class,
    MapModule::class
])
interface AppComponent {
    //App
    fun app(): Application

    //Network
    fun gson(): Gson
    fun okHttpClient(): OkHttpClient
    fun retrofitBuilder(): Retrofit.Builder

    //forecast
    fun forecastClient() : ForecastClient
    fun forecastBusiness() : ForecastBusiness
    fun forecastTypeMapper(): ForecastTypeMapper

    //direction
    fun directionBusiness() : DirectionBusiness
    fun directionWeatherFilter() : DirectionWeatherFilter

    //analytics
    fun mapAnalytics() : MapAnalytics

    //common
    fun preferencesBusiness() : PreferencesBusiness

    //location
    fun locationBusiness() : LocationBusiness
    fun directionLineBusiness() : DirectionLineBusiness
    fun directionParser() : GMapV2Direction

    //map
    fun weatherToMapPointParser() : WeatherToMapPointParser
    fun mapPointCreator() : MapPointCreator
}
