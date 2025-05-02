@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.vconexionsas.home.contrasena.onus

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun TplinkScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var currentUrl by remember { mutableStateOf("http://192.168.1.1") }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var webViewRef: WebView? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            // Barra superior similar a la de un navegador
            TopAppBar(
                title = {
                    // Muestra la URL actual
                    Text(text = currentUrl)
                },
                navigationIcon = {
                    // Botón para cerrar el navegador y volver atrás en la app
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Cerrar navegador"
                        )
                    }
                },
                actions = {
                    // Botones de navegación
                    IconButton(
                        onClick = { webViewRef?.goBack() },
                        enabled = canGoBack
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                    IconButton(
                        onClick = { webViewRef?.goForward() },
                        enabled = canGoForward
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "Adelante"
                        )
                    }
                    IconButton(
                        onClick = { webViewRef?.reload() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Recargar"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.LightGray,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Botón para inyectar la contraseña en el formulario de TP-Link
            Button(
                onClick = {
                    // Inyecta JavaScript para rellenar el campo con id "password"
                    webViewRef?.evaluateJavascript(
                        "javascript:(function() { " +
                                "var pwdField = document.getElementById('password'); " +
                                "if(pwdField){ pwdField.value='770202'; }" +
                                "})()", null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Rellenar Contraseña")
            }
            Divider(color = Color.Gray)
            // Contenedor del WebView sin ajustes forzados de escalado
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        // Se desactivan las opciones de escalado forzado para mostrar la interfaz completa
                        settings.loadWithOverviewMode = false
                        settings.useWideViewPort = false
                        settings.domStorageEnabled = true

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                // Actualiza la URL actual y el estado de navegación
                                currentUrl = url ?: currentUrl
                                canGoBack = canGoBack()
                                canGoForward = canGoForward()
                            }
                        }
                        loadUrl(currentUrl)
                        webViewRef = this
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
