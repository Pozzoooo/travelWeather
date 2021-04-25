package pozzo.apps.travelweather.map.movement

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class EdgeDectection {

    companion object {//TODO o que acha de mudar para aceleracao? Ao inves de boleanos posso ter um fator!
        private const val EDGE_REGION_FACTOR = .05
    }

    fun checkEdge(bounds: LatLngBounds, position: LatLng): Movement {
        val northeast = bounds.northeast
        val southwest = bounds.southwest

        val height = northeast.longitude - southwest.longitude
        val width = northeast.latitude - southwest.latitude

        val heightBorderSize = height * EDGE_REGION_FACTOR
        val widthBorderSize = width * EDGE_REGION_FACTOR

        val northBorder = northeast.latitude - heightBorderSize
        val eastBorder = northeast.longitude - widthBorderSize
        val southBorder = southwest.latitude + heightBorderSize
        val westBorder = southwest.longitude + widthBorderSize

        val movement = Movement()

        movement.north = position.latitude > northBorder
        movement.east = position.longitude > eastBorder
        movement.south = position.latitude < southBorder
        movement.west = position.longitude < westBorder

        return movement
    }
}
