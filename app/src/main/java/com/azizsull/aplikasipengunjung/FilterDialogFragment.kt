package com.azizsull.aplikasipengunjung

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_filters.*
import kotlinx.android.synthetic.main.dialog_filters.view.*

/**
 * Dialog Fragment containing filter form.
 */
class FilterDialogFragment : DialogFragment() {

    private lateinit var rootView: View

    private lateinit var spinLokasi: Spinner
    private lateinit var spinJambuka: Spinner
    private lateinit var spinHarga: Spinner
    private lateinit var spinJenisLapangan: Spinner

    private var filterListener: FilterListener? = null

    private val selectedLocation: String?
        get() {
            return spinnerLokasi.selectedItem as String
        }


    private val selectedType: String?
        get() {
            return spinnerJenisLapangan.selectedItem as String
        }

    private val selectedOpenTime: String?
        get() {
            return spinnerJamBuka.selectedItem as String
        }

    private val selectedPrice: String?
        get() {
            return spinnerHarga.selectedItem as String
        }


    private val filters: Filters
        get() {
            val filters = Filters()

            filters.location = selectedLocation
            filters.openTime = selectedOpenTime
            filters.type = selectedType
            filters.price = selectedPrice

            return filters
        }

    interface FilterListener {

        fun onFilter(filters: Filters)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.dialog_filters, container, false)

        rootView.buttonSearch.setOnClickListener { onSearchClicked() }
        rootView.buttonCancel.setOnClickListener { onCancelClicked() }

        spinLokasi = rootView.findViewById(R.id.spinnerLokasi)

        spinJenisLapangan = rootView.findViewById(R.id.spinnerJenisLapangan)
        spinHarga = rootView.findViewById(R.id.spinnerHarga)
        spinJambuka = rootView.findViewById(R.id.spinnerJamBuka)

        return rootView
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is FilterListener) filterListener = context
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun onSearchClicked() {
        filterListener?.onFilter(filters)
        dismiss()
    }

    private fun onCancelClicked() {
        dismiss()
    }

    fun resetFilters() {

        spinnerLokasi?.setSelection(0)
        spinnerHarga?.setSelection(0)
        spinnerJamBuka?.setSelection(0)
        spinnerJenisLapangan?.setSelection(0)

    }

    companion object {

        const val TAG = "FilterDialog"
    }
}


