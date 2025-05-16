# 📱 VConexionUsuarios App

Aplicación móvil oficial para clientes de **VConexionSAS**, desarrollada en Android Studio (Kotlin + Jetpack Compose). Permite a los usuarios consultar reportes de instalación, pagos, retiros, ver detalles de facturación y recibir notificaciones en tiempo real.

---

## 🚀 Funcionalidades principales

- Inicio de sesión seguro con validación por API
- Autenticación por cédula y contraseña
- Consulta de facturación
- Descarga y envío de facturas en PDF
- Cambio de contraseña
- Recuperación de acceso por correo con token
- Notificaciones push con FCM
- Gestión de perfil con imagen personalizada
- Contacto directo vía WhatsApp según sede
- Historial de facturación con filtro por mes y año
- Recepción de notificaciones en tiempo real vía Firebase

---

## 🛠️ Tecnologías utilizadas

- **Android Studio** con Jetpack Compose
- **Kotlin 2.0 + Gradle Kotlin DSL**
- **Volley / Retrofit** para APIs
- **Firebase Cloud Messaging (FCM)**
- **PHP** para backend
- **TCPDF** (para PDF)
- **SharedPreferences** para sesión segura

---

## 📌 Historial de cambios

Consulta el historial completo en el archivo [CHANGELOG.md](CHANGELOG.md)

---

## 🔐 Seguridad

- Uso de `EncryptedSharedPreferences` para almacenar de forma segura las credenciales del usuario (token, correo, cédula).
- Autenticación mediante `Bearer Token` en todas las llamadas API sensibles (como facturación y recuperación de contraseña).
- Envío cifrado de datos comocontraseñas usando HTTPS.
- Gestión de sesiones persistentes con expiración automática del token.


---

## 📄 Licencia

Desarrollado por y para **VConexionSAS**. Uso exclusivo de clientes. Todos los derechos reservados.
