# Trabajo Practico 3 - Bases de Datos NoSQL con MongoDB

## Seccion 1 - Bases de Datos NoSQL y Relacionales

### 1.1

#### 1.​ ¿Cuales de los siguientes conceptos de RDBMS existen en MongoDB? En caso de no existir, ¿hay alguna alternativa? ¿Cual es?

En MongoDB, los conceptos tradicionales de las bases de datos relacionales (RDBMS) tienen equivalencias directas que facilitan la transición:

- **Base de Datos:** El concepto existe y se mantiene igual en MongoDB.
- **Tabla / Relación:** No existen tablas rígidas; la alternativa es la Colección, que es una agrupación lógica de datos.
- **Fila / Tupla:** Se reemplazan por Documentos (en formato JSON/BSON).
- **Columna:** La alternativa son los Campos o Atributos dentro de cada documento.

#### 2.​ ¿Existen claves foraneas en MongoDB? ¿Que diferencias existen con las bases de datos de tipo relacional?

No existen las claves foráneas en MongoDB en el sentido tradicional de restricciones de integridad referencial controladas por el motor de la base de datos. En su lugar, MongoDB utiliza referencias (guardar el _id de un documento en otro) o documentos embebidos.

**Las principales diferencias con las bases de datos relacionales son:**

- **Integridad Referencial:** MongoDB es "ignorante" de la relación; no impide que borres un documento que está siendo referenciado por otro, dejando la responsabilidad de mantener esa consistencia a la lógica de tu aplicación.
- **Performance y Joins:** En RDBMS, las relaciones se resuelven con joins en tiempo de consulta, lo que puede ser costoso. En MongoDB, se busca minimizar estos saltos "embebiendo" la información relacionada dentro del mismo documento para que todo se recupere en una sola lectura.
- **Flexibilidad:** No necesitas definir estas relaciones de antemano en un esquema rígido; puedes empezar a guardar IDs de referencia en cualquier momento sin alterar la estructura de toda la colección.

#### 3.​ Para acelerar las consultas, MongoDB tiene soporte para indices. ¿Que tipos de indices soporta?

- **Un solo campo (Single field):** Se aplica a un atributo individual o a subcampos específicos dentro de documentos embebidos.
- **Compuestos (Compound field):** Involucran múltiples campos, lo que acelera consultas que filtran por varios criterios simultáneamente.
- **Geoespaciales (Geospatial field):** Específicos para datos de ubicación; por ejemplo, el tipo 2dsphere permite realizar búsquedas considerando la curvatura de la Tierra.
- **Índice en _id:** Es un índice que MongoDB crea automáticamente de forma obligatoria para garantizar la unicidad de cada documento.
Estos índices se definen a nivel de colección y pueden crearse en segundo plano (background) para evitar el bloqueo de la base de datos durante su generación.

#### 4.​ En MongoDB existen dos tipos de vistas. Explicar brevemente cuales son y que diferencias existen entre ellas. Ademas, mencionar algunos casos donde podria utilizarlas.

**1. Vistas Estándar (o Dinámicas)**

- **Definición:** Son consultas predefinidas (usualmente mediante el Aggregation Framework en MongoDB) que se calculan en el momento exacto en que la aplicación las solicita. No almacenan datos propios en el disco, sino que funcionan como una "ventana" a los datos reales.
- **Diferencias clave:**
  - **Frescura:** Los datos siempre están actualizados, ya que se calculan en tiempo real a partir de las colecciones base.
  - **Almacenamiento:** No ocupan espacio extra significativo, solo la definición de la lógica.
  - **Rendimiento:** Si la consulta subyacente es muy compleja o involucra muchos documentos, puede ser lenta.

**2. Vistas Materializadas**

- **Definición:** Son resultados de consultas que se han calculado por adelantado y se han almacenado físicamente en una colección de la base de datos.
- **Diferencias clave:**
  - **Rendimiento:** Son extremadamente rápidas de leer, ya que el cálculo pesado ya se realizó.
  - **Obsolescencia:** Los datos pueden estar "viejos" (stale) si la colección original cambió y la vista aún no se ha vuelto a generar mediante un proceso por lotes o actualizaciones programadas.
  - **Almacenamiento:** Ocupan espacio en disco igual que cualquier otra colección.

**Casos de Uso**

