package com.ljdit.digitalpublishing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ljdit.digitalpublishing.data.repository.DistributorRepository
import com.ljdit.digitalpublishing.model.Distributor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DistributorViewModel : ViewModel() {

    private val repository = DistributorRepository()

    private val _distributors = MutableStateFlow<List<Distributor>>(emptyList())
    val distributors: StateFlow<List<Distributor>> = _distributors

    init {
        loadDistributors()
    }

    private fun loadDistributors() {

        viewModelScope.launch {

            val response = repository.getDistributors()

            if (response.isSuccessful) {
                _distributors.value = response.body() ?: emptyList()
            }

        }
    }
}