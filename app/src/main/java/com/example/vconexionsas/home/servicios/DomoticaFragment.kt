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

class DomoticaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_domotica, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        val contactButton: Button = view.findViewById(R.id.contactButton)

        // Animación de aparición general
        val animacionEntrada =
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_up)

        // Aplicar animación a los principales elementos
        view.findViewById<View>(R.id.domoticaTitle).startAnimation(animacionEntrada)
        view.findViewById<View>(R.id.domoticaImage).startAnimation(animacionEntrada)
        view.findViewById<View>(R.id.domoticaDescription).startAnimation(animacionEntrada)
        view.findViewById<View>(R.id.domoticaIntroTitle).startAnimation(animacionEntrada)
        view.findViewById<View>(R.id.domoticaIntroDescription).startAnimation(animacionEntrada)
        view.findViewById<View>(R.id.benefitsImage).startAnimation(animacionEntrada)
        view.findViewById<View>(R.id.domoticaBenefitsTitle).startAnimation(animacionEntrada)
        view.findViewById<View>(R.id.domoticaBenefits).startAnimation(animacionEntrada)
        contactButton.startAnimation(animacionEntrada)

        // Botón "Atrás" - navega hacia atrás en el NavController
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Botón "Contáctanos" con animación de toque
        contactButton.setOnClickListener {
            val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)
            contactButton.startAnimation(scaleTap)

            val numeroTelefono = "+573024538585" // 📞 Cambia aquí por tu número real
            val mensaje = "¡Hola! Estoy interesado en conocer más sobre su servicio de Domotica."

            val url = "https://api.whatsapp.com/send?phone=$numeroTelefono&text=${mensaje.replace(" ", "%20")}"

            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Botón físico "Atrás" del dispositivo
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

    }
}
