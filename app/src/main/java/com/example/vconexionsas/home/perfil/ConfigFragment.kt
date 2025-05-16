package com.example.vconexionsas.home.perfil

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.example.vconexionsas.databinding.FragmentConfigBinding
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class ConfigFragment : Fragment() {

    private lateinit var binding: FragmentConfigBinding
    private var selectedImageBitmap: Bitmap? = null
    private lateinit var prefs: SharedPreferences

    // 📷 Nuevo Photo Picker
    private val imagePickerLauncher = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                }
                selectedImageBitmap = bitmap
                binding.imagePerfilEditar.setImageBitmap(bitmap)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfigBinding.inflate(inflater, container, false)
        prefs = requireContext().getSharedPreferences("perfil_usuario", Context.MODE_PRIVATE)

        // Nombre e imagen guardada
        val nombreGuardado = prefs.getString("nombre", "")
        val rutaImagen = prefs.getString("ruta_imagen", null)
        binding.editNombre.setText(nombreGuardado)

        rutaImagen?.let {
            val archivo = File(it)
            if (archivo.exists()) {
                val bitmap = BitmapFactory.decodeFile(it)
                binding.imagePerfilEditar.setImageBitmap(bitmap)
            }
        }

        // Abrir photo picker
        val abrirSelector = {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )

        }
        binding.imagePerfilEditar.setOnClickListener { abrirSelector() }
        binding.textCambiarFoto.setOnClickListener { abrirSelector() }

        // Guardar nombre e imagen
        binding.btnGuardar.setOnClickListener {
            val nuevoNombre = binding.editNombre.text.toString()
            val editor = prefs.edit()
            editor.putString("nombre", nuevoNombre)

            selectedImageBitmap?.let {
                val archivo = File(requireContext().filesDir, "perfil.jpg")
                val out = FileOutputStream(archivo)
                it.compress(Bitmap.CompressFormat.JPEG, 85, out)
                out.flush(); out.close()
                editor.putString("ruta_imagen", archivo.absolutePath)
            }

            editor.apply()

            val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(binding.editNombre.windowToken, 0)

            setFragmentResult("perfil_actualizado", bundleOf())
            requireActivity().onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        // Botón de regreso visual
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }
}

