# Trabajo Practico 4 - Bases de Datos NoSQL con Redis

## Sección 1 - Introducción a Redis

### 1.1

#### 1.​ ¿Qué tipo de base de datos es Redis? ¿En qué se diferencia de una base de datos relacional y de otras bases de datos NoSQL como MongoDB?

- **Frente a las relacionales (RDBMS):** A diferencia del modelo de tablas y filas con esquemas rígidos de SQL, Redis utiliza un modelo simple de llaves y valores sin esquema fijo. Además, al ser una base de datos en memoria, ofrece una velocidad mucho mayor para casos específicos como el almacenamiento de sesiones o caché, mientras que las RDBMS están diseñadas para la persistencia en disco y transacciones complejas.
- **Frente a MongoDB:** MongoDB es una base de datos orientada a documentos donde el contenido (JSON/BSON) es "examinable" por el motor, permitiendo consultas internas e índices secundarios. En cambio, en Redis el valor es generalmente un bloque "opaco" al que se accede solo por su clave, aunque admite estructuras de datos específicas como listas, conjuntos o hashes.

#### 2.​ ¿Dónde almacena los datos Redis? ¿Qué implicancias tiene esto en términos de velocidad y de persistencia?

- Redis almacena los datos principalmente en la memoria RAM
- **Velocidad**: Al eliminar la necesidad de acceder al disco físico (I/O), ofrece una respuesta extremadamente rápida, lo que la hace ideal para procesar datos en tiempo real, actuar como caché o gestionar sesiones de usuario.
- **Persistencia**: Al ser una base de datos en memoria, los datos son volátiles y podrían perderse si el servidor falla o se reinicia. Para mitigar esto, Redis ofrece mecanismos de respaldo opcionales como RDB (instantáneas del conjunto de datos a intervalos específicos) y AOF (un registro de cada operación de escritura recibida).

#### 3.​ ¿Qué tipos de datos soporta Redis? Listar y describir brevemente cada uno.

- **Strings**: Es el tipo más básico. Pueden almacenar texto (como nombres de usuario) o valores numéricos que permiten operaciones de incremento y decremento.
- **Listas (Lists)**: Colecciones de strings ordenadas por el orden de inserción. Permiten agregar o eliminar elementos tanto por el extremo izquierdo como por el derecho.
- **Conjuntos (Sets)**: Colecciones no ordenadas de elementos únicos. Son ideales para realizar operaciones de lógica de conjuntos como unión, intersección y diferencia.
- **Conjuntos Ordenados (Sorted Sets)**: Similares a los conjuntos, pero cada elemento tiene un puntaje (score) asociado que se utiliza para mantenerlos ordenados. Son útiles para rankings o tablas de clasificación.
- **Hashes**: Mapas de campos y valores, perfectos para representar objetos (como un perfil de usuario con múltiples atributos) dentro de una sola clave.
Geospatial: Permite almacenar coordenadas (longitud y latitud) y realizar consultas de proximidad o calcular distancias entre puntos.

#### 4.​ Enunciar las características principales de Redis.

- **Almacenamiento en memoria**: Guarda los datos primordialmente en la RAM, lo que le otorga una velocidad de respuesta extremadamente alta en comparación con bases de datos en disco.
- **Modelo Clave-Valor**: Utiliza un sistema simple donde se accede a la información a través de una clave única.
- **Servidor de Estructuras de Datos**: A diferencia de otros almacenes de clave-valor que solo guardan "bloques opacos", Redis entiende y permite manipular strings, listas, conjuntos (sets), conjuntos ordenados, hashes y datos geoespaciales.
- **Persistencia opcional**: Aunque es una base de datos en memoria, ofrece mecanismos como RDB (fotos del estado en un momento dado) y AOF (registro de todas las operaciones) para no perder datos ante un reinicio.
- **Versatilidad de roles**: Puede actuar no solo como base de datos, sino también como caché de alto rendimiento o como broker de mensajes (sistema de mensajería entre aplicaciones).
- **Soporte para Expiración**: Permite asignar un tiempo de vida (TTL) a las claves para que se eliminen automáticamente, ideal para gestionar sesiones o tokens temporales.

#### 5.​ Comparar Redis con los RDBMS: ¿en qué casos conviene usar Redis en lugar de una base de datos relacional y en cuáles no?

**¿Cuándo conviene usar Redis?**

