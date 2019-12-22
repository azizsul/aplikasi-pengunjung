package com.azizsull.aplikasipengunjung

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.azizsull.aplikasipengunjung.adapter.NewPlaceAdapter
import com.azizsull.aplikasipengunjung.adapter.PlaceAdapter
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.azizsull.aplikasipengunjung.viewmodel.MainActivityViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.core.Filter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.dialog_filters.*
import me.carleslc.kotlin.extensions.conversions.toInt
import me.carleslc.kotlin.extensions.standard.with
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),
    FilterDialogFragment.FilterListener {

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    var lat = 0.0
    var long = 0.0

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    private lateinit var filterDialog: FilterDialogFragment
    lateinit var adapter: PlaceAdapter
    lateinit var mAdapter: NewPlaceAdapter


    private lateinit var viewModel: MainActivityViewModel

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        // View model
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true)

        // Firestore
        firestore = FirebaseFirestore.getInstance()

        // Get ${LIMIT} restaurants
        query = firestore.collection("tempatFutsal")
            .orderBy("namaTempat", Query.Direction.ASCENDING)
            .limit(LIMIT.toLong())

        query.get().addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: " + task.result)

                    placeLists.clear()

                    for (item in document) {
                        Log.d(TAG, "${item.id} => ${item.data}")

                        var data = item.toObject(PlaceModel::class.java)

                        data.id = item.id

                        placeLists.add(data)

                        mAdapter = NewPlaceAdapter()

                        mAdapter.setData(this, placeLists)

//
                        recyclerPlace.layoutManager = LinearLayoutManager(this)
                        recyclerPlace.adapter = mAdapter

//                        mAdapter.adapter = placeLists
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
            }
        }


