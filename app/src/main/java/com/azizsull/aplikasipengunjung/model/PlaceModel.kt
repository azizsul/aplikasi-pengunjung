package com.azizsull.aplikasipengunjung.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * PlaceModel POJO.
 */
@IgnoreExtraProperties
class PlaceModel(
    val placeId: String = "",
    val namaTempat: String = "",
    val alamat: String ="",
    val jamBuka: String = "",
    val jamTutup: String = "",
    val fasilitas: String = "",
    val noTelp: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val jenisLapangan: String = "",


    val gambar: ArrayList<String> = arrayListOf(PlaceImages().gambar)
) {

    companion object {

        const val FIELD_PRICE = "price"
        const val PLACE_NAME = "namaTempat"
    }

    var jarak: Double = 0.0
        get() = field
        set(value){field = value}

    var isOpen: Boolean = false
        get() = field
        set(value){field = value}

    var hargaTerendah: Int = 0
        get() = field
        set(value){field = value}

    var hargaTertinggi: Int = 0
        get() = field
        set(value){field = value}



    fun getName(): String {
        return  this.namaTempat
    }

    fun getFieldType(): String {
        return  this.jenisLapangan
    }

//    fun getHargaTerendah(): String {
//        return  this.hargaTerendah.toString()
//    }

//    fun getHargaTertinggi(): String {
//        return  this.hargaTertinggi.toString()
//    }




}

class PlaceImages {
    val gambar: String = ""
}
