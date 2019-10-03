package com.azizsull.aplikasipengunjung.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azizsull.aplikasipengunjung.R
import com.azizsull.aplikasipengunjung.model.FieldModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_field.view.*

abstract class FieldAdapter(query: Query) :
    FirestoreAdapter<FieldAdapter.ViewHolder>(query) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_field, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            snapshot: DocumentSnapshot
        ) {

            val field = snapshot.toObject(FieldModel::class.java)
            if (field == null) {
                return
            }

            itemView.fieldName.text = field.kodeLapangan
            itemView.fieldType.text = field.jenis
            itemView.dayPrice.text = field.hargaSiang.toString()
            itemView.nightPrice.text = field.hargaMalam.toString()

            // Click listener
            itemView.setOnClickListener {
//                Toast.makeText(this, itemView.fieldName, Toast.LENGTH_LONG).show()
            }
        }
    }
}