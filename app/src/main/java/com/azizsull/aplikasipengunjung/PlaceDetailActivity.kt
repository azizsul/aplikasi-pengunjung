package com.azizsull.aplikasipengunjung

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.github.debop.javatimes.NowLocalDateTime
import com.github.debop.kodatimes.dateTimeOf
import com.github.debop.kodatimes.ranges.DateTimeRange
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.activity_restaurant_detail.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


class PlaceDetailActivity : AppCompatActivity(),
        EventListener<DocumentSnapshot>{

    private lateinit var firestore: FirebaseFirestore
    private lateinit var placeRef: DocumentReference

    private var placeRegistration: ListenerRegistration? = null

    private val dateTime = SimpleDateFormat.getTimeInstance().format(Date().time)
    private val beginning = "08:00"
    private val end = "12:44"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        // Get restaurant ID from extras
        val placesId = intent.extras?.getString(KEY_PLACE_ID)
                ?: throw IllegalArgumentException("Must pass extra $KEY_PLACE_ID")

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        println("TIME NOW IS: $dateTime")

        // Get reference to the restaurant
        placeRef = firestore.collection("Lapangan").document(placesId)

    }

    public override fun onStart() {
        super.onStart()

        placeRegistration = placeRef.addSnapshotListener(this)
    }

    public override fun onStop() {
        super.onStop()

        placeRegistration?.remove()
        placeRegistration = null
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
    }

    /**
     * Listener for the PlaceModel document ([.placeRef]).
     */
    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e)
            return
        }

        snapshot?.let {
            val place = snapshot.toObject(PlaceModel::class.java)
            if (place != null) {
                onPlaceLoaded(place)
            }
        }
    }

    private fun onPlaceLoaded(placeModel: PlaceModel) {
        tv_nama_lapangan.text = placeModel.name
        tv_alamat.text = placeModel.alamat
        tv_noTelp.text = placeModel.noTelp
        if(dateTime in beginning..end ) {
            tv_jamBuka.text = "BUKA"
        } else {
            tv_jamBuka.text = "TUTUP"
        }
        tv_fasilitas.text = placeModel.facility

        // Background image
//        Glide.with(placeImage.context)
//                .load(placeModel.images)
//                .into(placeImage)
    }

    private fun onBackArrowClicked() {
        onBackPressed()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    companion object {

        private const val TAG = "PlaceDetail"

        const val KEY_PLACE_ID = "key_place_id"
    }



}
