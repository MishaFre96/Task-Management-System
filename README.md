# ProgramaTaskManagementSystem

API REST para un sistema de gestión de tareas desarrollada con Spring Boot, Spring Security y JPA. Incluye funcionalidades de registro de usuarios, autenticación con tokens, gestión de tareas, asignación de tareas, cambio de estado, comentarios y filtros. Proyecto enfocado en el aprendizaje de desarrollo backend con Java y buenas prácticas básicas.

## Estructura del proyecto

- src/main/java/taskmanagement/model/ -> Entidades (User, Task, Token, Comment)
- src/main/java/taskmanagement/repository/ -> Repositorios JPA
- src/main/java/taskmanagement/service/ -> Lógica de negocio
- src/main/java/taskmanagement/controller/ -> Controladores REST
- src/main/java/taskmanagement/config/ -> Configuración de seguridad y filtros
- src/main/java/taskmanagement/dto/ -> Objetos de transferencia de datos
- src/main/resources/application.properties -> Configuración de la base de datos H2 y Actuator


## Fase 1 – Registro y autenticación de usuarios

En esta primera fase se ha implementado la base del sistema de gestión de tareas, centrada en la creación y autenticación de usuarios.

- Se ha desarrollado un endpoint POST /api/accounts que permite registrar nuevos usuarios proporcionando email y contraseña. Antes de almacenar los datos, se realizan validaciones básicas: comprobar que no haya campos vacíos, que el email tenga un formato mínimo (una expresión regular simple), que la contraseña tenga al menos 6 caracteres y que el email no esté ya registrado (sin distinguir mayúsculas/minúsculas, guardando siempre el email en minúsculas).
- Las contraseñas se almacenan de forma segura utilizando el cifrado BCrypt.
- Se ha configurado Spring Security para proteger la aplicación. El endpoint de registro es público, mientras que el resto de endpoints requieren autenticación mediante Basic Auth. También se han habilitado los endpoints necesarios para los tests, como /actuator/shutdown.
- Se ha preparado la estructura del proyecto con Spring Boot, JPA y una base de datos H2 persistente, sentando las bases para las siguientes fases.

## Fase 2 – Gestión de tareas (creación, listado y filtrado)

En esta segunda fase se ha ampliado el sistema para que los usuarios autenticados puedan crear y visualizar tareas.

- Se ha creado el endpoint POST /api/tasks que recibe un JSON con title y description. Se valida que no sean nulos ni vacíos; en caso contrario se responde con 400 BAD REQUEST. Si la petición es válida, se guarda la tarea en la base de datos con estado CREATED, la fecha de creación (para poder ordenar posteriormente) y el email del autor en minúsculas. La respuesta incluye un objeto JSON con id, title, description, status y author.
- Se ha modificado el endpoint GET /api/tasks para que devuelva un array con todas las tareas ordenadas de más reciente a más antigua (usando la fecha de creación). Además, acepta un parámetro opcional ?author=email que filtra las tareas de un usuario concreto (sin distinguir mayúsculas/minúsculas). Si no hay tareas, se devuelve un array vacío.
- La seguridad sigue activa: ambos endpoints requieren autenticación mediante Basic Auth. Si un usuario no autenticado intenta acceder, obtiene un 401 UNAUTHORIZED.
- Para garantizar el orden cronológico, se añadió un campo createdAt a la entidad Task (no se muestra en las respuestas JSON, pero se usa para ordenar las consultas).
- Los datos se almacenan en la misma base de datos H2 persistente, y la estructura del proyecto se ha organizado en capas (controlador, servicio, repositorio, DTO), siguiendo las buenas prácticas de Spring Boot.

Con esta fase, los usuarios ya pueden gestionar sus tareas y consultar las de otros compañeros, todo ello protegido por autenticación.

## Fase 3 – Autenticación con tokens (Bearer Token)

En esta fase se ha mejorado el sistema de autenticación para que los usuarios no tengan que enviar su email y contraseña en cada petición, sino solo una vez para obtener un token de acceso.

- Se ha creado el endpoint POST /api/auth/token. El usuario debe autenticarse mediante Basic Auth (email y contraseña). Si las credenciales son correctas, el servidor genera un token único (un UUID) y lo guarda en la base de datos junto con una fecha de expiración (24 horas). El token se devuelve en formato JSON: { "token": "..." }. Si las credenciales son incorrectas, se responde con 401 Unauthorized.
- Para el resto de endpoints protegidos (por ejemplo, GET /api/tasks o POST /api/tasks), el cliente debe incluir el token en la cabecera Authorization: Bearer <token>. Un filtro personalizado (BearerTokenAuthenticationFilter) intercepta cada petición, extrae el token, lo valida (comprueba que exista en la base de datos, que no esté caducado y que no esté revocado) y, si es válido, autentica al usuario en Spring Security. De este modo, el usuario puede acceder a los recursos sin necesidad de repetir sus credenciales.
- La seguridad se ha configurado para que el endpoint /api/auth/token sea accesible únicamente con Basic Auth (es decir, requiere autenticación), mientras que el resto de endpoints requieren el token Bearer. El registro de usuarios (/api/accounts) sigue siendo público.
- Los tokens se almacenan en la base de datos H2 mediante una nueva entidad Token, relacionada con el usuario. Se ha incluido un método para limpiar periódicamente los tokens expirados (aunque no es necesario para los tests).

