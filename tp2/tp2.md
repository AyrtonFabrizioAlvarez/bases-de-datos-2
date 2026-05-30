# Trabajo Practico 2 - Spring Data JPA

## Sección 1 - Introducción a Spring Data JPA

### 1.1 Spring Data JPA y su lugar en el ecosistema

#### 1.​ ¿Qué es Spring Data JPA? ¿Qué problema resuelve respecto de usar Hibernate directamente? Describir dos situaciones del proyecto donde Spring Data JPA simplifica código que en la Práctica 1 requería implementación manual.

Spring Data JPA es una capa de abstracción superior que se sitúa sobre el ORM (en este caso, Hibernate). Su función principal es simplificar la construcción de la capa de acceso a datos al automatizar la creación de repositorios.

**El principal problema que resuelve es la eliminación del código repetitivo o "boilerplate". Al usar Hibernate directamente, el desarrollador debe:**  

- Gestionar manualmente los objetos SessionFactory y Session.
- Implementar clases concretas para cada repositorio y escribir manualmente los métodos básicos de persistencia.
- Codificar explícitamente las consultas HQL/JPQL, configurar sus parámetros y manejar los resultados.
- Spring Data JPA automatiza estas tareas mediante la generación de implementaciones de repositorios en tiempo de ejecución, permitiendo que el programador se enfoque solo en definir interfaces.

**Dos situaciones del proyecto donde simplifica el código**

- **Operaciones básicas de CRUD:**
  - **En la Práctica 1:** Se debía implementar manualmente cada método (como save(), findById() o delete()) en cada una de las clases de repositorio, utilizando métodos específicos de la Session de Hibernate como session.persist() o session.get().
  - **Con Spring Data JPA:** Basta con definir una interfaz que extienda de CrudRepository. Al hacer esto, las operaciones básicas se heredan y se implementan automáticamente, eliminando decenas de líneas de código repetitivo por cada repositorio del modelo.
- **Definición de consultas simples (Query Methods):**
  - **En la Práctica 1:** Para buscar un usuario por su nombre o email, era necesario crear un método, escribir el String de la consulta HQL (ej: "from User u where u.username = :name"), setear los parámetros y ejecutar la consulta.
  - **Con Spring Data JPA:** Se pueden utilizar los "Query Methods", donde el framework deriva automáticamente la consulta a partir del nombre del método definido en la interfaz (por ejemplo, findByUsername(String name) o findByEmail(String email)). Esto elimina por completo la necesidad de escribir código de implementación y cadenas de consulta manuales para los casos más comunes.

#### 2.​ Spring Data JPA no es un ORM sino una capa de abstracción sobre el ORM. Explicar la diferencia: ¿qué hace Spring Data JPA? ¿Qué sigue haciendo Hibernate internamente?

La relación entre Spring Data JPA e Hibernate es la de una capa de abstracción sobre un motor de ejecución; mientras el primero simplifica la interacción del desarrollador con la persistencia, el segundo realiza el trabajo técnico de fondo.

**Spring Data JPA se encarga de automatizar la creación de la capa de acceso a datos, eliminando el código repetitivo (boilerplate) que antes se escribía a mano. Sus funciones principales son:**

- **Generación de Repositorios:** A partir de interfaces, genera automáticamente las implementaciones concretas en tiempo de ejecución utilizando proxies dinámicos.
- **Derivación de Consultas:** Interpreta los nombres de los métodos (ej. findByUsername) para generar automáticamente las consultas correspondientes sin necesidad de escribir HQL.
- **Abstracción de Operaciones CRUD:** Provee implementaciones estándar para métodos como save(), findById() y deleteById() a través de interfaces como CrudRepository.
- **Gestión de Transacciones:** Provee el soporte para la propagación declarativa de transacciones mediante la anotación @Transactional.

**Hibernate continúa siendo el motor de persistencia (ORM) que realmente interactúa con la base de datos. Spring Data JPA delega en Hibernate las tareas fundamentales del mapeo:**

- **Manejo del Ciclo de Vida:** Hibernate sigue gestionando el Persistence Context y los estados de las entidades (managed, detached, etc.).
- **Generación de SQL:** Traduce las peticiones de Spring Data (ya sean Query Methods o @Query) al lenguaje SQL nativo específico del motor de base de datos utilizado.
- **Mapeo Objeto-Relacional:** Utiliza las anotaciones de mapeo (como @Entity, @OneToMany o estrategias de herencia) definidas en las clases para saber cómo transformar tablas en objetos y viceversa.
- **Hidratación de Objetos:** Se encarga de procesar los ResultSet de la base de datos y reconstruir los grafos de objetos Java en memoria.
- **Manejo de Caché y Proxies:** Gestiona las cachés de Nivel 1 y Nivel 2, así como el uso de proxies para el soporte de carga diferida (lazy loading).

#### 3.​ La siguiente tabla lista tareas relacionadas con la persistencia. Marcar con una X la columna de la tecnología que resuelve ese problema en la nueva implementación con Spring Data JPA:

Tarea | JDBC | Hibernate | Spring Data JPA |
----- | ---- | --------- | --------------- |
Abrir y cerrar la conexión a la base de datos | | X | |
Implementar save(), findById() y deleteById() | | | X |
Gestionar el ciclo de vida de las entidades (@Entity) | | X | |
Derivar una consulta a partir del nombre del método | | | X |
Manejar el pool de conexiones | | X | |
Propagar transacciones con @Transactional | | | X |
Generar la implementación del repositorio en runtime | | | X |
Mapear ResultSet a objetos Java | | X | |
Proveer soporte nativo de paginación (Pageable) | | | X |

### 1.2 Configuración del proyecto

#### 4.​ Listar los cambios que deben realizarse en el proyecto de la Práctica 1 para incorporar Spring Data JPA.

- **Actualización de Dependencias:** Se deben actualizar las librerías en el archivo pom.xml para incluir los módulos necesarios de Spring Data JPA en lugar de depender únicamente de Hibernate nativo.
- **Cambio en la Configuración:** Es necesario reemplazar la configuración programática de Hibernate (la clase HibernateConfiguration para obtener la SessionFactory) por una configuración declarativa en el archivo application.properties de Spring Boot.
- **Transformación de Repositorios:** Los repositorios dejan de ser clases concretas que interactúan manualmente con la Session de Hibernate para convertirse en interfaces que extienden de CrudRepository (u otras interfaces de la jerarquía de Spring Data).
- **Adaptación de la Capa de Servicio:** Se debe actualizar la implementación de los servicios (ej. ToursServiceImpl) para que utilicen la inyección de dependencias de las nuevas interfaces de repositorio.
- **Reemplazo de Operaciones de Persistencia:** En los servicios, se deben sustituir todas las llamadas directas a métodos de la Session (como session.save(), session.get() o session.createQuery()) por las operaciones equivalentes provistas automáticamente por los repositorios de Spring Data.
- **Migración de Consultas:** Las consultas manuales escritas en el cuerpo de los métodos de los repositorios anteriores deben reimplementarse utilizando Query Methods (derivación por nombre) o la anotación @Query directamente en las interfaces.
- **Infraestructura del Proyecto:** Se deben realizar ajustes en clases auxiliares de la infraestructura, como AppConfig, DBInitializer y los archivos de Testing, para que operen bajo el nuevo esquema de persistencia.

#### 5.​ En la Práctica 1 se configuraba Hibernate mediante una clase de configuración Java para obtener la SessionFactory. ¿Cómo se reemplaza esta configuración usando application.properties en Spring Boot?

La configuración de Hibernate mediante una clase Java para obtener la SessionFactory se reemplaza en Spring Boot por una configuración declarativa centralizada en el archivo application.properties.

**Este cambio implica los siguientes puntos fundamentales:**

- **Abstracción de la infraestructura:** Ya no es necesario que el desarrollador escriba código técnico para gestionar manualmente el ciclo de vida de la SessionFactory. Spring Boot, al detectar las dependencias de JPA en el proyecto, configura automáticamente el EntityManagerFactory (el equivalente en JPA a la SessionFactory) basándose en las propiedades definidas en el archivo.
- **Parámetros del DataSource:** La conexión a la base de datos se establece definiendo propiedades estándar como la URL, el nombre de usuario, la contraseña y el driver específico del motor de base de datos.
- **Configuración del ORM:** Se definen de forma declarativa aspectos críticos de Hibernate que antes se codeaban, tales como:
- **El dialecto de Hibernate:** que permite al framework generar el SQL correcto para el motor de base de datos específico que se esté utilizando.
- **La propiedad ddl-auto:** que controla si Hibernate debe crear, actualizar, validar o no realizar cambios en el esquema de tablas de la base de datos al arrancar la aplicación.
- **Centralización y Flexibilidad:** Este enfoque permite que toda la configuración de persistencia resida en un único lugar fuera del código compilado, lo que facilita enormemente el cambio de parámetros entre diferentes entornos (como pasar de una base de datos local de desarrollo a una de producción) sin modificar la lógica del sistema.

