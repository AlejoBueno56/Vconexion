package com.example.vconexionsas.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object ContactoUtils {

    enum class TipoContacto {
        COMERCIAL, FACTURACION, TECNICO, DOMOTICA, CCTV, CABLEADO, IPTV
    }

    fun obtenerNumeroWhatsapp(context: Context, tipo: TipoContacto): String {
        // Usar EncryptedSharedPreferences igual que en MainActivity
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            context,
            "secure_user_prefs", // Mismo nombre que en MainActivity
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val sede = prefs.getString("sede", "Chitaga") ?: "Chitaga"

        return when (tipo) {
            TipoContacto.COMERCIAL -> when (sede) {
                "Pamplona" -> "573024538585"
                "Chitaga" -> "573203753623"
                "Toledo" -> "573153516178"
                else -> "573024538585"
            }

            TipoContacto.FACTURACION -> when (sede) {
                "Pamplona" -> "573164116348"
                "Chitaga" -> "573203753623"
                "Toledo" -> "573153516178"
                else -> "573164116348"
            }

            TipoContacto.TECNICO -> when (sede) {
                "Pamplona" -> "573173369779"
                "Chitaga" -> "573203753623"
                "Toledo" -> "573153516178"
                else -> "573173369779"
            }

            TipoContacto.DOMOTICA -> when (sede) {
                "Pamplona" -> "573024538585"
                "Chitaga" -> "573203753623"
                "Toledo" -> "573153516178"
                else -> "573024538585"
            }

            TipoContacto.CCTV -> when (sede) {
                "Pamplona" -> "573024538585"
                "Chitaga" -> "573203753623"
                "Toledo" -> "573153516178"
                else -> "573024538585"
            }

            TipoContacto.CABLEADO -> when (sede) {
                "Pamplona" -> "573024538585"
                "Chitaga" -> "573203753623"
                "Toledo" -> "573153516178"
                else -> "573024538585"
            }

            TipoContacto.IPTV -> when (sede) {
                "Pamplona" -> "573024538585"
                "Chitaga" -> "573203753623"
                "Toledo" -> "573153516178"
                else -> "573024538585"
            }
        }
    }
}

