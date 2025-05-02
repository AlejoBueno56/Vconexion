@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.vconexionsas.home.contrasena.onus

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.vconexionsas.R

@Composable
fun ZTE_CScreen(onBack: () -> Unit) {
    // Estado para controlar si se muestra el DropdownMenu
    var expanded by remember { mutableStateOf(false) }
    // Referencia al WebView para poder inyectar JavaScript
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ZTE_C") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                // Usamos fondo azul y título blanco, igual que en ADC
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // WebView que carga la página de EASY4LINK
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        webViewRef = this
                        // Hacemos el WebView transparente para que se vea el fondo si lo hay
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        settings.textZoom = 150
                        setInitialScale(150)
                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                url?.let { view?.loadUrl(it) }
                                return true
                            }
                        }
                        loadUrl("http://192.168.1.1")
                    }
                }
            )
            // Botón desplegable en la esquina inferior izquierda (similar al de ADC)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .zIndex(100f)
                    .background(Color.Transparent)
            ) {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Mostrar credenciales",
                        tint = Color.Blue
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(240.dp)
                        .background(Color(0xFFB3E5FC))
                ) {
                    // Muestra las credenciales (solo de visualización)
                    DropdownMenuItem(
                        text = {
                            Text(
                                "User: admin",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF265CAF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        },
                        onClick = { }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Pwd: admin",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF265CAF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        },
                        onClick = { }
                    )
                    // Opción de autocompletar
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Autocompletar",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF265CAF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        },
                        onClick = {
                            // Inyectamos JavaScript para rellenar los campos con name="Username" y name="Password"
                            webViewRef?.evaluateJavascript(
                                "(function() {" +
                                        "var userField = document.getElementsByName('Username')[0];" +
                                        "var passField = document.getElementsByName('Password')[0];" +
                                        "if (userField && passField) {" +
                                        "    userField.value = 'admin';" +
                                        "    passField.value = 'admin';" +
                                        "}" +
                                        "})()",
                                null
                            )
                            expanded = false
                        }
                    )

                }
            }
        }
    }
}