//        // RecyclerView
//        adapter = object : PlaceAdapter(this@MainActivity, query, this@MainActivity) {
//            override fun onDataChanged() {
//                // Show/hide content if the query returns empty.
//                if (itemCount == 0) {
//                    recyclerPlace.visibility = View.GONE
//                    viewEmpty.visibility = View.VISIBLE
//                } else {
//                    recyclerPlace.visibility = View.VISIBLE
//                    viewEmpty.visibility = View.GONE
//
//                }
//                progressLoading.visibility = View.GONE
//
//            }
//
//            override fun onError(e: FirebaseFirestoreException) {
//                // Show a snackbar on errors
//                Snackbar.make(
//                    findViewById(android.R.id.content),
//                    "Error: check logs for info.", Snackbar.LENGTH_LONG
//                ).show()
//                progressLoading.visibility = View.GONE
//
//            }
//        }
//
////        Log.e("adapter", query)
//
//        recyclerPlace.layoutManager = LinearLayoutManager(this)
//        recyclerPlace.adapter = adapter

        // Filter Dialog
        filterDialog = FilterDialogFragment()

        filterBar.setOnClickListener { onFilterClicked() }
        buttonClearFilter.setOnClickListener { onClearFilterClicked() }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()

        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String): Boolean {


                Log.e("search", p0)
//                onSearch(p0)
//                mAdapter.filter(p0)
//                adapter.search(p0)
                mAdapter?.search(p0)

                return false
            }
        })

    }

    private fun onSearch(p0: String) {

        var query: Query = firestore.collection("tempatFutsal")
        query.whereLessThanOrEqualTo("namaTempat", p0)
        query.whereGreaterThanOrEqualTo("namaTempat", p0)

        query.whereLessThanOrEqualTo("alamat", p0)
        query.whereGreaterThanOrEqualTo("alamat", p0)

        query.whereLessThanOrEqualTo("fasilitas", p0)
        query.whereGreaterThanOrEqualTo("fasilitas", p0)

        query.whereLessThanOrEqualTo("jenisLapangan", p0)
        query.whereGreaterThanOrEqualTo("jenisLapangan", p0)

        query = query.limit(LIMIT.toLong())

        // Update the query
//        adapter.setQuery(query)
    }



    public override fun onStart() {
        super.onStart()

        // Apply filters
//        onFilter(viewModel.filters)

        progressLoading.visibility = View.VISIBLE

        // Start listening for Firestore updates
//        adapter.startListening()
    }

    public override fun onStop() {
        super.onStop()
//        adapter.stopListening()

        progressLoading.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            viewModel.isSigningIn = false

            if (resultCode != Activity.RESULT_OK) {
                if (response == null) {
                    // User pressed the back button.
                    finish()
                } else if (response.error != null && response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    showSignInErrorDialog(R.string.message_no_network)
                } else {
                    showSignInErrorDialog(R.string.message_unknown)
                }
            }
        }
    }

    private fun onFilterClicked() {
        // Show the dialog containing filter options
        filterDialog.show(supportFragmentManager, FilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        filterDialog = FilterDialogFragment()
        filterDialog.resetFilters()
        mAdapter.resetFilter()

        Log.d("clearFilter", "clicked");

        onFilter(Filters.default)

    }
    

    override fun onFilter(filters: Filters) {
        // Construct query basic query
        var query: Query = firestore.collection("tempatFutsal")


        //get Current time and format to HH:mm
//        val now = SimpleDateFormat("HH:mm")
//        val currentTime = now.format(Date())
//        Log.e("now" , currentTime)
//
//        Log.e("currentime" , currentTime)
//
//        Log.e("filter" , filters.toString())

          mAdapter.filter(filters)

//
//
//        if (filters.openTime != "Semua Jam Buka") {
//            query.whereLessThanOrEqualTo("jamTutup", currentTime.toString())
//            query.whereGreaterThanOrEqualTo("jamBuka", currentTime.toString())
//
//        }
//
//
//        if(filters.price == "Harga Terendah"){
//            query = query.orderBy("hargaTerendah", Query.Direction.ASCENDING)
//        }else if(filters.price == "Harga Tertinggi"){
//            query = query.orderBy("hargaTertinggi", Query.Direction.DESCENDING)
//        }
//
//        if (filters.type == "Vinyl") {
//            query = query.whereIn("jenisLapangan", listOf("Vinyl", "Vinyl, Sintetis"))
//        }else if(filters.type == "Sintetis"){
//            query = query.whereIn("jenisLapangan", listOf("Sintetis", "Vinyl, Sintetis"))
//        }
//
//        if (filters.location != "Semua Lokasi") {
//            query = query.orderBy("latitude", Query.Direction.ASCENDING)
//            query = query.orderBy("longitude", Query.Direction.ASCENDING)
//
//        }
//
//        // Limit items
//        query = query.limit(LIMIT.toLong())
//
////        Log.e("query" , query.())
//
//
//        // Update the query
//        adapter.setQuery(query)
//
//        // Save filters
//        viewModel.filters = filters

    }

    private fun shouldStartSignIn(): Boolean {
        return !viewModel.isSigningIn && FirebaseAuth.getInstance().currentUser == null
    }

    private fun startSignIn() {
        // Sign in with FirebaseUI
        val intent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
            .setIsSmartLockEnabled(false)
            .build()

        startActivityForResult(intent, RC_SIGN_IN)
        viewModel.isSigningIn = true
    }

    private fun showSignInErrorDialog(@StringRes message: Int) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.title_sign_in_error)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.option_retry) { _, _ -> startSignIn() }
            .setNegativeButton(R.string.option_exit) { _, _ -> finish() }.create()

        dialog.show()
    }

    /**
     *GET CURRENT LOCATION
     */

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        lat = location.latitude
                        long = location.longitude

                        println("LATITITS  ::: $lat")
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            lat = mLastLocation.latitude
            long = mLastLocation.longitude
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    companion object {

        private const val TAG = "MainActivity"

        private const val RC_SIGN_IN = 9001

        private const val LIMIT = 50

        var placeLists = ArrayList<PlaceModel>()


        /**
         * GeoFire attr
         */
        private const val COLLECTION = "tempatFutsal"
        /**
         *End of GeoFire attr
         */
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}



//
//private fun Query.whereGreaterThanOrEqualTo(s: String, type: String?): Query {
//
//}
//


