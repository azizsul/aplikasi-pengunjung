package com.azizsull.aplikasipengunjung

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azizsull.aplikasipengunjung.adapter.FieldAdapter
import com.azizsull.aplikasipengunjung.model.FieldModel
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_place_detail.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.text.SimpleDateFormat
import java.util.*


class PlaceDetailActivity : AppCompatActivity(), EventListener<DocumentSnapshot> {

    inner class FieldViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        internal fun setFieldName(fieldName: String, fieldType: String, day: Int, night: Int) {
            val name = view.findViewById<TextView>(R.id.fieldName)
            val jenis = view.findViewById<TextView>(R.id.fieldType)
            val siang = view.findViewById<TextView>(R.id.dayPrice)
            val malam = view.findViewById<TextView>(R.id.nightPrice)
            name.text = fieldName
            jenis.text = fieldType
            siang.text = day.toString()
            malam.text = night.toString()

        }
    }

    inner class FieldFireStoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<FieldModel>) :
        FirestoreRecyclerAdapter<FieldModel, FieldViewHolder>(options) {
        override fun onBindViewHolder(
            fieldViewHolder: FieldViewHolder,
            position: Int,
            fieldModel: FieldModel
        ) {
            fieldViewHolder.setFieldName(fieldModel.name, fieldModel.jenis, fieldModel.hargaSiang, fieldModel.hargaMalam)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_field, parent, false)
            return FieldViewHolder(view)
        }
    }

    private lateinit var placeRef: DocumentReference
    lateinit var fieldAdapter: FieldAdapter
    lateinit var query: Query

    private var placeRegistration: ListenerRegistration? = null

    private val dateTime = Calendar.getInstance()
    private val time = dateTime.time
    private val currentTime = SimpleDateFormat("HH:mm").format(time)

    private var adapter: FieldFireStoreRecyclerAdapter? = null

    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this)

        val placesId = intent.extras?.getString(KEY_PLACE_ID)
            ?: throw IllegalArgumentException("Must pass extra $KEY_PLACE_ID")

        val reference = FirebaseFirestore.getInstance()


        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        println("TIME NOW IS: $currentTime")

        // Get reference to the restaurant
        placeRef = firestore.collection("Lapangan").document(placesId)

        val query = reference.collection("Lapangan").document(placesId).collection("listLapangan").orderBy("name")
        val options = FirestoreRecyclerOptions.Builder<FieldModel>().setQuery(query, FieldModel::class.java).build()

        adapter = FieldFireStoreRecyclerAdapter(options)
        println("MESSAGES $query")
        recyclerFields.layoutManager = LinearLayoutManager(this)
        recyclerFields.adapter = adapter

    }

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
                if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e)
            return
        }

        snapshot?.let {
            val place = snapshot.toObject(PlaceModel::class.java)
            if (place != null) {
                onPlaceLoaded(place)
            }
        }

    }
        private fun onPlaceLoaded(placeModel: PlaceModel) {

        val beginning = placeModel.jamBuka
        val end = placeModel.jamTutup

        tv_nama_lapangan.text = placeModel.name
        tv_alamat.text = placeModel.alamat
        tv_noTelp.text = placeModel.noTelp
        if (currentTime in beginning..end) {
            tv_jamBuka.text = getString(R.string.open_now)
        } else {
            tv_jamBuka.text = getString(R.string.close_now)
        }
        tv_fasilitas.text = placeModel.facility

        // Background image
        Glide.with(placeImage.context)
            .load(placeModel.images[0])
            .apply(RequestOptions())
            .into(placeImage)
    }

    private fun onBackArrowClicked() {
        onBackPressed()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
    }

    public override fun onStart() {
        super.onStart()

        placeRegistration = placeRef.addSnapshotListener(this)


        // Start listening for Firestore updates
        adapter!!.startListening()
    }

    public override fun onStop() {
        super.onStop()
        placeRegistration?.remove()
        placeRegistration = null
        adapter!!.stopListening()
    }

    companion object {

        private const val LIMIT = 50

        private const val TAG = "PlaceDetail"

        const val KEY_PLACE_ID = "key_place_id"
    }


}
