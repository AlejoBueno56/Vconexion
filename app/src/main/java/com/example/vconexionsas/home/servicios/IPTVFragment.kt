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

class IPTVFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_iptv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton: ImageButton = view.findViewById(R.id.backButton)
        val contactButton: Button = view.findViewById(R.id.contactButton)

        val fadeInSlideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_up)
        val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)

        listOf(
            R.id.iptvTitle, R.id.iptvImageSubtitle1, R.id.iptvSubtitle1, R.id.iptvDescription1,
            R.id.iptvImageSubtitle2, R.id.iptvSubtitle2, R.id.iptvDescription2,
            R.id.iptvImageSubtitle3, R.id.iptvSubtitle3, R.id.iptvDescription3
        ).forEach { view.findViewById<View>(it).startAnimation(fadeInSlideUp) }

        contactButton.startAnimation(fadeInSlideUp)

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        contactButton.setOnClickListener {
            contactButton.startAnimation(scaleTap)

            val numero = ContactoUtils.obtenerNumeroWhatsapp(requireContext(), ContactoUtils.TipoContacto.COMERCIAL)
            val mensaje = "¡Hola! Estoy interesado en conocer más sobre su servicio de IPTV."

            val url = "https://api.whatsapp.com/send?phone=$numero&text=${Uri.encode(mensaje)}"

            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }
}