- **Necesidad de alta velocidad**: Al ser una base de datos en memoria, Redis ofrece respuestas órdenes de magnitud más rápidas que un RDBMS basado en disco, lo que es ideal para aplicaciones en tiempo real.
- **Datos volátiles o temporales**: Es perfecto para gestionar sesiones de usuario, carritos de compras o caché, donde la velocidad es vital y la pérdida accidental de datos no es una tragedia.
- **Estructuras de datos simples**: Cuando los datos se acceden principalmente a través de una clave única y no requieren esquemas rígidos.

**¿Cuándo conviene usar un RDBMS?**

- **Relaciones complejas**: Si necesitas correlacionar datos entre múltiples entidades o realizar búsquedas complejas entre tablas, Redis (al ser clave-valor) resulta muy limitado.
- **Transacciones multi-operación**: Cuando requieres garantías ACID estrictas que abarquen varias operaciones (como transferencias bancarias), donde el fallo de una debe revertir todas las demás.
- **Consultas por contenido**: En Redis no puedes "mirar dentro" del valor fácilmente para filtrar; en un RDBMS puedes hacer consultas ad-hoc basadas en cualquier columna de forma nativa.

#### 6.​ ¿Redis tiene soporte para transacciones? ¿Cómo funcionan? ¿Qué garantías ofrecen y qué limitaciones tienen respecto de las transacciones ACID?

**¿Cómo funcionan?**

- Las transacciones en Redis permiten agrupar un conjunto de comandos para que se ejecuten como una unidad única y aislada. El proceso sigue estos pasos:
- **Inicio**: Se marca el comienzo de la transacción (usualmente con el comando MULTI).
- **Encolado**: Los comandos enviados a continuación no se ejecutan inmediatamente, sino que Redis los pone en una cola.
- **Ejecución**: Una vez enviados todos los comandos, se solicita su ejecución en bloque (usualmente con EXEC).

**Garantías que ofrecen**

- **Aislamiento**: Durante la ejecución de la transacción, Redis no atiende peticiones de otros clientes. Todos los comandos se ejecutan de forma secuencial y sin interrupciones.
- **Atomicidad a nivel de bloque**: Los comandos se ejecutan uno tras otro. Sin embargo, esta "atomicidad" tiene un matiz importante comparado con SQL.
Limitaciones frente a transacciones ACID

**La diferencia más crítica radica en el manejo de errores y la falta de Rollback:**

- **Sin Rollback**: A diferencia de los RDBMS, si un comando dentro de la transacción falla durante la ejecución (por ejemplo, operar matemáticamente sobre un string), Redis no revierte los comandos anteriores que sí tuvieron éxito. Los comandos restantes en la cola se siguen ejecutando.
- **Granularidad**: Están optimizadas para operaciones sobre una sola unidad (agregado). Si necesitas integridad absoluta en transacciones complejas que involucren múltiples claves y requieran deshacer cambios ante cualquier fallo, las bases de datos clave-valor como Redis no son la opción ideal.

#### 7.​ ¿Redis tiene persistencia? Describir los mecanismos disponibles (RDB y AOF) e indicar las diferencias entre ellos.

- **RDB (Redis Database)**: Realiza instantáneas (snapshots) de todo el conjunto de datos en disco a intervalos de tiempo específicos. Es como sacar una "foto" del estado de la base de datos en un momento dado.
- **AOF (Append Only File)**: Registra cada operación de escritura recibida por el servidor en un archivo de log. Cuando el servidor se reinicia, se vuelven a ejecutar todas las operaciones grabadas para reconstruir el estado original de los datos.

Característica | RDB (Snapshots) | AOF (Log de operaciones) |
-------------- | --------------- | ------------------------ |
Pérdida de datos | Puede perder los datos generados entre el último snapshot y el fallo. | Es más duradero, ya que registra casi cada cambio individualmente. |
Rendimiento | Muy rápido al cargar grandes volúmenes de datos en un reinicio. | Puede ser más lento al reiniciar, ya que debe "reproducir" todas las operaciones una por una. |
Tamaño | El archivo suele ser más compacto. | El archivo de log tiende a crecer mucho |

#### 8.​ ¿Cuáles son los principales casos de uso de Redis en aplicaciones reales?

**Redis es ideal para escenarios donde la baja latencia y el procesamiento en tiempo real son críticos**

- **Gestión de Sesiones**: Almacenamiento rápido de datos de sesión de usuario (tokens, estados de login) que se consultan en cada petición web.
- **Caché de alto rendimiento**: Para reducir la carga en bases de datos relacionales almacenando resultados de consultas frecuentes o fragmentos de páginas.
- **Carritos de compras**: Gestión de datos temporales de e-commerce que no necesitan persistencia inmediata en disco hasta que se completa la transacción.
- **Perfiles de Usuario y Preferencias**: Acceso rápido a configuraciones personalizadas del usuario mediante su ID único.
- **Broker de Mensajes**: Uso como sistema de mensajería entre aplicaciones (pub/sub) debido a su velocidad de respuesta.
- **Contadores y Analítica en Tiempo Real**: Seguimiento de visitas a páginas, visualizaciones de anuncios o métricas que cambian constantemente.
- **Servicios Geoespaciales**: Realizar consultas de proximidad o calcular distancias entre coordenadas en tiempo real.

