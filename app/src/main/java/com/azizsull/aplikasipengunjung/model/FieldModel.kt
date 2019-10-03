package com.azizsull.aplikasipengunjung.model

import android.text.TextUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class FieldModel(
    val kodeLapangan: String ="",
    val hargaSiang: Int = 0,
    val hargaMalam: Int = 0,
    val jenis: String =""
)
