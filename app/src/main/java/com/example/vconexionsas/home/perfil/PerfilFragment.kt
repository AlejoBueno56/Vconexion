package com.example.vconexionsas.home.perfil

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vconexionsas.R
import com.example.vconexionsas.login.MainActivity

class PerfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Referencias a vistas
        val nombreTextView: TextView = rootView.findViewById(R.id.nombreUsuario)
        val correoTextView: TextView = rootView.findViewById(R.id.emailUsuario)
        val avatarImageView: ImageView = rootView.findViewById(R.id.avatar)
        val avatarLoading: ProgressBar = rootView.findViewById(R.id.avatarLoading)
        val btnCerrarSesion: Button = rootView.findViewById(R.id.btn_logout)

        avatarImageView.setImageResource(R.drawable.ic_person)
        avatarLoading.visibility = View.VISIBLE

        // SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val perfilPrefs = requireActivity().getSharedPreferences("perfil_usuario", Context.MODE_PRIVATE)

        val nombre = perfilPrefs.getString("nombre", sharedPreferences.getString("nombre_usuario", "Usuario desconocido"))
        val correo = sharedPreferences.getString("correo_usuario", "correo@ejemplo.com")
        val imagenBase64 = perfilPrefs.getString("imagen", null)

        nombreTextView.text = nombre
        correoTextView.text = correo

        avatarImageView.post {
            if (!imagenBase64.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    avatarImageView.setImageBitmap(bitmap)
                } catch (_: Exception) {
                    avatarImageView.setImageResource(R.drawable.ic_person)
                }
            }
            avatarLoading.visibility = View.GONE
        }

        btnCerrarSesion.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            requireActivity().finish()
        }

        rootView.findViewById<Button>(R.id.btnNotificaciones).setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_notificacionesFragment)
        }

        rootView.findViewById<Button>(R.id.privacy_policy_button).setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_politicasFragment)
        }

        rootView.findViewById<Button>(R.id.configure_profile_button).setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_configFragment)
        }

        parentFragmentManager.setFragmentResultListener("perfil_actualizado", viewLifecycleOwner) { _, _ ->
            Toast.makeText(requireContext(), "Cambios guardados con éxito", Toast.LENGTH_SHORT).show()
            val nuevosDatos = requireActivity().getSharedPreferences("perfil_usuario", Context.MODE_PRIVATE)
            val nuevoNombre = nuevosDatos.getString("nombre", nombre)
            val nuevaImagen = nuevosDatos.getString("imagen", imagenBase64)

            nombreTextView.text = nuevoNombre
            if (!nuevaImagen.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(nuevaImagen, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    avatarImageView.setImageBitmap(bitmap)
                } catch (_: Exception) {
                    avatarImageView.setImageResource(R.drawable.ic_person)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()

        }

        return rootView
    }
}