- **Vistas Estándar:** Úsalas para encapsular lógica compleja y presentar a la aplicación una estructura simplificada o para ocultar campos sensibles de los documentos base.
- **Vistas Materializadas:** Son ideales para análisis de datos masivos (BI/Analytics) donde necesitas consultar información que cruza múltiples agregados (por ejemplo, calcular el total de ventas de un producto específico revisando todos los pedidos de los últimos meses). También sirven para reportes que no requieren datos al segundo, pero sí mucha velocidad de respuesta.

#### 5.​ Los documentos de una coleccion pueden diferir en la cantidad y tipos de campos. ¿Existen algunas formas de validar los elementos a insertar en una coleccion para evitar esta disparidad?

Aunque MongoDB es una base de datos "schemaless" (sin esquema fijo), existen formas de validar la estructura de los documentos para mantener la consistencia.

- **Validación a nivel de base de datos (Schema Validation):** MongoDB permite definir reglas de validación opcionales para una colección, generalmente utilizando el estándar JSON Schema. Con esto, puedes restringir qué campos son obligatorios, sus tipos de datos (string, integer, etc.) y los rangos de valores permitidos antes de aceptar una inserción.
- **Validación en la aplicación (Esquema Implícito):** Es la práctica más común donde la lógica del código se encarga de asegurar que los documentos tengan un formato homogéneo. Como los programas deben consumir estos datos de forma consistente, se suele implementar una capa de traducción o modelos de objetos que validan los datos antes de enviarlos a la base de datos.

#### 6.​ MongoDB tiene soporte para transacciones, pero no es igual que el de los RDBMS. ¿Cual es el alcance de una transaccion en MongoDB?

- El alcance de una transacción en MongoDB se limita generalmente a un único documento o agregado.
- MongoDB garantiza la atomicidad de las operaciones a nivel de documento: todos los cambios dentro de ese documento (incluso si es complejo y tiene subdocumentos) se aplican por completo o no se aplican en absoluto. A diferencia de los RDBMS tradicionales, cuyas transacciones ACID pueden abarcar múltiples tablas, MongoDB y otras bases de datos orientadas a agregados suelen evitar las transacciones que cruzan múltiples agregados para facilitar el escalado horizontal en clusters.
- En resumen, mientras un RDBMS protege la integridad entre tablas, MongoDB la asegura dentro de la unidad natural del dato que es el documento.

#### 7.​ Las relaciones entre documentos en MongoDB pueden establecerse mediante documentos embebidos o referencias. Investigar como se implementa cada una y analizar las ventajas y desventajas de cada una, comparandola con la forma estandar de establecer relaciones en una base de datos relacional.

**1. Documentos Embebidos (Denormalización)**

- Consiste en guardar los datos relacionados dentro de un solo documento complejo, utilizando subdocumentos o arreglos.
- **Implementación**: Se incluye la estructura completa del dato relacionado (por ejemplo, una lista de direcciones) dentro del documento raíz.
- **Ventajas**:
  - **Performance**: Permite obtener toda la información necesaria en una sola lectura (un solo request), evitando saltos adicionales a la base de datos.
  - **Atomicidad**: Las operaciones son atómicas a nivel de documento, asegurando la consistencia interna del agregado.
- **Desventajas**:
  - **Límite de tamaño**: Existe un límite físico por documento (actualmente 16MB).
  - **Redundancia**: Los datos pueden estar repetidos, lo que complica las actualizaciones masivas.

**2. Referencias (Normalización)**

- Consiste en guardar únicamente el identificador (_id) de un documento en otro para vincularlos.
- **Implementación**: Se almacena el ID del documento relacionado. Para recuperar los datos, la aplicación debe realizar una segunda consulta o usar un operador como $lookup.
- **Ventajas**:
  - **Flexibilidad**: Ideal para relaciones "uno a muchos" o "uno a millones" donde el documento embebido superaría el límite de tamaño.
  - **Independencia**: Permite tratar a las entidades como objetos separados que pueden ser actualizados sin afectar a otros agregados.
- **Desventajas**:
  - **Múltiples llamadas**: Requiere más trabajo del lado de la aplicación o del motor para "unir" los datos.
  - **Falta de Integridad**: La base de datos es "ignorante" de la relación; no hay restricciones automáticas que impidan borrar un documento referenciado.

**Comparación con BBDD Relacionales**

