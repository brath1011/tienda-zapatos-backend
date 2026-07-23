# ☕ UTPShop - Backend (Spring Boot)

Este es el backend de UTPShop, una API REST robusta construida con Java 25, Spring Boot 3 y Maven.

## 🛠️ Arquitectura y Tecnologías
* **Base de Datos:** PostgreSQL en la nube de **Supabase** (o base de datos PostgreSQL local).
* **Gestión de Medios:** Integración con **Cloudinary API** para la carga, optimización y almacenamiento seguro de imágenes de los productos.
* **Seguridad:** Autenticación y autorización basada en Roles con **JWT (JSON Web Tokens)**.
* **Notificaciones:** Envío automatizado de boletas de pago electrónicas en formato PDF mediante **Gmail SMTP**.
* **Tiempo Real:** Notificación y actualización de despachos en tiempo real para repartidores usando **WebSockets (STOMP)**.

## 💾 Configuración de la Base de Datos (PostgreSQL)

Para que el proyecto funcione en tu entorno local, tienes dos opciones para la base de datos:

### Opción A: Usar la Base de Datos de Supabase en la Nube (Recomendado)
El proyecto ya viene preconfigurado en local para conectarse directamente a la base de datos en la nube de Supabase. No requieres instalar una base de datos local ni realizar migraciones.

### Opción B: Usar una Base de Datos PostgreSQL Local
Si prefieres correr una base de datos en tu computadora:
1. Asegúrate de tener instalado **PostgreSQL** y corriendo en el puerto `5432`.
2. Crea una base de datos vacía llamada `desarrolloweb`.
3. Restaura las tablas e información semilla importando el archivo SQL ubicado en la ruta:
   `src/main/resources/database_dump.sql`.
4. Edita el archivo `src/main/resources/application-local.properties` para actualizar la URL del datasource apuntando a tu PostgreSQL local:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/desarrolloweb
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   ```

---

## 🚀 Cómo correr el proyecto en Local

### Requisitos previos:
* **Java 25** instalado.
* **Maven** instalado.

### Pasos de ejecución:
1. Verifica que tus credenciales de base de datos, Cloudinary y Gmail SMTP en `src/main/resources/application-local.properties` sean correctas.
2. Abre tu terminal en la carpeta del backend:
   ```bash
   cd tienda-zapatos-backend
   ```
3. Ejecuta el comando de compilación e inicio:
   ```bash
   mvn spring-boot:run
   ```
4. El servidor iniciará en el puerto `8090`. Puedes validar que el servidor está operativo abriendo:
   [http://localhost:8090/api/estado](http://localhost:8090/api/estado).
