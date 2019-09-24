package com.azizsull.aplikasipengunjung.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * PlaceModel POJO.
 */
@IgnoreExtraProperties
data class PlaceModel(
    val placeId: String = "",
    private  val name: String = "",
    val alamat: String ="",
    val jamBuka: String = "",
    val jamTutup: String = "",
    val facility: String = "",
    val noTelp: String = "",
    val lat: Double = 0.0,
    val long: Double = 0.0,
    val images: ArrayList<String> = arrayListOf(PlaceImages().images)
) {

    companion object {

        const val FIELD_PRICE = "price"
        const val OPEN_NOW = "jamBuka"
        const val FIELD_NAME = "name"
    }

    fun getName(): String {
        return  this.name
    }
}

class PlaceImages {
    val images: String = ""
}
