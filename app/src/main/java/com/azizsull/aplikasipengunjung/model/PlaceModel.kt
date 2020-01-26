package com.azizsull.aplikasipengunjung.model

import com.google.firebase.firestore.IgnoreExtraProperties


/**
 * PlaceModel POJO.
 */
@IgnoreExtraProperties
class PlaceModel(

    val namaTempat: String = "",
    val alamat: String = "",
    val jamBuka: String = "",
    val jamTutup: String = "",
    val fasilitas: String = "",
    val noTelp: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val jenisLapangan: String = "",
    val gambar: ArrayList<String> =
        arrayListOf(PlaceImages().gambar)
) {

    var id: String = ""

    var jarak: Double = 0.0

    var isOpen: Boolean = false

    var hargaTerendah: Int = 0

    var hargaTertinggi: Int = 0


    fun getName(): String {
        return this.namaTempat
    }

    fun getFieldType(): String {
        return this.jenisLapangan
    }

}

class PlaceImages {
    val gambar: String = ""
}
