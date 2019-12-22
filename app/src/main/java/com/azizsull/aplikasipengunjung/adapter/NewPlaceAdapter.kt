package com.azizsull.aplikasipengunjung.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azizsull.aplikasipengunjung.Filters
import com.azizsull.aplikasipengunjung.MainActivity
import com.azizsull.aplikasipengunjung.PlaceDetailActivity
import com.azizsull.aplikasipengunjung.R
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.item_place.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sqrt


class NewPlaceAdapter :
    RecyclerView.Adapter<NewPlaceAdapter.ViewHolder>() {
    private var listener: OnItemClickListener? = null
    private var places: ArrayList<PlaceModel>? = null
    private var filteredPlaces: ArrayList<PlaceModel>? = null

    private var searchPlaces: ArrayList<PlaceModel>? = null

    private var arrayList: ArrayList<PlaceModel>? = null



    private var mActivity: Activity? = null

    interface OnPlaceSelectedListener {

        fun onPlaceSelected(places: DocumentSnapshot)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(place: PlaceModel) {

            //get current location
            var latitude: Double
            var longitude: Double


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

                        place.jarak = actual
                    }
                }

            //set open hour
            val dateTime = Calendar.getInstance()
            val time = dateTime.time
            val currentTime = SimpleDateFormat("HH:mm").format(time)

            val beginning = place.jamBuka
            val end = place.jamTutup

            Log.e("id_place", place.id)

            if (currentTime in beginning..end) {
                itemView.closeHour.text = "BUKA"
                val open = itemView.closeHour.resources.getColor(R.color.open)
                itemView.closeHour.setTextColor(open)
                place.isOpen = true
            } else {
                val close = itemView.closeHour.resources.getColor(R.color.close)
                itemView.closeHour.setTextColor(close)
                itemView.closeHour.text = "TUTUP"
            }


            Glide.with(itemView.placeImage.context)
                .load(place.gambar[0])
                .apply(RequestOptions().centerInside())
                .into(itemView.placeImage)

            itemView.placeName.text = place.getName()

            itemView.tv_jenis_lapangan.text = place.getFieldType()

            itemView.tv_harga_terendah.text = place.hargaTerendah.toString()

            itemView.tv_harga_tertinggi.text = place.hargaTertinggi.toString()

            // Click listener


        }

    }

    fun setData(activity: Activity?, allPlaces: ArrayList<PlaceModel>?) {
        places = allPlaces
        mActivity = activity
        notifyDataSetChanged()
    }

    fun updateList(allPlaces: ArrayList<PlaceModel>?) {
        places = allPlaces
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        places?.get(position)?.let { room ->
            holder.bind(room)
        }

        holder.itemView.setOnClickListener { v ->
            listener?.onItemClickListener(v, holder.layoutPosition)

            Log.e("CLICK", places?.get(position)?.id )


            val intent = Intent(mActivity, PlaceDetailActivity::class.java)
            intent.putExtra(PlaceDetailActivity.KEY_PLACE_ID, places?.get(position)?.id)

            mActivity?.startActivity(intent)
            mActivity?.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left)


        }
    }

    fun filter(filters: Filters) {

        var temp: ArrayList<PlaceModel>? = places;
        var filteredList: ArrayList<PlaceModel>? = places;


        Log.e("placeAdapter" , filters.toString())

        if (filters.location != "Semua Lokasi"){
            filteredList?.sortBy { it.jarak }
        }else{
            filteredList = MainActivity.placeLists

        }

        if (filters.openTime == "Sekarang Buka") {

            val nowOpen = filteredList?.filter { it.isOpen == true }
            filteredList = nowOpen as ArrayList<PlaceModel>?

        }else{

            filteredList = filteredList

        }

        if (filters.price == "Harga Terendah") {

            filteredList?.sortBy { it.hargaTerendah }

        }else if(filters.price == "Harga Tertinggi"){

            filteredList?.sortByDescending { it.hargaTertinggi }

        }else{
            filteredList = filteredList

        }

        if (filters.type == "Vinyl") {

           val vinyl =  filteredList?.filter{ it.jenisLapangan.contains("Vinyl") }
            filteredList = vinyl as ArrayList<PlaceModel>?

        }else if(filters.type == "Sintetis"){

            val sintetis = places?.filter{ it.jenisLapangan.contains("Sintetis") }
            filteredList = sintetis as ArrayList<PlaceModel>?

        }else{
            filteredList = filteredList

        }

        updateList(filteredList)

    }

    fun resetFilter(){
        var places: ArrayList<PlaceModel>? = MainActivity.placeLists;
        updateList(places)
    }


    fun search(text: String) {
        val temp: ArrayList<PlaceModel>? = places
        var searchPlaces: ArrayList<PlaceModel>? = MainActivity.placeLists;

        Log.e("onSearch" , text)

        var charText = text
        charText = charText.toLowerCase(Locale.getDefault())


        if(charText != "" || charText != ""){
            val search =  searchPlaces?.filter{ it.namaTempat.toLowerCase(Locale.getDefault()).contains(charText) }

            searchPlaces = search as ArrayList<PlaceModel>?

        }else{
            searchPlaces = MainActivity.placeLists

        }

        updateList(searchPlaces)
    }

    override fun getItemCount(): Int {
        return places?.size ?: 0
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClickListener(v: View, pos: Int)
    }
}