package com.example.vconexionsas.home.perfil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.vconexionsas.R
import com.example.vconexionsas.login.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PerfilFragment : Fragment() {

    private lateinit var avatarImageView: ImageView
    private lateinit var avatarLoading: ProgressBar
    private lateinit var contentLayout: View
    private lateinit var perfilPrefs: SharedPreferences
    private lateinit var nombreTextView: TextView
    private lateinit var correoTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_perfil, container, false)

        nombreTextView = rootView.findViewById(R.id.nombreUsuario)
        correoTextView = rootView.findViewById(R.id.emailUsuario)
        avatarImageView = rootView.findViewById(R.id.avatar)
        avatarLoading = rootView.findViewById(R.id.avatarLoading)
        contentLayout = rootView.findViewById(R.id.perfilContentContainer)
        val btnCerrarSesion: Button = rootView.findViewById(R.id.btn_logout)

        avatarImageView.setImageResource(R.drawable.ic_person)
        avatarLoading.visibility = View.VISIBLE
        contentLayout.alpha = 0f

        actualizarPerfil()

        parentFragmentManager.setFragmentResultListener("perfil_actualizado", viewLifecycleOwner) { _, _ ->
            actualizarPerfil()
        }

        btnCerrarSesion.setOnClickListener {
            val masterKeyAlias = MasterKey.Builder(requireContext())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                requireContext(),
                "secure_user_prefs",
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            sharedPreferences.edit().clear().apply()
            perfilPrefs.edit().remove("ruta_imagen").apply()
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

        return rootView
    }

    private fun actualizarPerfil() {
        val masterKeyAlias = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            requireContext(),
            "secure_user_prefs",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        perfilPrefs = EncryptedSharedPreferences.create(
            requireContext(),
            "perfil_usuario",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val nombre = perfilPrefs.getString("nombre", sharedPreferences.getString("nombre_usuario", "Usuario desconocido"))
        val correo = sharedPreferences.getString("correo_usuario", "correo@ejemplo.com")
        val rutaImagen = perfilPrefs.getString("ruta_imagen", null)

        nombreTextView.text = nombre
        correoTextView.text = correo

        lifecycleScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                rutaImagen?.let {
                    val archivo = File(it)
                    if (archivo.exists()) BitmapFactory.decodeFile(it) else null
                }
            }
            avatarLoading.visibility = View.GONE
            if (bitmap != null) {
                avatarImageView.setImageBitmap(bitmap)
            } else {
                avatarImageView.setImageResource(R.drawable.ic_person)
            }
            contentLayout.animate().alpha(1f).setDuration(400).start()
        }
    }
}



