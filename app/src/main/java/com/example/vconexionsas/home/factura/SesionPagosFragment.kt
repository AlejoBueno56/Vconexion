package com.example.vconexionsas.home.factura

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vconexionsas.R

class SesionPagosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sesion_pagos, container, false)

        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            findNavController().popBackStack()
        }
        val btnEnviar = view.findViewById<Button>(R.id.btnEnviarComprobante)
        btnEnviar.setOnClickListener {
            val numero = "573164116348" // Reemplaza por el número real
            val mensaje = "Hola, deseo enviar el comprobante de pago de mi factura."
            val url = "https://wa.me/$numero?text=${Uri.encode(mensaje)}"

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }


        return view
    }
}