## Sección 2 - Manejo de Strings

### 2.1 Valores de texto

```bash
# SET comando estándar para almacenar Strings
SET clave valor
```

#### 9.​ Agregar una clave package con el valor "Bariloche 3 days".

```bash
SET package "Bariloche 3 days"
```

#### 10.​Agregar una clave user con el valor "Turismo BD2". Obtener el valor de la clave user.

```bash
SET user "Turismo BD2"
GET user
```

#### 11.​Obtener todas las claves almacenadas actualmente.

```bash
# esto no se usa en produccion porque recorre TODAS las claves
KEYS *
```

#### 12.​Agregar una clave user con el valor "Cronos Turismo". ¿Cuál es el valor actual de la clave user?

```bash
SET user "Cronos Turismo"
GET user
"Cronos Turismo"
# el valor de la clave user se sobreescribe en este caso con "Cronos Turismo"
```

#### 13.​Concatenar " S.A." a la clave user. ¿Cuál es el valor actual de la clave user?

```bash
APPEND user " S.A."
(integer) 19 # APPEND retorna la longitud del string
GET user
"Cronos Turismo S.A."
```

#### 14.​Eliminar la clave user. ¿Qué valor retorna si se intenta obtener la clave user luego de eliminarla?

```bash
DEL user
(integer) 1 # si la clave a eliminar existe retorna 1, caso contrario 0
GET user
(nil)
```

### 2.2 Valores numericos

#### 15.​Verificar si existe la clave visits.

```bash
EXISTS visits
(integer) 0 # si la clave NO existe retorna 0, caso contrario 1
```

#### 16.​Agregar una clave visits con el valor 0.

```bash
SET visits 0
GET visits
"0"
```

#### 17.​Incrementar en 1 la clave visits. ¿Cuál es el valor actual?

```bash
INCR visits # incrementa en 1, nunca se entra en race condition
```

#### 18.​Incrementar en 5 la clave visits. ¿Cuál es el valor actual?

```bash
INCRBY visits 5
(integer) 6
GET visits
"6"
```

#### 19.​Decrementar en 1 la clave visits. ¿Cuál es el valor actual?

```bash
DECR visits
(integer) 5
GET visits
"5"
```

#### 20.​Incrementar en 2 la clave visits. ¿Cuál es el valor actual?

```bash
INCRBY visits 2
(integer) 7
GET visits
"7"
```

#### 21.​Agregar una clave "value package" con el valor 539789.32.

```bash
SET "value package" 539789.32
GET "value package"
"539789.32"
```

#### 22.​Incrementar en 20000 la clave "value package". ¿Cuál es el valor actual?

```bash
INCRBYFLOAT "value package" 20000
"559789.31999999999999318"
```

#### 23.​¿Cual es el tipo de datos de "value package", visits y user?

```bash
TYPE "value package"
string
TYPE visits
string
TYPE user
none
```

## Sección 3 - Manejo de Claves

### 3.1

#### 24.​Obtener todas las claves que empiecen con "v".

```bash

```

#### 25.​Obtener todas las claves que contengan la letra "t".

```bash
KEYS v*
1) "value package"
2) "visits"
```

#### 26.​Obtener todas las claves que terminan con "age".

```bash
KEYS *age
1) "value package"
2) "package"
```

#### 27.​Renombrar la clave "package" por "bariloche package".

```bash
RENAME package "bariloche package"
```

#### 28.​¿Qué comando se utiliza para renombrar una clave solo si el nombre destino no existe aún?

```bash
RENAMENX origen destino # NX = Not Exist, en caso de no hacerce el renombre (clave destino ya existe) retorna 0
```

#### 29.​Eliminar todas las claves.

```bash
DBSIZE # ver tamaño de la db actual
(integer) 3
FLUSHDB # elimina todas las claves de la db actual (existe FLUSHALL para eliminar todas las claves de todas las db redis)
OK
DBSIZE
(integer) 0
```

## Sección 4 - Expiración de Claves

### 4.1

#### 30.​Agregar una clave agency con el valor "Cronos Tours".

```bash
SET agency "Cronos Tours"
```

#### 31.​¿Cuál es el tiempo de vida (TTL) de la clave agency?

