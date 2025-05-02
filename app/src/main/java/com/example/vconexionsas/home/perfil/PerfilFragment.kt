package com.example.vconexionsas.home.perfil

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vconexionsas.R
import com.example.vconexionsas.login.MainActivity

class PerfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val rootView = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Obtener los datos del usuario desde SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val nombre = sharedPreferences.getString("nombre_usuario", "Usuario desconocido") // Valor por defecto
        val correo = sharedPreferences.getString("correo_usuario", "correo@ejemplo.com") // Valor por defecto

        // Referencias a los TextViews donde se mostrarán los datos
        val nombreTextView: TextView = rootView.findViewById(R.id.nombreUsuario)
        val correoTextView: TextView = rootView.findViewById(R.id.emailUsuario)
        val btnCerrarSesion: Button = rootView.findViewById(R.id.btn_logout)

        // Asignar los datos a los TextViews
        nombreTextView.text = nombre
        correoTextView.text = correo

        btnCerrarSesion.setOnClickListener {
            sharedPreferences.edit().clear().apply()

            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            requireActivity().finish() // Finaliza actividad actual
        }

        // Dentro del onCreateView, después de inflar rootView:
        val btnNotificaciones: Button = rootView.findViewById(R.id.btnNotificaciones)
        btnNotificaciones.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_notificacionesFragment)

        }
        val privacy_policy_button: Button = rootView.findViewById(R.id.privacy_policy_button)
        privacy_policy_button.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_politicasFragment)

        }
        // Manejo del botón Atrás del sistema para volver al fragmento anterior
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        return rootView
    }
}
