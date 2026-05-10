# Task Management System

API REST para un sistema de gestión de tareas.  
Permite registrar usuarios, autenticarse con Basic Auth o token Bearer, crear y asignar tareas, cambiar estados, comentar y filtrar.  
Proyecto desarrollado como parte del track de **Spring Boot** en [Hyperskill](https://hyperskill.org/).

## 🚀 Funcionalidades principales

- ✅ Registro de usuarios con validación manual de email y contraseña  
- 🔐 Autenticación HTTP Basic y generación de token Bearer  
- 📝 CRUD de tareas con estados (CREATED, IN_PROGRESS, COMPLETED)  
- 👥 Asignación y desasignación de tareas a otros usuarios  
- 💬 Comentarios en tareas  
- 🔍 Filtros por autor y/o persona asignada  
- 📄 Listados ordenados por fecha de creación  
- ⚠️ Manejo centralizado de errores con `@ControllerAdvice`

## 🛠️ Tecnologías

- Java 17+
- Spring Boot (Web, Data JPA, Security, Actuator)
- Spring Security (Basic Auth + filtro Bearer Token)
- H2 Database (archivo local)
- Gradle
- JUnit 5