```bash
ttl agency
(integer) -1 # por defecto al crear la clave no tiene TTL
```

#### 32.​Agregar una expiración de 30 segundos a la clave agency.

```bash
expire agency 30
(integer) 1
```

#### 33.​¿Cuál es el tiempo de vida de la clave agency luego de agregar la expiración?

```bash
ttl agency
(integer) 28
```

#### 34.​Pasados los 30 segundos: ¿cuál es el TTL de agency? ¿Que retorna si se solicita el valorde agency?

```bash
ttl agency
(integer) -2
```

#### 35.​Agregar una clave agency con el valor "Cronos Tours" que expire en 20 segundos desde su creación.

```bash
SET agency "Cronos Tours" ex 20
OK
ttl agency
(integer) 18
```

## Sección 5 - Listas

### 5.1

#### 36.​Insertar una lista llamada pets con el valor "dog".

```bash
LPUSH pets dog
(integer) 1
TYPE pets
list
```

#### 37.​¿Qué sucede si se ejecuta el comando GET sobre pets? ¿Cómo se obtienen los valores de una lista?

```bash
GET pets
(error) WRONGTYPE Operation against a key holding the wrong kind of value
LRANGE pets 0 -1 # LRANGE pets inicio fin, 0 -1 nos da toda la lista
```

#### 38.​Agregar a la lista pets el valor "cat" por la izquierda.

```bash
LPUSH pets cat
(integer) 2
LRANGE pets 0 -1
1) "cat"
2) "dog"
```

#### 39.​Agregar a la lista pets el valor "fish" por la derecha.

```bash
RPUSH pets fish
(integer) 3
LRANGE pets 0 -1
1) "cat"
2) "dog"
3) "fish"
```

#### 40.​¿Qué tipo de dato es el valor de pets?

```bash
TYPE pets
list
```

#### 41.​Eliminar el valor del extremo izquierdo de la lista.

```bash
LPOP pets
"cat"
```

#### 42.​Eliminar el valor del extremo derecho de la lista.

```bash
RPOP pets
"fish"
```

#### 43.​Agregar a una clave "vuelo:ar389" los valores: aep, mdz, brc, nqn y mdq.

```bash
LPUSH vuelo:ar389 aep mdz brc nqn mdq
(integer) 5
LRANGE vuelo:ar389 0 -1
1) "mdq"
2) "nqn"
3) "brc"
4) "mdz"
5) "aep"
```

#### 44.​Ordenar los valores de la lista "vuelo:ar389". ¿Qué sucede si se solicitan todos los valores de la lista luego de ordenarla?

```bash
SORT vuelo:ar389 ALPHA # ALPHA es para indicar que son strings y que no intente castear los elementos de la lista
1) "aep"
2) "brc"
3) "mdq"
4) "mdz"
5) "nqn"
127.0.0.1:6379> LRANGE vuelo:ar389 0 -1 # vemos que la lista sigue "desordenada" entonces el comando SORT no modifica la lista existente
1) "mdq"
2) "nqn"
3) "brc"
4) "mdz"
5) "aep"
```

#### 45.​Insertar el valor "fte" inmediatamente después de "brc".

```bash
LINSERT vuelo:ar389 AFTER brc fte
(integer) 6
LRANGE vuelo:ar389 0 -1
1) "mdq"
2) "nqn"
3) "brc"
4) "fte"
5) "mdz"
6) "aep"
```

#### 46.​Insertar el valor "ush" inmediatamente antes de "fte".

```bash
LINSERT vuelo:ar389 BEFORE fte ush
(integer) 7
LRANGE vuelo:ar389 0 -1
1) "mdq"
2) "nqn"
3) "brc"
4) "ush"
5) "fte"
6) "mdz"
7) "aep"
```

#### 47.​Modificar el último elemento de la lista por "sla".

```bash
LSET vuelo:ar389 -1 sla # -1 retorna es el indice del último elemento de la lista
OK
LRANGE vuelo:ar389 0 -1
1) "mdq"
2) "nqn"
3) "brc"
4) "ush"
5) "fte"
6) "mdz"
7) "sla"
```

#### 48.​Obtener la cantidad de elementos de "vuelo:ar389".

```bash
LLEN vuelo:ar389
(integer) 7
```

#### 49.​Obtener el tercer valor de "vuelo:ar389".

```bash
LINDEX vuelo:ar389 2 # el indice comienza en 0
"brc"
```

#### 50.​Eliminar el valor "aep" de "vuelo:ar389".