- **Joins vs. Agregados**: En el modelo relacional, las relaciones se resuelven mediante joins en tiempo de consulta sobre tablas normalizadas. MongoDB prioriza el agregado como unidad de interacción para optimizar el acceso y la distribución en clusters.
- **Conciencia de la relación**: Mientras que una RDBMS garantiza la integridad referencial (claves foráneas), en MongoDB esta responsabilidad suele recaer en la lógica del código de la aplicación.
- **Flexibilidad de esquema**: En SQL debes definir la relación estrictamente de antemano; en MongoDB puedes evolucionar de referencias a embebido (o viceversa) con mayor facilidad según cambien tus patrones de acceso.

#### 8.​ Tomando como referencia el modelo de los trabajos practicos anteriores y suponiendo que este podria mapearse a una base de datos en MongoDB, proponer algunos casos donde la relacion seria conveniente mapearla como referencia y otros como documentos embebidos. Justificar la eleccion.

**1. Casos para Documentos Embebidos (Denormalización)**

Se eligen cuando los datos relacionados se consultan casi siempre junto con la entidad principal y forman una unidad lógica.

- **Route -> Stops**: En el modelo, las paradas son una composición de la ruta. Dado que una ruta tiene un número limitado y manejable de paradas, embeberlas permite recuperar el itinerario completo en una sola operación de lectura, optimizando la performance.
- **Purchase -> Review**: La reseña es opcional (0..1) y pertenece exclusivamente a una compra específica. Al estar embebida, se garantiza que al consultar el historial de compras el usuario vea su reseña inmediatamente sin realizar saltos adicionales a la base de datos.
- **Purchase -> ItemService**: Los ítems de una compra son detalles que rara vez se consultan de forma aislada. Embeberlos asegura la atomicidad de la compra: o se guarda toda la transacción con sus ítems o no se guarda nada.

**2. Casos para Referencias (Normalización)**

Se eligen para entidades que tienen un ciclo de vida independiente o cuando la relación puede crecer indefinidamente (uno-a-muchos/millones).

- **Route -> DriverUser / TourGuideUser**: Los choferes y guías son usuarios independientes que participan en múltiples rutas según la demanda. Si se embebieran en cada ruta, cualquier cambio en los antecedentes del chofer obligaría a actualizar decenas de documentos, generando una redundancia difícil de mantener.
- **User -> Purchases**: Un usuario frecuente puede acumular cientos o miles de compras a lo largo del tiempo (relación uno-a-muchos). Mantener todas las compras dentro del documento del usuario podría exceder el límite físico de 16MB por documento. Es mejor guardar el ID del usuario en cada documento de compra (referencia).
- **Service -> Supplier**: Un proveedor ofrece muchos servicios. El proveedor es una entidad con datos fiscales y de contacto propios que deben ser consistentes para todos sus servicios. Usar una referencia evita duplicar la información del proveedor en cada servicio ofrecido.


## Seccion 2 - Operaciones CRUD basicas

### 2.1 Configuracion inicial e inserciones

#### 9.​ Crear una nueva base de datos llamada "tours" y una coleccion llamada "recorridos".

#### 10.​En la nueva coleccion, utilizando el comando correspondiente, insertar el siguiente documento:

```json
{ "nombre": "City Tour", "precio": 200, "stops": ["Diagonal Norte", "Avenida de Mayo", "Plaza del Congreso"], "totalKm": 5 }
```

#### 11.​Recuperar la informacion insertada usando db.recorridos.find() (puede agregarse .pretty() al final para ver los datos indentados). ¿Que diferencia se observa entre el documento insertado y el documento recuperado?

La diferencia es que se agrego el atributo `_id: ObjectId('6a0b2dfa8bd6be801f9df8a3')` que es la clave primaria del documento, al no definirlo se crea automaticamente, internamente este id hexadecimal contiene informacion como timestamp, maquina, proceso, contador incremental de esta manera se evitan problemas en sistemas distribuidos.

```js
tours> db.recorridos.find().pretty()
[
  {
    _id: ObjectId('6a0b2dfa8bd6be801f9df8a3'),
    nombre: 'City Tour',
    precio: 200,
    stops: [ 'Diagonal Norte', 'Avenida de Mayo', 'Plaza del Congreso' ],
    totalKm: 5
  }
]
```

