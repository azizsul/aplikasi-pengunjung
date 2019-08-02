package com.azizsull.aplikasipengunjung.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * PlaceModel POJO.
 */
@IgnoreExtraProperties
data class PlaceModel(
    var name: String? = null,
    var alamat: String? = null,
    var category: String? = null,
//    var images: String? = null,
    var price: Int = 0,
    var numRatings: Int = 0,
    var avgRating: Double = 0.toDouble()
) {

    companion object {

        const val FIELD_CITY = "alamat"
        const val FIELD_CATEGORY = "category"
        const val FIELD_PRICE = "price"
        const val FIELD_POPULARITY = "numRatings"
        const val FIELD_AVG_RATING = "avgRating"
    }
}
