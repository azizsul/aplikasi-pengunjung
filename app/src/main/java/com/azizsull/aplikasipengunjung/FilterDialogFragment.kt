package com.azizsull.aplikasipengunjung

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_filters.spinnerCategory
import kotlinx.android.synthetic.main.dialog_filters.spinnerCity
import kotlinx.android.synthetic.main.dialog_filters.spinnerPrice
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
            val selected = spinnerCity.selectedItem as String
            return if (getString(R.string.value_any_city) == selected) {
                null
            } else {
                selected
            }
        }

    private val selectedPrice: Int
        get() {
            val selected = spinnerPrice.selectedItem as String
            return when (selected) {
                getString(R.string.price_1) -> 1
                getString(R.string.price_2) -> 2
                else -> -1
            }
        }


    val filters: Filters
        get() {
            val filters = Filters()

            filters.category = selectedCategory
            filters.city = selectedCity
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
        spinnerCity?.setSelection(0)
        spinnerPrice?.setSelection(0)
    }

    companion object {

        const val TAG = "FilterDialog"
    }
}
