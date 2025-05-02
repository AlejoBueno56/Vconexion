package com.example.vconexionsas.home.servicios

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vconexionsas.R
import com.example.vconexionsas.databinding.FragmentCableadoEstructuradoBinding

class CableadoEstructuradoFragment : Fragment() {

    private var _binding: FragmentCableadoEstructuradoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inicializar el ViewBinding
        _binding = FragmentCableadoEstructuradoBinding.inflate(inflater, container, false)
        // Manejo del botón Atrás del sistema para volver al fragmento anterior
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargar animación
        val animacionEntrada =
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_up)

        // Aplica animaciones con binding
        binding.cableadoHeaderImage.startAnimation(animacionEntrada)
        binding.titleServicios.startAnimation(animacionEntrada)
        binding.cableadoSubtitle1.startAnimation(animacionEntrada)
        binding.cableadoDescription1.startAnimation(animacionEntrada)
        binding.cableadoSubtitle2.startAnimation(animacionEntrada)
        binding.cableadoDescription2.startAnimation(animacionEntrada)
        binding.cableadoIntroDescription.startAnimation(animacionEntrada)
        binding.cableadoBenefitsImage.startAnimation(animacionEntrada)
        binding.cableadoBenefitsDescription.startAnimation(animacionEntrada)
        binding.contactButton.startAnimation(animacionEntrada)

        // Animación al presionar botón
        binding.contactButton.setOnClickListener {
            val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)
            it.startAnimation(scaleTap)
            val numeroTelefono = "+573024538585" // 📞 Cambia aquí por tu número real
            val mensaje = "¡Hola! Estoy interesado en conocer más sobre su servicio de Cableado Estructurado."

            val url = "https://api.whatsapp.com/send?phone=$numeroTelefono&text=${mensaje.replace(" ", "%20")}"

            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
            // Configurar el botón de retroceso
            binding.backButton.setOnClickListener {
                findNavController().navigateUp() // Navega hacia atrás
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            // Limpiar el binding
            _binding = null
        }
}


