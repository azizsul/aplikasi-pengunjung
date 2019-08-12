package com.azizsull.aplikasipengunjung.viewmodel

import androidx.lifecycle.ViewModel
import com.azizsull.aplikasipengunjung.Filters

class MainActivityViewModel : ViewModel() {

    var isSigningIn: Boolean = false
    var filters: Filters = Filters.default
}
