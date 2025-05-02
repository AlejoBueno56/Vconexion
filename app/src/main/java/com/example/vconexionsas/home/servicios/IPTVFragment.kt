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

class IPTVFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_iptv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar botones
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        val contactButton: Button = view.findViewById(R.id.contactButton)

        // Cargar animaciones
        val fadeInSlideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_up)
        val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)

        // Aplicar animación de entrada a los componentes principales
        view.findViewById<View>(R.id.iptvTitle).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.iptvImageSubtitle1).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.iptvSubtitle1).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.iptvDescription1).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.iptvImageSubtitle2).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.iptvSubtitle2).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.iptvDescription2).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.iptvImageSubtitle3).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.iptvSubtitle3).startAnimation(fadeInSlideUp)
        view.findViewById<View>(R.id.iptvDescription3).startAnimation(fadeInSlideUp)
        contactButton.startAnimation(fadeInSlideUp)

        // Acción botón "Atrás"
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Acción botón "Contáctanos"
        contactButton.setOnClickListener {
            contactButton.startAnimation(scaleTap)

            val numeroTelefono = "+573024538585" // 📞 Cambia aquí por tu número real
            val mensaje = "¡Hola! Estoy interesado en conocer más sobre su servicio de IPTV."

            val url = "https://api.whatsapp.com/send?phone=$numeroTelefono&text=${mensaje.replace(" ", "%20")}"

            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Manejar botón físico atrás del dispositivo
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }
}





