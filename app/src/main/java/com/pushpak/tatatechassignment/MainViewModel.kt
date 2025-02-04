package com.pushpak.tatatechassignment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pushpak.tatatechassignment.model.IAVContentData

class MainViewModel: ViewModel() {
    var iavContentData by mutableStateOf(emptyList<IAVContentData>())
        private set

    fun updateIAVContentData(iavContentData: List<IAVContentData>){
        this.iavContentData = iavContentData
    }
}