package com.example.vconexionsas.home.servicios

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

class CCTVFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cctv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias a vistas
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        val contactButton: Button = view.findViewById(R.id.contactButton)

        // Cargar animaciones
        val fadeInSlideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_up)
        val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)

        // Aplicar animaciones de entrada a los elementos
        view.findViewById<View>(R.id.cctvTitle).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.cctvImageMain).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.cctvSubtitle1).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.cctvDescription1).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.cctvImageSecondary).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.cctvSubtitle2).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.cctvDescription2).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.cctvImageBenefits).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.cctvSubtitle3).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.cctvDescription3).startAnimation(fadeInSlideUp)
        contactButton.startAnimation(fadeInSlideUp)

        // Botón "Atrás"
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Botón "Contáctanos" con rebote y abrir WhatsApp
        contactButton.setOnClickListener {
            contactButton.startAnimation(scaleTap)

            val numeroTelefono = "+573024538585" // Cambiar aquí tu número real de WhatsApp
            val mensaje = "¡Hola! Estoy interesado en conocer más sobre sus servicios de CCTV."

            val url = "https://api.whatsapp.com/send?phone=$numeroTelefono&text=${mensaje.replace(" ", "%20")}"

            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Botón físico atrás del dispositivo
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }
}
