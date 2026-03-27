package com.ljdit.digitalpublishing.core.session

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SessionManager {

    private const val KEY = "auth_token"

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    var token: String? = null
        private set

    fun init(context: Context) {

        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

        token = prefs.getString(KEY, null)

        _isLoggedIn.value = token != null
    }

    fun save(context: Context, newToken: String) {

        token = newToken

        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, newToken)
            .apply()

        _isLoggedIn.value = true
    }

    fun logout(context: Context) {

        token = null

        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .edit()
            .remove(KEY)
            .apply()

        _isLoggedIn.value = false
    }

}