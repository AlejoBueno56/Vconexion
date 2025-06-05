package com.example.vconexionsas.home.servicios

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.vconexionsas.R
import com.example.vconexionsas.utils.ContactoUtils
import com.google.android.material.card.MaterialCardView

class CCTVFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cctv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        val contactButton: Button = view.findViewById(R.id.contactButton)

        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_up)
        val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)

        // Aplicar animaciones a las nuevas tarjetas según la estructura del XML
        listOf(
            R.id.heroImageCard,
            R.id.reliabilityCard,
            R.id.differenceCard,
            R.id.introCard,
            R.id.benefitsCard
        ).forEach {
            view.findViewById<MaterialCardView>(it)?.startAnimation(fadeIn)
        }

        contactButton.startAnimation(fadeIn)

        // Configurar click listeners para las tarjetas (opcional)
        view.findViewById<MaterialCardView>(R.id.reliabilityCard)?.setOnClickListener {
            it.startAnimation(scaleTap)
        }

        view.findViewById<MaterialCardView>(R.id.differenceCard)?.setOnClickListener {
            it.startAnimation(scaleTap)
        }

        view.findViewById<MaterialCardView>(R.id.introCard)?.setOnClickListener {
            it.startAnimation(scaleTap)
        }

        view.findViewById<MaterialCardView>(R.id.benefitsCard)?.setOnClickListener {
            it.startAnimation(scaleTap)
        }

        contactButton.setOnClickListener {
            it.startAnimation(scaleTap)
            val numero = ContactoUtils.obtenerNumeroWhatsapp(requireContext(), ContactoUtils.TipoContacto.CCTV)
            val mensaje = "¡Hola! Estoy interesado en conocer más sobre sus servicios de CCTV."
            val url = "https://api.whatsapp.com/send?phone=$numero&text=${Uri.encode(mensaje)}"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }
}
