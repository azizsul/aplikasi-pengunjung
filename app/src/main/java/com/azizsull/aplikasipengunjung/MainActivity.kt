package com.azizsull.aplikasipengunjung

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.azizsull.aplikasipengunjung.adapter.PlaceAdapter
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.google.android.gms.location.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    FilterDialogFragment.FilterListener {

    private val permissionId = 42
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    var lat = 0.0
    var long = 0.0

    private lateinit var firestore: FirebaseFirestore
    private lateinit var query: Query

    private lateinit var filterDialog: FilterDialogFragment
    lateinit var mAdapter: PlaceAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        svSearch.queryHint = "Cari berdasarkan nama"

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true)

        // Firestore
        firestore = FirebaseFirestore.getInstance()

        // Get ${LIMIT} places futsal
        query = firestore.collection("tempatFutsal")
            .orderBy("namaTempat", Query.Direction.ASCENDING)
            .limit(LIMIT.toLong())

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {

                    placeLists.clear()

                    for (item in document) {

                        val data = item.toObject(PlaceModel::class.java)

                        data.id = item.id

                        placeLists.add(data)

                        mAdapter = PlaceAdapter()

                        mAdapter.setData(this, placeLists)

                        recyclerPlace.layoutManager = LinearLayoutManager(this)
                        recyclerPlace.adapter = mAdapter

                    }
                } else {
                    Toast.makeText(this, "Tidak ada item untuk ditampilkan", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
//                Log.d(TAG, "get failed with ", task.exception)
            }
        }


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
                mAdapter.search(p0)

                return false
            }
        })

    }


    public override fun onStart() {
        super.onStart()

        progressLoading.visibility = View.VISIBLE
    }

    public override fun onStop() {
        super.onStop()

        progressLoading.visibility = View.GONE
    }

    private fun onFilterClicked() {
        // Show the dialog containing filter options
        filterDialog.show(supportFragmentManager, FilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        filterDialog = FilterDialogFragment()
        filterDialog.resetFilters()
        mAdapter.resetFilter()

        onFilter(Filters.default)

    }

    override fun onFilter(filters: Filters) {

        mAdapter.filter(filters)
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
        val mLocationRequest = LocationRequest()
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
            permissionId
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    companion object {

        private const val LIMIT = 50
        var placeLists = ArrayList<PlaceModel>()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}