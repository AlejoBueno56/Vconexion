package com.example.vconexionsas.home.servicios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vconexionsas.R
import com.google.android.material.card.MaterialCardView

class ServiciosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_servicios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Encontrar las vistas
        val domoticaCard: MaterialCardView = view.findViewById(R.id.domoticaCard)
        val cableadoCard: MaterialCardView = view.findViewById(R.id.cableadoCard)
        val iptvCard: MaterialCardView = view.findViewById(R.id.iptvCard)
        val cctvCard: MaterialCardView = view.findViewById(R.id.CCTVCard)
        val backButton: ImageButton = view.findViewById(R.id.backButton)

        // Cargar animación
        val animacionEntrada = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_up)

        // Aplicar animaciones a cada tarjeta
        domoticaCard.startAnimation(animacionEntrada)
        cableadoCard.startAnimation(animacionEntrada)
        iptvCard.startAnimation(animacionEntrada)
        cctvCard.startAnimation(animacionEntrada)

        // Configurar clics en las tarjetas
        domoticaCard.setOnClickListener {
            findNavController().navigate(R.id.action_servicios_to_domoticaFragment)
        }

        cableadoCard.setOnClickListener {
            findNavController().navigate(R.id.action_inicioFragment_to_cableadoEstructuradoFragment)
        }

        iptvCard.setOnClickListener {
            findNavController().navigate(R.id.action_servicios_to_iptv)
        }

        cctvCard.setOnClickListener {
            findNavController().navigate(R.id.action_servicios_to_cctv)
        }

        // Botón de regreso arriba (ImageButton)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Manejo del botón físico atrás del dispositivo
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }
}

