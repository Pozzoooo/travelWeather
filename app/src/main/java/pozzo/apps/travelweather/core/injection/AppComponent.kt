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
import pozzo.apps.travelweather.direction.DirectionModule
import pozzo.apps.travelweather.forecast.ForecastBusiness
import pozzo.apps.travelweather.forecast.ForecastClient
import pozzo.apps.travelweather.forecast.ForecastModule
import pozzo.apps.travelweather.forecast.ForecastTypeMapper
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Root component.
 */
@Singleton
@Component(modules = [
    AppModule::class,
    NetworkModule::class,
    ForecastModule::class,
    DirectionModule::class,
    AnalyticsModule::class,
    CommonModule::class
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

    //analytics
    fun mapAnalytics() : MapAnalytics

    //common
    fun preferencesBusiness() : PreferencesBusiness
}