```bash
#LREM key count element
#count > 0: Remove elements equal to element moving from head to tail.
#count < 0: Remove elements equal to element moving from tail to head.
#count = 0: Remove all elements equal to element.
LREM vuelo:ar389 0 aep
(integer) 0
127.0.0.1:6379> LRANGE vuelo:ar389 0 -1 # por como inserte antes aep se reemplazo por sla
1) "mdq"
2) "nqn"
3) "brc"
4) "ush"
5) "fte"
6) "mdz"
7) "sla"
LREM vuelo:ar389 0 mdq
(integer) 1
127.0.0.1:6379> LRANGE vuelo:ar389 0 -1
1) "nqn"
2) "brc"
3) "ush"
4) "fte"
5) "mdz"
6) "sla"
```

#### 51.​Quedarse únicamente con los valores de las posiciones 3 a 5 de "vuelo:ar389".

```bash
LRANGE vuelo:ar389 2 4 # indice comienza en 0
1) "ush"
2) "fte"
3) "mdz"
```

#### 52.​Agregar en "vuelo:ar389" el valor "fte". ¿Cuántas veces aparece ahora en la lista?

```bash
RPUSH vuelo:ar389 fte
(integer) 7
127.0.0.1:6379> LRANGE vuelo:ar389 0 -1
1) "nqn"
2) "brc"
3) "ush"
4) "fte" # 1
5) "mdz"
6) "sla"
7) "fte" # 2
# el valor aparece 2 veces
```

## Sección 6 - Conjuntos (Sets)

### 6.1

#### 53.​Agregar un conjunto llamado airports con los siguientes valores:

`eze eqs aep nqn mdz mdq ush fte sla aep nqn brc cpc juj aep tuc (en total 16 por repetidos)`

```bash
SADD airports eze eqs aep nqn mdz mdq ush fte sla aep nqn brc cpc juj aep tuc
(integer) 13
SMEMBERS airports
1) "eze"
2) "eqs"
3) "aep"
4) "nqn"
5) "mdz"
6) "mdq"
7) "ush"
8) "fte"
9) "sla"
10) "brc"
11) "cpc"
12) "juj"
13) "tuc"
```

#### 54.​¿Cuántos valores tiene el conjunto? ¿Por qué puede diferir de la cantidad de valores ingresados?

```bash
SCARD airports
(integer) 13 # difiere de los 16 ingresados porque al ser un conjunto no hay elementos repetidos
```

#### 55.​Listar los valores del conjunto airports.

```bash
SMEMBERS airports
 1) "eze"
 2) "eqs"
 3) "aep"
 4) "nqn"
 5) "mdz"
 6) "mdq"
 7) "ush"
 8) "fte"
 9) "sla"
10) "brc"
11) "cpc"
12) "juj"
13) "tuc"
```

#### 56.​Quitar el valor "cpc" del conjunto airports.

```bash
SREM airports cpc
(integer) 1
SMEMBERS airports
1) "eze"
2) "eqs"
3) "aep"
4) "nqn"
5) "mdz"
6) "mdq"
7) "ush"
8) "fte"
9) "sla"
10) "brc"
11) "juj"
12) "tuc"
```

#### 57.​Quitar un valor aleatorio del conjunto airports.

```bash
SPOP airports
"fte"
SMEMBERS airports
1) "eze"
2) "eqs"
3) "aep"
4) "nqn"
5) "mdz"
6) "mdq"
7) "ush"
8) "sla"
9) "brc"
10) "juj"
11) "tuc"
```

#### 58.​¿Qué cantidad de valores tiene airports ahora?

```bash
SCARD airports
(integer) 11
```

#### 59.​Comprobar si "cpc" es miembro del conjunto airports.

```bash
SISMEMBER airports cpc # retorna 0 si no es miembro, caso contrario 1
(integer) 0
```

#### 60.​Mover los valores "sla" y "juj" a un nuevo conjunto denominado noa_airports.

```bash
SMOVE airports noa_airports sla
(integer) 1
SMOVE airports noa_airports juj
(integer) 1
SMEMBERS noa_airports
1) "sla"
2) "juj"
SMEMBERS airports
1) "eze"
2) "eqs"
3) "aep"
4) "nqn"
5) "mdz"
6) "mdq"
7) "ush"
8) "brc"
9) "tuc"
```

#### 61.​Obtener la unión de los conjuntos airports y noa_airports. ¿Modifica los conjuntos originales?

