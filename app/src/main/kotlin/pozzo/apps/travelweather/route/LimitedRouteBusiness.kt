package pozzo.apps.travelweather.route

import pozzo.apps.travelweather.common.business.PreferencesBusiness
import pozzo.apps.travelweather.forecast.model.Route
import pozzo.apps.travelweather.forecast.model.point.FinishPoint
import pozzo.apps.travelweather.forecast.model.point.StartPoint

class LimitedRouteBusiness(
        private val unlimitedRouteBusiness : UnlimitedRouteBusiness,
        private val preferencesBusiness: PreferencesBusiness) : RouteBusiness {
    companion object {
        private const val DAILY_MAX_FREE_REQUESTS = 100
        private const val RESET_SPAN_MILLI = 24L * 60L * 60L * 1000L
    }

    override fun createRoute(startPoint: StartPoint?, finishPoint: FinishPoint?): Route {
        assertRemainingRequests()
        val route = unlimitedRouteBusiness.createRoute(startPoint, finishPoint)
        preferencesBusiness.getDaySelectionCount()
        incrementCounter(route)
        return route
    }

    private fun assertRemainingRequests() {
        checkRequestCounter()
        if (DAILY_MAX_FREE_REQUESTS <= preferencesBusiness.getUsedRequestCount()) {
            throw RequestLimitReached()
        }
    }

    private fun checkRequestCounter() {
        val min = System.currentTimeMillis() - RESET_SPAN_MILLI
        if (min > preferencesBusiness.getLastRemainingRequestReset()) {
            preferencesBusiness.resetUsedRequestCount()
        }
    }

    private fun incrementCounter(route: Route) {
        preferencesBusiness.addUsedRequestCount(route.weatherLocationCount)
    }

    /*
    todo Faltam:
        - conectar visualizacao do count com viewModel
        - descobrir como criar a parada pa pagar
        - alterar mensagem de erro
        - descobrir como ligar mensagem de erro para uma janela de pagamento
        - click listener pra mostrar janela de pagamento a partir do count na tela
        - adicionar analytics para entender como esta sendo utilizado
        - olhar analytics para decidir qual um maximo apropriado
     */
    fun getMaxRequest() : Int {
        return DAILY_MAX_FREE_REQUESTS //todo add paid ones
    }//todo sera que seria uma boa usar live data pra esses caras?

    fun getAvailableRequests(): Int {
        checkRequestCounter()
        val count = preferencesBusiness.getUsedRequestCount()
        val total = DAILY_MAX_FREE_REQUESTS - count//todo paid implementation here as well
        return if (total < 0) 0 else total
    }
}
