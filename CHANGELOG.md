# Changelog - VConexionUsuarios App

## [2.0.0] - 2025-05-09
### Añadido
- Recepción de notificaciones en tiempo real mediante Firebase Cloud Messaging (FCM)
- Mejoras visuales en los reportes de instalaciones, retiros y pagos (tarjetas, filtros automáticos, diseño empresarial)
- Exportación de reportes a PDF y Excel desde la app (incluye firma e imágenes)
- Visualización de facturación: plan, megas, estado, fecha de carga y valor
- Descarga de factura PDF desde la app y vista de histórico por mes y año

### Corregido
- Manejo de firma vacía y errores en reportes de retiros
- Problemas de visualización con filtros en pantallas de reportes
- Compatibilidad con Kotlin 2.0 y Jetpack Compose

---

## [1.2.0] - 2025-04-24
### Añadido
- Consulta de facturación desde API Node.js y PHP
- Generación de factura PDF con diseño corporativo usando TCPDF
- Almacenamiento de datos de sesión (código, nombre, correo, token)

---

## [1.1.0] - 2025-04-10
### Añadido
- Pantalla de perfil con nombre, correo y avatar del usuario
- Navegación con fragments desde el menú principal
- Interfaz moderna para reportes de instalación, retiro y traslado

### Mejorado
- Persistencia de sesión segura usando SharedPreferences
- Compatibilidad con Firebase y configuración inicial

---

## [1.0.0] - 2025-03-20
### Añadido
- Login de usuario con validación por API (PHP y Google Sheets)
- Navegación básica y almacenamiento de sesión
- Conexión a APIs desde Android con Volley y Retrofit

### Base
- Inicio de proyecto Android con Jetpack Compose y Kotlin DSL
- Comunicación con servidor y bases de datos MySQL