```bash
SUNION airports noa_airports
 1) "mdz"
 2) "eze"
 3) "eqs"
 4) "aep"
 5) "brc"
 6) "ush"
 7) "juj"
 8) "tuc"
 9) "mdq"
10) "sla"
11) "nqn"
SMEMBERS airports # no se modifican los conjuntos originales
1) "eze"
2) "eqs"
3) "aep"
4) "nqn"
5) "mdz"
6) "mdq"
7) "ush"
8) "brc"
9) "tuc"
SMEMBERS noa_airports # no se modifican los conjuntos originales
1) "sla"
2) "juj"
```

#### 62.​Realizar la unión de airports y noa_airports y almacenar el resultado en un nuevo conjunto llamado total_airports.

```bash
SUNIONSTORE total_airports airports noa_airports
(integer) 11
SMEMBERS noa_airports
1) "sla"
2) "juj"
SMEMBERS airports
1) "eze"
2) "eqs"
3) "aep"
4) "nqn"
5) "mdz"
6) "mdq"
7) "ush"
8) "brc"
9) "tuc"
SMEMBERS total_airports
1) "eze"
2) "eqs"
3) "aep"
4) "nqn"
5) "mdz"
6) "mdq"
7) "ush"
8) "brc"
9) "tuc"
10) "sla"
11) "juj"
```

#### 63.​Realizar la intersección entre total_airports y noa_airports.

```bash
SINTER total_airports noa_airports
1) "sla"
2) "juj"
```

#### 64.​Realizar la diferencia entre total_airports y noa_airports.

```bash
SDIFF total_airports noa_airports
1) "mdz"
2) "brc"
3) "aep"
4) "eze"
5) "eqs"
6) "nqn"
7) "ush"
8) "tuc"
9) "mdq"
```

## Sección 7 - Conjuntos Ordenados (Sorted Sets)

### 7.1

#### 65.​Agregar a un conjunto ordenado llamado passengers los siguientes datos (score nombre):
`2.5 federico 4 alejandra 3 julian 1 ivan 2 tomas 2 luciana 2.4 natalia`

```bash
ZADD passengers 2.5 federico 4 alejandra 3 julian 1 ivan 2 tomas 2 luciana 2.4 natalia
(integer) 7
```

#### 66.​Obtener los valores del conjunto passengers.

```bash
ZRANGE passengers 0 -1
1) "ivan"
2) "luciana"
3) "tomas"
4) "natalia"
5) "federico"
6) "julian"
7) "alejandra"
127.0.0.1:6379> ZRANGE passengers 0 -1 WITHSCORES
1) "ivan"
2) "1"
3) "luciana"
4) "2"
5) "tomas"
6) "2"
7) "natalia"
8) "2.4"
9) "federico"
10) "2.5"
11) "julian"
12) "3"
13) "alejandra"
14) "4"
```

#### 67.​Actualizar el score de luciana a 2.7.

```bash
ZADD passengers 2.7 luciana # entiendo que con el parametro CH hubiese devuelto un integer de 1 ya que no fue una nueva insercion en este caso
(integer) 0
ZRANGE passengers 0 -1 WITHSCORES
1) "ivan"
2) "1"
3) "tomas"
4) "2"
5) "natalia"
6) "2.4"
7) "federico"
8) "2.5"
9) "luciana"
10) "2.7"
11) "julian"
12) "3"
13) "alejandra"
14) "4"
```

#### 68.​Agregar al conjunto passengers a silvia con score 5.1.

```bash
ZADD passengers 5.1 silvia
(integer) 1
ZRANGE passengers 0 -1 WITHSCORES
1) "ivan"
2) "1"
3) "tomas"
4) "2"
5) "natalia"
6) "2.4"
7) "federico"
8) "2.5"
9) "luciana"
10) "2.7"
11) "julian"
12) "3"
13) "alejandra"
14) "4"
15) "silvia"
16) "5.1"
```

#### 69.​Incrementar en 2 el score de alejandra.

```bash
ZINCRBY passengers 2 alejandra
"6"
ZRANGE passengers 0 -1 WITHSCORES
1) "ivan"
2) "1"
3) "tomas"
4) "2"
5) "natalia"
6) "2.4"
7) "federico"
8) "2.5"
9) "luciana"
10) "2.7"
11) "julian"
12) "3"
13) "silvia"
14) "5.1"
15) "alejandra"
16) "6"
```

#### 70.​Obtener los valores del conjunto passengers con sus scores.

```bash
ZRANGE passengers 0 -1 WITHSCORES
1) "ivan"
2) "1"
3) "tomas"
4) "2"
5) "natalia"
6) "2.4"
7) "federico"
8) "2.5"
9) "luciana"
10) "2.7"
11) "julian"
12) "3"
13) "silvia"
14) "5.1"
15) "alejandra"
16) "6"
```

