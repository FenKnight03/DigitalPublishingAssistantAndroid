package com.ljdit.digitalpublishing.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ljdit.digitalpublishing.data.repository.PhotoRepository
import com.ljdit.digitalpublishing.model.ConnectionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConnectionsViewModel : ViewModel() {

    private val repository = PhotoRepository()

    private val _status =
        MutableStateFlow<ConnectionStatus?>(null)
    val status: StateFlow<ConnectionStatus?> = _status

    private val _isLoading =
        MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage =
        MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.getConnectionStatus()

                if (response.isSuccessful) {
                    _status.value = response.body()
                } else {
                    _errorMessage.value =
                        "No se pudo consultar el estado de Meta."
                }
            } catch (e: Exception) {
                _errorMessage.value =
                    e.message ?: "No se pudo consultar el estado de Meta."
            }

            _isLoading.value = false
        }
    }
}
