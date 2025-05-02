package com.example.vconexionsas.home.contrasena

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vconexionsas.R
import com.google.android.material.card.MaterialCardView

class ContrasenaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contrasena, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el botón de regreso
        val backButton: ImageButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Animaciones
        val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)

        // Configurar el texto de instrucciones
        val instruccionesTextView = view.findViewById<TextView>(R.id.textoInstrucciones)
        val textoConSaltos = getString(R.string.instrucciones_contrasena).replace("\n", "<br>")
        instruccionesTextView.text = Html.fromHtml(textoConSaltos, Html.FROM_HTML_MODE_LEGACY)

        // Configurar tarjetas de modelos ONU
        configurarTarjeta(view, R.id.card_adc, R.id.action_contrasenaFragment_to_adcFragment, scaleTap)
        configurarTarjeta(view, R.id.card_easylink, R.id.action_contrasenaFragment_to_easy4linkFragment, scaleTap)
        configurarTarjeta(view, R.id.card_tplink, R.id.action_contrasenaFragment_to_tplinkFragment, scaleTap)
        configurarTarjeta(view, R.id.card_latic, R.id.action_contrasenaFragment_to_laticFragment, scaleTap)
        configurarTarjeta(view, R.id.card_zkxx, R.id.action_contrasenaFragment_to_zkxxFragment, scaleTap)
        configurarTarjeta(view, R.id.card_ztec, R.id.action_contrasenaFragment_to_ztecFragment, scaleTap)
        //configurarTarjeta(view, R.id.card_zten, R.id.action_contrasenaFragment_to_ztenFragment, scaleTap)

        // Configurar botón físico "atrás"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    // Función para configurar tarjetas de forma limpia
    private fun configurarTarjeta(view: View, cardId: Int, actionId: Int, animacion: android.view.animation.Animation) {
        val card = view.findViewById<MaterialCardView>(cardId)
        card.setOnClickListener {
            card.startAnimation(animacion)
            findNavController().navigate(actionId)
        }
    }
}
