package com.example.fianancetracker.core.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.fianancetracker.core.domain.repository.PinRepository

/** PIN в [EncryptedSharedPreferences]: значение шифруется мастер-ключом из Android Keystore. */
internal class PinRepositoryImpl(context: Context) : PinRepository {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "secure_pin",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    override fun isPinSet(): Boolean = prefs.contains(KEY_PIN)

    override fun setPin(pin: String) {
        prefs.edit().putString(KEY_PIN, pin).apply()
    }

    override fun verifyPin(pin: String): Boolean = prefs.getString(KEY_PIN, null) == pin

    override fun clearPin() {
        prefs.edit().remove(KEY_PIN).apply()
    }

    private companion object {
        const val KEY_PIN = "pin"
    }
}