#### 12.​Agregar a la coleccion, utilizando un solo comando, los documentos especificados en el archivo "material_adicional_1.json" adjunto a esta practica.


### 2.2 Operaciones de actualizacion y eliminacion

#### 13.​Actualizar el recorrido "Cultural Odyssey" para que su total de kilometros sea 12.

```js
db.recorridos.updateOne({"nombre":"Cultural Odyssey"}, {$set:{"totalKm":12}})
```

#### 14.​Actualizar el listado de stops del recorrido "Delta Tour" para agregar "Tigre".

```js
db.recorridos.updateOne({"nombre":"Delta Tour"}, {$push:{"stops":"Tigre"}})
```

#### 15.​Aumentar un 10% el precio de todos los recorridos.

```js
db.recorridos.updateMany({}, {$mul:{"precio":1.1}})
```

#### 16.​Eliminar el recorrido con nombre "Temporal Route".

```js
db.recorridos.deleteOne({"nombre": "Temporal Route"})
```

#### 17.​Crear el array de etiquetas (tags) para la ruta "Urban Exploration" y agregar el elemento "Gastronomia" a dicho arreglo.

```js
db.recorridos.updateOne({"nombre": "Urban Exploration"}, {$push:{"tags": "Gastronomia"}})
```

Operador | Qué Hace |
-------- | -------- |
$set | Modifica o crea un campo con el valor dado |
$push | Agrega un elemento a un array (lo crea si no existe) |
$mul | Multiplica el valor actual de un campo por el número dado |


### 2.3 Consultas con find()

#### 18.​Obtener la ruta con nombre "Museum Tour".

```js
db.recorridos.find({"nombre":"Museum Tour"})
```

#### 19.​Las rutas con precio superior a $60.000.

```js
db.recorridos.find({"precio": {$gt:60000}})
```

#### 20.​Las rutas con precio superior a $50.000 y con un total de kilometros mayor a 10.

```js
db.recorridos.find({"precio": {$gt:50000}, "totalKm": {$gt:10}})
```

#### 21.​Las rutas que incluyan el stop "San Telmo".

```js
db.recorridos.find({"stops": "San Telmo"})
```

#### 22.​Las rutas que incluyan el stop "Recoleta" y no el stop "Plaza Italia".

```js
//DOS POSIBILIDADES
db.recorridos.find({"stops": {$all:["Recoleta"], $nin:["Plaza Italia"]}})

db.recorridos.find({$and:[{"stops": "Recoleta"}, {"stops": {$nin:["Plaza Italia"]}}]})
```
aca el $and es obligatorio ya que filtramos por el mismo campo "stops"

#### 23.​El nombre y el total de km (si es que posee) de las rutas que incluyan el stop "Delta" y tengan un precio menor a $50.000.

```js
// find(filtro, proyeccion) la proyeccion con valor 1 muestra el campo, 0 lo excluye
db.recorridos.find({"stops": "Delta", "precio":{$lt:50000}}, {"nombre":1, "totalKm":1, "_id":0})
```

#### 24.​Las rutas que incluyen tanto "San Telmo" como "Recoleta" y "Avenida de Mayo" entre sus stops.

```js
db.recorridos.find({"stops": {$all:["San Telmo", "Recoleta", "Avenida de Mayo"]}})
```

#### 25.​Solo el nombre de las rutas que dispongan de mas de 5 stops.

```js
db.recorridos.find({$expr:{$gt:[{$size:"$stops"},5]}}, {"nombre":1, "_id":0})
```

#### 26.​Las rutas que no tengan definido el total de sus kilometros.

```js
db.recorridos.find({"totalKm":{$exists:false}})
```

#### 27.​Los nombres y el listado de stops de aquellas rutas que incluyen algun museo en sus recorridos.

```js
//$regex es para buscar patrones, aclaramos $options:"i" para que no sea case sensitive
db.recorridos.find({"stops":{$regex:"museo", $options:"i"}},{"nombre":1, "stops":1, "_id":0})
```

#### 28.​La cantidad total de elementos que posee la coleccion.

```js
db.recorridos.countDocuments()
```

Operador | Qué Hace |
-------- | -------- |
$gt / $lt | Mayor que / Menor que |
$ne / $nin | Distinto de / No está en la lista |
$all | El array contiene todos los valores indicados |
$exists | Verifica si el campo existe o no |
$size | Compara el tamaño de un array |
$regex | Busca por expresión regular en un string |

