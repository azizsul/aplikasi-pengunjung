package com.azizsull.aplikasipengunjung

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_restaurant.view.restaurantItemCategory
import kotlinx.android.synthetic.main.item_restaurant.view.restaurantItemCity
import kotlinx.android.synthetic.main.item_restaurant.view.restaurantItemImage
import kotlinx.android.synthetic.main.item_restaurant.view.restaurantItemName
import kotlinx.android.synthetic.main.item_restaurant.view.restaurantItemNumRatings
import kotlinx.android.synthetic.main.item_restaurant.view.restaurantItemPrice
import kotlinx.android.synthetic.main.item_restaurant.view.restaurantItemRating

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

            val resources = itemView.resources

            // Load image
//            Glide.with(itemView.restaurantItemImage.context)
//                    .load(place.images)
//                    .into(itemView.restaurantItemImage)

            val numRatings: Int = place.numRatings

            itemView.restaurantItemName.text = place.name
//            itemView.restaurantItemRating.rating = place.avgRating.toFloat()
            itemView.restaurantItemCity.text = place.alamat
//            itemView.restaurantItemCategory.text = place.category
//            itemView.restaurantItemNumRatings.text = resources.getString(
//                    R.string.fmt_num_ratings,
//                    numRatings)
//            itemView.restaurantItemPrice.text = PlaceUtil.getPriceString(place)

            // Click listener
            itemView.setOnClickListener {
                listener?.onPlaceSelected(snapshot)
            }
        }
    }
}
