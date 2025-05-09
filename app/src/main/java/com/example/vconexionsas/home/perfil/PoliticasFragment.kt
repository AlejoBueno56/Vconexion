package com.example.vconexionsas.home.perfil

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.vconexionsas.databinding.FragmentPoliticasBinding
import com.example.vconexionsas.R

class PoliticasFragment : Fragment() {

    private lateinit var binding: FragmentPoliticasBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPoliticasBinding.inflate(inflater, container, false)

        // Leer archivo con HTML formateado desde res/raw/politica.txt
        val politicaHtml = resources.openRawResource(R.raw.politica)
            .bufferedReader()
            .use { it.readText() }

        binding.textoPoliticas.text = Html.fromHtml(politicaHtml, Html.FROM_HTML_MODE_LEGACY)

        // Manejo del botón atrás
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}