## Seccion 3 - Aggregation Framework

### 3.1 Configuracion

#### 29.​Crear una nueva base de datos llamada "tours2". Guardar el archivo "generador1.js" adjunto a esta practica y ejecutarlo con:

```js
load(<ruta del archivo 'generador1.js'>)
```

### 3.2 Consultas con Aggregation Framework

```js
db.coleccion.aggregate([
    { etapa1 },
    { etapa2 },
    { etapa3 }
])

//  Colección
//     ↓
//  $match (filtrar documentos, equivalente al WHERE)
//     ↓
//  $project (Selecciona, elimina o transforma campos, equivalente al SELECT)
//     ↓
//  $group (Agrupa documentos, equivalente a GROUP BY)
//     ↓
//  Resultado

// EJEMPLO
db.route.aggregate([
    {
        $match: {
            precio: { $gt: 50000 }
        }
    },
    {
        $project: {
            nombre: 1,
            precio: 1,
            _id: 0
        }
    },
    {
        $sort: {
            precio: -1
        }
    }
])
```

#### 30.​Obtener una muestra de 5 rutas aleatorias de la coleccion.

```js
db.route.aggregate([{$sample:{size:5}}])
```

#### 31.​Extender la consulta anterior para incluir en el resultado toda la informacion de cada una de las stops. Tener en cuenta que pueden ligarse por su codigo.

```js
// aca básicamente hacemos un "join" entre la coleccion de route y stop para obtener la informacion de stops matcheando con el code
// y mostrandolo luego en una lista de stops
db.route.aggregate([{$sample:{size:5}}, {$lookup:{from:"stop", localField:"stops", foreignField:"code", as:"stops"}}])
```

#### 32.​Obtener la informacion de las rutas (incluyendo la de sus stops) que tengan un precio mayor o igual a $90.000.

```js
db.route.aggregate([{$match:{price:{$gte:900}}}, {$lookup:{from:"stop", localField:"stops", foreignField:"code", as:"stops"}}])
```

#### 33.​Obtener la informacion de las rutas que tengan 5 stops o mas.

```js
db.route.aggregate([{$match:{$expr:{$gte:[{$size:"$stops"}, 5]}}}])
```

#### 34.​Obtener la informacion de las rutas que tengan incluido en su nombre el string "111".

```js
db.route.aggregate([{$match:{name:{$regex:/111/}}}])
```

#### 35.​Obtener solo las stops de la ruta con nombre "Route100".

```js
db.route.aggregate([{$match:{name:"route100"}}, {$lookup:{from:"stop", localField:"stops", foreignField:"code", as:"stopInfo"}}, {$project: {_id:0, stopInfo:1}}])
```

#### 36.​Obtener la informacion del stop que mas apariciones tiene en rutas.

```js
db.route.aggregate([{$unwind:"$stops"}, {$group:{_id:"$stops", cantidad:{$sum:1}}}, {$sort:{cantidad:-1}}, {$limit:1}, {$lookup:{from:"stop", localField:"_id", foreignField:"code", as:"stopInfo"}}])
```

#### 37.​Obtener las rutas con precio inferior a $15.000. Agregar a cada una una nueva propiedad que especifique la cantidad de stops que posee. Crear una nueva coleccion llamada "rutas_economicas" y almacenar estos elementos.

```js
db.route.aggregate([{$match:{price:{$lt:15000}}}, {$addFields: {stopsQty: {$size:"$stops"}}}, {$out:"rutas_economicas"}])
```

#### 38.​Por cada stop existente en la coleccion, calcular el precio promedio de las rutas que la incluyen.

```js
db.route.aggregate([{$unwind:"$stops"}, {$group:{_id:"$stops", promedio:{$avg:"$price"}}}, {$lookup:{from:"stop", localField:"_id", foreignField:"code", as:"stopInfo"}}, {$sort: {_id:1}}])
```

Etapa | Qué hace |
----- | -------- |
$sample | Devuelve N documentos aleatorios |
$lookup | Join entre colecciones |
$match | Filtra documentos (como find ) |
$unwind | Descompone un array en documentos individuales |
$group | Agrupa y calcula (sum, avg, etc.) |
$addFields | Agrega campos calculados |
$project | Selecciona qué campos mostrar |
$out | Guarda el resultado en una nueva colección |