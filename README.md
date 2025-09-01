# SGRH - Sistema de Gestión de Recursos Humanos

Autor: Alexis Gonzalez Sogamoso
Evidencia: APK (Desarrollar módulos móvil según requerimientos del proyecto) GA8-220501096-AA2-EV02

Este proyecto corresponde al desarrollo de una aplicación móvil (APK) creada en 
**Android Studio** como parte del sistema de gestión de recursos humanos (**SGRH**). 
La app permite a empleados, gerentes, supervisores y el área de RRHH interactuar con el sistema de manera remota.

---

## 🚀 Tecnologías utilizadas

- **Android Studio (Kotlin/Jetpack Compose)** → Desarrollo de la aplicación móvil.
- **Node.js + Express** → Backend para la gestión de peticiones.
- **MongoDB** → Base de datos NoSQL para almacenar la información del sistema (empleados, asistencias, nómina, permisos, reportes, etc.).
- **Cloudflared** → Exposición segura del servidor local para pruebas y conexión de la app móvil al backend.
- **Retrofit** → Cliente HTTP en Android para la comunicación con el backend.
- **Mongoose** → ODM para gestionar la conexión con MongoDB desde Node.js.

---

## 📂 Estructura del proyecto

- `app/` → Código fuente de la aplicación móvil en Android Studio.
- `backend/` → Servidor Node.js con Express y controladores.
- `database/` → Modelos y esquemas de MongoDB (Mongoose).
- `README.md` → Documentación del proyecto.

---

## Funcionalidades principales

- **Autenticación y roles** → Login diferenciado para empleados, RRHH, supervisores y gerentes.  
- **Gestión de empleados** → Registro, edición y consulta de información.  
- **Gestión de asistencias** → Control de entradas y salidas de empleados.  
- **Gestión de permisos** → Solicitudes, aprobación y rechazo en la misma interfaz.  
- **Reportes e informes** → Generación y consulta de reportes del personal.  
- **Configuración del sistema** → Edición de datos de la empresa y ajustes internos.  

---

## Conexión de la app con el backend

La aplicación se conecta al servidor Node.js mediante **Cloudflared**, 
lo que permite exponer el backend local a través de un túnel seguro sin necesidad de abrir puertos.

Ejemplo de configuración en `RetrofitClient.kt`:

```kotlin
private const val BASE_URL = "https://<tu-subdominio>.trycloudflare.com/"