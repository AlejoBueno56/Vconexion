package com.example.vconexionsas.home.contrasena

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.AnimationUtils
import android.widget.ImageButton
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
        //configurarTarjeta(view, R.id.card_tplink, R.id.action_contrasenaFragment_to_tplinkFragment, scaleTap)
        configurarTarjeta(view, R.id.card_latic, R.id.action_contrasenaFragment_to_laticFragment, scaleTap)
        configurarTarjeta(view, R.id.card_zkxx, R.id.action_contrasenaFragment_to_zkxxFragment, scaleTap)
        configurarTarjeta(view, R.id.card_ztec, R.id.action_contrasenaFragment_to_ztecFragment, scaleTap)
        //configurarTarjeta(view, R.id.card_zten, R.id.action_contrasenaFragment_to_ztenFragment, scaleTap)

        // Botón detectar ONU (tarjeta dedicada)
        view.findViewById<MaterialCardView>(R.id.btn_detectar_onu)?.setOnClickListener {
            it.startAnimation(scaleTap)
            navController.navigate(R.id.action_contrasenaFragment_to_fragmentPruebaDetectarONU)
        }

        // Menú desplegable de modelos ONU
        val menuModelos = view.findViewById<View>(R.id.layout_modelos_onu)
        val tituloMenu = view.findViewById<TextView>(R.id.titulo_menu_modelos)
        val contenedorExpandible = view.findViewById<ViewGroup>(R.id.menu_modelos)

        tituloMenu.setOnClickListener {
            TransitionManager.beginDelayedTransition(contenedorExpandible, AutoTransition())
            if (menuModelos.visibility == View.GONE) {
                menuModelos.visibility = View.VISIBLE
                tituloMenu.text = getString(R.string.menu_modelos_abierto)
            } else {
                menuModelos.visibility = View.GONE
                tituloMenu.text = getString(R.string.menu_modelos)
            }
        }
        // Botón de contacto por WhatsApp usando ContactoUtils
        view.findViewById<MaterialCardView>(R.id.btn_contactar)?.setOnClickListener {
            val numero = ContactoUtils.obtenerNumeroWhatsapp(requireContext(), ContactoUtils.TipoContacto.TECNICO)
            val mensaje = "Hola, necesito ayuda con mi Wi-Fi"
            val url = "https://wa.me/$numero?text=${java.net.URLEncoder.encode(mensaje, "UTF-8")}"

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
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


