package com.example.vconexionsas.home.factura

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.vconexionsas.R
import com.example.vconexionsas.utils.ContactoUtils
import com.google.android.material.card.MaterialCardView

class SesionPagosFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_sesion_pagos, container, false)

        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            findNavController().popBackStack()
        }

        // Botón para enviar comprobante
        val btnEnviar = view.findViewById<Button>(R.id.btnEnviarComprobante)
        btnEnviar.setOnClickListener {

            val masterKey = MasterKey.Builder(requireContext())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val prefs = EncryptedSharedPreferences.create(
                requireContext(),
                "secure_user_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            val nombre = prefs.getString("nombre_usuario", "Usuario")
            val codigo = prefs.getString("codigo_usuario", "000000")
            val numero = ContactoUtils.obtenerNumeroWhatsapp(requireContext(), ContactoUtils.TipoContacto.FACTURACION)
            val mensaje = """
        *Solicitud desde la App VConexion*

        *Nombre del usuario:* $nombre
        *Código de cliente:* $codigo

        Hola, deseo enviar el comprobante de pago de mi factura.
    """.trimIndent()
            val url = "https://wa.me/$numero?text=${Uri.encode(mensaje)}"

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        // Botones copiar cuenta y NIT
        view.findViewById<MaterialCardView?>(R.id.btnCopiarCuenta)?.setOnClickListener {
            copiarAlPortapapeles("Cuenta", "47600000740")
        }

        view.findViewById<MaterialCardView?>(R.id.btnCopiarNIT)?.setOnClickListener {
            copiarAlPortapapeles("NIT", "901398501")
        }

        return view
    }

    private fun copiarAlPortapapeles(titulo: String, texto: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(titulo, texto)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "$titulo copiado", Toast.LENGTH_SHORT).show()
    }
}


