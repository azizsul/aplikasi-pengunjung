package com.azizsull.aplikasipengunjung

import android.text.TextUtils
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.google.firebase.firestore.Query

/**
 * Object for passing filters around.
 */
class Filters {

    var city: String? = null
    var price = -1
    var sortBy: String? = null
    var sortDirection: Query.Direction = Query.Direction.DESCENDING

    fun hasCity(): Boolean {
        return !TextUtils.isEmpty(city)
    }

    fun hasPrice(): Boolean {
        return price > 0
    }

    fun hasSortBy(): Boolean {
        return !TextUtils.isEmpty(sortBy)
    }

    fun getSearchDescription(): String {
        val desc = StringBuilder()

        if (city != null) {
            desc.append("<b>")
            desc.append(city)
            desc.append("</b>")
        }

        if (price > 0) {
            desc.append(" for ")
            desc.append("<b>")
//            desc.append(PlaceUtil.getPriceString(price))
            desc.append("</b>")
        }

        return desc.toString()
    }

//    fun getOrderDescription(context: Context): String {
//        return when (sortBy) {
//            PlaceModel.FIELD_PRICE -> context.getString(R.string.sorted_by_price)
//            PlaceModel.FIELD_POPULARITY -> context.getString(R.string.sorted_by_popularity)
//            else -> context.getString(R.string.sorted_by_alphabet)
//        }
//    }

    companion object {

        val default: Filters
            get() {
                val filters = Filters()
                filters.sortBy = PlaceModel.PLACE_NAME
                filters.sortDirection = Query.Direction.ASCENDING

                return filters
            }
    }
}
