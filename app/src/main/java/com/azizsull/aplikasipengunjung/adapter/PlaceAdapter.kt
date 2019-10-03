package com.azizsull.aplikasipengunjung.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azizsull.aplikasipengunjung.MainActivity
import com.azizsull.aplikasipengunjung.R
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_place.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.sqrt
import com.azizsull.aplikasipengunjung.R.color as colors


/**
 * RecyclerView adapter for a list of Restaurants.
 */
abstract class PlaceAdapter(
    mContext: Context, query: Query, private val listener: OnPlaceSelectedListener
) :
    FirestoreAdapter<PlaceAdapter.ViewHolder>(query) {

    internal var inflater: LayoutInflater = LayoutInflater.from(mContext)
    private var arrayList: ArrayList<PlaceModel> = ArrayList()

    init {
        this.arrayList.addAll(MainActivity.placeLists)
    }

    interface OnPlaceSelectedListener {

        fun onPlaceSelected(places: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_place, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("ResourceAsColor", "MissingPermission")
        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnPlaceSelectedListener?
        ) {

            //get current location
            var latitude: Double
            var longitude: Double

            val place = snapshot.toObject(PlaceModel::class.java) ?: return

            val mLocationRequest = LocationRequest()
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest.interval = 0
            mLocationRequest.fastestInterval = 0
            mLocationRequest.numUpdates = 1

            var fusedLocationProviderClient: FusedLocationProviderClient? = null

            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(itemView.context)

            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude

                        //set distance
                        val degree = 0.0174532925
                        val radius = 6371

                        //current location marker
                        val lat = latitude * degree
                        val long = longitude * degree

                        //place location marker
                        val lat2 = place.latitude * degree
                        val long2 = place.longitude * degree

                        val x = (long2 - long) * cos((lat + lat2) / 2)
                        val y = (lat2 - lat)
                        val actual = sqrt(x * x + y * y) * radius

                        val format: String = DecimalFormat("##.##").format(actual)

                        val distance = "$format Km"

                        itemView.placeDistance.text = distance
                    }
                }

            //set open hour
            val dateTime = Calendar.getInstance()
            val time = dateTime.time
            val currentTime = SimpleDateFormat("HH:mm").format(time)

            val beginning = place.jamBuka
            val end = place.jamTutup

            if (currentTime in beginning..end) {
                itemView.closeHour.text = "BUKA"
                val open = itemView.closeHour.resources.getColor(colors.open)
                itemView.closeHour.setTextColor(open)
            } else {
                val close = itemView.closeHour.resources.getColor(colors.close)
                itemView.closeHour.setTextColor(close)
                itemView.closeHour.text = "TUTUP"
            }

            // Load image
            Glide.with(itemView.placeImage.context)
                .load(place.gambar[0])
                .apply(RequestOptions().centerInside())
                .into(itemView.placeImage)

            itemView.placeName.text = place.getName()

            itemView.tv_jenis_lapangan.text = place.getFieldType()

            itemView.tv_harga_terendah.text = place.getHargaTerendah()

            itemView.tv_harga_tertinggi.text = place.getHargaTertinggi()



            // Click listener
            itemView.setOnClickListener {
                listener?.onPlaceSelected(snapshot)
            }
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun filter(text: String) {

        var charText = text
        charText = charText.toLowerCase(Locale.getDefault())
        arrayList.clear()
        for (wp in arrayList) {
            if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                arrayList.add(wp)
            }

        }

        notifyDataSetChanged()
    }

}
