package com.azizsull.aplikasipengunjung

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_restaurant.view.*

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
        return ViewHolder(inflater.inflate(R.layout.item_restaurant, parent, false))
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

            // Load image
//            Glide.with(itemView.placeImage.context)
//                    .load(place.images)
//                    .into(itemView.placeImage)

            itemView.placeName.text = place.name
//            itemView.restaurantItemRating.fieldType = place.avgRating.toFloat()
            itemView.placeDistance.text = place.alamat
//            itemView.restaurantItemCategory.price = place.category
//            itemView.restaurantItemNumRatings.price = resources.getString(
//                    R.string.fmt_num_ratings,
//                    numRatings)
//            itemView.placePrice.price = PlaceUtil.getPriceString(place)

            // Click listener
            itemView.setOnClickListener {
                listener?.onPlaceSelected(snapshot)
            }
        }
    }
}
