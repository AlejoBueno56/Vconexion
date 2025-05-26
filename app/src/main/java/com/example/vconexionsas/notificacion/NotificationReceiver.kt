package com.example.vconexionsas.notificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.vconexionsas.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationReceiver", "🟢 onReceive() ejecutado")
        Toast.makeText(context, "Recordatorio enviado", Toast.LENGTH_SHORT).show()

        val canalId = "canal_factura"
        val canalNombre = "Recordatorio de Factura"
        val notificationId = 105

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                canalId,
                canalNombre,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para recordar el pago de la factura cada mes"
            }
            notificationManager.createNotificationChannel(canal)
        }

        val builder = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📅 Recordatorio de pago")
            .setContentText("Hoy es 5 del mes. ¡Recuerda pagar tu factura!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}

