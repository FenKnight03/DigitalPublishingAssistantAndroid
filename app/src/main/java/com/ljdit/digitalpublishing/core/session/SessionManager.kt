package com.ljdit.digitalpublishing.core.session

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SessionManager {

    private const val KEY = "auth_token"
    private const val KEY_IS_ADMIN = "is_admin"
    private const val KEY_DISTRIBUTOR_ID = "distributor_id"
    private const val KEY_DISTRIBUTOR_NAME = "distributor_name"

    var isAdmin: Boolean = false
        private set

    var distributorId: Int? = null
        private set

    var distributorName: String? = null
        private set

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    var token: String? = null
        private set

    fun init(context: Context) {

        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

        token = prefs.getString(KEY, null)

        isAdmin =
            prefs.getBoolean(KEY_IS_ADMIN, false)

        distributorId =
            if (prefs.contains(KEY_DISTRIBUTOR_ID))
                prefs.getInt(KEY_DISTRIBUTOR_ID, -1)
            else
                null

        distributorName =
            prefs.getString(KEY_DISTRIBUTOR_NAME, null)

        if (distributorId == -1) {
            distributorId = null
        }

        _isLoggedIn.value = token != null
    }

    fun save(
        context: Context,
        newToken: String,
        isAdminValue: Boolean,
        distributorIdValue: Int?,
        distributorNameValue: String?
    ) {

        token = newToken
        isAdmin = isAdminValue
        distributorId = distributorIdValue
        distributorName = distributorNameValue

        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, newToken)
            .putBoolean(KEY_IS_ADMIN, isAdminValue)
            .putString(KEY_DISTRIBUTOR_NAME, distributorNameValue)
            .apply {
                if (distributorIdValue != null) {
                    putInt(KEY_DISTRIBUTOR_ID, distributorIdValue)
                } else {
                    remove(KEY_DISTRIBUTOR_ID)
                }
            }
            .apply()

        _isLoggedIn.value = true
    }

    fun logout(context: Context) {

        token = null

        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .edit()
            .remove(KEY)
            .remove(KEY_IS_ADMIN)
            .remove(KEY_DISTRIBUTOR_ID)
            .remove(KEY_DISTRIBUTOR_NAME)
            .apply()

        _isLoggedIn.value = false
    }

}
