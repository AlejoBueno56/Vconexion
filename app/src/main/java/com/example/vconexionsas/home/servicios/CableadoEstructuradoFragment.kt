package com.example.vconexionsas.home.servicios

import android.content.Context
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
import com.example.vconexionsas.utils.ContactoUtils

class CableadoEstructuradoFragment : Fragment() {

    private var _binding: FragmentCableadoEstructuradoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCableadoEstructuradoBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animacionEntrada = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_slide_up)
        val scaleTap = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_tap)

        listOf(
            binding.cableadoHeaderImage,
            binding.titleServicios,
            binding.cableadoSubtitle1,
            binding.cableadoDescription1,
            binding.cableadoSubtitle2,
            binding.cableadoDescription2,
            binding.cableadoIntroDescription,
            binding.cableadoBenefitsImage,
            binding.cableadoBenefitsDescription,
            binding.contactButton
        ).forEach { it.startAnimation(animacionEntrada) }

        binding.contactButton.setOnClickListener {
            it.startAnimation(scaleTap)

            val numero = ContactoUtils.obtenerNumeroWhatsapp(requireContext(), ContactoUtils.TipoContacto.CABLEADO)
            val mensaje = "¡Hola! Estoy interesado en conocer más sobre su servicio de Cableado Estructurado."
            val url = "https://api.whatsapp.com/send?phone=$numero&text=${Uri.encode(mensaje)}"

            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}