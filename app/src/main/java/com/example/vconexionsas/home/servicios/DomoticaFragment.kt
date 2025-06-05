package com.example.vconexionsas.home.servicios

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.vconexionsas.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.example.vconexionsas.utils.ContactoUtils

class DomoticaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_domotica, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        val contactButton = view.findViewById<MaterialButton>(R.id.contactButton)

        val animacionEntrada = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_up)
        val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)

        // Animar vistas principales
        listOf(
            R.id.heroImageCard,
            R.id.controlCard, R.id.controlTitle, R.id.controlDescription,
            R.id.introCard, R.id.introTitle, R.id.introDescription,
            R.id.benefitsCard, R.id.benefitsTitle,
            R.id.contactButton
        ).forEach {
            view.findViewById<View>(it)?.startAnimation(animacionEntrada)
        }

        // Acción botón de contacto
        contactButton.setOnClickListener {
            contactButton.startAnimation(scaleTap)
            val numero = ContactoUtils.obtenerNumeroWhatsapp(requireContext(), ContactoUtils.TipoContacto.DOMOTICA)
            val mensaje = "¡Hola! Estoy interesado en conocer más sobre su servicio de Domótica."
            val url = "https://api.whatsapp.com/send?phone=$numero&text=${Uri.encode(mensaje)}"

            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Acción botón de regreso
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }
}
