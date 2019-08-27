package com.azizsull.aplikasipengunjung

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.azizsull.aplikasipengunjung.model.PlaceModel
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.dialog_filters.*
import kotlinx.android.synthetic.main.dialog_filters.view.buttonCancel
import kotlinx.android.synthetic.main.dialog_filters.view.buttonSearch

/**
 * Dialog Fragment containing filter form.
 */
class FilterDialogFragment : DialogFragment() {

    private lateinit var rootView: View

    private var filterListener: FilterListener? = null

    private val selectedCity: String?
        get() {
            val selected = spinnerLokasi.selectedItem as String
            return if (getString(R.string.value_any_location) == selected) {
                null
            } else {
                selected
            }
        }

    private val selectedPrice: Int
        get() {
            val selected = spinnerHarga.selectedItem as String
            return when (selected) {
                getString(R.string.harga_terendah) -> 1
                getString(R.string.harga_tertinggi) -> 2
                getString(R.string.price_3) -> 3
                else -> -1
            }
        }

    private val selectedSortBy: String?
        get() {
            val selected = spinnerJenisLapangan.selectedItem as String
            if (getString(R.string.all_field_type) == selected) {
                return PlaceModel.FIELD_NAME
            }
            if (getString(R.string.sort_by_synthetic) == selected) {
                return PlaceModel.FIELD_PRICE
            }
            return if (getString(R.string.sort_by_vinyl) == selected) {
                PlaceModel.FIELD_POPULARITY
            } else {
                null
            }
        }

    private val sortDirection: Query.Direction
        get() {
            val selected = spinnerJenisLapangan.selectedItem as String
            if (getString(R.string.all_field_type) == selected) {
                return Query.Direction.DESCENDING
            }
            if (getString(R.string.sort_by_synthetic) == selected) {
                return Query.Direction.ASCENDING
            }
            return if (getString(R.string.sort_by_vinyl) == selected) {
                Query.Direction.DESCENDING
            } else {
                Query.Direction.DESCENDING
            }
        }

    val filters: Filters
        get() {
            val filters = Filters()

            filters.city = selectedCity
            filters.price = selectedPrice
            filters.sortBy = selectedSortBy
            filters.sortDirection = sortDirection

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

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is FilterListener) {
            filterListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        dialog.window?.setLayout(
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
        spinnerJamBuka?.setSelection(0)
        spinnerHarga?.setSelection(0)
        spinnerJenisLapangan?.setSelection(0)
    }

    companion object {

        const val TAG = "FilterDialog"
    }
}
