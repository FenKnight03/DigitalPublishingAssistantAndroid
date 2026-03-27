package com.ljdit.digitalpublishing.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ljdit.digitalpublishing.core.session.SessionManager
import com.ljdit.digitalpublishing.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(
        context: Context,
        username: String,
        password: String
    ) {

        viewModelScope.launch {

            _loading.value = true
            _error.value = null

            try {

                val token = repository.login(username, password)

                SessionManager.save(context, token)

            } catch (e: Exception) {

                _error.value = e.message

            }

            _loading.value = false
        }

    }

}