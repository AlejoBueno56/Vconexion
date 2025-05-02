package com.example.vconexionsas.home.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.vconexionsas.databinding.FragmentPoliticasBinding

class PoliticasFragment : Fragment() {

    private lateinit var binding: FragmentPoliticasBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPoliticasBinding.inflate(inflater, container, false)

        // Aquí puedes cargar texto dinámico si lo necesitas
        binding.textoPoliticas.text = """
            En VConexión respetamos y protegemos la privacidad de nuestros usuarios.

            - No compartimos información personal con terceros.
            - Usamos los datos únicamente para mejorar tu experiencia.
            - Puedes solicitar la eliminación de tus datos cuando lo desees.

            Al usar esta aplicación, aceptas nuestras políticas.
        """.trimIndent()

        // Permitir volver con el botón físico
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}
