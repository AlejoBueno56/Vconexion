package com.example.vconexionsas.home.contrasena.onus

import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CredencialesDropdown(modelo: String, webViewRef: WebView?) {
    var visible by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    val cred = when (modelo) {
        "ADC" -> arrayOf("admin", "admin", "username", "password", "byId")
        "EASY4link" -> arrayOf("admin", "1234", "username", "password", "byId")
        "LATIC" -> arrayOf("admin", "admin", "username", "password", "byName")
        "ZTE_China" -> arrayOf("admin", "admin", "Username", "Password", "byName")
        "ZC" -> arrayOf("user", "120o605u", "username", "password", "byId")
        else -> emptyArray()
    }

    if (modelo == "TPLINK") {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8)),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "TP-Link - Rellenar contraseña",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF265CAF))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    webViewRef?.evaluateJavascript(
                        "javascript:(function() { var pwdField = document.getElementById('password'); if(pwdField){ pwdField.value='770202'; } })()",
                        null
                    )
                }) {
                    Text("Rellenar Contraseña")
                }
            }
        }
    } else if (cred.isNotEmpty()) {
        val (usuario, clave, userField, passField, method) = cred

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { visible = !visible }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Información",
                    tint = Color(0xFF265CAF),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = if (visible) "Ocultar credenciales" else "Mostrar credenciales",
                    color = Color(0xFF265CAF),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                )
            }

            AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8)),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Acceso rápido",
                            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF265CAF))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Usuario: $usuario", color = Color(0xFF333333), fontSize = 14.sp)
                        Text("Contraseña: $clave", color = Color(0xFF333333), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val js = when (method) {
                                    "byName" -> "(function() { var userField = document.getElementsByName('$userField')[0]; var passField = document.getElementsByName('$passField')[0]; if (userField && passField) { userField.value = '$usuario'; passField.value = '$clave'; } })()"
                                    else -> "(function() { var userField = document.getElementById('$userField'); var passField = document.getElementById('$passField'); if (userField && passField) { userField.value = '$usuario'; passField.value = '$clave'; } })()"
                                }
                                webViewRef?.evaluateJavascript("javascript:$js", null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF265CAF))
                        ) {
                            Text("Autocompletar Credenciales", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
