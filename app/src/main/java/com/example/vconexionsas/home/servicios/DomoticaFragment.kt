package com.example.vconexionsas.home.servicios

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.vconexionsas.R
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

        val backButton: ImageButton = view.findViewById(R.id.backButton)
        val contactButton: Button = view.findViewById(R.id.contactButton)

        val animacionEntrada = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_up)
        val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)

        listOf(
            R.id.domoticaTitle, R.id.domoticaImage, R.id.domoticaDescription,
            R.id.domoticaIntroTitle, R.id.domoticaIntroDescription,
            R.id.benefitsImage, R.id.domoticaBenefitsTitle, R.id.domoticaBenefits
        ).forEach { view.findViewById<View>(it).startAnimation(animacionEntrada) }

        contactButton.startAnimation(animacionEntrada)

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

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }
}