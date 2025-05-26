package com.example.vconexionsas.notificacion

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.vconexionsas.R

class NotificacionesFragment : Fragment() {

    private lateinit var switchNotificaciones: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notificaciones, container, false)

        switchNotificaciones = view.findViewById(R.id.switchNotificaciones)
        val btnVolver: Button = view.findViewById(R.id.btnVolver)

        val masterKeyAlias = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            requireContext(),
            "notificaciones",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val activadas = prefs.getBoolean("activadas", false)
        switchNotificaciones.isChecked = activadas

        if (activadas) {
            NotificacionScheduler.programarNotificacionMensual(requireContext())
            Log.d("NotificacionesFragment", "✅ Notificación reprogramada automáticamente al iniciar")
        }

        switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("activadas", isChecked).apply()

            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Toast.makeText(
                            context,
                            "Debes permitir alarmas exactas en Configuración",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                        switchNotificaciones.isChecked = false
                        prefs.edit().putBoolean("activadas", false).apply()
                        return@setOnCheckedChangeListener
                    }
                }
                NotificacionScheduler.programarNotificacionMensual(requireContext())
                Toast.makeText(requireContext(), "Notificación mensual activada", Toast.LENGTH_SHORT).show()
            } else {
                NotificacionScheduler.cancelarNotificacion(requireContext())
                Toast.makeText(requireContext(), "Notificación mensual desactivada", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}