#### 71.​Obtener los valores del conjunto passengers con sus scores en orden inverso.

```bash
ZRANGE passengers 0 -1 WITHSCORES REV
1) "alejandra"
2) "6"
3) "silvia"
4) "5.1"
5) "julian"
6) "3"
7) "luciana"
8) "2.7"
9) "federico"
10) "2.5"
11) "natalia"
12) "2.4"
13) "tomas"
14) "2"
15) "ivan"
16) "1"
```

#### 72.​Obtener la cantidad de elementos del conjunto passengers.

```bash
ZCARD passengers
(integer) 8
```

#### 73.​Obtener la cantidad de elementos que tienen scores entre 2 y 3.

```bash
ZRANGE passengers 2 3 BYSCORE WITHSCORES
1) "tomas"
2) "2"
3) "natalia"
4) "2.4"
5) "federico"
6) "2.5"
7) "luciana"
8) "2.7"
9) "julian"
10) "3"
```

#### 74.​Obtener el ranking de julian en el conjunto passengers.

```bash
ZRANK passengers julian
(integer) 5
```

#### 75.​Obtener el score de tomas en el conjunto passengers.

```bash
ZSCORE passengers tomas
"2
```

#### 76.​Extraer el elemento con menor score del conjunto passengers.

```bash
ZPOPMIN passengers
1) "ivan"
2) "1"
```

#### 77.​Extraer el elemento con mayor score del conjunto passengers.

```bash
ZPOPMAX passengers
1) "alejandra"
2) "6"
```

#### 78.​Eliminar del conjunto passengers al valor silvia.

```bash
ZREM passengers silvia
(integer) 1
ZRANGE passengers 0 -1 WITHSCORES
 1) "tomas"
 2) "2"
 3) "natalia"
 4) "2.4"
 5) "federico"
 6) "2.5"
 7) "luciana"
 8) "2.7"
 9) "julian"
10) "3"
```

## Sección 8 - Hashes

### 8.1

#### 79.​Agregar a un hash llamado user:cronos los siguientes campos:

`"razon social" - "cronos s.a"`  
`domicilio      - "47 236 La Plata"`  
`"telefono"     - 2215556677`  

```bash
HSET user:cronos "razon social" "cronos s.a" domicilio "47 236 La Plata" telefono 2215556677
(integer) 3
HGETALL user:cronos
1) "razon social"
2) "cronos s.a"
3) "domicilio"
4) "47 236 La Plata"
5) "telefono"
6) "2215556677"
```

#### 80.​Agregar el campo mail con el valor info@cronos.com.ar al hash user:cronos.

```bash
HSET user:cronos mail info@cronos.com.ar
(integer) 1
HGETALL user:cronos
1) "razon social"
2) "cronos s.a"
3) "domicilio"
4) "47 236 La Plata"
5) "telefono"
6) "2215556677"
7) "mail"
8) "info@cronos.com.ar"
```

#### 81.​Obtener todos los campos y valores de user:cronos.

```bash
HGETALL user:cronos
1) "razon social"
2) "cronos s.a"
3) "domicilio"
4) "47 236 La Plata"
5) "telefono"
6) "2215556677"
7) "mail"
8) "info@cronos.com.ar"
```

#### 82.​Obtener únicamente el valor del campo mail de user:cronos.

```bash
HGET user:cronos mail
"info@cronos.com.ar"
```

#### 83.​Eliminar el campo teléfono de user:cronos.

```bash
HDEL user:cronos telefono
(integer) 1
HGETALL user:cronos
1) "razon social"
2) "cronos s.a"
3) "domicilio"
4) "47 236 La Plata"
5) "mail"
6) "info@cronos.com.ar"
```

#### 84.​Obtener la cantidad de campos de user:cronos.

```bash
HLEN user:cronos
(integer) 3
```

#### 85.​Obtener las claves (nombres de campos) de user:cronos.

```bash
HKEYS user:cronos
1) "razon social"
2) "domicilio"
3) "mail"
```

#### 86.​Determinar si existe el campo cuil en user:cronos.

```bash
HEXISTS user:cronos cuil
(integer) 0 # retorna 0 si el campo no existe
HEXISTS user:cronos mail
(integer) 1 # retorna 1 si el campo existe
```

#### 87.​Obtener todos los valores (sin los nombres de campos) de user:cronos.

```bash
HVALS user:cronos
1) "cronos s.a"
2) "47 236 La Plata"
3) "info@cronos.com.ar"
```

#### 88.​Obtener la longitud del valor del campo mail de user:cronos.

```bash
HSTRLEN user:cronos mail
(integer) 18
```

