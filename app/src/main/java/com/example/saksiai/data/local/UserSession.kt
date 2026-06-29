package com.example.saksiai.data.local

import android.content.Context
import android.content.SharedPreferences

class UserSession(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("saksi_ai_prefs", Context.MODE_PRIVATE)

    var userRole: String
        get() = prefs.getString("user_role", "VIEWER") ?: "VIEWER"
        set(value) = prefs.edit().putString("user_role", value).apply()

    fun isAuthorizedForSensitiveData(): Boolean {
        val role = userRole
        return role == "ADMIN" || role == "COMPLIANCE" || role == "ANALYST"
    }
}