Esta fase mejora la experiencia de usuario y la seguridad, acercando el proyecto a un estándar profesional de autenticación con tokens, pero manteniendo una implementación sencilla y comprensible (tokens opacos en lugar de JWT).

## Fase 4 – Asignación de tareas, cambio de estado y filtros avanzados

En esta cuarta fase se ha mejorado la gestión de tareas permitiendo asignarlas a otros usuarios, cambiar su estado y filtrar por persona asignada.

- Se ha modificado el endpoint POST /api/tasks para que en la respuesta incluya el campo assignee con el valor "none" (ya que la tarea recién creada no tiene asignado).
- Se ha añadido el endpoint PUT /api/tasks/{taskId}/assign que recibe un JSON con { "assignee": <email|"none"> }. Solo el autor de la tarea puede asignarla o desasignarla. El servidor valida que el email de destino pertenezca a un usuario registrado (o que sea "none"). Si la tarea no existe, el email no es válido o el usuario no es el autor, se devuelven los códigos de error correspondientes (404, 400, 403).
- Se ha creado el endpoint PUT /api/tasks/{taskId}/status que acepta un JSON con { "status": "CREATED" | "IN_PROGRESS" | "COMPLETED" }. Pueden cambiar el estado el autor de la tarea o la persona asignada (si la tiene). Si el estado no es uno de los tres valores permitidos, se responde con 400 BAD REQUEST. Si el usuario no está autorizado, se devuelve 403 FORBIDDEN. En caso de éxito, se responde con la tarea actualizada.
- Se ha mejorado el endpoint GET /api/tasks para que acepte un nuevo parámetro opcional ?assignee=email (insensible a mayúsculas). Además, se pueden combinar los filtros author y assignee para obtener tareas que cumplan ambas condiciones (intersección). Si no se proporcionan filtros, se devuelven todas las tareas ordenadas por fecha descendente (como antes). Las respuestas JSON incluyen ahora el campo assignee.
- Para garantizar la integridad de los estados, se ha definido un enum TaskStatus con los valores CREATED, IN_PROGRESS y COMPLETED. Esto evita errores de escritura y hace el código más robusto.
- Los datos se siguen almacenando en la base de datos H2, manteniendo la estructura de capas y la seguridad con tokens Bearer (o Basic Auth para ciertos endpoints).

Con esta fase, los usuarios pueden asignar tareas a otros compañeros, hacer seguimiento del progreso (cambiando estados) y filtrar tareas por persona asignada, mejorando la colaboración y la organización.

## Fase 5 – Comentarios en las tareas

En esta quinta y última fase se ha añadido la posibilidad de que cualquier usuario autenticado pueda comentar las tareas y ver todos los comentarios de una tarea concreta. Además, en el listado de tareas ahora aparece el número total de comentarios que tiene cada una.

- Se ha creado el endpoint POST /api/tasks/{taskId}/comments que recibe un JSON con { "text": "..." }. El texto no puede estar vacío; si lo está, se responde con 400 BAD REQUEST. Si la tarea no existe, se devuelve 404 NOT FOUND. Solo los usuarios autenticados pueden comentar (de lo contrario 401 UNAUTHORIZED). Cuando todo es correcto, se guarda el comentario en la base de datos con el email del usuario que lo escribe y la fecha de creación, y se responde con 200 OK (sin cuerpo).
- Se ha creado el endpoint GET /api/tasks/{taskId}/comments que devuelve un array con todos los comentarios de la tarea, ordenados de más nuevo a más antiguo. Cada comentario incluye id, task_id (el identificador de la tarea a la que pertenece), text y author (email del usuario que lo escribió). Si la tarea no existe, responde con 404 NOT FOUND. También requiere autenticación (401 si no se está logueado).
- Se ha modificado el endpoint GET /api/tasks para que cada tarea incluya un nuevo campo total_comments con el número de comentarios que tiene (un número entero). El resto de campos (id, title, description, status, author, assignee) se mantienen igual, y los filtros por author y assignee siguen funcionando como antes.
- Para implementar esto, se ha creado una nueva entidad Comment relacionada con Task (cada comentario pertenece a una tarea) y con User (cada comentario tiene un autor). También se ha creado su repositorio CommentRepository, el servicio CommentService y el controlador CommentController. En TaskService se ha añadido un método para obtener el número de comentarios de una tarea sin hacer consultas extra ineficientes.

Con esta fase, el sistema de gestión de tareas queda completo, permitiendo a los usuarios no solo crear y asignar tareas, sino también comentarlas y saber cuántos comentarios tienen, todo ello manteniendo la seguridad por tokens y la organización en capas.