## Sección 9 - Geospatial

### 9.1

#### 89.​Agregar en un conjunto denominado cities las siguientes localidades con sus coordenadas (longitud, latitud):

Ciudad | Latitud | Longitud |
------ | ------- | -------- |
Buenos Aires | -34.61315 | -58.37723 |
Cordoba | -31.41350 | -64.18105 |
Rosario | -32.94682 | -60.63932 |
Mendoza | -32.89084 | -68.82717 |
San Miguel de Tucuman | -26.82414 | -65.22260 |
La Plata | -34.92145 | -57.95453 |
Mar del Plata | -38.00042 | -57.55620 |
Salta | -24.78590 | -65.41166 |
Santa Fe | -31.64881 | -60.70868 |
San Juan | -31.53750 | -68.53639 |
Resistencia | -27.46056 | -58.98389 |
Santiago del Estero | -27.79511 | -64.26149 |
Posadas | -27.36708 | -55.89608 |
San Salvador de Jujuy | -24.19457 | -65.29712 |
Bahia Blanca | -38.71959 | -62.27243 |
Parana | -31.73271 | -60.52897 |

```bash
GEOADD cities -58.37723 -58.37723 "Buenos Aires"
(integer) 1
GEOADD cities -64.18105 -64.18105 Cordoba
(integer) 1
GEOADD cities -60.63932 -32.94682 Rosario -68.82717 -32.89084 Mendoza -65.22260  -26.82414 "San Miguel de Tucuman" -57.95453 -34.92145 "La Plata"
(integer) 4
GEOADD cities -57.55620 -38.00042 "Mar del Plata" -65.41166 -24.78590 Salta -60.70868 -31.64881 "Santa Fe" -68.53639 -31.53750  "San Juan"
(integer) 4
GEOADD cities -58.98389 -27.46056 Resistencia -64.26149-27.79511 "Santiago del Estero" -55.89608 -27.36708 Posadas -65.29712 -24.19457 "San Salvador de Jujuy"
GEOADD cities -58.98389 -27.46056 Resistencia -64.26149 -27.79511 "Santiago del Estero" -55.89608 -27.36708 Posadas -65.29712 -24.19457 "San Salvador de Jujuy"
(integer) 4
GEOADD cities -62.27243 -38.71959 "Bahia Blanca" -60.52897 -31.73271 Parana
(integer) 2
```

#### 90.​Obtener todos los miembros del conjunto cities.

```bash
ZRANGE cities 0 -1 # se utiliza como un conjunto ordenado
 1) "Cordoba"
 2) "Buenos Aires"
 3) "Mendoza"
 4) "San Juan"
 5) "Bahia Blanca"
 6) "Mar del Plata"
 7) "La Plata"
 8) "Rosario"
 9) "San Miguel de Tucuman"
10) "Santiago del Estero"
11) "Parana"
12) "Santa Fe"
13) "Resistencia"
14) "Salta"
15) "San Salvador de Jujuy"
16) "Posadas"
```

#### 91.​Obtener las coordenadas almacenadas de Santa Fe.

```bash
GEOPOS cities "Santa Fe"
1) 1) "-60.708681643009186" # longitud
   2) "-31.64881101691541" # latitud
```

#### 92.​Obtener la distancia en kilómetros entre Buenos Aires y Córdoba.

```bash
GEODIST cities "Buenos Aires" Cordoba KM
"715.5275"
```

#### 93.​Obtener las ciudades que se encuentran en un radio de 100 km de la coordenada (-27.37, -55.9), incluyendo la distancia de cada una.

```bash
GEOSEARCH cities FROMLONLAT -55.9 -27.37 BYRADIUS 100 KM WITHDIST
1) 1) "Posadas"
   2) "0.5055"
```

#### 94.​Obtener las ciudades que se encuentran a menos de 700 km de Córdoba.

```bash
GEOSEARCH cities FROMMEMBER Cordoba BYRADIUS 700 KM WITHDIST
 1) 1) "Cordoba"
    2) "0.0000"
 2) 1) "San Miguel de Tucuman"
    2) "520.3835"
 3) 1) "Santiago del Estero"
    2) "402.5353"
 4) 1) "Parana"
    2) "347.8771"
 5) 1) "Santa Fe"
    2) "330.2202"
 6) 1) "Resistencia"
    2) "668.2173"
 7) 1) "Buenos Aires"
    2) "647.6303"
 8) 1) "La Plata"
    2) "698.5502"
 9) 1) "Rosario"
    2) "374.4698"
10) 1) "San Juan"
    2) "413.3545"
11) 1) "Mendoza"
    2) "467.3004"
```