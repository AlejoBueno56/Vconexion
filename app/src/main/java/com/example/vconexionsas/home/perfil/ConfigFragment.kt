package com.example.vconexionsas.home.perfil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.vconexionsas.databinding.FragmentConfigBinding
import java.io.ByteArrayOutputStream

class ConfigFragment : Fragment() {

    private lateinit var binding: FragmentConfigBinding
    private var selectedImageBitmap: Bitmap? = null

    private val IMAGE_PICK_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfigBinding.inflate(inflater, container, false)

        val prefs = requireContext().getSharedPreferences("perfil_usuario", Context.MODE_PRIVATE)
        val nombreGuardado = prefs.getString("nombre", "")
        val imagenBase64 = prefs.getString("imagen", null)

        // Mostrar nombre guardado
        binding.editNombre.setText(nombreGuardado)

        // Mostrar imagen guardada si existe
        imagenBase64?.let {
            try {
                val imageBytes = Base64.decode(it, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                binding.imagePerfilEditar.setImageBitmap(bitmap)
            } catch (_: Exception) {}
        }

        // Seleccionar imagen
        val pickImageIntent = {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        binding.imagePerfilEditar.setOnClickListener { pickImageIntent() }
        binding.textCambiarFoto.setOnClickListener { pickImageIntent() }

        // Guardar cambios
        binding.btnGuardar.setOnClickListener {
            val nuevoNombre = binding.editNombre.text.toString()

            val editor = prefs.edit()
            editor.putString("nombre", nuevoNombre)

            selectedImageBitmap?.let {
                val outputStream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val imageBase64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
                editor.putString("imagen", imageBase64)
            }

            editor.apply()

            // ✅ Ocultar teclado
            val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(binding.editNombre.windowToken, 0)

            // ✅ Notificar al fragmento anterior
            setFragmentResult("perfil_actualizado", bundleOf())

            // ✅ Volver al fragmento anterior
            requireActivity().onBackPressed()
        }

        // Botón físico atrás
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                selectedImageBitmap = bitmap
                binding.imagePerfilEditar.setImageBitmap(bitmap)
            }
        }
    }
}
