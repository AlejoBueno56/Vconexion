package com.example.vconexionsas.home.contrasena

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.vconexionsas.R
import com.example.vconexionsas.utils.ContactoUtils
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

        val navController = findNavController()
        val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)

        // Botón de regreso (ícono visual)
        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Botón físico Android
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        // Renderizar instrucciones con saltos de línea
        val instruccionesTextView = view.findViewById<TextView>(R.id.textoInstrucciones)
        val textoConSaltos = getString(R.string.instrucciones_contrasena).replace("\n", "<br>")
        instruccionesTextView.text = Html.fromHtml(textoConSaltos, Html.FROM_HTML_MODE_LEGACY)

        // Navegación tarjetas ONU
        configurarTarjeta(view, R.id.card_adc, R.id.action_contrasenaFragment_to_adcFragment, scaleTap)
        configurarTarjeta(view, R.id.card_easylink, R.id.action_contrasenaFragment_to_easy4linkFragment, scaleTap)
        configurarTarjeta(view, R.id.card_latic, R.id.action_contrasenaFragment_to_laticFragment, scaleTap)
        configurarTarjeta(view, R.id.card_zkxx, R.id.action_contrasenaFragment_to_zkxxFragment, scaleTap)
        configurarTarjeta(view, R.id.card_ztec, R.id.action_contrasenaFragment_to_ztecFragment, scaleTap)

        // Botón detectar ONU (tarjeta dedicada)
        view.findViewById<MaterialCardView>(R.id.btn_detectar_onu)?.setOnClickListener {
            it.startAnimation(scaleTap)
            navController.navigate(R.id.action_contrasenaFragment_to_fragmentPruebaDetectarONU)
        }

        // Configurar menú desplegable de modelos ONU
        configurarMenuModelos(view)

        // Configurar sección de ayuda colapsible
        configurarSeccionAyuda(view)

        // Botón de contacto por WhatsApp usando ContactoUtils
        view.findViewById<MaterialCardView>(R.id.btn_contactar)?.setOnClickListener {
            it.startAnimation(scaleTap)
            val numero = ContactoUtils.obtenerNumeroWhatsapp(requireContext(), ContactoUtils.TipoContacto.TECNICO)
            val mensaje = "Hola, necesito ayuda con mi Wi-Fi"
            val url = "https://wa.me/$numero?text=${java.net.URLEncoder.encode(mensaje, "UTF-8")}"

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        }
    }

    private fun configurarMenuModelos(view: View) {
        val menuModelos = view.findViewById<View>(R.id.layout_modelos_onu)
        val spacerModelos = view.findViewById<View>(R.id.spacer_modelos)
        val tituloMenu = view.findViewById<TextView>(R.id.titulo_menu_modelos)
        val contenedorExpandible = view.findViewById<ViewGroup>(R.id.menu_modelos)
        val arrowIndicator = view.findViewById<ImageView>(R.id.arrow_indicator)

        tituloMenu.setOnClickListener {
            TransitionManager.beginDelayedTransition(contenedorExpandible, AutoTransition())

            val isExpanded = menuModelos.visibility == View.VISIBLE

            if (isExpanded) {
                // Colapsar
                menuModelos.visibility = View.GONE
                spacerModelos.visibility = View.GONE
                tituloMenu.text = getString(R.string.menu_modelos)
                arrowIndicator.rotation = 0f
            } else {
                // Expandir
                menuModelos.visibility = View.VISIBLE
                spacerModelos.visibility = View.VISIBLE
                tituloMenu.text = getString(R.string.menu_modelos_abierto)
                arrowIndicator.rotation = 180f
            }
        }
    }

    private fun configurarSeccionAyuda(view: View) {
        val ayudaHeader = view.findViewById<View>(R.id.ayuda_header)
        val instruccionesContainer = view.findViewById<View>(R.id.instrucciones_container)
        val ayudaContainer = view.findViewById<ViewGroup>(R.id.ayuda_container)
        val helpArrowIndicator = view.findViewById<ImageView>(R.id.help_arrow_indicator)

        ayudaHeader.setOnClickListener {
            toggleExpandableSection(
                container = ayudaContainer,
                content = instruccionesContainer,
                arrow = helpArrowIndicator
            )
        }
    }

    private fun toggleExpandableSection(
        container: ViewGroup,
        content: View,
        title: TextView? = null,
        arrow: ImageView? = null,
        closedText: String? = null,
        openText: String? = null
    ) {
        TransitionManager.beginDelayedTransition(container, AutoTransition())

        val isExpanded = content.visibility == View.VISIBLE

        if (isExpanded) {
            // Colapsar
            content.visibility = View.GONE
            title?.text = closedText
            arrow?.rotation = 0f
        } else {
            // Expandir
            content.visibility = View.VISIBLE
            title?.text = openText
            arrow?.rotation = 180f
        }
    }

    private fun configurarTarjeta(view: View, cardId: Int, actionId: Int, animacion: android.view.animation.Animation) {
        val card = view.findViewById<MaterialCardView>(cardId)
        card?.setOnClickListener {
            it.startAnimation(animacion)
            findNavController().navigate(actionId)
        }
    }
}

