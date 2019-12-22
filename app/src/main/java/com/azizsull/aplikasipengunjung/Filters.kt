package com.azizsull.aplikasipengunjung

import android.text.TextUtils
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.google.firebase.firestore.Query

/**
 * Object for passing filters around.
 */
class Filters {

    var location: String? = null
        get() = field
        set(value) {
            field = value
        }
    var openTime: String? = null
        get() = field
        set(value) {
            field = value
        }
    var price: String? = null
        get() = field
        set(value) {
            field = value
        }
    var type: String? = null
        get() = field
        set(value) {
            field = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Filters

        if (location != other.location) return false
        if (openTime != other.openTime) return false
        if (price != other.price) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = location?.hashCode() ?: 0
        result = 31 * result + (openTime?.hashCode() ?: 0)
        result = 31 * result + (price?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        return result
    }

    companion object {

        val default: Filters
            get() {
                val filters = Filters()
                filters.location = "Semua Lokasi"
                return filters
            }
    }

    override fun toString(): String {
        return "Filters(location=$location, openTime=$openTime, price=$price, type=$type)"
    }




}
