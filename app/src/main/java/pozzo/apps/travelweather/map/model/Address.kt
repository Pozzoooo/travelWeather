package pozzo.apps.travelweather.map.model

/**
 * Represent a single point in map.
 */
class Address {
    var address: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val address = other as Address

        if (java.lang.Double.compare(address.latitude, latitude) != 0) return false
        return if (java.lang.Double.compare(address.longitude, longitude) != 0) false
        else !if (this.address != null) this.address != address.address else address.address != null
    }

    override fun hashCode(): Int {
        var result: Int = if (address != null) address!!.hashCode() else 0
        var temp: Long = java.lang.Double.doubleToLongBits(latitude)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Double.doubleToLongBits(longitude)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        return result
    }
}
