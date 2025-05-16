package com.example.vconexionsas.home.traslado

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.vconexionsas.R
import com.example.vconexionsas.utils.ContactoUtils
import java.net.URLEncoder

class TrasladoFragment : Fragment() {

    private lateinit var editDireccion: EditText
    private lateinit var btnEnviar: Button
    private lateinit var textNombre: TextView
    private lateinit var textCodigo: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_traslado, container, false)

        editDireccion = view.findViewById(R.id.editDireccionTraslado)
        btnEnviar = view.findViewById(R.id.btnEnviarTraslado)
        textNombre = view.findViewById(R.id.textNombreUsuario)
        textCodigo = view.findViewById(R.id.textCodigoUsuario)

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val nombre = prefs.getString("nombre_usuario", "Usuario")
        val codigo = prefs.getString("codigo_usuario", "000000")

        textNombre.text = "Nombre: $nombre"
        textCodigo.text = "Código: $codigo"

        btnEnviar.setOnClickListener {
            val direccion = editDireccion.text.toString().trim()

            if (direccion.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor ingresa la dirección de traslado.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val mensaje = "*Solicitud de Traslado desde la App VConexion*\n\n*Nombre:* $nombre\n*Código:* $codigo\n*Dirección nueva:* $direccion"

            val numero = ContactoUtils.obtenerNumeroWhatsapp(requireContext(), ContactoUtils.TipoContacto.TECNICO)
            val uri = Uri.parse("https://wa.me/$numero?text=" + URLEncoder.encode(mensaje, "UTF-8"))
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        val backButton: ImageButton = view.findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
