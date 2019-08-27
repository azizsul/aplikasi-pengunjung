package com.azizsull.aplikasipengunjung

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_restaurant_detail.*
import java.text.SimpleDateFormat
import java.util.*


class PlaceDetailActivity : AppCompatActivity(),
    EventListener<DocumentSnapshot> {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var placeRef: DocumentReference

    private var placeRegistration: ListenerRegistration? = null

    private val dateTime = Calendar.getInstance()
    private val time = dateTime.time
    private val currentTime = SimpleDateFormat("HH:mm").format(time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        // Get restaurant ID from extras
        val placesId = intent.extras?.getString(KEY_PLACE_ID)
            ?: throw IllegalArgumentException("Must pass extra $KEY_PLACE_ID")

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        println("TIME NOW IS: $currentTime")

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

        val beginning = placeModel.jamBuka
        val end = placeModel.jamTutup

        tv_nama_lapangan.text = placeModel.name
        tv_alamat.text = placeModel.alamat
        tv_noTelp.text = placeModel.noTelp
        if (currentTime in beginning..end) {
            tv_jamBuka.text = getString(R.string.open_now)
        } else {
            tv_jamBuka.text = getString(R.string.close_now)
        }
        tv_fasilitas.text = placeModel.facility

        // Background image
        Glide.with(placeImage.context)
            .load(placeModel.images[0])
            .apply(RequestOptions())
            .into(placeImage)
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
