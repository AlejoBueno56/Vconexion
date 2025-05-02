@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.vconexionsas.home.contrasena.onus

import android.graphics.Color as AndroidColor
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun AdcScreen(onBack: () -> Unit) {
    // Estado para desplegar el DropdownMenu
    var expanded by remember { mutableStateOf(false) }
    // Referencia al WebView
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    // Manejo del botón Atrás del sistema para volver al fragmento anterior


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ADC") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF265CAF),
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

            // WebView configurado con fondo transparente para que se vea la imagen
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        webViewRef = this
                        setBackgroundColor(AndroidColor.TRANSPARENT)
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        settings.textZoom = 150
                        setInitialScale(150)
                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                // Inyecta CSS para fijar la imagen de fondo (opcional)
                                view?.evaluateJavascript(
                                    "javascript:(function() {" +
                                            "document.body.style.backgroundImage = 'url(\"file:///android_asset/fondo_adc.png\")';" +
                                            "document.body.style.backgroundRepeat = 'no-repeat';" +
                                            "document.body.style.backgroundSize = 'cover';" +
                                            "document.body.style.backgroundAttachment = 'fixed';" +
                                            "})()",
                                    null
                                )
                            }
                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                url?.let { view?.loadUrl(it) }
                                return true
                            }
                        }
                        loadUrl("http://192.168.1.1")
                    }
                }
            )
            // Botón desplegable en la esquina inferior izquierda con alta prioridad de dibujo
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 16.dp)
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
                    DropdownMenuItem(
                        text = {
                            Text(
                                "user name: admin",
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
                                "password: admin",
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
                                "Autocompletar",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF265CAF),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        },
                        onClick = {
                            webViewRef?.evaluateJavascript(
                                "javascript:(function() {" +
                                        "var userField = document.getElementById('username');" +
                                        "var passField = document.getElementById('password');" +
                                        "if(userField && passField) {" +
                                        "userField.value = 'admin'; passField.value = 'admin';" +
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
