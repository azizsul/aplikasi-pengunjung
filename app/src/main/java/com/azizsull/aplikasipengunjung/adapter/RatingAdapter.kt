package com.azizsull.aplikasipengunjung

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_rating.view.*
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * RecyclerView adapter for a list of [FieldModel].
 */
open class RatingAdapter(query: Query) : FirestoreAdapter<RatingAdapter.ViewHolder>(query) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rating, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position).toObject(FieldModel::class.java))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(fieldModel: FieldModel?) {
            if (fieldModel == null) {
                return
            }

            itemView.fieldName.text = fieldModel.fieldName
            itemView.fieldType.text = fieldModel.fieldType
            itemView.fieldPrice.text = fieldModel.price.toString()

        }

    }
}
