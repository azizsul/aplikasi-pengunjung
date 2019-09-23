package com.azizsull.aplikasipengunjung

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azizsull.aplikasipengunjung.model.FieldModel
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.glide.slider.library.Animations.DescriptionAnimation
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.SliderTypes.BaseSliderView
import com.glide.slider.library.SliderTypes.TextSliderView
import com.glide.slider.library.Tricks.ViewPagerEx
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_place_detail.*
import java.util.*


class PlaceDetailActivity : AppCompatActivity(), EventListener<DocumentSnapshot>, BaseSliderView.OnSliderClickListener,
    ViewPagerEx.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onSliderClick(slider: BaseSliderView?) {

    }

    inner class FieldViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        internal fun setFieldName(fieldName: String, fieldType: String, day: Int, night: Int) {
            val name = view.findViewById<TextView>(R.id.fieldName)
            val jenis = view.findViewById<TextView>(R.id.fieldType)
            val siang = view.findViewById<TextView>(R.id.dayPrice)
            val malam = view.findViewById<TextView>(R.id.nightPrice)
            name.text = "Lap. $fieldName"
            jenis.text = fieldType
            siang.text = "Siang: $day"
            malam.text = "Malam: $night"

        }
    }

    inner class FieldFireStoreRecyclerAdapter internal constructor(options: FirestoreRecyclerOptions<FieldModel>) :
        FirestoreRecyclerAdapter<FieldModel, FieldViewHolder>(options) {
        override fun onBindViewHolder(
            fieldViewHolder: FieldViewHolder,
            position: Int,
            fieldModel: FieldModel
        ) {
            fieldViewHolder.setFieldName(
                fieldModel.name,
                fieldModel.jenis,
                fieldModel.hargaSiang,
                fieldModel.hargaMalam
            )

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_field, parent, false)
            return FieldViewHolder(view)
        }
    }

    private lateinit var placeRef: DocumentReference

    private var placeRegistration: ListenerRegistration? = null

    private var adapter: FieldFireStoreRecyclerAdapter? = null

    lateinit var firestore: FirebaseFirestore

    var requestOptions = RequestOptions()

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

        // Get reference to the restaurant
        placeRef = firestore.collection("Lapangan").document(placesId)

        val query = reference.collection("Lapangan").document(placesId).collection("listLapangan")
            .orderBy("name")
        val options =
            FirestoreRecyclerOptions.Builder<FieldModel>().setQuery(query, FieldModel::class.java)
                .build()

        adapter = FieldFireStoreRecyclerAdapter(options)
        println("MESSAGES $query")
        recyclerFields.layoutManager = LinearLayoutManager(this)
        recyclerFields.adapter = adapter

    }

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            Log.w(TAG, "place:onEvent", e)
            return
        }

        snapshot?.let {
            val place = snapshot.toObject(PlaceModel::class.java)
            if (place != null) {
                onPlaceLoaded(place)
            }
        }

    }

    @SuppressLint("CheckResult")
    private fun onPlaceLoaded(placeModel: PlaceModel) {

        tv_nama_lapangan.text = placeModel.getName()
        tv_alamat.text = placeModel.alamat
        tv_noTelp.text = placeModel.noTelp
        tv_jamBuka.text = "${placeModel.jamBuka} - ${placeModel.jamTutup}"
        tv_fasilitas.text = placeModel.facility

        val list = placeModel.images
        requestOptions.centerCrop()

        // Background image
        for (i in 0 until list.size) {
            val textSliderView =
                TextSliderView(this)
            // initialize a SliderLayout

            textSliderView
                .image(list[i])
                .setRequestOption(requestOptions)
                .setProgressBarVisible(false)
                .setOnSliderClickListener(this)
            //add your extra information

            textSliderView.bundle(Bundle())
            carousels.addSlider(textSliderView)
        }
        carousels.setPresetTransformer(SliderLayout.Transformer.Fade)
        carousels.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        carousels.setDuration(4000)
        carousels.addOnPageChangeListener(this)

        fabShowMaps.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra(MapsActivity().extraName, placeModel.getName())
            intent.putExtra(MapsActivity().extraLat, placeModel.lat.toString())
            intent.putExtra(MapsActivity().extraLong, placeModel.long.toString())

            startActivity(intent)
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
