package com.azizsull.aplikasipengunjung

import android.content.Context
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_restaurant_detail.fabShowRatingDialog
import kotlinx.android.synthetic.main.activity_restaurant_detail.recyclerRatings
import kotlinx.android.synthetic.main.activity_restaurant_detail.restaurantButtonBack
import kotlinx.android.synthetic.main.activity_restaurant_detail.restaurantCity
import kotlinx.android.synthetic.main.activity_restaurant_detail.restaurantImage
import kotlinx.android.synthetic.main.activity_restaurant_detail.restaurantName
import kotlinx.android.synthetic.main.activity_restaurant_detail.viewEmptyRatings

class PlaceDetailActivity : AppCompatActivity(),
        EventListener<DocumentSnapshot>,
        RatingDialogFragment.RatingListener {

    private var ratingDialog: RatingDialogFragment? = null

    private lateinit var firestore: FirebaseFirestore
    private lateinit var placeRef: DocumentReference
    private lateinit var ratingAdapter: RatingAdapter

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

        // Get ratings
        val ratingsQuery = placeRef
                .collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)

        // RecyclerView
        ratingAdapter = object : RatingAdapter(ratingsQuery) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    recyclerRatings.visibility = View.GONE
                    viewEmptyRatings.visibility = View.VISIBLE
                } else {
                    recyclerRatings.visibility = View.VISIBLE
                    viewEmptyRatings.visibility = View.GONE
                }
            }
        }
        recyclerRatings.layoutManager = LinearLayoutManager(this)
        recyclerRatings.adapter = ratingAdapter

        ratingDialog = RatingDialogFragment()

        restaurantButtonBack.setOnClickListener { onBackArrowClicked() }
        fabShowRatingDialog.setOnClickListener { onAddRatingClicked() }
    }

    public override fun onStart() {
        super.onStart()

        ratingAdapter.startListening()
        placeRegistration = placeRef.addSnapshotListener(this)
    }

    public override fun onStop() {
        super.onStop()

        ratingAdapter.stopListening()

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
        restaurantName.text = placeModel.name
//        restaurantRating.rating = placeModel.avgRating.toFloat()
//        restaurantNumRatings.text = getString(R.string.fmt_num_ratings, placeModel.numRatings)
        restaurantCity.text = placeModel.alamat
//        restaurantCategory.text = placeModel.category
//        restaurantPrice.text = PlaceUtil.getPriceString(placeModel)

        // Background image
//        Glide.with(restaurantImage.context)
//                .load(placeModel.images)
//                .into(restaurantImage)
    }

    private fun onBackArrowClicked() {
        onBackPressed()
    }

    private fun onAddRatingClicked() {
        ratingDialog?.show(supportFragmentManager, RatingDialogFragment.TAG)
    }

    override fun onRating(rating: Rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(placeRef, rating)
                .addOnSuccessListener(this) {
                    Log.d(TAG, "Rating added")

                    // Hide keyboard and scroll to top
                    hideKeyboard()
                    recyclerRatings.smoothScrollToPosition(0)
                }
                .addOnFailureListener(this) { e ->
                    Log.w(TAG, "Add rating failed", e)

                    // Show failure message and hide keyboard
                    hideKeyboard()
                    Snackbar.make(findViewById(android.R.id.content), "Failed to add rating",
                            Snackbar.LENGTH_SHORT).show()
                }
    }

    private fun addRating(placeRef: DocumentReference, rating: Rating): Task<Void> {
        // Create reference for new rating, for use inside the transaction
        val ratingRef = placeRef.collection("ratings").document()

        // In a transaction, add the new rating and update the aggregate totals
        return firestore.runTransaction { transaction ->
            val place = transaction.get(placeRef).toObject(PlaceModel::class.java)
            if (place == null) {
                throw Exception("Place not found at ${placeRef.path}")
            }

            // Compute new number of ratings
            val newNumRatings = place.numRatings + 1

            // Compute new average rating
            val oldRatingTotal = place.avgRating * place.numRatings
            val newAvgRating = (oldRatingTotal + rating.rating) / newNumRatings

            // Set new restaurant info
            place.numRatings = newNumRatings
            place.avgRating = newAvgRating

            // Commit to Firestore
            transaction.set(placeRef, place)
            transaction.set(ratingRef, rating)

            null
        }
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
