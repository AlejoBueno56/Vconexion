package com.example.vconexionsas.home.cobertura

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.vconexionsas.databinding.FragmentCoberturaBinding

class CoberturaFragment : Fragment() {

    private lateinit var binding: FragmentCoberturaBinding
    private val lugares = listOf("San Cayetano", "Pamplona", "Toledo", "Labateca", "Chitagá")
    private val descripciones = mapOf(
        "San Cayetano" to "Zona Recientemente Conectada a Fibra Óptica.",
        "Pamplona" to "Planes desde 50MB hasta 400MB disponibles.",
        "Toledo" to "Cobertura Urbana de Calidad.",
        "Labateca" to "Conexión Estable y Rápida en Expansión.",
        "Chitagá" to "Fibra Óptica Directa con Alta Velocidad."
    )

    private var index = 0
    private val handler = Handler(Looper.getMainLooper())

    private fun generarTextoColoreado(lugar: String): SpannableString {
        val texto = "Estamos en\n$lugar"
        val spannable = SpannableString(texto)

        // Azul para "Estamos en"
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#265CAF")),
            0,
            texto.indexOf("\n") + 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Verde + más grande para el nombre del lugar
        val startLugar = texto.indexOf(lugar)
        val endLugar = startLugar + lugar.length

        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#2DA950")),
            startLugar,
            endLugar,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            RelativeSizeSpan(1.5f),
            startLugar,
            endLugar,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannable
    }

    private val animarCambioTexto = object : Runnable {
        override fun run() {
            binding.textoUbicacion.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    index = (index + 1) % lugares.size
                    val lugar = lugares[index]

                    binding.textoUbicacion.text = generarTextoColoreado(lugar)
                    binding.descripcionCobertura.text = descripciones[lugar] ?: ""

                    binding.textoUbicacion.animate().alpha(1f).setDuration(300).start()
                }.start()
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCoberturaBinding.inflate(inflater, container, false)

        // Configurar texto inicial
        binding.textoUbicacion.text = generarTextoColoreado(lugares[0])
        binding.descripcionCobertura.text = descripciones[lugares[0]]
        binding.textoUbicacion.alpha = 1f

        // Botón de regreso visual
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Botón de regreso físico
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        handler.post(animarCambioTexto)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}



