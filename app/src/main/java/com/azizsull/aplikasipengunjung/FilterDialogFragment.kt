package com.azizsull.aplikasipengunjung

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.dialog_filters.spinnerCategory
import kotlinx.android.synthetic.main.dialog_filters.spinnerSort
import kotlinx.android.synthetic.main.dialog_filters.view.buttonCancel
import kotlinx.android.synthetic.main.dialog_filters.view.buttonSearch

/**
 * Dialog Fragment containing filter form.
 */
class FilterDialogFragment : DialogFragment() {

    private lateinit var rootView: View

    private var filterListener: FilterListener? = null

    private val selectedCategory: String?
        get() {
            val selected = spinnerCategory.selectedItem as String
            return if (getString(R.string.value_any_field) == selected) {
                null
            } else {
                selected
            }
        }

    private val selectedCity: String?
        get() {
            val selected = `@+id/spinnerLokasi`.selectedItem as String
            return if (getString(R.string.value_any_location) == selected) {
                null
            } else {
                selected
            }
        }

    private val selectedPrice: Int
        get() {
            val selected = `@+id/spinnerHarga`.selectedItem as String
            return when (selected) {
                getString(R.string.harga_terendah) -> 1
                getString(R.string.harga_tertinggi) -> 2
                getString(R.string.price_3) -> 3
                else -> -1
            }
        }

    private val selectedSortBy: String?
        get() {
            val selected = spinnerSort.selectedItem as String
            if (getString(R.string.all_field_type) == selected) {
                return PlaceModel.FIELD_AVG_RATING
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
            val selected = spinnerSort.selectedItem as String
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

            filters.category = selectedCategory
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
        spinnerCategory?.setSelection(0)
        `@+id/spinnerLokasi`?.setSelection(0)
        `@+id/spinnerHarga`?.setSelection(0)
        spinnerSort?.setSelection(0)
    }

    companion object {

        const val TAG = "FilterDialog"
    }
}
