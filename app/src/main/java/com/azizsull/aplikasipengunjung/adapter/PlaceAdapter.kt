package com.azizsull.aplikasipengunjung.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.azizsull.aplikasipengunjung.R
import com.azizsull.aplikasipengunjung.model.FieldModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_place.view.*

/**
 * RecyclerView adapter for a list of Restaurants.
 */
open class PlaceAdapter(query: Query, private val listener: OnPlaceSelectedListener) :
    FirestoreAdapter<PlaceAdapter.ViewHolder>(query) {

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

        fun bind(
            snapshot: DocumentSnapshot,
            listener: OnPlaceSelectedListener?
        ) {

            val place = snapshot.toObject(PlaceModel::class.java)
            if (place == null) {
                return
            }

//            val field = snapshot.id
            println(itemView.placeImage)

            // Load image
            Glide.with(itemView.placeImage.context)
                .load(place.images[0])
                .apply(RequestOptions().centerInside())
                .into(itemView.placeImage)

            itemView.placeName.text = place.name
            itemView.openHour.text = place.jamBuka
            itemView.closeHour.text = place.jamTutup
//            itemView.placePrice.text = place.hargaSiang

            // Click listener
            itemView.setOnClickListener {
                listener?.onPlaceSelected(snapshot)
            }
        }
    }
}
