package com.example.vconexionsas.home.plan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vconexionsas.adapters.PlanAdapter
import com.example.vconexionsas.databinding.FragmentPlanesBinding
import com.example.vconexionsas.model.PlanData
import com.example.vconexionsas.home.plan.obtenerPlanes

class PlanFragment : Fragment() {

    private lateinit var binding: FragmentPlanesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanesBinding.inflate(inflater, container, false)

        // Mostrar barra de carga al iniciar
        binding.progressBar.visibility = View.VISIBLE

        // Llamar a la API
        obtenerPlanes(
            context = context ?: return binding.root,
            onSuccess = { fetchedPlans ->
                if (!isAdded) return@obtenerPlanes
                val plans = fetchedPlans.map { plan ->
                    val features = when (plan.speed) {
                        "50 MB" -> listOf("Ideal para navegación básica", "Calidad SD", "Encriptación avanzada")
                        "70 MB" -> listOf("Ideal para múltiples dispositivos", "Calidad HD", "Soporte técnico")
                        "100 MB" -> listOf("Simetría en la Velocidad", "Para streaming", "Soporte técnico")
                        "120 MB" -> listOf("Ideal para oficinas pequeñas", "Streaming UHD", "Estabilidad garantizada")
                        "150 MB" -> listOf("Para hogares exigentes", "Gaming sin lag", "Alta velocidad")
                        "400 MB" -> listOf("Empresarial dedicado", "IP pública disponible", "Atención prioritaria")
                        else -> listOf("Conectividad estable", "Sin cláusula", "Soporte 24/7")
                    }
                    plan.copy(features = features)
                }

                binding.progressBar.visibility = View.GONE

                val adapter = PlanAdapter(plans)
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = adapter

                adapter.setOnContactClickListener { plan ->
                    openWhatsApp(plan.title)
                }
            },
            onError = {
                if (!isAdded) return@obtenerPlanes
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error de Conexión", Toast.LENGTH_SHORT).show()
            }
        )

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Botón atrás
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun openWhatsApp(planName: String) {
        val phoneNumber = "+573024538585"
        val message = "Me interesa el $planName"
        val uri = Uri.parse("https://wa.me/$phoneNumber?text=$message")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}

