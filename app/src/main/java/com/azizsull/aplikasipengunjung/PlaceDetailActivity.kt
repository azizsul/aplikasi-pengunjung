package com.azizsull.aplikasipengunjung

import android.content.Context
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_restaurant_detail.*

class PlaceDetailActivity : AppCompatActivity(),
        EventListener<DocumentSnapshot>{

    private lateinit var firestore: FirebaseFirestore
    private lateinit var placeRef: DocumentReference

    private var placeRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        // Get restaurant ID from extras
        val placesId = intent.extras?.getString(KEY_PLACE_ID)
                ?: throw IllegalArgumentException("Must pass extra $KEY_PLACE_ID")

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

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
//        restaurantRating.rating = placeModel.avgRating.toFloat()
//        restaurantNumRatings.text = getString(R.string.fmt_num_ratings, placeModel.numRatings)
        tv_alamat.text = placeModel.alamat
//        tv_alamat.text = placeModel.category
//        tv_alamat.text = placeModel.hargaSiang

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
