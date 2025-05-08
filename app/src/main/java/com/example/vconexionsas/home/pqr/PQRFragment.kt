package com.example.vconexionsas.home.pqr

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
import java.net.URLEncoder

class PqrFragment : Fragment() {

    private lateinit var editMotivo: EditText
    private lateinit var autoDetalles: AutoCompleteTextView
    private lateinit var editOtroDetalle: EditText
    private lateinit var btnEnviar: Button
    private lateinit var textNombre: TextView
    private lateinit var textCodigo: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_pqr, container, false)

        editMotivo = view.findViewById(R.id.editMotivo)
        autoDetalles = view.findViewById(R.id.autoDetalles)
        editOtroDetalle = view.findViewById(R.id.editOtroDetalle)
        btnEnviar = view.findViewById(R.id.btnEnviarWhatsapp)
        textNombre = view.findViewById(R.id.textNombreUsuario)
        textCodigo = view.findViewById(R.id.textCodigoUsuario)

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val nombre = prefs.getString("nombre_usuario", "Usuario")
        val codigo = prefs.getString("codigo_usuario", "000000")

        textNombre.text = "Nombre: $nombre"
        textCodigo.text = "Código: $codigo"

        val detallesArray = resources.getStringArray(R.array.motivos_pqr)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, detallesArray)
        autoDetalles.setAdapter(adapter)
        autoDetalles.threshold = 0

        autoDetalles.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) autoDetalles.showDropDown()
        }
        autoDetalles.setOnClickListener {
            autoDetalles.showDropDown()
        }

        autoDetalles.setOnItemClickListener { _, _, position, _ ->
            val item = detallesArray[position]
            editOtroDetalle.visibility = if (item == "Otros (especificar)") View.VISIBLE else View.GONE
        }

        btnEnviar.setOnClickListener {
            val motivo = editMotivo.text.toString().trim()
            val seleccionado = autoDetalles.text.toString().trim()
            val detalle = if (seleccionado == "Otros (especificar)") {
                editOtroDetalle.text.toString().trim()
            } else seleccionado

            if (motivo.isEmpty() || detalle.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val mensaje = "*PQR desde la App VConexion*\n\n*Nombre:* $nombre\n*Código:* $codigo\n*Motivo:* $motivo\n*Detalles:* $detalle"
            val uri = Uri.parse("https://wa.me/573173369779?text=" + URLEncoder.encode(mensaje, "UTF-8"))
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        // Configurar el botón de regreso
        val backButton: ImageButton = view.findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Configurar botón físico "atrás"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
