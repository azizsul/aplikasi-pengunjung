package com.azizsull.aplikasipengunjung

import android.text.TextUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Model POJO for a fieldType.
 */
data class FieldModel(
    var userId: String? = null,
    var fieldName: String? = null,
    var fieldType: String? = null,
    var price: Int? = 0
) {

    constructor(user: FirebaseUser, name: String, field: String, text: Int) : this() {
        this.userId = user.uid
        this.fieldName = name
        this.fieldType = field
        this.price = text
    }
}
