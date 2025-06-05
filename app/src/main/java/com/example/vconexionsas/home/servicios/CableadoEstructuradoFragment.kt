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

class CableadoEstructuradoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_cableado_estructurado, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        val contactButton = view.findViewById<MaterialButton>(R.id.contactButton)

        val animacionEntrada = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_up)
        val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)

        listOf(
            R.id.headerTitle,
            R.id.Icon,R.id.heroIcon, R.id.heroImageCard,
            R.id.reliabilityCard, R.id.reliabilityTitle, R.id.reliabilityDescription,
            R.id.differenceCard, R.id.differenceTitle, R.id.differenceDescription,
            R.id.introCard, R.id.introTitle, R.id.introDescription,
            R.id.benefitsCard, R.id.benefitsTitle,
            R.id.contactButton
        ).forEach {
            view.findViewById<View>(it)?.startAnimation(animacionEntrada)
        }

        contactButton.setOnClickListener {
            contactButton.startAnimation(scaleTap)
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:soporte@vconexion.com")
                putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre Cableado Estructurado")
            }
            startActivity(intent)
        }

        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }
}


