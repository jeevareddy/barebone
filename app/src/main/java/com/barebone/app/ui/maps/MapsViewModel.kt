package com.barebone.app.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapsViewModel : ViewModel() {
    var results = MutableLiveData<List<HashMap<Any, Any>>>().apply {
        value = listOf()
    }
    var resultList: LiveData<List<HashMap<Any, Any>>> = results
    fun setResults(res: List<HashMap<Any, Any>>) {
        results = MutableLiveData<List<HashMap<Any, Any>>>().apply {
            Log.d("VIEW MODEL UPDATE", res.size.toString())
            value = res
        }
        resultList = results
    }

}