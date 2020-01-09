@file:Suppress("DEPRECATION")

package com.azizsull.aplikasipengunjung

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    val extraName: String = "namaTempat"
    val extraLat: String = "latitude"
    val extraLong: String = "longitude"

    private val myPermissionRequestLocation = 99

    private var mMap: GoogleMap? = null
    private var mapFrag: SupportMapFragment? = null
    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    var mCurrLocationMarker: Marker? = null
//    var placeMarker: Marker? = null
    private var mCircle: Circle? = null

    private var radiusInMeters = 500.0
    private var strokeColor = -0x10000 //Color Code you want

    private var shadeColor = 0x089CFF0 //opaque red fill


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mapFrag =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag!!.getMapAsync(this)
    }

    override fun onPause() {
        super.onPause()

        //stop location updates when Activity is no longer active


        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }

    }



    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient?.connect()
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onLocationChanged(location: Location) {

        val lat = intent.getStringExtra(extraLat)
        val lng = intent.getStringExtra(extraLong)
        val namaTempat = intent.getStringExtra(extraName)

        val x = lat.toDouble()
        val y = lng.toDouble()

        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        //Place current location marker
        val myLocation = LatLng(location.latitude,location.longitude)
        val addCircle: CircleOptions? = CircleOptions().center(myLocation).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2f)
        mCircle = mMap!!.addCircle(addCircle)

        if (x != 0.0 && y != 0.0) {
            val destinationLoc = LatLng(x, y)
            val destinationMarkerOptions = MarkerOptions().position(destinationLoc).title(namaTempat)
            mMap?.addMarker(destinationMarkerOptions)
            try {
                val bounds = LatLngBounds.builder().include(destinationLoc).include(myLocation).build()
                mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300))
            } catch (ex: Exception) {
//                Toast.makeText(activity, ex.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        } else {
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12F))
        }

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
            )
        }


    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown

                        ActivityCompat.requestPermissions(
                            this@MapsActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            myPermissionRequestLocation
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    myPermissionRequestLocation
                )
            }
        }
    }
}
