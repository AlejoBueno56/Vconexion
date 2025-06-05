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
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.vconexionsas.R
import com.example.vconexionsas.utils.ContactoUtils
import com.example.vconexionsas.utils.VersionUtils
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.picasso.Picasso

class InicioFragment : Fragment() {

    private lateinit var tituloPromo: TextView
    private lateinit var descripcionPromo: TextView
    private lateinit var imagenPromo: ImageView
    private val db = FirebaseFirestore.getInstance()
    private var promoListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

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
            navController.navigate(R.id.action_home_to_contrasena)
        }

        view.findViewById<MaterialCardView>(R.id.conocenosCard).setOnClickListener {
            navController.navigate(R.id.action_home_to_conocenos)
        }

        view.findViewById<MaterialCardView>(R.id.SoporteTecnicoCard).setOnClickListener {
            navController.navigate(R.id.action_home_to_pqr)
        }

        view.findViewById<MaterialCardView>(R.id.LineaComercialCard).setOnClickListener {
            val numero = ContactoUtils.obtenerNumeroWhatsapp(
                requireContext(),
                ContactoUtils.TipoContacto.COMERCIAL
            )
            abrirChatWhatsapp(requireContext(), numero)
        }

        view.findViewById<MaterialCardView>(R.id.LineaFacturacionCard).setOnClickListener {
            val numero = ContactoUtils.obtenerNumeroWhatsapp(
                requireContext(),
                ContactoUtils.TipoContacto.FACTURACION
            )
            abrirChatWhatsapp(requireContext(), numero)
        }
        view.findViewById<MaterialCardView>(R.id.trasladoCard).setOnClickListener {
            navController.navigate(R.id.action_home_to_traslado)
        }
        view.findViewById<MaterialCardView>(R.id.coberturaCard).setOnClickListener {
            navController.navigate(R.id.action_home_to_cobertura)
        }

        val container = view.findViewById<View>(R.id.inicioContainer)
        container.alpha = 0f
        container.animate().alpha(1f).setDuration(400).start()

        val version = VersionUtils.getAppVersion(requireContext())
        val versionTextView = view.findViewById<TextView>(R.id.textVersion)
        versionTextView.text = "Versión: $version"

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
        val masterKeyAlias = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            requireContext(),
            "secure_user_prefs",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val sede = prefs.getString("sede", "Chitaga") ?: "Chitaga"

        val documentoPromocion = when (sede.lowercase()) {
            "pamplona" -> "promo_pamplona"
            "toledo" -> "promo_toledo"
            else -> "promo_actual"
        }

        promoListener = db.collection("promociones").document(documentoPromocion)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error al obtener promoción para $sede: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot == null || !snapshot.exists()) {
                    Log.w("Firestore", "Documento $documentoPromocion no existe o está vacío.")
                    return@addSnapshotListener
                }

                val titulo = snapshot.getString("titulo") ?: ""
                val descripcion = snapshot.getString("descripcion") ?: ""
                val imagenUrl = snapshot.getString("imagenUrl") ?: ""

                Log.d(
                    "Firestore",
                    "Promo ($sede): titulo=$titulo, descripcion=$descripcion, imagenUrl=$imagenUrl"
                )

                tituloPromo.text = titulo
                descripcionPromo.text = descripcion

                if (imagenUrl.isNotEmpty()) {
                    Picasso.get().load(imagenUrl)
                        .into(imagenPromo, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                Log.d("Picasso", "Imagen cargada correctamente.")
                            }

                            override fun onError(e: Exception?) {
                                Log.e("Picasso", "Error al cargar imagen: ${e?.message}")
                            }
                        })
                } else {
                    Log.w("Picasso", "URL de imagen vacía.")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        promoListener?.remove()
        promoListener = null
    }
}






