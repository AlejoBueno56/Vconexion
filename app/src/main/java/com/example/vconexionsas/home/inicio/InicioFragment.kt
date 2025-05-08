package com.example.vconexionsas.home.inicio

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vconexionsas.R
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class InicioFragment : Fragment() {

    private lateinit var tituloPromo: TextView
    private lateinit var descripcionPromo: TextView
    private lateinit var imagenPromo: ImageView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        // Referencias a los elementos de promoción
        tituloPromo = view.findViewById(R.id.textoTituloPromo)
        descripcionPromo = view.findViewById(R.id.textoDescripcionPromo)
        imagenPromo = view.findViewById(R.id.imagenPromocion)

        escucharCambiosPromocion()

        val navController = findNavController()

        view.findViewById<MaterialCardView>(R.id.planesCard).setOnClickListener {
            navController.navigate(R.id.action_home_to_plan)
        }

        view.findViewById<MaterialCardView>(R.id.speedTestCard).setOnClickListener {
            navController.navigate(R.id.action_home_to_speedTestFragment)
        }

        view.findViewById<MaterialCardView>(R.id.serviciosCard).setOnClickListener {
            navController.navigate(R.id.action_home_to_servicios)
        }

        view.findViewById<MaterialCardView>(R.id.facturaCard).setOnClickListener {
            navController.navigate(R.id.action_home_to_factura)
        }
        view.findViewById<MaterialCardView>(R.id.cambioClaveCard).setOnClickListener {
            navController.navigate(R.id.action_contrasenaFragment_to_fragmentPruebaDetectarONU)
        }
        view.findViewById<MaterialCardView>(R.id.conocenosCard).setOnClickListener {
            navController.navigate(R.id.action_home_to_conocenos)
        }
        view.findViewById<MaterialCardView>(R.id.SoporteTecnicoCard).setOnClickListener {
            navController.navigate(R.id.action_home_to_pqr)
        }

        view.findViewById<MaterialCardView>(R.id.LineaComercialCard).setOnClickListener {
            abrirChatWhatsapp(requireContext(), "+573024538585")
        }

        view.findViewById<MaterialCardView>(R.id.LineaFacturacionCard).setOnClickListener {
            abrirChatWhatsapp(requireContext(), "+573164116348")
        }

        return view
    }

    private fun abrirChatWhatsapp(context: Context, numero: String) {
        val url = "https://wa.me/${numero.replace("+", "")}"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    private fun escucharCambiosPromocion() {
        db.collection("promociones").document("promo_actual")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "❌ Error al obtener promoción: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot == null || !snapshot.exists()) {
                    Log.w("Firestore", "⚠️ Documento 'promo_actual' no existe o está vacío.")
                    return@addSnapshotListener
                }

                val titulo = snapshot.getString("titulo") ?: ""
                val descripcion = snapshot.getString("descripcion") ?: ""
                val imagenUrl = snapshot.getString("imagenUrl") ?: ""

                Log.d("Firestore", "📦 Promo recibida: titulo=$titulo, descripcion=$descripcion, imagenUrl=$imagenUrl")

                tituloPromo.text = titulo
                descripcionPromo.text = descripcion

                if (imagenUrl.isNotEmpty()) {
                    Picasso.get().load(imagenUrl)
                        .into(imagenPromo, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                Log.d("Picasso", "✅ Imagen cargada correctamente.")
                            }

                            override fun onError(e: Exception?) {
                                Log.e("Picasso", "❌ Error al cargar imagen: ${e?.message}")
                            }
                        })
                } else {
                    Log.w("Picasso", "⚠️ URL de imagen vacía.")
                }
            }
    }
}