#### 6.​ Describir las propiedades más relevantes de Spring Data JPA que deben configurarse en application.properties para este proyecto. Incluir al menos: datasource (url, username, password, driver), dialecto de Hibernate, y la propiedad que controla si Hibernate debe crear, actualizar o validar el esquema. ¿Cuál de estos valores conviene usar durante el desarrollo?

En el archivo application.properties de Spring Boot se centraliza la configuración declarativa de la persistencia.

**Propiedades del Datasource *(Estas propiedades establecen la conexión física con el motor de base de datos:)***

- **spring.datasource.url:** Especifica la ubicación de la base de datos (por ejemplo, jdbc:mysql://localhost:3306/tours).
- **spring.datasource.username:** El nombre de usuario definido en el motor de base de datos para la aplicación.
- **spring.datasource.password:** La contraseña correspondiente a dicho usuario.
- **spring.datasource.driver-class-name:** Clase del driver JDBC específica para el motor utilizado (por ejemplo, para MySQL o HSQLDB).

**Dialecto de Hibernate**

- **spring.jpa.properties.hibernate.dialect:** Esta propiedad es fundamental ya que le indica a Hibernate qué variante de SQL debe generar. Al existir sutiles diferencias entre motores (como el manejo de palabras reservadas o tipos de datos), el dialecto asegura que el SQL sea compatible con la base de datos configurada (ej. org.hibernate.dialect.MySQL8Dialect).

**Control del Esquema (ddl-auto)**

- La propiedad **spring.jpa.hibernate.ddl-auto** (o su equivalente hibernate.hbm2ddl.auto) controla cómo debe reaccionar Hibernate frente al esquema de tablas al iniciar la aplicación. Los valores principales son:
  - **create:** Crea el esquema desde cero cada vez que arranca la aplicación, borrando cualquier dato previo.
  - **update:** Compara el modelo de objetos con las tablas existentes y aplica solo los cambios necesarios (como agregar columnas nuevas) sin borrar los datos.
  - **validate:** Simplemente comprueba que las tablas coincidan con el mapeo de las entidades; si hay diferencias, la aplicación no arranca.
  - **none:** No realiza ninguna acción sobre la base de datos.

**Recomendación para el desarrollo**
- Durante la etapa de desarrollo de este proyecto, conviene utilizar el valor update o create.
- Se recomienda create (o create-drop) cuando se están realizando cambios estructurales profundos en las entidades y jerarquías, permitiendo que las tablas se regeneren correctamente.
- Se recomienda update una vez que el mapeo es más estable, para poder conservar los datos de prueba entre reinicios de la aplicación.

*Importante: En entornos de producción, nunca debe usarse create o update; en esos casos se utiliza validate o none para evitar alteraciones accidentales de los datos reales*

### 1.3 La interfaz CrudRepository y la jerarquía de repositorios

#### 7.​ ¿Qué es CrudRepository? ¿De qué interfaz hereda y qué operaciones provee automáticamente?

**CrudRepository** es una interfaz central del framework Spring Data JPA que actúa como el punto de entrada para las operaciones de persistencia en una aplicación. Su propósito es automatizar la creación de la capa de acceso a datos, permitiendo al desarrollador definir simplemente una interfaz en lugar de implementar manualmente una clase con código de Hibernate.  

**CrudRepository** hereda directamente de la interfaz base **Repository<T, ID>**, que es la raíz de toda la jerarquía de repositorios en Spring Data. Esta jerarquía continúa luego con interfaces más especializadas como **PagingAndSortingRepository** y **JpaRepository**.  

**Al extender esta interfaz, Spring Data JPA genera en tiempo de ejecución las implementaciones para las operaciones fundamentales de CRUD (Create, Read, Update, Delete). Las operaciones más relevantes que provee son:** 

- **save(S entity):** Utilizada tanto para persistir un objeto nuevo (INSERT) como para actualizar uno existente (UPDATE).
- **findById(ID id):** Recupera una entidad por su clave primaria.
- **findAll():** Retorna todas las instancias de la entidad almacenadas en la base de datos.
- **deleteById(ID id):** Elimina el registro correspondiente al identificador provisto.
- **count():** Retorna la cantidad total de entidades.
- **existsById(ID id):** Indica si existe una entidad con el ID especificado.

#### 8.​ ¿Que agrega cada nivel de la jerarquía respecto del anterior? Describir brevemente las diferencias entre CrudRepository, PagingAndSortingRepository y JpaRepository, indicando que operaciones o capacidades incorpora cada uno

- **CrudRepository:** Es el nivel base de funcionalidad operativa. Provee las operaciones fundamentales de CRUD (Create, Read, Update, Delete) sobre una entidad. Incorpora métodos automáticos como save(), findById(), findAll(), count() y deleteById(), permitiendo gestionar la persistencia básica sin escribir código manual.
- **PagingAndSortingRepository:** Hereda de CrudRepository y agrega la capacidad de gestionar grandes volúmenes de datos de manera eficiente. Su principal aporte es el soporte nativo para paginación (Pageable) y ordenamiento (Sort). Esto permite recuperar los datos en "páginas" o bloques (por ejemplo, los primeros 10 resultados) y definir el orden de los mismos (por ejemplo, por fecha descendente) sin necesidad de modificar las consultas manualmente.
- **JpaRepository:** Es el nivel más alto y especializado de la jerarquía. Además de heredar todas las capacidades de CRUD, paginación y ordenamiento, incorpora operaciones específicas de JPA para un control más fino del contexto de persistencia. Entre sus capacidades adicionales se encuentran métodos para sincronizar cambios inmediatamente con la base de datos (flush()), realizar eliminaciones masivas optimizadas (deleteInBatch()) y devolver colecciones de tipo List en lugar de Iterable, lo que facilita su uso en la capa de servicio.

#### 9.​ Crear las interfaces de repositorio para las entidades del modelo extendiendo CrudRepository. Indicar los parámetros de tipo correctos (entidad e ID) para cada una: PurchaseRepository, RouteRepository, UserRepository, ServiceRepository, SupplierRepository y ReviewRepository.

#### 10.​¿Cómo genera Spring Data JPA la implementación concreta de los repositorios en tiempo de ejecución? ¿Qué rol juega el proxy dinámico de Java en este mecanismo?

Spring Data JPA genera la implementación concreta de los repositorios en tiempo de ejecución utilizando el patrón Proxy, lo que permite que el desarrollador solo deba definir interfaces sin necesidad de escribir clases de implementación manual.

**Generación de implementaciones en tiempo de ejecución**

Cuando la aplicación arranca, Spring escanea el proyecto en busca de interfaces que extienden de Repository (o sus subinterfaces como CrudRepository). En lugar de requerir un archivo de clase física en el disco para cada repositorio, el framework utiliza la infraestructura de proxies dinámicos de Java (o librerías como CGLIB) para crear un objeto concreto que implementa esa interfaz "al vuelo".  
Este proceso permite que métodos como save(), findById() o las consultas derivadas por nombre (Query Methods) tengan una implementación real sin que el programador haya escrito una sola línea de código imperativo para ellas.

**Rol del Proxy Dinámico de Java**

**El proxy dinámico actúa como un intermediario o sustituto (surrogate/placeholder) de la interfaz del repositorio, y desempeña las siguientes funciones críticas:**

- **Intercepción de llamadas:** El proxy intercepta cada mensaje (llamada a un método) enviado al repositorio. Al ser una interfaz, los métodos no tienen cuerpo; el proxy captura la invocación y decide qué lógica ejecutar según el tipo de método.
- **Delegación de operaciones CRUD:** Si el método invocado es una operación estándar (como save), el proxy delega la ejecución a una implementación interna del framework (como SimpleJpaRepository) que sabe cómo interactuar con el EntityManager de Hibernate.
- **Traducción de Query Methods:** Si se invoca un método derivado (ej: findByUsername), el proxy analiza el nombre del método en tiempo de ejecución, genera la consulta JPQL/HQL correspondiente y la envía al motor de persistencia.
- **Ejecución de @Query:** Para métodos anotados con @Query, el proxy recupera la cadena de consulta definida por el desarrollador, vincula los parámetros y solicita a Hibernate su ejecución.
- **Transparencia y Desacoplamiento:** El uso de proxies garantiza que el sistema orientado a objetos sea independiente de la tecnología de persistencia, permitiendo que la capa de servicio trabaje con los repositorios como si fueran colecciones en memoria, manteniendo el principio de independencia.

#### 11.​¿Que diferencia hay entre save() en Spring Data JPA y session.save() / session.merge() en Hibernate directo? ¿Cómo decide Spring Data JPA si debe hacer un INSERT o un UPDATE?

- **save() en Spring Data JPA:** Es un método unificado. A diferencia de Hibernate, donde el desarrollador debe elegir el mensaje técnico correcto según el estado del objeto, Spring Data JPA ofrece una única operación que abstrae esta complejidad, encargándose internamente de decidir si debe persistir un objeto nuevo o sincronizar uno existente.
- **session.save() en Hibernate:** Este método está diseñado específicamente para volver persistente una instancia transitoria (nueva), forzando un INSERT en la base de datos y devolviendo el identificador generado.
- **session.merge() en Hibernate:** Se utiliza principalmente para reincorporar objetos que están en estado desasociado (detached) al contexto de persistencia. Lo que hace es copiar el estado del objeto pasado a una instancia manejada por la sesión, lo que habitualmente deriva en un UPDATE.

**¿Cómo decide Spring Data JPA si hacer INSERT o UPDATE?**

- **Evaluación del Identificador (@Id):** Por defecto, el framework inspecciona el atributo marcado como clave primaria. Si el valor del ID es null (o 0 en tipos primitivos), Spring Data asume que el objeto es nuevo y llama internamente a EntityManager.persist(), lo que genera un INSERT.
- **Entidades con ID presente:** Si el objeto ya tiene un valor en su identificador, Spring Data JPA asume que la entidad podría existir en la base de datos (estado detached). En este caso, invoca internamente a EntityManager.merge(). Hibernate entonces verifica si el objeto existe; si existe, sincroniza los cambios mediante un UPDATE; si no existe, intenta un INSERT con ese ID.
- **Interfaz Persistable:** Para casos más complejos (como cuando se usan IDs asignados manualmente que no son nulos), una entidad puede implementar la interfaz Persistable de Spring para definir su propia lógica de cuándo debe considerarse como nueva a través del método isNew().

## Sección 2 - Repositorios con Spring Data JPA

### 2.1 Migración de repositorios

#### 12.​Comparar el código de PurchaseRepository de la Práctica 1 (con Session de Hibernate) con el nuevo PurchaseRepository de Spring Data JPA. ¿Cuántas líneas de código se eliminaron? ¿Qué operaciones ya no es necesario implementar manualmente?

Básicamente se eliminaron las operaciones que nos provee la interfaz CrudRepository, el resto cuando llegue el momento de implementarlo lo ahremos con QueryMethod o con Query

#### 13.​En la Práctica 1, los repositorios gestionaban internamente la Session. ¿Que recibe ahora un servicio que necesita usar un repositorio Spring Data? ¿Cómo se declara esa dependencia? ¿Dónde queda ahora la lógica de apertura y cierre de sesiones?

En lugar de una clase de repositorio manual que gestiona la Session, el servicio recibe un proxy dinámico. Este objeto es una implementación concreta generada automáticamente por el framework en tiempo de ejecución para la interfaz del repositorio definida.

La dependencia se declara como una variable de la interfaz del repositorio dentro de la clase de servicio. Esta variable se inicializa mediante Inyección de Dependencias, preferentemente a través de un constructor que reciba las interfaces de los repositorios necesarios como parámetros. @Service es fundamental aquí. Esta anotación registra la clase en el contenedor de Spring, permitiendo que el framework realice la Inyección de Dependencias en el constructor que declaramos de forma automática (ya no utilizamos @Bean e instanciamos el servicio en appconfig). Sin @Service, Spring no "maneja" el objeto y no inyectaría nada.

**¿Dónde queda la lógica de apertura y cierre de sesiones?:**

Esta lógica técnica ha sido abstraída y automatizada por la infraestructura de Spring Data JPA y Hibernate. Spring se encarga ahora de gestionar el ciclo de vida de la sesión "bajo cuerda", coordinándolo con los límites de la transacción definidos en la capa de servicio. Generalmente, esto se logra mediante el uso de la anotación @Transactional, que instruye al contenedor para abrir la sesión al iniciar el método y cerrarla (confirmando o deshaciendo los cambios) al finalizar la unidad de trabajo.

### 2.2 Query Methods

#### 14.​¿Cómo funciona la generación de consultas por nombre de método en Spring Data JPA? Describir las palabras clave principales que puede interpretar (findBy, existsBy, countBy, deleteBy) y cómo se combinan con los atributos de la entidad.

Este mecanismo funciona mediante un analizador (parser) que Spring Data JPA utiliza al arrancar la aplicación. El framework escanea las interfaces de repositorio y, por cada método que sigue una convención de nombres específica, realiza lo siguiente:

- **Descompone el nombre del método:** Identifica el prefijo (la operación) y el resto del nombre (los criterios de filtrado).
- **Deriva la consulta**: Traduce esa estructura a una consulta JPQL/HQL válida basándose en los atributos de la entidad asociada al repositorio.
- **Generación en Runtime:** Utilizando el mecanismo de proxies dinámicos de Java, Spring genera una implementación concreta para ese método "al vuelo", vinculando los parámetros del método con los marcadores de posición de la consulta generada.

**Palabras clave principales**

- **findBy...:** Es la palabra clave más común y se utiliza para recuperar o seleccionar entidades. Genera internamente una sentencia SELECT. Por ejemplo, findByUsername(String name) buscará registros donde el atributo username coincida con el parámetro.
- **existsBy...:** Se utiliza para verificar la existencia de una entidad que cumpla con los criterios especificados. Retorna un valor booleano (true si existe, false si no).
- **countBy...:** Se emplea para obtener la cantidad total de registros que satisfacen una condición dada. Genera una consulta de tipo SELECT COUNT(...).
- **deleteBy... (o removeBy...):** Permite realizar operaciones de borrado basadas en condiciones. Genera internamente una sentencia DELETE.

**Combinación con atributos de la entidad**

Las palabras clave se combinan con los nombres de los atributos de la clase de dominio (entidad) siguiendo la convención CamelCase:  

- **Correspondencia directa:** Si la entidad tiene un atributo email, el método debe llamarse findByEmail.
- **Navegación de relaciones:** El analizador puede incluso "navegar" por las relaciones de la entidad. Si una Purchase tiene un User y este tiene un username, se puede definir findByUserUsername(String name).
- **Operadores lógicos y de comparación:** Se pueden combinar múltiples atributos y condiciones utilizando palabras adicionales como And, Or, Between, LessThan, Like o IgnoreCase. Por ejemplo: findByPriceLessThanAndNameLike(...).

#### 15.​¿Cómo se pasan los parametros a un Query Method? ¿Cómo hace Spring Data JPA para asociar cada parámetro del método con la condición correspondiente en la consulta? ¿Qué ocurre si el orden de los parámetros no coincide con el orden de las condiciones en el nombre del método?

- **Paso de parámetros:** Los parámetros se pasan como argumentos estándar en la firma del método definido en la interfaz del repositorio. Por ejemplo: List<User> findByEmailAndPassword(String email, String password).
- **Asociación de parámetros:** Spring Data JPA utiliza un mecanismo de vinculación posicional por defecto. Al analizar el nombre del método de izquierda a derecha, identifica las propiedades de la entidad que forman parte del filtro y las asocia con los parámetros del método en el mismo orden de aparición.
En el ejemplo findByEmailAndPassword, el primer parámetro del método se asocia automáticamente a la condición Email y el segundo a Password.
- **Discordancia en el orden:** Si el orden de los parámetros en la firma del método no coincide con el orden de las condiciones expresadas en su nombre, la consulta fallará lógicamente o generará errores de tipo.
  - Debido a que la asociación es estrictamente por posición, si defines findByUsernameAndEmail(String email, String username), Spring intentará buscar el valor del parámetro email en la columna del username de la base de datos y viceversa.
  - Si los tipos de datos son compatibles (por ejemplo, ambos son String), la consulta se ejecutará pero no devolverá los resultados esperados; si los tipos son incompatibles, se producirá una excepción en tiempo de ejecución al intentar ejecutar la consulta.

#### 16.​Investigar y para cada uno de los siguientes requerimientos indicar si es posible resolverlo con un Query Method y escribir la firma del método correspondiente. Si no es posible, explicar por qué:

- a.​ Buscar todas las Purchase de un usuario dado su username.
  - public List<Purchase> findByUserUsername(String username)
- b.​ Verificar si existe alguna Purchase para una Route dada.
  - public boolean existByROute(Route route)
- c.​ Contar cuantas Review tienen un rating mayor o igual a un valor dado.
  - public int countByRatingGreaterThanEqual(int rating)
- d.​ Obtener todas las Route cuyo precio sea menor a un valor dado, ordenadas por nombre.
  - public List<Route> findByPriceLessThanOrderByNameAsc(float price)
- e.​ Buscar un User por su email.
  - public User findByEmail(String email)
- f.​ Obtener los top 3 Route con mayor cantidad de Purchase.
  - entiendo que esto no se puede, o al menos yo lo resolveria con group by y count, entonces funciones de agregacion y agrupamiento van con query

#### 17.​Investigar cuales son las palabras clave (keywords) disponibles en Spring Data JPA para construir Query Methods. ¿Qué tipos de condiciones, comparaciones y operadores lógicos soporta?

- **Introducciones de consulta**
  - **findBy...By / readBy...By / getBy...By:** Inician una consulta de selección (SELECT).
  - **existsBy...By:** Verifica la existencia de un registro y devuelve un booleano.
  - **countBy...By:** Realiza un conteo de los registros que cumplen los criterios (SELECT COUNT).
  - **deleteBy...By / removeBy...By:** Realiza una operación de borrado físico (DELETE).
- **Operadores Lógicos**
  - **And:** Une dos condiciones que deben cumplirse simultáneamente (ej: findByEmailAndStatus).
  - **Or:** Une dos condiciones donde basta con que se cumpla una de ellas (ej: findByUsernameOrEmail).
- **Condiciones y Comparadores**
  - **Igualdad y Rango:**
    - **(Is)Equal:** Comportamiento por defecto si no se especifica nada (ej: findByAge).
    - **Between:** Para valores dentro de un rango inclusivo.
    - **LessThan / LessThanEqual:** Menor que o menor/igual que.
    - **GreaterThan / GreaterThanEqual:** Mayor que o mayor/igual que.
    - **Before / After:** Específicos para comparar fechas y tiempos.
  - **Nulidad:**
    - **IsNull / IsNotNull:** Para verificar si una columna es nula o no.
  - **Búsqueda de Patrones (Strings):**
    - **Like / NotLike:** Búsqueda con comodines manuales.
    - **StartingWith / EndingWith:** Busca cadenas que comiencen o terminen con el valor dado.
    - **Containing:** Busca el valor en cualquier parte de la cadena (equivalente a %valor%).
    - **IgnoreCase:** Ignora mayúsculas y minúsculas durante la comparación.
  - **Colecciones y Booleanos:**
    - **True / False:** Para atributos de tipo booleano.
    - **In / NotIn:** Verifica si el atributo coincide con alguno de los valores de una colección enviada como parámetro.
- **Ordenamiento**
  - **OrderBy:** Seguido del atributo y la dirección (Asc o Desc). Ejemplo: findByPriceLessThanOrderByNameAsc(float price).

#### 18.​¿Qué limitaciones tienen los Query Methods? Describir al menos tres casos del modelo donde esta técnica resulta insuficiente y es necesario otro enfoque. Describa los otros enfoques posibles para implementar dichas consultas.

**Limitaciones de los Query Methods**

- **Complejidad en los nombres:** A medida que se agregan más condiciones y operadores lógicos (And, Or), los nombres de los métodos se vuelven extremadamente largos y difíciles de leer o mantener.
- **Agrupaciones y Funciones de Agregación:** No pueden manejar cláusulas complejas de agrupamiento (GROUP BY) o condiciones sobre grupos (HAVING) de forma nativa en el nombre del método.
- **JOINs No Convencionales:** Aunque permiten navegar relaciones simples (como findByUserUsername), resultan insuficientes para joins complejos que involucren múltiples entidades o condiciones de unión específicas.
- **Subconsultas:** No tienen la capacidad de representar subconsultas dentro de su estructura de nomenclatura.
- **Funciones Propietarias:** No pueden invocar funciones específicas de un motor de base de datos que no estén contempladas en la abstracción de Spring Data JPA.

**Casos del modelo donde resultan insuficientes**

- **getTopNSuppliersInPurchases(int n):** Este requerimiento exige contar la cantidad de apariciones de cada proveedor en las compras, agrupar por proveedor, ordenar por ese conteo de forma descendente y limitar el resultado a los primeros "n". El procesamiento de conteos agrupados excede la capacidad de derivación de nombres.
- **getMostDemandedService():** Para obtener el servicio más demandado es necesario sumar las cantidades (quantity) de cada ItemService agrupando por Service, encontrar el máximo de esa suma y retornar el objeto correspondiente. Esta lógica de agregación aritmética sobre una relación es imposible de expresar en un simple nombre de método.
- **getTop3RoutesWithMaxRating():** Este caso requiere calcular el promedio de ratings de todas las Review asociadas a cada Route para luego devolver las tres rutas con el promedio más alto. Al implicar un cálculo de promedio (AVG) sobre entidades relacionadas y un ordenamiento basado en ese resultado calculado, la técnica de Query Methods es insuficiente.


**Enfoques alternativos para implementar dichas consultas**

- **Anotación @Query con JPQL/HQL (Recomendado):** Permite escribir la consulta de forma manual utilizando el lenguaje de consulta orientado a objetos. Es más claro y mantenible para JOINs complejos, agrupaciones y subconsultas, ya que trabaja sobre las entidades y sus atributos en lugar de las tablas físicas.
- **@Query con nativeQuery = true:** Se utiliza cuando es estrictamente necesario ejecutar SQL nativo para aprovechar características específicas del motor de base de datos (como funciones propietarias) que JPQL no conoce directamente.
- **Criteria API:** Es un enfoque programático donde la consulta se construye utilizando objetos de Java en lugar de cadenas de texto. Es más robusto para consultas dinámicas (donde los filtros cambian en tiempo de ejecución) y permite detectar errores de sintaxis en tiempo de compilación, aunque es más verborrágico que JPQL.
- **Iteradores de Modelo (Puro OO):** En casos muy específicos, se podría recuperar un objeto raíz (como el Tracker) y navegar sus colecciones utilizando iteradores estándar de Java para filtrar la información en memoria, aunque este enfoque suele ser ineficiente para grandes volúmenes de datos.

### 2.3 Consultas con @Query

#### 19.​En la Práctica 1 se utilizaron consultas HQL (Hibernate Query Language). ¿Que diferencia hay entre HQL y JPQL (Java Persistence Query Language)? ¿Son intercambiables? ¿Cual de los dos acepta @Query por defecto?

**Diferencia entre HQL y JPQL:**

- **HQL (Hibernate Query Language)** es el lenguaje de consulta propio y específico del framework Hibernate. Es un lenguaje potente que permite trabajar con clases y sus atributos en lugar de tablas y columnas.
- **JPQL (Java Persistence Query Language)** es el lenguaje de consulta definido por el estándar JPA (Java Persistence API). JPQL se creó basándose fuertemente en HQL para estandarizar la forma de consultar objetos en Java.
- En términos prácticos, HQL es considerado un superset de JPQL; esto significa que Hibernate (al ser la implementación de JPA) puede interpretar ambos, pero HQL suele incluir algunas funciones o capacidades técnicas adicionales que no están contempladas en el estándar más genérico de JPQL.

**Intercambiabilidad:**

Para la mayoría de las consultas estándar (SELECT, FROM, WHERE), son prácticamente intercambiables debido a que comparten la misma estructura y sintaxis básica.  
Sin embargo, si se utiliza una característica muy específica de Hibernate en una consulta HQL, esta dejaría de ser una consulta JPQL estándar y no sería portable a otros proveedores de JPA que no sean Hibernate.

**¿Cuál acepta @Query por defecto?:**

La anotación @Query en Spring Data JPA acepta JPQL por defecto.  
Cuando escribes una consulta dentro de @Query, el framework asume que estás utilizando el lenguaje estándar de JPA para operar sobre tus entidades. Para utilizar el lenguaje de la base de datos física, se debe especificar explícitamente el parámetro nativeQuery = true.

#### 20.​¿Que diferencia hay entre una consulta @Query con JPQL y una con nativeQuery = true? ¿Cuándo conviene cada una? Dar un ejemplo concreto del modelo para cada caso.

**Diferencias fundamentales**

**JPQL (Java Persistence Query Language):**

- Es el lenguaje de consulta por defecto en la anotación @Query.
- Utiliza los nombres de las entidades y sus atributos, no los nombres de las tablas físicas.
- Permite la navegación de relaciones mediante el operador punto (.) de forma natural (path expressions).
- Es portable, ya que Hibernate se encarga de traducirlo al dialecto SQL específico del motor de base de datos que se esté utilizando.

**Native Query (SQL Nativo):**

- Se activa mediante el parámetro nativeQuery = true.
- Se escribe en el dialecto específico del motor de base de datos (MySQL, Oracle, etc.).
- Utiliza los nombres reales de tablas y columnas tal como están definidos en el esquema SQL.
- No es portable; si se cambia de motor de base de datos, la consulta podría fallar si usa funciones propietarias.

**¿Cuándo conviene cada una?**

**Conviene JPQL cuando:**

- Se desea mantener la independencia tecnológica del proyecto y la transparencia del modelo.
- La consulta se puede expresar fácilmente navegando el grafo de objetos y relaciones.
- Es el enfoque preferido por su mantenibilidad y legibilidad dentro de un entorno orientado a objetos.

**Conviene SQL Nativo cuando:**

- Existen razones de performance crítica donde la sobrecarga de traducción del ORM es inaceptable.
- Se necesita invocar funciones propietarias del motor de base de datos que no están contempladas en el estándar JPA.
- Se requiere realizar operaciones masivas o reportes muy complejos sobre tablas no mapeadas.
- Es necesario omitir filtros globales impuestos por Hibernate, como la anotación @Where utilizada en el borrado lógico.

**Ejemplos concretos del modelo**

- Ejemplo con JPQL: Obtener las compras de un usuario navegando la relación.
  - Aquí se usa la clase Purchase y el atributo user.username, tratando a la base de datos como una colección de objetos.
- Ejemplo con Native Query: Consultar usuarios inactivos (borrados lógicamente) para un panel de administración, asumiendo que la entidad User tiene un filtro @Where que los oculta normalmente.
  - En este caso, la consulta nativa permite "saltarse" el filtro automático de JPA que normalmente excluiría estos registros en las consultas JPQL estándar.

#### 21.​¿Cómo se pasan parámetros a una consulta @Query? Describir y comparar las dos formas: parámetros posicionales (?1, ?2) y parámetros nombrados (@Param). ¿Cuál es la forma recomendada y por qué?

- **Parámetros Posicionales (?1, ?2, ...)**
  - En esta modalidad, los parámetros se identifican mediante un signo de interrogación seguido de un índice numérico que representa su posición en la firma del método (comenzando en 1).
  - Funcionamiento: Spring asocia el primer argumento del método con ?1, el segundo con ?2, y así sucesivamente.
  - Riesgo: Si el desarrollador cambia el orden de los parámetros en el método Java pero olvida actualizar los índices en la cadena de la consulta, se producirán errores lógicos o de tipo en tiempo de ejecución.
- **Parámetros Nombrados (@Param)**
  - Este mecanismo utiliza nombres específicos precedidos por dos puntos (:) dentro de la consulta. Para vincular estos nombres con los argumentos del método, se utiliza la anotación @Param.
  - Funcionamiento: El vínculo se establece por el nombre de la variable, ignorando por completo el orden en el que aparecen en el método.

**Comparación y Recomendación**

Característica | Parámetros Posicionales | Parámetros Nombrados (@Param)
Sintaxis | Más breve (?1) | Más descriptiva (:nombre)
Mantenibilidad | Baja (sensible al orden) | Alta (independiente del orden)
Legibilidad | Difícil en consultas largas | Muy clara y fácil de entender
Robustez | Propensa a errores por refactorización | Segura ante cambios en la firma del método

**La forma recomendada es el uso de parámetros nombrados (@Param)**

- **Robustez ante cambios:** Permite refactorizar el código Java (cambiar el orden o agregar parámetros intermedios) sin romper la consulta SQL/JPQL asociada.
- **Claridad del código:** En consultas complejas con múltiples filtros, es mucho más fácil identificar qué valor se está inyectando en cada parte de la sentencia cuando se usan nombres descriptivos en lugar de índices numéricos.
- **Prevención de errores:** Elimina la posibilidad de errores de "desplazamiento" (donde un valor termina en la columna equivocada porque se movió un parámetro en el método), una de las causas más comunes de fallos en consultas manuales.

### 2.4 Paginación y ordenamiento

#### 22.​¿Qué es la interfaz Pageable? ¿Cómo se construye una instancia de Pageable? ¿Qué información encapsula (número de página, tamaño, ordenamiento)?

**¿Qué es la interfaz Pageable?**

Es una interfaz perteneciente al framework Spring Data que define un contrato para solicitar subconjuntos de datos (páginas). Se utiliza comúnmente como parámetro en los métodos de los repositorios que extienden de PagingAndSortingRepository o JpaRepository para indicarle al motor de persistencia qué segmento de la información debe recuperar.

**¿Qué información encapsula?**

- **Número de página (Page number):** Indica el índice de la página que se desea recuperar (comenzando generalmente por 0 para la primera página).
- **Tamaño de la página (Page size):** Define la cantidad máxima de registros o elementos que debe contener cada página.
- **Ordenamiento (Sort):** Contiene los criterios de ordenación (atributos y dirección ascendente/descendente) que deben aplicarse a la consulta antes de segmentar los resultados.

**¿Cómo se construye una instancia de Pageable?**

- Para construir una instancia se suelen utilizar métodos de fábrica estáticos:  
- PageRequest.of(int page, int size): Crea una petición de página simple.
- PageRequest.of(int page, int size, Sort sort): Crea una petición que incluye criterios de ordenamiento específicos.
- En la capa de servicio, esta instancia se pasa como argumento al método del repositorio para obtener un objeto de tipo Page<T> o Slice<T>.

#### 23.​¿Que diferencia hay entre los tipos de retorno Page<T> y Slice<T>? ¿Cuándo conviene cada uno? ¿Qué consulta adicional ejecuta Page<T> que Slice<T> no ejecuta?

- **Slice<T>:**
  - **Qué hace:** Solo sabe si hay un segmento de datos siguiente disponible o no.
  - **Cómo funciona:** Para determinar si existe una página siguiente, Spring Data JPA solicita un elemento adicional al tamaño de página solicitado (ej. si pides 10, recupera 11). Si el elemento 11 existe, marca la "rebanada" como que tiene una página siguiente.
  - **Información:** No conoce el total de elementos en la base de datos ni cuántas páginas totales existen.

- **Page<T>:**
- **Qué hace:** Es una extensión de Slice que, además de saber si hay una página siguiente, conoce el número total de elementos y de páginas.
- **Cómo funciona:** Además de recuperar los datos del segmento actual, necesita calcular el tamaño total del conjunto de resultados.

**¿Qué consulta adicional ejecuta Page<T>?**

- **Page<T>** ejecuta una consulta adicional de tipo COUNT. Para poder decirte que hay "500 resultados en total (50 páginas)", el framework debe emitir un SELECT COUNT(*) FROM ... con los mismos filtros que tu consulta original.
- **Slice<T>** NO ejecuta esta consulta COUNT, lo que la hace mucho más eficiente en tablas con millones de registros donde el conteo total es una operación costosa.

**¿Cuándo conviene usar cada uno?**

- **Conviene usar Page<T> cuando:**
  - La interfaz de usuario requiere mostrar una barra de navegación con números de página específicos (ej. "Página 1 de 20").
  - Es indispensable que el usuario sepa cuántos resultados totales existen para su búsqueda.
- **Conviene usar Slice<T> cuando:**
  - Estás implementando un mecanismo de "Scroll Infinito" o un botón de "Cargar más". En estos casos, al usuario no le importa el número total de páginas, solo si puede seguir bajando.
  - La tabla es extremadamente grande y la consulta COUNT adicional penaliza notablemente el tiempo de respuesta.

#### 24.​Mostrar como se invocará desde la capa de servicio un método paginado para obtener la segunda página de compras de un usuario, con 10 resultados ordenados por fecha descendente.

**Definición en el Repositorio**  
Primero, el repositorio (PurchaseRepository) debe tener un método que acepte un parámetro de tipo Pageable. Este parámetro es el que encapsula toda la información de paginación y ordenamiento.

```java 
// En PurchaseRepository
Page<Purchase> findByUser(User user, Pageable pageable);
```

**Invocación desde la Capa de Servicio**  
En la clase de servicio (ToursServiceImpl), se debe construir una instancia de Pageable (usualmente mediante la clase PageRequest) y pasarla al repositorio.

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// 1. Definir el ordenamiento: por atributo "date" de forma descendente
Sort sort = Sort.by("date").descending();

// 2. Construir el Pageable: 
Pageable pageable = PageRequest.of(1, 10, sort);

// 3. Invocar al repositorio
Page<Purchase> segundaPagina = purchaseRepo.findByUser(user, pageable);
return segundaPagina;
```

**Puntos clave de la implementación:**

- **Índice de página:** Como se mencionó en nuestra conversación previa, el número de página en Spring Data es 0-based. Para pedir la "segunda página", el parámetro debe ser 1. (PageRequest.of(1, 10, sort))
- **Encapsulamiento:** La interfaz Pageable abstrae los tres datos solicitados: el número de página (1), el tamaño (10) y el criterio de ordenamiento (fecha desc).
- **Tipo de retorno:** Se utiliza Page<T> si se necesita conocer el total de elementos y páginas disponibles (lo cual requiere una consulta COUNT adicional), o Slice<T> si solo se necesita saber si existe una página siguiente.

#### 25.​¿Cómo se agrega ordenamiento a un Query Method sin usar Pageable? ¿Qué palabra clave se usa en el nombre del método? Mostrar un ejemplo concreto con el modelo.

Para agregar ordenamiento a un Query Method sin utilizar el objeto Pageable, se debe incorporar la lógica directamente en el nombre del método en la interfaz del repositorio utilizando una convención específica.  

- **Palabra clave principal:** Se utiliza la palabra clave OrderBy inmediatamente después de los criterios de búsqueda (o después del nombre de la entidad si no hay filtros).
- **Sentido del orden:** Se pueden añadir las palabras clave Asc (ascendente) o Desc (descendente) después del nombre del atributo para especificar la dirección. Si no se especifica, Spring Data JPA aplica un orden ascendente por defecto.
- **Combinación de atributos:** Es posible ordenar por múltiples atributos concatenándolos después del OrderBy. Por ejemplo: OrderByLastNameAscFirstNameDesc.

Si tomamos la entidad Route del sistema de tours y queremos obtener todas las rutas que cuesten menos de un monto determinado, pero ordenadas alfabéticamente por su nombre, el método sería:  

```java
// En RouteRepository
List<Route> findByPriceLessThanOrderByNameAsc(float price);
```

**En este ejemplo:**

- **findByPriceLessThan:** Es el criterio de filtrado sobre el atributo price.
- **OrderByName:** Indica que el resultado debe ordenarse por el atributo name de la entidad Route.
- **Asc:** Especifica que el orden del nombre debe ser de la A a la Z.

### 2.5 Implementación de las consultas del modelo

#### 26.​Para cada consulta, indicar la estrategia elegida (Query Method o @Query) e implementarla en el repositorio correspondiente:

Consulta | Repositorio | Estrategia elegida |
getAllPurchasesOfUsername(Stringusername) | PurchaseRepository | QueryMethod |
getUserSpendingMoreThan(float amount) | UserRepository | Query |
getTopNSuppliersInPurchases(int n) | SupplierRepository | Query |
getCountOfPurchasesBetweenDates(Datefrom, Date to) | PurchaseRepository | QueryMethod |
getRoutesWithStop(Stop stop) | RouteRepository | QueryMethod |
getMaxStopOfRoutes() | RouteRepository | Query |
getRoutesNotSell() | RouteRepository | Query |
getTop3RoutesWithMaxRating() | RouteRepository | Query |
getMostDemandedService() | ServiceRepository | Query |
getTourGuidesWithRating1() | UserRepository | Query |


## Sección 3 - Transacciones

### 3.1 Aspectos avanzados de @Transactional

#### 29.​¿Cuál es el atributo readOnly = true en @Transactional? ¿Qué optimizaciones activa? ¿En qué métodos del servicio conviene aplicarlo?

El atributo readOnly = true en la anotación @Transactional es una indicación (hint) que se le da al administrador de transacciones de Spring para informarle que el método solo realizará operaciones de lectura y no modificará el estado de la base de datos.  

**Optimizaciones que activa**

- **Desactivación del Dirty Checking:** En un entorno con Hibernate, el framework realiza normalmente un "dirty checking" (comprobación de cambios) al finalizar la transacción para ver si algún objeto en memoria fue modificado y debe persistirse. Con readOnly = true, Hibernate omite este proceso, ahorrando tiempo de CPU y memoria al no tener que comparar el estado actual de los objetos con su "foto" original.
- **Gestión del Flush:** Se configura el modo de flush (sincronización con la base de datos) como MANUAL o NEVER. Esto evita que el framework intente sincronizar cambios innecesarios antes de ejecutar cada consulta de lectura, reduciendo el tráfico y la carga sobre la base de datos.
- **Optimización a nivel de Driver:** Algunos controladores JDBC pueden aprovechar esta bandera para enviar las consultas a réplicas de solo lectura de la base de datos (en arquitecturas de base de datos distribuidas), aligerando la carga del nodo principal de escritura [Información no presente en las fuentes].

**¿En qué métodos conviene aplicarlo?**

- **Consultas de búsqueda y listado:** Todos los métodos que simplemente recuperan información, como getAllPurchasesOfUsername, getRoutesWithStop o findById.
- **Conteos y estadísticas:** Métodos que devuelven valores escalares o agregados, como getCountOfPurchasesBetweenDates o getMaxStopOfRoutes.
- **Carga de DTOs:** Métodos del servicio cuya única función sea recuperar entidades para transformarlas en objetos de transferencia de datos (DTOs) antes de cerrar la transacción

#### 30.​¿Cómo maneja Spring el rollback automático? ¿Sobre qué tipos de excepción hace rollback por defecto? ¿Cómo se configura para el rollback? Dar un ejemplo con el modelo.

Spring maneja el rollback automático a través de la gestión declarativa de transacciones utilizando la anotación @Transactional, la cual actúa como un interceptor o decorador sobre los métodos de la capa de servicio.

**¿Cómo maneja Spring el rollback automático?**

Spring utiliza el concepto de Inversión de Control y Proxies dinámicos para interceptar la llamada al método del servicio. Cuando un método marcado con @Transactional comienza su ejecución, Spring inicia una transacción; si el método termina correctamente, realiza un commit, pero si se lanza una excepción que cumpla con las reglas configuradas, Spring captura dicha excepción y envía el mensaje de rollback() al administrador de transacciones para deshacer todos los cambios realizados en los objetos durante esa unidad de trabajo.

**Tipos de excepción para rollback por defecto**

Por defecto, Spring Data JPA solo realiza un rollback automático ante excepciones de tipo no comprobadas (unchecked exceptions):

- **RuntimeException:** Como NullPointerException, IllegalArgumentException o las propias excepciones de persistencia de Spring (ej. DataAccessException).
- **Error:** Fallos críticos a nivel de la JVM.
- Las excepciones comprobadas (checked exceptions), aquellas que heredan de Exception y que el programador está obligado a capturar o declarar (como IOException o excepciones de negocio personalizadas), no disparan el rollback por defecto.

**Configuración del rollback**

Para alterar este comportamiento y asegurar que una excepción de negocio también provoque la anulación de la transacción, se utilizan los atributos de la anotación:

- **rollbackFor:** Permite especificar una o varias clases de excepciones ante las cuales se debe hacer rollback (útil para incluir excepciones comprobadas).
- **noRollbackFor:** Permite definir excepciones que, aunque ocurran, no deben cancelar la transacción (útil para errores que se pueden ignorar o manejar sin perder el estado).

**Ejemplo con el modelo**

Supongamos un método en ToursServiceImpl para registrar una compra. Si durante el proceso de guardar los ítems de servicio ocurre un error, queremos que no se persista ni la compra ni ninguno de los ítems para mantener la atomicidad del modelo.

```java
@Transactional(rollbackFor = {InvalidStockException.class, BusinessException.class})
public PurchaseDTO registerPurchase(Long userId, Long routeId, List<ItemDTO> items) throws BusinessException {
    // 1. Recuperar Usuario y Ruta (Objetos persistentes)
    User user = userRepo.findById(userId).get();
    Route route = routeRepo.findById(routeId).get();
    
    // 2. Crear la compra y vincularla
    Purchase purchase = new Purchase(user, route);
    
    // 3. Agregar ítems de servicio
    for (ItemDTO dto : items) {
        Service s = serviceRepo.findById(dto.getServiceId()).get();
        // Si aquí se lanza una BusinessException (checked), el rollback ocurrirá por la configuración
        purchase.addItem(new ItemService(s, dto.getQuantity())); 
    }
    
    // 4. Persistencia por alcance al guardar la compra
    purchaseRepo.save(purchase);
    
    return dtoFactory.createCompleteDTOForUser(purchase);
}
```

En este ejemplo, si se lanzara una BusinessException (que es una excepción comprobada), Spring ejecutaría el rollback porque se especificó explícitamente en rollbackFor, garantizando que no queden compras parciales o inconsistentes en la base de datos.

### 3.2 Adaptación de la capa de servicio

#### 31.​Revisar y actualizar la capa de servicio de la Práctica 1 para que utilice los repositorios Spring Data JPA. Reemplazar todas las llamadas directas a la Session (session.save, session.get, session.createQuery, etc.) por las operaciones equivalentes de los repositorios. La lógica de negocio y las anotaciones @Transactional ya existentes no deben modificarse salvo que sea necesario por los cambios anteriores.

## Sección 4 - DTOs y Proyecciones

### 4.1 Qué es un DTO y cuando usarlo

#### 32.​¿Que es un DTO (Data Transfer Object)? ¿Cuál es su propósito dentro de la arquitectura de una aplicación? ¿Por qué no siempre conviene devolver una entidad JPA directamente desde la capa de acceso a datos?

Un DTO (Data Transfer Object) es un objeto diseñado exclusivamente con el propósito de transportar datos entre diferentes procesos o capas de una aplicación. Según la definición de Fowler, su fin es reducir el número de llamadas a métodos al agrupar la información necesaria en una sola estructura.

**Propósito del DTO en la arquitectura**

- **Transferencia de información entre capas:** Actúan como mensajeros que llevan datos desde la capa de dominio/servicio hacia la capa de presentación (web o API).
- **Acceso fuera de transacciones:** Permiten que la información sea leída incluso después de que la transacción de la base de datos se haya cerrado, algo que no siempre es posible con las entidades persistentes.
- **Simplificación de datos:** En muchos casos, la aplicación solo necesita un subconjunto de los atributos de una entidad; el DTO permite filtrar y enviar solo lo relevante para un caso de uso específico.
- **Aislamiento de lógica:** Al no contener lógica de negocios (solo poseen getters y setters), los DTOs refuerzan la separación de capas y evitan que la capa de presentación ejecute accidentalmente operaciones de negocio.

**¿Por qué no conviene devolver entidades JPA directamente?**

- **Ciclos de serialización:** Las entidades JPA suelen tener relaciones bidireccionales (ej. User conoce sus Purchases y cada Purchase conoce a su User). Al intentar convertir esto a un formato como JSON, se pueden producir bucles infinitos que rompen la aplicación.
- **Acoplamiento entre capas:** Si la interfaz de usuario consume directamente las entidades, cualquier cambio en el esquema de la base de datos o en el mapeo de los objetos obligará a modificar también la capa de presentación, eliminando la independencia tecnológica.
- **Problemas con el Lazy Loading:** Las entidades JPA suelen cargar sus relaciones de forma "perezosa" (Lazy). Si se intenta acceder a una relación fuera del contexto de una transacción abierta (como en la capa web), se producirá una LazyInitializationException porque el objeto ya no tiene conexión con la base de datos.
- **Exposición indebida de lógica y datos:** Las entidades contienen toda la lógica de negocio y atributos sensibles (como contraseñas o estados internos). Devolver la entidad completa permite que un cliente malintencionado o un error de programación acceda o intente ejecutar métodos que deberían estar restringidos a la capa de servicio.
- **Eficiencia de transacciones:** Para evitar errores de carga de datos en la interfaz, se podría tener la tentación de mantener las transacciones abiertas por más tiempo. Sin embargo, esto es ineficiente ya que las transacciones deben durar milisegundos, mientras que la interacción con el usuario puede durar segundos o minutos.

#### 33.​Describir dos situaciones concretas del modelo donde devolver un DTO sería más adecuado que devolver la entidad completa. Para cada caso indicar qué campos contendría el DTO y por que.

**Vista de Perfil Público o Listado de Usuarios**

- En una interfaz donde se listan los usuarios del sistema (o guías turísticos), no es necesario ni seguro devolver la entidad User completa.
- **Campos que contendría el DTO:** username, fullName, email y, si es un guía, education.

**Por qué es más adecuado:**

- **Seguridad y Privacidad:** La entidad User contiene el atributo password. Exponer la entidad completa en la capa de presentación implicaría enviar datos sensibles que nunca deberían salir de la capa de servicio.
- **Evitar la LazyInitializationException:** La entidad User tiene una relación @OneToMany con Purchase. Si la capa de vista intenta acceder a la colección de compras una vez que la transacción del servicio se ha cerrado, la aplicación fallará porque el objeto ya no está conectado a la base de datos.
- **Performance:** El DTO permite enviar solo un subconjunto de atributos, evitando cargar y serializar todo el grafo de objetos relacionados (como la lista de compras del usuario) que no son necesarios para un simple listado.

**Reporte de Resumen de Ventas por Ruta**

- Cuando se requiere generar un reporte estadístico sobre el desempeño de las rutas, como se sugiere en los requerimientos de la práctica.
- **Campos que contendría el DTO:** routeName, purchaseCount (cantidad de compras realizadas) y averagePrice (precio promedio de esas compras).

**Por qué es más adecuado:**

- **Información Agregada/Calculada:** Estos datos (conteo y promedio) no existen como atributos físicos en la entidad Route. Un DTO es el contenedor ideal para transportar resultados de funciones de agregación (COUNT, AVG) que se calculan en la consulta JPQL.
- **Aislamiento de Lógica:** Evita que la capa de presentación tenga que realizar cálculos manuales iterando sobre las entidades. El DTO entrega la información ya procesada y lista para mostrar, manteniendo el modelo de objetos puro y sin "ensuciarlo" con atributos temporales de reporte.
- **Evitar Ciclos de Serialización:** Las entidades suelen tener relaciones bidireccionales (ej. Route conoce sus Purchases y Purchase conoce su Route). Intentar devolver la entidad Route directamente podría causar un bucle infinito al convertirla a JSON, riesgo que el DTO elimina al ser una estructura plana.


#### 34.​¿Qué riesgos concretos tiene exponer entidades JPA directamente como respuesta de un servicio o endpoint? Mencionar al menos: ciclos de serialización y acoplamiento entre capas.

**1. Ciclos de serialización (Recursión infinita)**  
Las entidades JPA suelen tener relaciones bidireccionales (por ejemplo, un User conoce sus Purchase y cada Purchase referencia a su User). Cuando un serializador (como Jackson para JSON) intenta convertir la entidad, entra en un bucle infinito siguiendo la referencia de un objeto a otro, lo que suele provocar un error de desbordamiento de pila (stack overflow) y rompe la respuesta del servicio.

**2. Acoplamiento entre capas**  
Si la capa de presentación consume directamente las entidades, se produce un acoplamiento fuerte con el esquema de la base de datos. Cualquier cambio en la estructura de las tablas o en el mapeo de los objetos obligará a modificar también el cliente o la interfaz de usuario, eliminando la independencia tecnológica y dificultando el mantenimiento del sistema.

**3. Problemas con la carga diferida (Lazy Initialization Exception)**  
Las relaciones en JPA suelen configurarse como Lazy (carga bajo demanda) para optimizar el rendimiento. Al intentar devolver una entidad en un endpoint, el serializador intentará acceder a todas sus colecciones; si la transacción ya se cerró en la capa de servicio, se lanzará una LazyInitializationException porque el objeto ya no tiene una conexión activa con la base de datos para recuperar esos datos.

**4. Exposición de datos sensibles y lógica de negocio**  

- **Seguridad:** Las entidades contienen todos los atributos del modelo, incluyendo información sensible como contraseñas (campo password en User) o estados internos que el cliente no debería conocer.  
- **Lógica:** Al devolver una entidad, se entrega un objeto que contiene lógica de negocio. Un cliente podría intentar invocar métodos que alteren el estado del objeto de forma indebida, mientras que los DTOs, al ser "objetos planos" sin lógica, actúan como una frontera segura.

**5. Ineficiencia en el manejo de transacciones**  
Para evitar errores de carga de datos en la vista, se podría caer en la mala práctica de mantener las transacciones abiertas por más tiempo del necesario (segundos o minutos mientras el usuario interactúa), cuando estas deberían durar milisegundos. Esto aumenta la probabilidad de conflictos de concurrencia y bloqueos en la base de datos.  

### 4.2 Implementación

#### 35.​Definir e implementar un DTO que resuma información de las rutas del modelo: nombre de la ruta, cantidad de compras realizadas y el precio promedio de esas compras. Implementar la consulta correspondiente en RouteRepository.

## Sección 5 - Borrado Lógico (Soft Delete)

### 5.1 Concepto y estrategias

#### 36.​¿Que es el borrado lógico (soft delete)? Pensar en el modelo de tours: si un usuario decide darse de baja del sistema, ¿tiene sentido eliminar físicamente su registro? ¿Qué ocurriría con todas las compras que ese usuario ha realizado? Describir por qué el borrado lógico resuelve este problema y qué ventaja ofrece frente al borrado físico en este caso concreto.

El borrado lógico (soft delete) es una estrategia de persistencia en la que los registros no se eliminan físicamente de las tablas de la base de datos, sino que se marcan como inactivos o "borrados" (generalmente mediante un campo booleano o una fecha de baja). De este modo, el registro permanece en el soporte físico pero es ignorado por las consultas habituales de la aplicación.

**1. ¿Tiene sentido eliminar físicamente al usuario?**  
No tiene sentido. En un sistema de gestión real, la información histórica es vital para el negocio. Eliminar físicamente a un usuario que ha interactuado con el sistema rompe la trazabilidad de las operaciones realizadas.

**2. ¿Qué ocurriría con sus compras?**  

- **Pérdida de datos vinculados:** Si la relación está configurada en cascada (CascadeType.REMOVE), al borrar al usuario se borrarían todas sus compras (Purchase), perdiendo el registro contable y estadístico de esas ventas.
- **Inconsistencia de la base de datos:** Si no se borran las compras en cascada, quedarían registros de compra apuntando a un ID de usuario que ya no existe (registros "huérfanos"), lo que provocaría errores en reportes financieros o al intentar cargar el detalle de una ruta vendida.

**3. ¿Por qué el borrado lógico resuelve este problema?**  

- El borrado lógico permite que el objeto User siga existiendo físicamente para satisfacer las restricciones de clave foránea de la tabla de compras.
- Las compras realizadas siguen referenciando a un usuario válido en la base de datos, manteniendo la consistencia.
- Para la lógica del día a día (login, listados de clientes activos), el usuario simplemente "desaparece" porque las consultas se filtran para ignorar a los marcados como borrados.

**4. Ventajas frente al borrado físico**  

- **Preservación de la historia:** Permite generar reportes históricos y auditorías (ej. "cuánto se vendió el año pasado") sin perder los datos de los clientes que ya no usan el servicio.
- **Seguridad y Recuperación:** Es una operación reversible; si un usuario se dio de baja por error, restaurar su cuenta es tan simple como cambiar un valor booleano, sin necesidad de recurrir a backups de la base de datos.
- **Evita validaciones complejas:** El modelo original incluía métodos como canBeDeactive() para evitar borrar usuarios con compras asociadas y así prevenir inconsistencias. Con el borrado lógico, estas restricciones se relajan ya que la integridad física nunca se pone en riesgo.

#### 37.​Describir dos estrategias para implementar soft delete en JPA/Hibernate: campo booleano (active/deleted) y campo de fecha (deletedAt). ¿Qué diferencias hay entre ambas en cuanto a consultas y recuperación de datos?​

**1. Estrategia de Campo Booleano (active / deleted)**

- En esta modalidad, se añade un atributo de tipo booleano a la entidad (por ejemplo, private boolean deleted).
- **Funcionamiento:** Por defecto, el valor es false. Cuando se ejecuta un "borrado", el valor cambia a true.
- **Filtro global**: Se suele utilizar la anotación @Where(clause = "deleted = false") sobre la entidad para que Hibernate excluya automáticamente estos registros en todas las consultas ordinarias (findAll, findById, etc.).
- **Ventaja:** Es la implementación más simple y directa, con un consumo mínimo de espacio en la base de datos.

**2. Estrategia de Campo de Fecha (deletedAt)**

- Esta estrategia utiliza un campo de tipo fecha o timestamp (por ejemplo, LocalDateTime deletedAt).
- **Funcionamiento:** Un registro se considera "activo" si el campo es NULL. Al realizar el borrado lógico, se guarda la fecha y hora exacta de la operación en lugar de un simple flag.
- **Filtro global**: El filtro en este caso sería @Where(clause = "deleted_at IS NULL").
- **Ventaja:** Proporciona un valor añadido para la auditoría, ya que permite saber exactamente cuándo se produjo la baja sin necesidad de consultar tablas de logs adicionales.

#### 38.​¿Que es la anotación @SQLDelete de JPA? ¿Qué permite hacer? ¿Cómo se combina con @Where para que las consultas ordinarias ignoren los registros borrados? Mostrar un ejemplo de uso sobre la entidad User.

La anotación @SQLDelete es una herramienta de Hibernate (utilizada frecuentemente en proyectos JPA) que permite redefinir la sentencia SQL que el framework ejecuta cuando se invoca el borrado de una entidad.

**1. ¿Qué permite hacer @SQLDelete?**  
Permite sustituir el borrado físico (DELETE FROM...) por una instrucción personalizada, generalmente un UPDATE. En lugar de eliminar el registro de la tabla, se utiliza para cambiar el estado de una columna (por ejemplo, de activo a inactivo), logrando así que el registro permanezca físicamente en la base de datos para preservar la integridad referencial e histórica.

**2. Combinación con @Where**  

- Mientras que @SQLDelete se encarga de "marcar" el registro como borrado, la anotación @Where se encarga de la visibilidad.
- **Filtro Global:** @Where permite definir una cláusula que Hibernate añadirá automáticamente a todas las consultas ordinarias (findAll, findById, Query Methods) generadas para esa entidad.
- **Resultado:** Al configurar @Where(clause = "deleted = false"), el sistema ignorará automáticamente todos los registros marcados, haciendo que el borrado lógico sea transparente para el resto de la aplicación.

**3. Ejemplo de uso en la entidad User**

```java
@Entity
@Table(name = "users")
// 1. Redefinimos el borrado físico por uno lógico
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
// 2. Filtramos globalmente para que las consultas ignoren a los borrados
@Where(clause = "deleted = false")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    // 3. Campo necesario para llevar el estado del borrado
    private boolean deleted = false;

    // Getters y setters...
}
```

- **Consideración importante:** La anotación @Where es global. Si se requiere consultar usuarios inactivos (por ejemplo, para un panel de administración que necesite "restaurar" usuarios), se debe utilizar SQL nativo o una consulta JPQL explícita que omita el filtro, ya que las consultas estándar de Hibernate siempre aplicarán la restricción.


#### 40.​La implementacion actual muestra el metodo canBeDeactivate() en User, DriverUser y TourGuideUser. El objetivo de estos métodos es evitar que se eliminen usuarios que poseen compras o asignaciones y, por tanto queden inconsistencias en la base de datos. Describa como esta nueva implementación afecta dichos métodos.

**1. Eliminación del riesgo de inconsistencia física**  

El objetivo original de canBeDeactivate() era actuar como una "guarda" para evitar errores de integridad referencial. En un borrado físico, eliminar un usuario con compras asociadas provocaría registros "huérfanos" o violaciones de claves foráneas (Foreign Keys) en la base de datos.
Con la nueva implementación mediante @SQLDelete, la operación de borrado se convierte en un UPDATE (por ejemplo, SET deleted = true). Dado que el registro permanece físicamente en la tabla, las relaciones de la base de datos (como las compras en Purchase o las asignaciones en Route) siguen apuntando a un registro existente, eliminando así el riesgo de inconsistencia técnica.

**2. Redundancia técnica de los métodos**  

Debido a que el borrado lógico no rompe el modelo de datos ni las restricciones de integridad:  

- Relajación de restricciones: Ya no es estrictamente necesario impedir la "baja" de un usuario solo porque tenga compras pasadas, ya que dichas compras seguirán vinculadas correctamente al registro (ahora marcado como inactivo) para fines estadísticos o contables.
- Simplificación del modelo: Estos métodos, que antes eran críticos para la salud de la base de datos, pasan a ser opcionales. La lógica de "no poder borrar" se traslada a una lógica de "ocultar" mediante la anotación @Where, que filtra automáticamente a los usuarios inactivos de las consultas ordinarias (findAll, findById).

**3. Cambio de enfoque: de integridad a reglas de negocio**  

Si bien la necesidad técnica de canBeDeactivate() desaparece, estos métodos podrían evolucionar para representar reglas de negocio puras en lugar de protecciones de integridad:

- Antes: Se usaban para evitar que la aplicación "explotara" por una FK violada.
- Ahora: Podrían usarse para definir si un usuario puede darse de baja según su estado actual (por ejemplo, si tiene un tour en curso en ese momento), pero ya no para proteger la estructura de las tablas de compras o rutas vendidas.