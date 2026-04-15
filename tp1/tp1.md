# Trabajo Practico 1 - Hibernate y JPA

## Seccion 1 - Introduccion a los ORM

### 1.1 El problema del ORM

#### 1.​ ¿Qué problema concreto resuelve un ORM? Describir al menos 3 (tres) inconvenientes que aparecen al intentar persistir objetos directamente con JDBC puro

Un ORM (Object-Relational Mapper) resuelve fundamentalmente el problema conocido como diferencia de impedancia.  

- **Falta de transparencia y dependencia tecnológica:** Al usar JDBC puro, es necesario escribir código SQL embebido dentro de los objetos de la aplicación. Esto genera diversos problemas:  
  - **Dependencia del motor de base de datos:** El SQL puede variar entre proveedores (por ejemplo, palabras reservadas como USER en Oracle que no lo son en MySQL), lo que dificulta la portabilidad.
  - **Errores en tiempo de ejecución:** Para lenguajes como Java, el SQL es simplemente una cadena de caracteres (String), por lo que cualquier error sintáctico o cambio en el esquema de la base de datos solo se detecta al ejecutar el código.

- **Complejidad técnica para mapear jerarquías y grafos:** El modelo relacional es muy simple en comparación con la riqueza de los objetos. JDBC requiere que el desarrollador gestione manualmente situaciones que la base de datos no soporta de forma natural:
  - **Herencia:** Las bases de datos relacionales no contemplan jerarquías de clases; para reconstruir un objeto de una subclase mediante JDBC, se deben realizar múltiples JOINs o UNIONs complejos de forma manual.
  - **Navegación vs. Joins:** En objetos, se accede a la información navegando por relaciones, mientras que en JDBC se debe recurrir a operaciones de JOIN, que son costosas y difíciles de mantener si el grafo de objetos es profundo.

- **Baja mantenibilidad y repetición de código (Boilerplate):** Desarrollar una "capa de persistencia casera" con JDBC implica escribir una gran cantidad de código tedioso y repetitivo para traducir cada atributo de clase a una columna de tabla y viceversa. Este código suele ser difícil de reutilizar entre diferentes aplicaciones, lo que obliga al desarrollador a invertir más tiempo en detalles técnicos de persistencia que en la lógica de negocio de la aplicación.

#### 2.​ El modelo relacional y el modelo OO presentan tensiones conocidas como impedance mismatch. Identificar cómo se manifiesta cada una en el modelo dado  

Las tensiones de la diferencia de impedancia (impedance mismatch) entre el modelo relacional y el modelo orientado a objetos (OO) se manifiestan en el sistema de gestión de tours de la siguiente manera:

- a. **Identidad:** cómo es posible identificar un objeto Purchase en Java vs.en la base de datos?  
  - En Java: La identidad de un objeto Purchase viene definida por su OID (Object Identifier), que es un identificador único gestionado por el entorno de ejecución. Dos objetos pueden tener los mismos valores en sus atributos pero ser instancias distintas con identidades diferentes.
  - En la base de datos: La identidad se establece mediante una Clave Primaria (PK) en la tabla correspondiente a Purchase. Para vincular ambos mundos, el ORM suele mapear este identificador de la tabla a un atributo (como id: Long) dentro de la clase Java.
- b.​ **Relaciones:** cómo se navega de una Purchase a sus ItemService en Java vs.en SQL?  
  - En Java: Se navega desde Purchase hacia sus ItemService a través de la navegación de grafos de objetos, accediendo directamente a la colección de colaboradores (por ejemplo, llamando al método getItemServiceList()).
  - En SQL: La navegación no es directa; se requiere realizar una operación de JOIN entre la tabla de compras y la tabla de ítems de servicio, comparando las claves foráneas para reconstruir la relación.
- c.​ **Herencia:** ¿cómo podría representarse la jerarquía User/DriverUser/TourGuideUser en una tabla relacional?  
Para representar la jerarquía User, DriverUser y TourGuideUser, el modelo relacional dispone de tres estrategias principales:
  - **SINGLE_TABLE:** Una única tabla para toda la jerarquía que incluye todas las columnas de las subclases y una columna discriminadora para saber qué tipo de usuario es cada fila.
  - JOINED**:** Una tabla para la superclase User con los datos comunes y tablas separadas para DriverUser (con el atributo expedient) y TourGuideUser (con education), vinculadas por claves foráneas.
  - **TABLE_PER_CLASS:** Tablas independientes para cada clase concreta, donde los atributos de User se repiten en las tablas de las subclases.
- d.​ Ciclos de referencia: ¿existe algún ciclo en el modelo? ¿Cómo impacta en la persistencia?  
  - **Ciclo identificado:** El modelo presenta un ciclo en la relación bidireccional entre Purchase e ItemService. Una Purchase contiene una lista de ItemService, y cada ItemService mantiene una referencia de regreso a su Purchase. También existe un ciclo potencial entre Purchase y Review si se mapea bidireccionalmente.
  - **Impacto en la persistencia:** Los ciclos obligan al desarrollador a mantener la sincronización manual de ambos lados de la relación en memoria (por ejemplo, al agregar un ítem a la compra, se debe setear la compra en el ítem). A nivel de ORM, se debe definir qué lado es el "dueño" de la relación (usualmente con mappedBy) para evitar que Hibernate intente ejecutar actualizaciones redundantes o infinitas en la base de datos

#### 3.​ Describir las ventajas y desventajas concretas de usar un ORM en un proyecto como este.  

- **Ventajas**
  - **Resolución de la diferencia de impedancia:** Permite trabajar con conceptos de objetos (herencia entre User, DriverUser y TourGuideUser) que no existen naturalmente en el modelo relacional, mapeándolos automáticamente a tablas mediante estrategias como SINGLE_TABLE o JOINED.
  - **Transparencia y enfoque en el negocio:** El desarrollador puede centrarse en la lógica de tours y compras sin "ensuciar" el código con sentencias SQL embebidas. Esto permite un diseño más natural y orientado a objetos.
  - **Reducción de código repetitivo (Boilerplate):** El ORM se encarga de las operaciones CRUD básicas y del mapeo de atributos a columnas, lo que reduce drásticamente la cantidad de código manual necesario para persistir los datos en comparación con JDBC puro.
  - **Gestión de asociaciones complejas:** Facilita la navegación por el grafo de objetos (por ejemplo, acceder de una Route a sus Stop o de un User a sus Purchase) mediante la persistencia por alcance, donde vincular un objeto volátil a uno persistente puede bastar para guardarlo automáticamente.
  - **Optimización de rendimiento incorporada:** Ofrece mecanismos como Lazy Loading (carga perezosa) para no traer todas las paradas de una ruta si no se necesitan, y Caching de nivel 1 y 2 para minimizar los accesos redundantes a la base de datos.
- **Desventajas**
  - **Contaminación del modelo de dominio:** Aunque busca transparencia, el uso de anotaciones (como @Entity, @Id) o archivos de configuración "ensucia" las clases del modelo, rompiendo parcialmente el principio de independencia tecnológica.
  - **Responsabilidad de sincronización manual:** En relaciones bidireccionales (como la de Purchase e ItemService), el ORM no sincroniza ambos lados automáticamente en memoria; es responsabilidad total del desarrollador mantener la consistencia del grafo antes de persistir.
  - **Complejidad técnica y excepciones en tiempo de ejecución:** Introducir conceptos como Proxies puede generar errores difíciles de depurar, como la LazyInitializationException si se intenta acceder a una colección (ej. los servicios de una compra) fuera de una sesión activa.
  - **Curva de aprendizaje y errores silenciosos:** El uso incorrecto de estrategias de carga (configurar todo como EAGER) puede degradar severamente el rendimiento. Además, los errores en consultas HQL/JPQL suelen detectarse recién en tiempo de ejecución, ya que son cadenas de caracteres para el compilador.
  - **Sobrecarga de memoria:** El mantenimiento del Contexto de Persistencia y el seguimiento del estado de los objetos gestionados implica un consumo de memoria adicional que debe ser monitoreado en aplicaciones de gran escala.

### 1.2 JPA e Hibernate

#### 4.​ ¿Qué es JPA? ¿Qué nos permite definir? ¿Por qué se dice que es una especificación y no una implementación?

JPA (Java Persistence API) es el estándar de persistencia para la plataforma Java, que define un contrato común para el mapeo objeto-relacional. Surgió como una evolución posterior a Hibernate, basándose en gran medida en sus conceptos y en otros productos como EJB para unificar la forma en que se gestionan los datos en Java.

JPA nos permite definir gracias a un conjunto de herramientas cómo los objetos de la aplicación se vinculan con la base de datos:

- **Anotaciones de mapeo:** Permite marcar POJOs como clases persistentes utilizando @Entity, y especificar detalles técnicos como el nombre de la tabla con @Table o la identificación con @Id.
- **Relaciones y cardinalidad:** Facilita la definición de asociaciones entre objetos (como @OneToMany o @ManyToOne) y cómo deben comportarse estas relaciones.
- **Estrategias de herencia:** Permite elegir cómo representar una jerarquía de clases en tablas relacionales (como SINGLE_TABLE, JOINED o TABLE_PER_CLASS).
- **Comportamiento de persistencia:** Nos permite configurar operaciones en cascada (cómo se propagan las acciones de persistir o eliminar) y tipos de fetch (si los datos relacionados se cargan de forma perezosa —LAZY— o inmediata —EAGER—).
- **Consultas:** Define un lenguaje de consultas orientado a objetos llamado JPQL (Java Persistence Query Language), similar a SQL pero operando sobre entidades y atributos en lugar de tablas y columnas.

Se dice que JPA es una especificación porque constituye un conjunto de interfaces, reglas y definiciones (el "qué" se debe hacer) que no contiene el código ejecutable para realizar las tareas de persistencia por sí mismo.
La diferencia fundamental es:  

- **JPA (Especificación):** Define el estándar, las anotaciones y las interfaces que los desarrolladores usan en su código para que sea portable.  
- **Hibernate (Implementación):** Es el producto o herramienta concreta que provee el código real para ejecutar esas definiciones, generar el SQL y manejar el contexto de persistencia. Hibernate es considerado la implementación de referencia de JPA.

#### 5.​ ¿Cuál es la relación entre JPA e Hibernate? ¿Puede usarse JPA sin Hibernate o Hibernate sin JPA?

- **JPA (Java Persistence API)** es el estándar o contrato de persistencia para Java. No es un producto ejecutable, sino un conjunto de interfaces, reglas y anotaciones que definen cómo se deben mapear los objetos a una base de datos relacional.
- **Hibernate es la herramienta** o producto concreto que proporciona el código para ejecutar esas definiciones. Es considerado la implementación de referencia de JPA.
- **Influencia histórica:** JPA surgió después de Hibernate y se basó fuertemente en sus conceptos debido al predominio de este último en la industria.

**¿Puede usarse JPA sin Hibernate?**  
Sí. Dado que JPA es una especificación, se puede utilizar el estándar JPA en una aplicación y optar por una implementación distinta a Hibernate (como EclipseLink u OpenJPA). Esto permite que el código sea más portable, ya que el desarrollador interactúa con la API estándar y no con una herramienta específica.

**¿Puede usarse Hibernate sin JPA?**  
Sí. Hibernate existía antes que JPA y posee su propia API nativa (conocida a veces como "Hibernate vanilla"). Es posible utilizar Hibernate directamente mediante sus propias clases (como Session en lugar de EntityManager) y sus propios mecanismos de mapeo (como archivos XML nativos) sin seguir estrictamente el estándar JPA. Sin embargo, en la práctica moderna, lo más común es utilizar Hibernate a través de la especificación JPA para mantener la independencia tecnológica del modelo.

#### 6.​ ¿Qué es la SessionFactory? ¿Qué patrón de diseño implementa? Justificar por qué se crea una sola instancia durante todo el ciclo de vida de la aplicación y no una por operación

La SessionFactory en Hibernate es un componente central que actúa como una caché inmutable de mapeos compilados para una base de datos específica. Su función primordial es servir como la fuente para la creación de sesiones de trabajo (Session), las cuales representan la conversación activa entre la aplicación y el almacenamiento persistente.

Este componente implementa principalmente dos patrones de diseño:

- **Factory:** Como indica su nombre, su responsabilidad es fabricar y proporcionar instancias de Session a la aplicación cuando se requieren.
- **Singleton:** En la arquitectura de una aplicación basada en persistencia, la fábrica de sesiones (o su equivalente en JDO, la PersistenceManagerFactory) se diseña para que exista únicamente una sola instancia compartida.

**Justificación de una sola instancia**
La práctica de crear una sola instancia de la SessionFactory para todo el ciclo de vida de la aplicación, en lugar de una por cada operación, se justifica por los siguientes motivos técnicos y de rendimiento:

- **Alto costo de inicialización:** Al levantarse la aplicación, Hibernate debe leer el archivo de configuración (hibernate.cfg.xml) y todos los archivos de mapeo, procesando las anotaciones y compilando el modelo de objetos a tablas relacionales. Este proceso es extremadamente costoso y lento; hacerlo una sola vez al inicio optimiza significativamente el tiempo de respuesta del sistema.
- **Gestión de la Caché de Nivel 2:** La SessionFactory es la encargada de gestionar la Caché de Nivel 2 (L2). A diferencia de la caché de nivel 1 (que es propia de cada sesión), la L2 es una caché compartida que cubre a todas las sesiones a la vez. Tener una sola instancia permite que los objetos recuperados por una sesión puedan ser servidos desde la caché a otras sesiones, minimizando los accesos a la base de datos.
- **Administración centralizada de conexiones:** La SessionFactory es el punto de partida para la creación y administración de las conexiones a la base de datos, permitiendo a Hibernate optimizar este recurso tan costoso.
- **Consistencia de mapeos:** Al ser una estructura inmutable que contiene los mapeos ya compilados, asegura que todas las operaciones de la aplicación se rijan bajo las mismas reglas de persistencia definidas al arranque. Si se intentara crear una SessionFactory por cada operación, la aplicación sufriría una degradación severa de performance, ya que pasaría más tiempo reconfigurando el entorno de persistencia que ejecutando la lógica de negocio.

#### 7.​ La SessionFactory ofrece dos formas de obtener una Session: openSession() y getCurrentSession(). Responder

- a.​ ¿Cuál es la diferencia entre ambos métodos? ¿Qué ocurre con el ciclo de vida de la Session en cada caso?
  - **openSession():** Este método siempre abre una nueva instancia de Session. El desarrollador es el dueño total de su ciclo de vida; debe abrirla manualmente y asegurarse de cerrarla para liberar los recursos de la base de datos.
  - **getCurrentSession():** Este método busca y devuelve una sesión vinculada al contexto actual (usualmente la transacción activa). Si no existe una sesión en el contexto, Hibernate la crea; si ya existe una, devuelve la instancia actual para que pueda ser compartida por varios componentes (como distintos repositorios) dentro de la misma transacción.
- b.​ ¿Qué condición debe cumplirse para poder usar getCurrentSession()? ¿Que configuración requiere en Hibernate?
  - **Condición:** Para poder utilizar getCurrentSession(), debe existir un contexto de sesión activo, lo cual generalmente implica que debe haber una transacción en curso.
  - **Configuración:** Requiere que en el archivo de configuración de Hibernate (hibernate.cfg.xml o mediante Spring) se defina la propiedad hibernate.current_session_context_class. Los valores comunes para esta propiedad son thread (para aplicaciones simples) o delegar la gestión al framework (como se ve en la configuración de HibernateTransactionManager en las fuentes).
- c.​ ¿Quién es responsable de cerrar la Session cuando se usa getCurrentSession()? ¿Y cuando se usa openSession()?
  - **openSession():** La responsabilidad recae enteramente en el desarrollador. El código debe incluir explícitamente una llamada a session.close(), habitualmente dentro de un bloque finally para garantizar que el recurso se libere incluso si ocurre una excepción.
  - **getCurrentSession():** La responsabilidad de cierre recae en Hibernate o el gestor de transacciones (como Spring). Una vez que la unidad de trabajo (transacción) termina mediante un commit o rollback, el sistema cierra la sesión automáticamente.
- d.​ ¿Cuál de los dos métodos resulta más adecuado para usar en los repositorios de esta práctica y por qué?  
Al utilizar getCurrentSession(), el repositorio se integra correctamente con la capa de servicios, la cual es la encargada de coordinar las operaciones y delimitar los marcos transaccionales. Esto asegura que todas las operaciones dentro de un servicio (que puede involucrar a varios repositorios) se ejecuten como una única unidad de trabajo atómica.

#### 8.​ Completar la tabla comparativa entre JPA e Hibernate

Aspecto | JPA | Hibernate |
------- | --- | --------- |
Tipo | Especificación | Implementación |
Define interfaces y anotaciones | Si | No |
Proporciona implementación concretas | No | Si |
Genera SQL a partir de consultas JPQL/HQL | No | Si |
Maneja el Persistence Context | No (define el estandar y reglas) | Si (mediante la Session) |

### 1.3 Ciclo de vida de las entidades

#### 9.​ Describir los cuatro estados posibles de una entidad en Hibernate (transient, managed, detached, removed) e indicar qué operación dispara cada transición entre ellos

- **Transient (Transitorio)**
  - **Descripción:** Es el estado inicial de un objeto recién instanciado con el operador new. En este estado, el objeto existe únicamente en la memoria de Java; no tiene una identidad (ID) asociada en la base de datos ni está vinculado a ninguna Session de Hibernate.
  - **Transición a Managed:** Se dispara mediante las operaciones save(), persist() o saveOrUpdate(). También puede ocurrir por persistencia por alcance si el objeto transitorio es referenciado por una entidad que ya está en estado managed.
- **Managed (Gestionado o Persistente)**
  - **Descripción:** El objeto está asociado a una Session activa y tiene un identificador único que lo vincula a una fila de la base de datos. Hibernate monitorea cualquier cambio en los atributos del objeto y se encarga de sincronizarlos automáticamente con la base de datos al realizar un flush o un commit de la transacción.
  - **Transición desde Transient:** Mediante persist() o save().
  - **Transición desde Detached:** Mediante las operaciones merge(), update() o saveOrUpdate(), las cuales vuelven a vincular un objeto a la sesión actual.
**Detached (Desasociado)**
  - **Descripción:** El objeto posee una identidad en la base de datos (tiene un ID), pero ya no está vinculado a ninguna Session activa. Esto ocurre típicamente después de que la sesión se ha cerrado o el objeto ha sido expulsado del contexto de persistencia. Hibernate ya no realiza el seguimiento de sus cambios.
  - **Transición desde Managed:** Se dispara al cerrar la sesión (session.close()), al limpiar la sesión completa (session.clear()) o al desasociar el objeto explícitamente mediante evict() o detach().
  - **Transición a Managed:** Mediante merge() o update().
**Removed (Eliminado)**
  - **Descripción:** El objeto todavía está asociado a la Session, pero ha sido marcado para ser borrado físicamente de la base de datos. La eliminación efectiva de la fila correspondiente se ejecutará al final de la transacción o cuando se fuerce un flush.
  - **Transición desde Managed:** Se dispara mediante la operación delete() o remove().
  - **Transición desde Detached:** Normalmente requiere primero reasociar el objeto a la sesión (pasarlo a managed) y luego marcarlo para eliminar.

#### 10.​Describir el ciclo de vida completo de una entidad persistente, tome como ejemplo un objeto de la clase Purchase: desde que se instancia con new, pasando por supersistencia, hasta que se elimina. Indicar explícitamente en qué estado se encuentra el objeto en cada paso

- **Instanciación: Estado Transient (Transitorio)**  
El ciclo comienza cuando se crea una nueva instancia de la clase en Java:
  - **Acción:** Purchase purchase = new Purchase();.
  - **Estado:** Transient.
  - **Descripción:** En este punto, el objeto es un POJO común que reside únicamente en la memoria de la aplicación. No tiene una identidad (ID) asociada en la base de datos ni está vinculado a ninguna sesión de Hibernate.

- **Persistencia: Transición a Managed (Gestionado)**  
Para que el objeto se guarde en la base de datos, debe pasar al contexto de persistencia:
  - **Acción:** session.save(purchase); o session.persist(purchase);.
  - **Estado:** Managed.
  - **Descripción:** El objeto ahora tiene un identificador único que lo vincula a una fila de la base de datos. Hibernate monitorea cualquier cambio realizado en los atributos de la compra (como la fecha o el código) y sincronizará esos cambios automáticamente al realizar un flush o el commit() de la transacción. También puede alcanzar este estado por persistencia por alcance si es referenciado por otro objeto ya gestionado (ej. al agregar el Purchase a la lista de un User persistente).

- **Cierre de Sesión: Transición a Detached (Desasociado)**  
Si la conversación con la base de datos termina pero el objeto sigue en memoria:
  - **Acción:** session.close();, session.clear(); o al finalizar la transacción cuando se usa getCurrentSession().
  - **Estado:** Detached.
  - **Descripción:** El objeto mantiene su ID y los datos que tenía al momento de desconectarse, pero Hibernate ya no rastrea sus cambios. Si el desarrollador modifica un atributo de la compra en este estado, esos cambios no se reflejarán en la base de datos a menos que el objeto sea reasociado mediante un método como merge() o update().

- **Eliminación: Transición a Removed (Eliminado)**  
Cuando se decide que la compra ya no debe existir en el sistema:
  - **Acción:** session.delete(purchase); o session.remove(purchase);.
  - **Estado:** Removed.
  - **Descripción:** El objeto todavía existe en la memoria de Java y está asociado a la sesión, pero ha sido marcado para ser borrado físicamente de la base de datos. La ejecución efectiva del DELETE en SQL se realizará al final de la unidad de trabajo (transacción).

- **Resumen de los estados del objeto Purchase:**
  - **Transient:** Recién creado con new, sin ID y sin sesión.
  - **Managed:** Con ID y bajo el control de una sesión activa (los cambios se sincronizan).
  - **Detached:** Con ID pero fuera del control de la sesión (la sesión se cerró).
  - **Removed:** Marcado para ser eliminado de la base de datos.

#### 11.​Investigue sobre los métodos de Session: session.save(), session.persist(), session.merge() y session.saveOrUpdate(). ¿Qué permite hacer cada uno y cuál es la diferencia entre ellos? Indicar en qué estado debe estar un objeto para usar cada uno correctamente

- **session.save()**
  - **Qué permite:** Almacena un objeto en la base de datos asignándole un identificador único.
  - **Estado inicial:** El objeto debe estar en estado Transient (recién creado con new y sin ID).
  - **Particularidad:** Es un método nativo de Hibernate que retorna el identificador generado (Serializable) de forma inmediata. Al invocarlo, el objeto pasa automáticamente al estado Managed.
- **session.persist()**
  - **Qué permite:** Al igual que save(), hace que una instancia transitoria sea persistente.
  - **Estado inicial:** El objeto debe estar en estado Transient.
  - **Diferencia:** Es el método definido por la especificación JPA. A diferencia de save(), su tipo de retorno es void y no garantiza que el identificador se asigne inmediatamente, sino que puede esperar hasta el momento del flush de la sesión. Está diseñado para ser usado dentro de los límites de una transacción.
- **session.merge()**
  - **Qué permite:** Reasocia un objeto desasociado con la sesión actual, copiando su estado a una instancia gestionada.
  - **Estado inicial:** Se utiliza principalmente para objetos en estado Detached (tienen ID pero ya no están vinculados a la sesión activa).
  - **Particularidad:** Si el objeto existe en la base de datos, Hibernate carga una instancia en memoria, le copia los valores del objeto desasociado y devuelve la instancia gestionada. El objeto original pasado como parámetro permanece en estado Detached.
- **session.saveOrUpdate()**
  - **Qué permite:** Es un método de conveniencia que decide si debe insertar o actualizar el objeto basándose en la presencia de su identificador.
  - **Estado inicial:** Puede utilizarse tanto para objetos Transient como Detached.
  - **Diferencia:** Si el objeto no tiene ID (Transient), internamente ejecuta un save(); si ya posee un ID (Detached), ejecuta un update() para volver a vincularlo al contexto de persistencia y que Hibernate rastree sus cambios.  

**Resumen de Diferencias y Estados**  

Método | Estado del Objeto | Resultado en la BD | Estándar |
------ | ----------------- | ------------------ | -------- |
save() | Transient | Genera un INSERT y retorna el ID. | Hibernate nativo |
persist() | Transient | Genera un INSERT (retorno void). | JPA estándar |
merge() | Detached | Sincroniza cambios y retorna copia gestionada. | JPA estándar |
saveOrUpdate() | Transient / Detached | Decide entre INSERT o UPDATE. | Hibernate nativo |

## Sección 2 - Mapeo de Entidades

### 2.1 Mapeo de una entidad simple: Service

#### 12.​¿Cuál es el conjunto mínimo de anotaciones que debe tener una clase para ser persistente con JPA?

- **@Entity:** Esta anotación se coloca a nivel de clase e indica a JPA (y a la implementación como Hibernate) que esa clase es una entidad persistente. Al marcarla así, el motor de persistencia asume por defecto que todos sus campos deben mapearse a una tabla, aplicando el principio de Convención sobre Configuración.
- **@Id:** Se debe colocar sobre el atributo que funcionará como el identificador único de la entidad. Es un requisito obligatorio porque cada fila en la base de datos necesita una Clave Primaria para identificar unívocamente al objeto y permitir su posterior recuperación y actualización.

#### 13.​¿Qué significa que JPA use persistencia por alcance (persistence by reachability)? ¿Qué consecuencia tiene si un objeto referenciado no está todavía persistido?

La persistencia por alcance (persistence by reachability), también conocida como persistencia transitiva, es un concepto central en los ORM que establece que todo objeto al cual se pueda navegar a partir de un objeto ya persistente debe ser, necesariamente, persistente a su vez.

¿Qué significa en la práctica?

- **Vínculo automático:** Para persistir un objeto nuevo (volátil), no es estrictamente necesario invocar métodos de guardado para cada instancia; basta con vincularlo a través de una relación (por ejemplo, agregándolo a una colección) con un objeto que ya esté en estado managed o gestionado.
- **Reducción de código:** Permite que la aplicación sea más independiente de la tecnología de persistencia, ya que reduce drásticamente la necesidad de llamadas explícitas a métodos como save() o persist() dentro de la lógica de negocio.
- **Sincronización en el Commit:** Al finalizar una transacción, Hibernate recorre el grafo de objetos modificados y aplica este principio para decidir qué objetos nuevos deben insertarse y cuáles existentes deben actualizarse para mantener la integridad del modelo.

¿Qué ocurre si un objeto referenciado no está persistido?

La consecuencia depende de cómo se haya configurado el mapeo de la relación:  

- **Si se configuró el mapeo con cascada (CascadeType):** JPA propagará la operación. Por ejemplo, si la relación tiene CascadeType.PERSIST o ALL, el objeto referenciado se guardará automáticamente en la base de datos cuando se persista el objeto principal.
- **Si NO se configuró la cascada:** JPA no propaga las operaciones por defecto. Si intentas persistir un objeto que referencia a otro que es "transitorio" (recién creado con new y sin ID), el ORM detectará una situación irregular.
  - **Excepción en el Commit:** Al momento de intentar el commit o el flush, el sistema lanzará una excepción. Esto sucede porque la base de datos no puede satisfacer las restricciones de integridad (como una Clave Foránea no nula) si el objeto referenciado aún no existe como una fila en una tabla.
  - **Responsabilidad del desarrollador:** En relaciones bidireccionales, es vital que el desarrollador mantenga ambos lados sincronizados en memoria (por ejemplo, item.setPurchase(p) y p.getItems().add(item)) antes de persistir, para evitar que Hibernate intente guardar referencias nulas o inconsistentes.

#### 14.​¿Qué diferencia hay entre las estrategias IDENTITY, SEQUENCE y TABLE para la generación de IDs? ¿Cuál tiene mejor rendimiento en inserciones masivas y por qué?

- **IDENTITY**: Utiliza una columna de tipo autoincremental propia de la base de datos (como en MySQL). En este caso, el valor del ID se genera después de ejecutar la sentencia INSERT en la tabla.
- **SEQUENCE**: Utiliza un objeto especial de la base de datos llamado "secuencia" (común en Oracle o PostgreSQL) para obtener los valores numéricos. A diferencia de la anterior, el ID se puede obtener antes de realizar la inserción física del registro.
- **TABLE**: Emplea una tabla dedicada en la base de datos para llevar la cuenta de los identificadores asignados. Es una estrategia universal que funciona en cualquier motor de base de datos, pero es considerada la más lenta de todas.
Rendimiento en inserciones masivas

La estrategia que ofrece el mejor rendimiento para inserciones masivas es **SEQUENCE**.  
El motivo principal es que esta estrategia es más eficiente para procesar grandes volúmenes de datos. Técnicamente, al utilizar una secuencia, Hibernate puede conocer de antemano el valor de los identificadores antes de enviar los datos a la base de datos. Esto permite que el ORM agrupe múltiples inserciones en un solo envío (batching), algo que no es posible con IDENTITY, ya que con esa estrategia Hibernate se ve obligado a ejecutar cada INSERT individualmente para poder conocer el ID que la base de datos acaba de generar.

#### 15.​Implementar el mapeo completo de la entidad Service según el diagrama. La implementación debe incluir:

- e.​ Clave primaria con estrategia de generación automática. Elegir entre IDENTITY, SEQUENCE o TABLE y justificar la elección.
- f.​ Atributos: name (no nulo, max. 100 caracteres), description (opcional), price (no nulo).
- g.​ Al menos una restricción de unicidad a nivel de columna.

### 2.2 Relaciones entre entidades

#### 16.​Para la relación Purchase -> ItemService (composición uno-a-muchos):

- h.​ ¿Qué anotaciones se necesitan en cada lado?
  - En el lado de **Purchase** (el "uno"): Se utiliza la anotación @OneToMany. Dado que es una composición, es fundamental incluir cascade = CascadeType.ALL y orphanRemoval = true para que el ciclo de vida de los ítems esté atado a la compra.
  - En el lado de **ItemService** (el "muchos"): Se utiliza la anotación @ManyToOne. Adicionalmente, se suele usar @JoinColumn para definir el nombre de la columna que actuará como clave foránea.
- i.​ ¿Qué columna o tabla aparece en la base de datos para representar esta relación?
  - En una configuración estándar con mappedBy, aparece una columna de clave foránea (Foreign Key) en la tabla de la entidad "muchos" (ITEM_SERVICE) que apunta al ID de la tabla PURCHASE.
  - Si no se especifica el mapeo correctamente, JPA podría generar por defecto una tabla intermedia, lo cual es menos eficiente para este tipo de relación.
- j.​ ¿Qué es mappedBy y en qué lado de la relación va? ¿Qué ocurre si se omite en ambos lados?
  - **Qué es:** Es un atributo que indica que la relación ya ha sido mapeada por la otra entidad; define al lado que no es dueño de la relación (lado inverso).
  - **En qué lado va:** Siempre debe ir en el lado "uno" de la relación (en este caso, en la clase Purchase).
  - **Si se omite en ambos lados:** JPA no sabrá que se trata de la misma relación bidireccional y la tratará como si fueran dos relaciones unidireccionales independientes. Esto resultará en la creación de una tabla intermedia innecesaria o en errores de redundancia de datos.
- k.​ ¿Es esta relación bidireccional o unidireccional según el diagrama? ¿Cómo se refleja en el código Java?
  - **Direccionalidad:** Según el diagrama de clases, la relación es bidirectional. Esto se sabe porque ambos extremos de la relación tienen nombres de rol y visibilidad: Purchase conoce a su lista de itemServiceList y ItemService conoce a su purchase correspondiente.
  - **Reflejo en Java:** Se refleja mediante atributos de instancia en ambas clases:
    - **En Purchase:** private List<ItemService> itemServiceList;.
    - **En ItemService:** private Purchase purchase;.
    - **Responsabilidad:** El desarrollador debe asegurarse de sincronizar ambos lados en memoria (por ejemplo, al agregar un ítem a la lista de la compra, también se debe ejecutar item.setPurchase(purchase)), ya que Hibernate no lo hace automáticamente.
  
#### 17.​Para las relaciones Route <-> DriverUser y Route <-> TourGuideUser (muchos-a-muchos)

- l. ¿Qué anotaciones se usan?  
Para este tipo de relaciones se utiliza la anotación @ManyToMany en ambos extremos de la asociación.  
Uno de los lados debe ser definido como el dueño de la relación (donde se configura físicamente la unión).  
El otro lado es el inverso y debe incluir el atributo mappedBy dentro de la anotación para indicar qué campo en la clase dueña gestiona la relación.  
- m. ¿Qué tabla adicional genera JPA? ¿Qué columnas tiene? Definirla explícitamente usando @JoinTable.  
JPA genera automáticamente una tabla intermedia (o join table) para representar físicamente el vínculo entre las dos entidades.  
  - **Columnas:** Esta tabla tiene, como mínimo, dos columnas que actúan como claves foráneas (FK), cada una apuntando a la clave primaria (ID) de las tablas de las entidades relacionadas.  
  - **Definición explícita para Route <-> DriverUser:**  

  ```java
  @ManyToMany
  @JoinTable(
      name = "ROUTE_DRIVER", // Nombre de la tabla intermedia
      joinColumns = @JoinColumn(name = "route_id"), // FK que apunta al ID de Route
      inverseJoinColumns = @JoinColumn(name = "driver_id") // FK que apunta al ID de DriverUser
  )
  private List<DriverUser> drivers;
  ```

  - **Definición explícita para Route <-> TourGuideUser:**  

  ```java
  @ManyToMany
  @JoinTable(
      name = "ROUTE_TOUR_GUIDE", // Nombre de la tabla intermedia
      joinColumns = @JoinColumn(name = "route_id"), // FK que apunta al ID de Route
      inverseJoinColumns = @JoinColumn(name = "guide_id") // FK que apunta al ID de TourGuideUser
  )
  private List<DriverUser> guiders;
  ```
- n. ¿Pueden ambas relaciones compartir la misma tabla join? ¿Por qué?
No, no pueden compartir la misma tabla.  
  - **Justificación:** Cada relación representa un concepto de negocio distinto (conducir una ruta vs. guiar una ruta). Si se utilizara una única tabla para ambas, Hibernate no podría distinguir qué usuarios pertenecen a la lista de conductores y cuáles a la de guías al hidratar los objetos.
  - Incluso si ambos (choferes y guías) heredan de la misma clase User, cada asociación requiere su propia estructura física de unión para mantener la integridad de los datos y permitir consultas específicas sobre cada rol.

#### 18.​La relación Purchase -> Review es opcional (0..1). Implementar el mapeo de ambos lados. ¿Cómo se representa la opcionalidad en JPA?

Para mapear la relación entre Purchase y Review, que según el diagrama es uno-a-uno (1:1) y opcional (0..1), se utiliza la anotación @OneToOne en ambos lados de la asociación.  

- **Implementación del mapeo** *(TENER EN CUENTA QUE CUALQUIERA PODRIA SER DUEÑA DE LA RELACION)*
  - **En el lado de Purchase (Dueño de la relación):** Se coloca la anotación @OneToOne. Al ser el lado que habitualmente gestiona la clave foránea, se utiliza @JoinColumn.
  - **En el lado de Review (Lado inverso):** Se utiliza el atributo mappedBy para indicar que la relación ya está definida en la clase Purchase.

¿Cómo se representa la opcionalidad en JPA?  
La opcionalidad (el "0..1") se representa de tres maneras principales en este framework:  

- **Atributo optional:** Dentro de las anotaciones de relación (@OneToOne o @ManyToOne), existe el parámetro optional = true. Por defecto, JPA asume que todas las relaciones son opcionales. Si se establece en false, Hibernate lanzará una excepción si se intenta guardar la entidad sin su contraparte.
- **Nulabilidad en la base de datos:** A nivel físico, JPA traduce la opcionalidad permitiendo que la columna de la Clave Foránea (FK) acepte valores NULL. Si la relación fuera obligatoria (1..1), se configuraría la columna como nullable = false.
- **Convención sobre Configuración:** Fiel a este principio mencionado en las fuentes, JPA asume que si un atributo de tipo objeto no tiene una restricción específica, este puede ser nulo en la memoria de Java y, por ende, opcional en la base de datos.

#### 19.​ItemService referencia a Service (muchos-a-uno). Analizar el diagrama: ¿es navegable esta relación desde Service hacia ItemService? Justificar si conviene hacerla bidireccional o no

- Navegabilidad según el diagrama  
La relación es navegable desde Service hacia ItemService.
  - En el diagrama, la línea que une ambas entidades presenta el nombre de rol - items y la multiplicidad * del lado de ItemService. Esto indica que la clase Service tiene la capacidad de conocer y acceder a su colección de ítems relacionados. El diagrama también muestra que la clase Service posee el método + addItem(ItemService item), lo que refuerza la intención de que la entidad gestione activamente sus objetos relacionados.
- ¿Conviene hacerla bidireccional?
  - Aunque el diagrama sugiere bidireccionalidad, desde el punto de vista del diseño de persistencia y rendimiento, no suele ser conveniente que esta relación sea bidireccional. La justificación técnica es la siguiente:
  - **Problema de escala y memoria:** La entidad Service funciona como una definición de catálogo (por ejemplo, "Mate pintado artesanalmente"). En un sistema real, un mismo servicio puede ser referenciado por miles o millones de ItemService a lo largo del tiempo.
  - **Impacto en la Performance (Fetch Type):** Si la relación es bidireccional, al cargar un objeto Service desde la base de datos, Hibernate intentará gestionar la colección de todos los ítems asociados. Si no se configura correctamente como LAZY, se cargará una cantidad masiva de datos innecesarios en memoria; e incluso siendo LAZY, el mantenimiento de esa colección es costoso.
  - **Mantenimiento de la integridad:** En las relaciones bidireccionales la responsabilidad de mantener ambos lados sincronizados recae en el desarrollador. En este caso, la complejidad extra de sincronización no suele aportar valor al negocio, ya que normalmente se navega desde la Purchase hacia sus ItemService y de allí al Service, pero raramente se necesita listar todos los ítems de todas las compras desde un objeto de definición de servicio.
  - **Conclusión:** Es preferible mantener la relación como unidireccional (de ItemService a Service). Si en algún momento se requiere conocer qué ítems pertenecen a un servicio específico, es mucho más eficiente realizar una consulta HQL/JPQL filtrando por el ID del servicio en lugar de mantener una colección pesada en la clase de dominio.

#### 20.​Implementar el mapeo completo de las siguientes entidades con todas sus relaciones, siguiendo las anotaciones y decisiones discutidas: Supplier, Purchase, ItemService, Route, Stop y Review. Para cada relación bidireccional, incluir las anotaciones en ambos lados

### 2.3 Fetch Type: LAZY vs. EAGER

#### 21.​¿Para qué sirve la propiedad fetch en las anotaciones de relación? ¿Cuáles son los valores posibles? ¿Cuál es el valor por defecto para @OneToMany, para @ManyToOne y para @ManyToMany?

La propiedad fetch en las anotaciones de relación de JPA/Hibernate sirve para definir la estrategia de carga de los objetos asociados. Es decir, determina si los objetos relacionados se traen de la base de datos en el mismo momento en que se carga la entidad principal o solo cuando se accede a ellos por primera vez en el código.

Existen dos valores posibles para esta propiedad:  

- **LAZY (Perezoso):** Los objetos asociados no se cargan inicialmente. En su lugar, Hibernate coloca un proxy (un sustituto o "placeholder") y la consulta real a la base de datos se realiza recién cuando se solicita la información del objeto relacionado. Esto ayuda a aliviar la carga de memoria y reducir consultas innecesarias.
- **EAGER (Ansioso):** Los objetos asociados se cargan de forma obligatoria e inmediata junto con la entidad principal. Es útil cuando se sabe con certeza que siempre se requerirá la información del objeto relacionado.

Fiel al principio de Convención sobre Configuración, JPA establece valores por defecto para cada tipo de multiplicidad, generalmente basados en proteger la memoria del sistema:  

Anotación | Valor por defecto | Justificación técnica |
--------- | ----------------- | --------------------- |
@OneToMany | LAZY | Evita cargar colecciones potencialmente masivas de objetos en memoria de forma automática |
@ManyToMany | LAZY | Al igual que en OneToMany, se busca evitar la hidratación de grafos de objetos muy grandes que podrían causar problemas de performance |
@ManyToOne | EAGER | Al referenciar a un único objeto, el impacto en memoria es menor, por lo que el estándar JPA asume que se desea tener la información disponible de inmediato |

#### 22.​Describir ventajas y desventajas concretas de EAGER y LAZY, en términos de performance de acceso como de espacio en memoria. ¿Por qué configurar EAGER en todas las relaciones suele ser una mala idea en aplicaciones reales?

- **Estrategia LAZY (Carga perezosa)**
  - **Performance de acceso:** Su principal ventaja es que ahorra consultas iniciales a la base de datos, ya que solo recupera la entidad solicitada y coloca un proxy (un sustituto o representante) en lugar de los objetos relacionados. Esto acelera la carga inicial del objeto principal. Su desventaja es que, si luego se necesita acceder a muchos elementos de una colección, puede generar el problema de las N+1 consultas, realizando un acceso extra a la base de datos por cada objeto individual, lo cual resulta extremadamente costoso.
  - **Espacio en memoria:** Es muy eficiente, ya que alivia la carga en la memoria al evitar traer grafos de objetos que quizás nunca se utilicen en la ejecución actual.
- **Estrategia EAGER (Carga ansiosa)**
  - **Performance de acceso:** Es ventajosa cuando existe la certeza de que siempre se requerirá la información del objeto relacionado, permitiendo obtener todo en una sola consulta (usualmente mediante un join), lo que reduce la latencia de múltiples viajes a la base de datos. Su desventaja es que penaliza el tiempo de respuesta inicial al procesar y traer datos que podrían no ser necesarios en ese contexto.
  - **Espacio en memoria:** Presenta una desventaja clara en el consumo de recursos, ya que obliga a la aplicación a mantener en memoria todos los objetos asociados desde el primer momento.

**¿Por qué EAGER en todas las relaciones es una mala idea?**  

- **Hidratación masiva de grafos:** Debido al principio de persistencia por alcance, cargar una sola entidad podría desencadenar una reacción en cadena que termine intentando traer a memoria una parte masiva de la base de datos.
- **Degradación drástica del rendimiento:** El motor de persistencia se ve obligado a realizar una cantidad enorme de transferencias de datos y procesar múltiples asociaciones innecesarias, lo que ralentiza todas las operaciones de lectura de la aplicación.
- **Riesgo de falta de memoria:** En sistemas con grandes volúmenes de información, el intento de precargar colecciones completas de forma ansiosa puede agotar rápidamente el espacio en memoria (RAM) disponible para la aplicación.
- **Pérdida de flexibilidad:** Se pierde la posibilidad de optimizar el acceso a datos según el caso de uso específico, forzando un modelo de carga pesado para todas las situaciones, incluso aquellas que solo requieren atributos básicos del objeto.

#### 23.​Para cada relación del modelo, elegir el FetchType más adecuado y justificar. Luego implemente su decisión en el proyecto tours

Relación | Tipo de anotación | FetchType elegido | Justificación |
-------- | ----------------- | ----------------- | ------------- |
Purchase -> User | @ManyToOne | EAGER | Al ser un solo usuario por compra no tenemos problemas de memoria |
Purchase -> Route | @ManyToOne | EAGER | Al ser una sola ruta por compra no tenemos problemas de memoria|
Purchase -> itemServiceList | @OneToMany | LAZY | Al ser muchos itemService por compra podemos tener problemas de memoria, también podemos sufrir N+1 |
Purchase -> Review | @OneToOne | EAGER | Al ser un solo review por compra no tenemos problemas de memoria |
ItemService -> Service | @ManyToOne | EAGER | Al ser un service por ItemService no tenemos problemas de memoria |
Route -> stops | @OneToMany | LAZY | Al ser muchas paradas por ruta podemos sufrir problemas de memoria, también podemos sufrir N+1 |
Route -> drivers(DriverUser) | @ManyToMany | LAZY | Al ser muchos choferes por ruta podemos sufrir problemas de memoria, también podemos sufrir N+1 |
Route -> tourGuides(TourGuideUser) | @ManyToMany | LAZY | Al ser muchos guias por ruta podemos sufrir problemas de memoria, también podemos sufrir N+1 |
User -> purchases | @OneToMany | LAZY | Al ser muchas compras por usuario podemos sufrir porblemas de memoria, también podemos sufrir N+1 |
Service -> supplier | @ManyToOne | EAGER | Al ser un proveedor por servicio no tenemso problemas de memoria |

#### 24.​¿Cómo podría producirse una LazyInitializationException en el modelo? Investigue de qué representa esta excepción y escribir un escenario concreto explicando al menos formas de resolverlo sin cambiar el FetchType a EAGER

Esta excepción ocurre cuando el código intenta acceder a una relación marcada como LAZY (un proxy) después de que la Session de Hibernate (o el EntityManager) se haya cerrado.  
Cuando recuperas una entidad con una colección LAZY, Hibernate no trae los datos de esa colección de la base de datos inmediatamente; en su lugar, coloca un proxy (un objeto "sustituto" vacío). Si intentas navegar por esa relación (por ejemplo, llamando a un .get...()), el proxy intenta conectarse a la base de datos para recuperar los datos. Si la sesión ya se cerró, el proxy no tiene "puente" hacia la base de datos y lanza la LazyInitializationException.  

**Consideremos la relación entre User y su lista de purchases, la cual definimos previamente como LAZY para proteger la memoria.**

- En la capa de Servicio, buscas un usuario por su ID: User u = userRepository.findById(1L);.
- El método del servicio termina y, por ende, la transacción y la sesión se cierran.
- En la capa de Vista (ej. un controlador o una página web), intentas mostrar el historial del usuario: for (Purchase p : u.getPurchases()) { ... }.
- Resultado: En el momento en que el bucle intenta acceder al primer elemento de la lista, se dispara la LazyInitializationException porque Hibernate ya no tiene una conexión abierta para llenar ese proxy de compras.

**Formas de resolverlo sin usar EAGER**

- **Uso de DTOs (Data Transfer Objects)**
  - Esta es la solución más recomendada para mantener la arquitectura limpia. Consiste en copiar los datos necesarios desde las entidades hacia objetos simples de Java mientras la transacción aún está abierta.
  - **Implementación:** Dentro del método del servicio, antes de que cierre la sesión, recorres la colección de compras y pasas sus datos a un UserDTO que contenga una lista de PurchaseDTO. Al retornar el DTO a la vista, ya no hay dependencias con la sesión de Hibernate.
- **Fetch Join (Consultas con JOIN FETCH)**
  - En lugar de depender de la navegación de objetos, se define una consulta específica en el Repositorio usando HQL o JPQL que fuerce la carga de la relación necesaria solo para ese caso de uso.
  - **Ejemplo de consulta:** SELECT u FROM User u JOIN FETCH u.purchases WHERE u.id = :id
  - **Ventaja:** Trae el usuario y sus compras en una sola consulta SQL, inicializando la colección antes de que la sesión se cierre, eliminando el riesgo de la excepción y el problema de las N+1 consultas.

**En la práctica, lo más eficiente y robusto es combinar ambas opciones:**

- El Repositorio ejecuta una consulta optimizada (Fetch Join) para traer todos los datos necesarios de un solo golpe.
- El Servicio recibe esos objetos ya "hidratados" y los transforma a DTOs antes de que la transacción se cierre.
- La Vista recibe el DTO con toda la información necesaria, sin riesgo de excepciones ni problemas de performance.
- En conclusión, para optimizar el rendimiento, el repositorio es tu herramienta principal. Los DTOs son tu herramienta para la seguridad y el desacoplamiento de las capas una vez que los datos ya están en memoria.

### 2.4 Operaciones en cascada

#### 25.​Enumerar todos los valores de CascadeType y explicar qué operación propaga cada uno sobre las entidades relacionadas.

- **PERSIST:** Propaga la operación de guardado. Cuando se persiste un objeto (por ejemplo, con session.persist()), todos los objetos relacionados marcados con esta cascada se persistirán también automáticamente si aún no lo están.
- **REMOVE:** Propaga la eliminación. Si se elimina una entidad principal, Hibernate eliminará automáticamente las entidades relacionadas. Es la opción típica para relaciones de composición, donde la existencia de los hijos depende del padre.
- **MERGE:** Propaga la operación de fusión. Se utiliza cuando se tiene una entidad en estado detached (desasociada) y se desea reincorporar al contexto de persistencia; esta cascada asegura que los cambios en los objetos relacionados también se sincronicen con la base de datos.
- **REFRESH:** Propaga la operación de refresco. Al invocarla, la entidad principal y sus relacionadas vuelven a cargar su estado actual directamente desde la base de datos, descartando cualquier cambio en memoria que no haya sido persistido.
- **DETACH:** Propaga la desasociación. Quita tanto a la entidad principal como a las relacionadas del contexto de persistencia (la Session de Hibernate), de modo que dejen de ser gestionadas y sus cambios ya no se sigan automáticamente.
- **ALL:** Es una opción de conveniencia que engloba todas las operaciones anteriores (PERSIST, REMOVE, MERGE, REFRESH y DETACH). Se utiliza frecuentemente para simplificar el código cuando se desea una persistencia por alcance total entre dos entidades.
- **Nota de seguridad:** El uso de CascadeType.REMOVE (o ALL) debe hacerse con precaución, especialmente en relaciones @ManyToMany, ya que podría causar eliminaciones no deseadas de entidades que aún son referenciadas por otros objetos.

#### 26.​¿Cuál es el comportamiento por defecto cuando no se define cascade? ¿Cuál es la finalidad general de CASCADE? Proponga un caso del modelo donde definir un CASCADE inadecuado podría traer problemas a la consistencia de la base de datos.

Cuando no se define la propiedad cascade en una relación, el comportamiento por defecto en JPA es que no se propaga ninguna operación. Esto significa que cualquier acción realizada sobre la entidad principal (como guardarla o eliminarla) no afectará automáticamente a los objetos que esta referencia.  
La finalidad de las operaciones en cascada es establecer la persistencia por alcance (o persistencia transitiva). Su objetivo es permitir que el desarrollador indique qué operaciones específicas deben propagarse hacia las entidades relacionadas, de modo que el grafo de objetos se mantenga consistente en la base de datos sin necesidad de invocar manualmente operaciones sobre cada objeto individual.

Un caso crítico donde un CASCADE mal configurado traería problemas graves a la consistencia y la integridad de los datos es la relación Route <-> DriverUser (muchos-a-muchos):  

- **Escenario:** Si se definiera CascadeType.REMOVE (o ALL) en el lado de la relación que gestiona los conductores de una ruta.
- **Problema de consistencia:** Al intentar eliminar una Route (por ejemplo, porque ya no se ofrece ese recorrido), Hibernate propagaría la eliminación a todos los objetos DriverUser asociados a esa ruta.
- **Consecuencia:** Como los choferes pueden estar asignados a múltiples rutas, la eliminación de una sola ruta provocaría la pérdida no deseada de datos de usuarios (conductores) que todavía son necesarios para otros recorridos del sistema. Esto rompería la integridad lógica del negocio, dejando otras rutas sin sus conductores asignados o forzando errores de claves foráneas en la base de datos.

#### 27.​¿Cuál es la diferencia entre cascade = REMOVE y orphanRemoval = true? ¿Pueden usarse conjuntamente? Ejemplificar con el par Purchase -> ItemService.

- **cascade = REMOVE (o CascadeType.REMOVE):** Propaga la operación de eliminación desde la entidad padre a la hija. Si eliminas el objeto padre (la compra), Hibernate eliminará automáticamente todos los objetos hijos asociados en la base de datos. Sin embargo, si solo "desconectas" al hijo del padre en la memoria de Java (quitándolo de la lista), el hijo no se borra de la base de datos; simplemente queda con su clave foránea en NULL (o genera un error de integridad si esta es obligatoria).
- **orphanRemoval = true:** Es un atributo específico de las anotaciones @OneToMany y @OneToOne que se encarga de los "huérfanos". Si eliminas una referencia de un hijo de la colección del padre en Java (por ejemplo, haciendo itemServiceList.remove(unItem)), Hibernate detecta que ese objeto ya no pertenece a la relación y lo elimina físicamente de la tabla correspondiente al realizar el flush.

Sí, pueden y suelen usarse conjuntamente, especialmente en casos de composición. De hecho, es común ver la configuración cascade = CascadeType.ALL (que incluye REMOVE) junto con orphanRemoval = true para garantizar que el ciclo de vida de los hijos dependa totalmente del padre.

**Ejemplo: Purchase -> ItemService**

- **Con cascade = REMOVE:** Al ejecutar session.delete(miCompra), Hibernate borrará automáticamente todas las entradas en la tabla ITEM_SERVICE que pertenezcan a esa compra.
- **Con orphanRemoval = true:** Si un usuario decide quitar un ítem de su carrito antes de confirmar y tú ejecutas miCompra.getItemServiceList().remove(itemViejo), Hibernate borrará ese ítem específico de la tabla ITEM_SERVICE para evitar que queden registros sueltos ("huérfanos") que ya no pertenecen a ninguna compra activa.

#### 28.​Para la relación Purchase -> ItemService (composición):

- o.​ ¿Qué tipos de cascade configurarías? Justificar cada uno.
Para este caso, se recomienda configurar **CascadeType.ALL**. La justificación para cada operación incluida es la siguiente:  
  - **PERSIST:** Permite que al guardar una nueva Purchase, todos los ItemService agregados a su lista se persistan automáticamente sin necesidad de llamar al repositorio por cada uno, cumpliendo con el principio de persistencia por alcance.
  - **REMOVE:** Es esencial en una composición; si la compra se elimina, todos sus ítems asociados deben borrarse también, ya que no tienen sentido de existencia independiente.
  - **MERGE y REFRESH:** Aseguran que si se actualizan los datos de la compra o se refrescan desde la base de datos, los ítems reflejen esos mismos cambios de estado de forma sincronizada.
- p.​ ¿Usarias orphanRemoval? ¿Por qué?
Sí, se debe utilizar orphanRemoval = true. Porque en una composición, el objeto "parte" (ItemService) está estrictamente vinculado al "todo" (Purchase). Si se elimina un ítem de la colección en Java (por ejemplo, haciendo itemServiceList.remove(unItem)), este queda "huérfano". Al activar esta propiedad, se garantiza que el registro se elimine físicamente de la base de datos en lugar de quedar con una referencia nula, manteniendo la integridad lógica del modelo de negocio.
- q.​ Describir qué ocurre a nivel de base de datos cuando se elimina un ItemService de la lista de una Purchase y Hibernate realiza el flush.
Cuando se elimina un ItemService de la lista de una Purchase en el código Java y Hibernate realiza el flush (sincronización con la base de datos), ocurre lo siguiente:  
  - Hibernate detecta que una instancia que antes formaba parte de una relación marcada con orphanRemoval ya no está presente en la colección gestionada.
  - A nivel físico, Hibernate ejecuta una sentencia DELETE sobre la tabla de ítems (ej. DELETE FROM ITEM_SERVICE WHERE id = ...).
  - Esto diferencia el comportamiento de una simple desvinculación: sin esta propiedad, Hibernate solo intentaría poner en NULL la columna de la clave foránea (purchase_id), lo cual fallaría si dicha columna se definió como obligatoria (optional = false).

#### 29.​Para la relación Purchase -> Review:

- r.​ ¿Qué cascades configurarías?
Se recomienda configurar CascadeType.ALL (o en su defecto PERSIST, MERGE y REMOVE). La justificación técnica es la siguiente:
  - **PERSIST y MERGE:** Permiten que, si el sistema decide guardar o actualizar una compra que ya incluye una reseña, esta se almacene o sincronice automáticamente en la base de datos sin llamadas adicionales al repositorio de reseñas.
  - **REMOVE:** Es fundamental para evitar datos huérfanos. Al ser una relación 1:1 donde la reseña está ligada a una compra específica, la eliminación de la compra debe propagar el borrado a su reseña asociada.
- s.​ Si se elimina una Purchase, ¿debería eliminarse también su Review? Justificar desde el modelo de negocio.
Justificación desde el modelo de negocio: Según la descripción del dominio, la posibilidad de agregar una Review (comentarios y puntaje) se habilita únicamente una vez que se ha registrado la Purchase del usuario para un recorrido determinado. Por lo tanto:
  - **Dependencia de Contexto:** Una reseña no es una entidad independiente del catálogo (como un Service), sino un testimonio sobre una experiencia de compra y viaje particular.
  - **Integridad de la Información:** Si la compra desaparece del sistema (por ejemplo, por una anulación total de registros o limpieza de datos), la reseña pierde su referencia base y su validez técnica. Mantener una reseña sin su compra asociada generaría una inconsistencia, ya que no habría registro oficial de que ese usuario efectivamente adquirió el derecho a realizar ese comentario y puntaje.

#### 30.​Para la relación Supplier -> Service:

- t.​ ¿Qué cascades tienen sentido?
  - **PERSIST:** Resulta muy útil para la gestión del catálogo. Permite que, al crear un nuevo proveedor, se puedan persistir simultáneamente todos los servicios que este ofrece sin necesidad de llamar al método de guardado por cada servicio individualmente.
  - **MERGE:** Tiene sentido para asegurar que, si se actualizan los datos de un proveedor (como su razón social), cualquier modificación realizada en la lista de sus servicios en memoria también se sincronice con la base de datos.
  - **REFRESH **y DETACH: Son adecuados para mantener la consistencia del grafo de objetos en memoria respecto al estado de la sesión de Hibernate.
- u.​ Si se elimina un Supplier, ¿qué debería ocurrir con sus Service? ¿Y con las Purchase que los contienen a través de ItemService?
  - **Sobre sus Service:** Los servicios no deberían eliminarse en cascada. Aunque el proveedor desaparezca como entidad, sus servicios representan definiciones históricas que han sido consumidas por usuarios. Si se borraran los servicios, se perdería la referencia de lo que efectivamente se ofreció y vendió.
  - **Sobre las Purchase (a través de ItemService):** Si se utilizara un CASCADE.REMOVE inadecuado que borrara los servicios, se rompería la integridad referencial de las compras.
    - Cada ItemService referencia a un Service.
    - Si el servicio desaparece, el ítem de la compra quedaría apuntando a la nada, invalidando el historial de la Purchase.
    - Dado que el objetivo de la compra es que el usuario tenga control de lo abonado y de los servicios solicitados, eliminar esta información causaría una pérdida crítica de datos históricos y financieros.

#### 31.​¿Por que CascadeType.REMOVE en una relación @ManyToMany (por ejemplo Route <-> DriverUser) suele ser peligroso? Describir un escenario donde su uso cause pérdida no deseada de datos.

Un caso crítico donde un CASCADE mal configurado traería problemas graves a la consistencia y la integridad de los datos es la relación Route <-> DriverUser (muchos-a-muchos):  

- **Escenario:** Si se definiera CascadeType.REMOVE (o ALL) en el lado de la relación que gestiona los conductores de una ruta.
- **Problema de consistencia:** Al intentar eliminar una Route (por ejemplo, porque ya no se ofrece ese recorrido), Hibernate propagaría la eliminación a todos los objetos DriverUser asociados a esa ruta.
- **Consecuencia:** Como los choferes pueden estar asignados a múltiples rutas, la eliminación de una sola ruta provocaría la pérdida no deseada de datos de usuarios (conductores) que todavía son necesarios para otros recorridos del sistema. Esto rompería la integridad lógica del negocio, dejando otras rutas sin sus conductores asignados o forzando errores de claves foráneas en la base de datos.


#### 32.​Implemente las operaciones en cascada adecuada para todas las relaciones del modelo tours

### 2.5 Jerarquía de herencia: User, DriverUser y TourGuideUser

#### 33.​Describir las tres estrategias de mapeo de herencia. Para cada una indicar qué tablas se crean, si aparece columna discriminadora y cómo resuelve Hibernate una consulta polimórfica. Completar la tabla:

Aspecto | SINGLE_TABLE | JOINED | TABLE_PER_CLASS |
------- | ------------ | ------ | --------------- |
Tablas creadas en la BD | una sola tabla User | Una tabla por cada clase incluyendo la tabla padre | Una tabla por cada clase concreta |
Columna discriminadora | SI | NO | NO |
NULLs en columnas de subclases | SI | NO (solo tiene sus atributos) | NO (tiene tabla completa, incluso ID) |
Consulta polimórfica 'todos los Users' | `SELECT` a una sola tabla (eficiente) | `JOIN` de tabla padre e hijas (menos eficiente) | `UNION` entre todas la subclases (ineficiente) |
Cargar un DriverUser por ID | `SELECT` con filtro por ID (rapida) | `JOIN` entre USER y DRIVER (lenta) | `SELECT` con filtro por ID (muy rapida) |
Integridad referencial (FK posibles) | nula ya que tenemos los atributos en null | permite NOT NULL en tablas hijas | problema para establecer fk "polimorficas" ya que cada tabla tiene un ID propio por mas que USER y DRIVER sean el mismo |
Performance en lecturas simples (de una entidad) | Buena ya que no hay `JOIN` pero suma discriminador | Baja `JOIN` obligatorios | Buena para tipos concretos |
Que implica para agregar nueva subclase | Agregar columnas nuevas | Agregar tabla de subclase | nueva tabla con toda la estructura |

#### 34.​Implementar el mapeo de la jerarquía User/DriverUser/TourGuideUser usando la estrategia SINGLE_TABLE. Especificar @Inheritance, @DiscriminatorColumn y @DiscriminatorValue para cada clase. Incluir todos los atributos.

- v. Ejecutar los tests provistos y analizar el DDL generado: ¿cuántas tablas aparecen? ¿Qué columnas tiene la tabla? ¿Dónde están los atributos expedient y education?
NO PUEDO EJECUTAR LOS TEST PEEEEERO, aparece una sola tabla, tiene los atributos de USER, DRIVER y GUIDE juntos en la tabla USER mas el discriminante "user_type"
- w.​ ¿Qué ocurre con las columnas de subclase cuando se inserta un TourGuideUser? ¿Y cuando se inserta un DriverUser?
Cuando se inserta una subclase especifica por ejemplo TourGuideUser se establece "expedient" en NULL ya que pertenece a un DriverUser, y si insertamos un DriverUser se establece "education" en NULL ya que pertenece a un TourGuideUser.
- x.​ ¿Cuáles son las ventajas y desventajas de esta estrategia para este modelo concreto?
  - **Ventajas**:
    - **Performance:** Las consultas polimórficas (como "traer todos los Users") son extremadamente rápidas porque no requieren JOINs ni UNIONs.
    - **Simplicidad:** El esquema de base de datos es muy sencillo de administrar y entender.
    - **Carga de instancias:** Cargar un usuario por ID es una operación de un solo SELECT directo.
  - **Desventajas**:
    - **Integridad referencial débil:** Los atributos expedient y education deben ser declarados como nullable en la base de datos. Esto es un riesgo, ya que técnicamente se podría insertar un guía sin educación a nivel de base de datos, aunque la lógica de negocio diga lo contrario.
    - **Desperdicio de espacio:** Si la jerarquía crece mucho o tiene subclases muy distintas, la tabla terminará con muchísimas columnas en NULL, lo cual no es óptimo para la limpieza del esquema.
    - **Acoplamiento de cambios:** Cualquier cambio en la estructura de una subclase obliga a modificar la tabla única que comparten todos.

#### 35.​Reimplementar el mapeo de la misma jerarquía usando la estrategia JOINED.

- y. Ejecutar los tests y analizar el DDL: ¿cuantas tablas aparecen? ¿Qué FK existe entre ellas?
NO PUEDO EJECUTAR LOS TEST PEEEEERO, aparecen 3 tablas (User, DriverUser, TourGuideUser).  
Las tablas de las subclases (DRIVERS y GUIDES) contienen una columna (ID) que actúa como clave primaria y clave foránea (FK) al mismo tiempo, apuntando a la clave primaria de la tabla padre USERS (una tupla de USER y DRIVER seguro comparten ID, lo mismo con GUIDE). Esto garantiza que un registro en DRIVERS no pueda existir sin su contraparte correspondiente en USERS.
- z. Comparar el SQL generado por Hibernate al cargar un DriverUser en JOINED vs. en SINGLE_TABLE. ¿Cómo difiere?
NO PUEDO CORRER LOS TEST PEEEEERO, entiendo que en este caso la diferencia es que en JOINED se hace un JOIN entre USER y DriverUser mientras que en SUNGLE_TABLE es un SELECT directo (esto para la consulta). La pregunta es mas por el insert? de ser asi supongo que SINGLE_tABLE hace un solo insert mientras que en JOINED necesitariamos 2 (1 para USER, otro para DRIVER, y ambos vinculados por id de USER)
- aa.¿Cuáles son las ventajas y desventajas de JOINED para este modelo?
  - **Ventajas**:
    - **Integridad de Datos (Fuerte):** Es la mayor ventaja. Permite que atributos como expedient sean NOT NULL en la base de datos, ya que residen en tablas donde solo existen conductores.
    - **Esquema Normalizado:** No hay desperdicio de espacio con columnas en NULL. El diseño de la base de datos es "limpio" y sigue fielmente el diagrama de clases.
    - **Independencia de Subclases:** Si agregas un nuevo atributo a DriverUser, solo se modifica la tabla DRIVERS, sin afectar a los guías o usuarios base.
  - **Desventajas**:
    - **Performance en Lecturas:** Cualquier consulta, incluso por ID, requiere un JOIN. Si la jerarquía es profunda, el número de joins aumenta, degradando el rendimiento.
    - **Consultas Polimórficas:** Si pides "todos los Users", Hibernate debe hacer un OUTER JOIN con todas las tablas de la jerarquía (DRIVERS y GUIDES) para saber qué tipo de objeto instanciar, lo cual es ineficiente si hay muchas subclases.

#### 36.​Realice el mismo proceso ahora con la estrategia TABLE_PER_CLASS. Indique cuál le parece la mejor estrategia para este modelo concreto y justifique su elección.

Para el modelo de Tours, la mejor estrategia es SINGLE_TABLE (Tabla única), seguida muy de cerca por JOINED. La estrategia TABLE_PER_CLASS queda descartada para este caso concreto por las siguientes razones:  

- **Fuerte polimorfismo hacia User:** En este modelo, la entidad Purchase tiene una relación @ManyToOne con la clase padre User (el cliente). En TABLE_PER_CLASS, la base de datos no puede establecer una Foreign Key (FK) física desde PURCHASE hacia USER porque no existe una tabla central de usuarios; los datos están repartidos en tablas independientes.
- **Performance en consultas polimórficas:** El sistema probablemente requiera listar "todos los usuarios" (para administradores o procesos de login). En SINGLE_TABLE, esto es un SELECT simple. En TABLE_PER_CLASS, Hibernate debe ejecutar un UNION, que es una de las operaciones más costosas y lentas de SQL.
- **Simplicidad vs. Integridad:** Aunque JOINED ofrece una mejor normalización y permite restricciones NOT NULL en subclases, para una jerarquía pequeña de solo tres niveles y con atributos simples (expedient, education), la complejidad adicional de los JOINs constantes no suele justificar la ganancia frente a la velocidad y simplicidad de la tabla única.
- **Conclusión:** Dado que el modelo trata frecuentemente a los conductores, guías y clientes como un User genérico (por ejemplo, en la compra o el acceso al sistema), SINGLE_TABLE es la opción más eficiente y balanceada.

#### 37.​Route tiene relaciones muchos-a-muchos con DriverUser y TourGuideUser (subclases de User). Analizar el impacto de la estrategia de herencia sobre la tabla join:

- bb.​Si la jerarquía es SINGLE_TABLE, ¿a que tabla apunta la FK en la tabla join?
  - Si se utiliza la estrategia SINGLE_TABLE, toda la jerarquía de usuarios (incluyendo conductores y guías) se almacena en una única tabla física (por ejemplo, la tabla USERS).
  - **Hacia qué tabla apunta la FK:** En las tablas join (como ROUTE_DRIVERS o ROUTE_GUIDES), la clave foránea que referencia al usuario apunta siempre a la tabla única USERS.
  - **Identificación:** Hibernate utiliza la columna discriminadora dentro de esa tabla única para diferenciar si el ID al que apunta la FK en la tabla join corresponde efectivamente a un conductor o a un guía en el momento de hidratar los objetos.
- cc.Si la jerarquía es JOINED, ¿cambia la tabla destino de esa FK?
  - Si se utiliza la estrategia JOINED, la tabla destino de la clave foránea sí cambia respecto al caso anterior.
  - **Tabla destino de la FK:** En esta estrategia, cada subclase tiene su propia tabla física independiente (por ejemplo, la tabla DRIVERS para DriverUser y GUIDES para TourGuideUser).
  - **Cambio en la relación:** Cuando se define una relación específica con una subclase (como Route con DriverUser), JPA configura la FK de la tabla join para que apunte directamente a la tabla de la subclase correspondiente (DRIVERS o GUIDES) en lugar de a la tabla padre general.
  - **Integridad:** Esto permite una integridad referencial más fuerte a nivel de base de datos, ya que la tabla intermedia garantiza que la relación sea específicamente con un registro existente en la tabla del tipo concreto de usuario requerido.

#### 38.​¿Qué estrategia resulta más robusta ante cambios futuros como agregar una nueva subclase de User? Justificar con al menos dos argumentos.

La estrategia que resulta más robusta ante cambios futuros, como la adición de una nueva subclase, es la estrategia JOINED. Aunque es técnicamente más compleja que SINGLE_TABLE, ofrece una estructura más flexible y segura para la evolución del modelo.

- **Independencia y limpieza del esquema (Normalización):**
  - Al agregar una nueva subclase en la estrategia JOINED, solo es necesario crear una nueva tabla independiente para almacenar los atributos específicos de esa clase.
  - Esto evita "contaminar" las tablas existentes. En cambio, en SINGLE_TABLE, cada nueva subclase obliga a agregar columnas a la tabla única central, la cual se vuelve cada vez más grande y difícil de administrar al tener múltiples columnas que solo contienen valores nulos para la mayoría de los registros.
- **Integridad de datos superior (Restricciones NOT NULL):**
  - JOINED permite que los atributos específicos de la nueva subclase sean declarados como NOT NULL directamente en la base de datos, garantizando que la información obligatoria del negocio se cumpla a nivel del motor de BD.
  - En la estrategia SINGLE_TABLE, todos los atributos de las subclases deben ser obligatoriamente nullable=true en la base de datos (ya que una fila de otra subclase dejaría esos campos vacíos), lo que debilita la integridad referencial y deja la validación de datos únicamente en manos de la aplicación.
- **Facilidad de mantenimiento de la jerarquía:**
  - Si en el futuro se decide agregar un atributo común a todas las clases (en la clase padre User), en la estrategia JOINED solo se debe modificar la tabla padre.
  - Esto es mucho más robusto que la estrategia TABLE_PER_CLASS, donde habría que recordar y modificar manualmente cada una de las tablas de las subclases donde se repartió la información, lo cual es propenso a errores y omisiones en sistemas con gran cantidad de tablas.

## Sección 3 - Repositorios, Consultas y Transacciones

### 3.1 Patrón de acceso a datos: DAO y Repository

#### 39.​Definir el patrón DAO (Data Access Object) y el patrón Repository. ¿Cuál es la diferencia conceptual más importante entre ambos? ¿En qué se diferencia su rol dentro de la arquitectura de la aplicación?

El patrón DAO (Data Access Object) y el patrón Repository son abstracciones utilizadas para gestionar la persistencia, pero difieren fundamentalmente en su concepción y en cómo se integran en la arquitectura.

- **DAO (Data Access Object):** Es un patrón que establece que, por cada objeto de dominio persistente, debe existir una clase encargada de persistir y recuperar sus instancias. Su enfoque es técnico y está estrechamente ligado a las operaciones de la base de datos (típicamente CRUD: Create, Retrieve, Update, Delete).
- **Repository:** Se define como una colección de objetos en memoria que además persiste su estado. En lugar de verse como una vía de acceso a una base de datos, actúa como un almacén de objetos al que se le pueden aplicar filtros y criterios de búsqueda, emulando el comportamiento de las colecciones de Smalltalk.

**Diferencia conceptual más importante**  

- El DAO representa una capa de acceso a datos pura; es un intermediario que sabe cómo hablar con la base de datos para un objeto específico.
- El Repository representa una colección de objetos persistentes. No se enfoca en "guardar" datos, sino en gestionar un conjunto de objetos. Gracias a la persistencia por alcance de los ORM modernos (como Hibernate), un repositorio a menudo no necesita operaciones explícitas de "update", ya que los cambios en los objetos de la colección se sincronizan automáticamente con la base de datos.

**Diferencias en el rol dentro de la arquitectura**  

- **Nivel de Abstracción:**
  - El DAO suele estar en un nivel más bajo, a menudo ocultando código SQL nativo o JDBC.
  - El Repository se sitúa entre la capa de dominio y la de acceso a datos, ofreciendo una interfaz más limpia a la capa de servicio. Su rol es ocultar la complejidad de los lenguajes de consulta como HQL o JPQL, actuando como una "máscara" que devuelve objetos del dominio.
- **Granularidad y Lógica:**
  - Los DAOs pueden generar modelos "anémicos" si la lógica del negocio empieza a mudarse hacia ellos debido a su estructura de 1:1 con las tablas.
  - Los Repositorios permiten centralizar responsabilidades de búsqueda y filtrado de forma más organizada, permitiendo que la capa de servicio coordine las operaciones del modelo sin conocer los detalles técnicos de la persistencia.
- **Gestión del Ciclo de Vida:**
  - En un esquema típico de DAO, se invoca explícitamente el guardado o la actualización.
  - En un Repository que utiliza un ORM, se aprovecha el contexto de persistencia para que el objeto interactúe con la Session de Hibernate, permitiendo que el repositorio se enfoque primordialmente en la recuperación (fetch) eficiente de los datos.

#### 40.​El patrón Repository puede pensarse como una colección de objetos en memoria que además persiste su estado. Describir cómo se implementa este concepto usando Hibernate: ¿qué responsabilidades concentra un repositorio? ¿Con qué objeto de Hibernate interactúa internamente?

El patrón Repository se implementa en Hibernate bajo la metáfora de una colección de objetos persistentes, actuando como un intermediario que simula tener todos los objetos en memoria aunque estos residan físicamente en la base de datos.  

**Responsabilidades de un repositorio**

- **Abstracción de la persistencia:** Actúa como una "máscara" que oculta la complejidad del lenguaje de consultas (HQL, JPQL o Criteria) y los detalles técnicos del acceso a datos, ofreciendo una interfaz limpia a la capa de servicio.
- **Gestión de búsquedas y recuperación (Fetch):** Su responsabilidad principal es la operación de recuperación (Retrieve). Se enfoca en proveer métodos de filtrado específicos (como findProjectByName o getUsersCount) que emulan el comportamiento de las colecciones de lenguajes como Smalltalk.
- **Encapsulamiento de criterios de búsqueda:** Centraliza la lógica necesaria para filtrar elementos de forma eficiente, evitando que el resto de la aplicación deba conocer cómo se realizan técnicamente las consultas al repositorio secundario.
- **Delegación de actualizaciones:** A diferencia de un DAO tradicional, un repositorio a menudo no requiere métodos explícitos de "update" o "save" para cada cambio, ya que aprovecha el mecanismo de persistencia por alcance del ORM para sincronizar automáticamente el estado de los objetos modificados dentro de una transacción.

Internamente, el repositorio interactúa directamente con el objeto Session de Hibernate.  
Para realizar sus funciones, el repositorio suele recibir una inyección de la SessionFactory y utiliza el método getCurrentSession() para obtener la sesión activa de la transacción. A través de este objeto Session, el repositorio ejecuta las sentencias HQL o Criteria necesarias para hidratar los objetos del dominio desde la base de datos.

### 3.2 Transacciones

#### 42.​¿Que es una transacción en el contexto de Hibernate? ¿Por qué es necesaria? ¿Qué ocurre si se realizan operaciones de escritura sin una transacción activa?

Una transacción define los límites de una unidad de trabajo; es decir, representa un conjunto de operaciones (como guardar, actualizar o borrar objetos) que deben ejecutarse de forma atómica, consistente, aislada y duradera (propiedades ACID). En un entorno orientado a objetos, las transacciones se tratan como objetos propiamente dichos a los que se les envían mensajes como begin() para iniciarla, commit() para confirmar los cambios o rollback() para deshacerlos en caso de error.  
La transacción es el mecanismo fundamental para garantizar la integridad de la información en ambientes concurrentes y multiusuario. Su necesidad se fundamenta en las propiedades ACID:  

- **Atomicidad:** Asegura que todo el conjunto de operaciones se ejecute como una unidad indivisible; si una falla, no se ejecuta nada.
- **Consistencia:** Garantiza que la base de datos pase de un estado coherente a otro.
- **Aislamiento:** Evita que las operaciones de un usuario interfieran con las de otros hasta que hayan finalizado satisfactoriamente.
- **Durabilidad:** Asegura que, una vez confirmado el cambio, este se mantenga incluso ante fallas del sistema. Además, Hibernate utiliza la transacción para llevar un registro de los cambios en los objetos y decidir, al momento del commit, si debe actualizar la base de datos o desestimar los cambios.

Realizar operaciones sin una transacción activa compromete la seguridad y coherencia de los datos:

- **Falta de persistencia:** Los cambios realizados en los objetos en memoria no se verán reflejados en la base de datos, ya que es en el momento del commit() cuando Hibernate aplica la "persistencia por alcance" y sincroniza los cambios con el repositorio físico.
- **Excepciones técnicas:** Por norma general, no está permitido operar con información vinculada a la base de datos fuera de una transacción para mantener las propiedades ACID.
- **Errores en colaboradores:** Aunque por optimización de rendimiento la lectura de atributos básicos en memoria podría no fallar inmediatamente, intentar acceder a objetos relacionados (colaboradores) o realizar escrituras sin un marco transaccional provocará una excepción.

#### 43.​¿En qué capa de la aplicación debería gestionarse la transacción: en el repositorio o en la capa de servicio? Justificar la elección. ¿Qué ocurre si una misma operación necesita de varios accesos a la base de datos?

En una arquitectura multicapa, la gestión de la transacción debe realizarse en la capa de servicio, ya que es la encargada de coordinar las operaciones del modelo y definir los límites de la unidad de trabajo.  

- **Coordinación de operaciones (Unidad de Trabajo):** La capa de servicio implementa la lógica de aplicación y suele requerir la interacción con múltiples repositorios o la ejecución de varios pasos lógicos que deben ser atómicos. Si la transacción se gestionara en el repositorio, cada método de acceso a datos abriría y cerraría su propia transacción, lo que impediría tratar a la operación completa como una sola unidad indivisible.
- **Transparencia y Desacoplamiento:** Al gestionar las transacciones en los servicios (a menudo utilizando patrones como Decorator o mediante configuración declarativa con frameworks como Spring), se logra que el modelo de dominio y los repositorios permanezcan independientes de los detalles técnicos de la persistencia. Esto facilita las pruebas unitarias y el mantenimiento del código.
- **Preservación de las propiedades ACID:** La capa de servicio garantiza que toda la serie de operaciones cumpla con las propiedades ACID (Atomicidad, Consistencia, Aislamiento y Durabilidad), asegurando que si una parte de la lógica de negocio falla, todos los cambios previos realizados en esa misma transacción se deshagan (rollback) para mantener la integridad de la base de datos.

**¿Qué ocurre si una misma operación necesita varios accesos a la base de datos?**

Si una operación lógica (por ejemplo, realizar una compra que implica registrar ítems, actualizar stock y vincular un usuario) requiere varios accesos al repositorio físico, todos ellos deben ejecutarse dentro del marco de la misma transacción iniciada por el servicio.  

- **Flujo de ejecución:** El servicio inicia la transacción, invoca a los diferentes repositorios necesarios para recuperar o persistir información y, una vez finalizada toda la lógica de negocio satisfactoriamente, se realiza el commit.
- **Hidratación y Proxy:** Durante este proceso, Hibernate puede utilizar proxies para cargar objetos de forma diferida (lazy loading), asegurando que los datos relacionados solo se traigan de la base de datos si el servicio realmente los requiere, todo bajo el marco protector de la misma transacción activa.
- **Manejo de fallos:** Si cualquiera de esos múltiples accesos o la lógica intermedia falla, el servicio es responsable de asegurar que se ejecute un rollback, evitando que la base de datos quede en un estado inconsistente con solo una parte de la información procesada.

#### 44.​Implementar una capa de servicio que coordine las operaciones del modelo usando transacciones correctamente. Como mínimo implementar:​

- a. Creación de todas las entidades persistentes. ​
- b. Actualización del precio de un servicio existente.​
- c. Agregar un nuevo ítem a una compra existente.​
- d. Eliminar una ruta existente siempre que no tenga compras asociadas.

### 3.3 Consultas con HQL y JPQL

#### 45.​¿Que diferencia hay entre HQL/JPQL y SQL nativo? ¿Qué entidades, atributos y relaciones entienden HQL/JPQL que SQL no conoce directamente?

La diferencia fundamental entre HQL/JPQL y SQL nativo radica en el modelo sobre el cual operan: mientras que SQL está orientado al modelo relacional (tablas y columnas), HQL/JPQL está orientado al modelo de objetos (clases y atributos).

- **Entidades y Clases vs. Tablas**
  - **HQL/JPQL:** En la cláusula FROM, se utilizan los nombres de las clases de Java (entidades) definidas en el modelo (ej. FROM Route).
  - **SQL:** Solo entiende nombres de tablas físicas en el motor de base de datos. Si una clase se mapea a una tabla con nombre distinto, SQL requiere el nombre de la tabla, mientras que HQL usa el de la clase.
- **Atributos vs. Columnas**
  - **HQL/JPQL:** Las consultas se realizan sobre las variables de instancia (atributos) de los objetos. Hibernate se encarga de traducir estos nombres a las columnas reales de la base de datos basándose en el mapeo.
  - **SQL:** Requiere conocer el nombre exacto de las columnas de la tabla.
- **Jerarquías e Herencia (Polimorfismo) (Esta es una de las mayores diferencias conceptuales):**
  - **HQL/JPQL:** Entiende el concepto de polimorfismo. Si se realiza una consulta sobre una superclase (ej. FROM User), Hibernate automáticamente incluye instancias de sus subclases (DriverUser, TourGuideUser) en el resultado.
  - **SQL:** No comprende la herencia. Para obtener un resultado similar, SQL requeriría realizar múltiples JOIN o UNION de forma manual entre las tablas que representan a los hijos, ya que para el motor relacional son tablas independientes.
- **Relaciones y Navegación (Path Expressions)**
  - **HQL/JPQL:** Permite "navegar" por las relaciones del grafo de objetos utilizando el operador punto (.), lo que se conoce como path expressions (ej. select p.user.username from Purchase p). Hibernate genera automáticamente los JOINs necesarios bajo cuerda.
  - **SQL:** No puede navegar por relaciones; requiere que el desarrollador especifique explícitamente las condiciones de unión (JOIN) comparando claves foráneas y primarias entre tablas.
- T**ipos de Retorno**
  - **HQL/JPQL:** Retorna directamente objetos del dominio (instancias de las clases de Java) ya "hidratados" y listos para ser usados.
  - **SQL:** Retorna un conjunto de tuplas (filas y columnas) que luego deben ser transformadas manualmente a objetos si no se utiliza un ORM.


#### 47.​¿En qué casos conviene usar una consulta SQL nativa en lugar de HQL/JPQL? Describir al menos un caso concreto del modelo donde esto sería necesario.

El uso de consultas SQL nativas en lugar de HQL/JPQL se recomienda principalmente en situaciones de extrema necesidad donde el nivel de abstracción del ORM se convierte en una limitación técnica o de rendimiento.

**Casos en los que conviene usar SQL nativo:**

- **Rendimiento crítico:** SQL es técnicamente más rápido que HQL/Criteria porque se comunica directamente con el motor sin la sobrecarga de traducción y mapeo de objetos que realiza Hibernate.
- **Uso de funciones específicas del motor:** Cuando se requiere utilizar características propietarias de una base de datos (como Oracle, MySQL o PostgreSQL) que no forman parte del estándar SQL-92 y que HQL no puede interpretar. Ejemplos de esto son las window functions (RANK(), LEAD()), búsquedas de texto completo (full-text search) o funciones geoespaciales.
- **Consultas de reporte masivas:** Si un reporte requiere procesar millones de tuplas para devolver solo un resultado agregado, hidratar miles de objetos Java a través de HQL sería ineficiente en memoria y tiempo. SQL nativo permite procesar esto íntegramente en el motor de BD.
- **Optimización manual de JOINS:** En modelos con múltiples relaciones y recursión donde el SQL generado por Hibernate puede ser poco eficiente (generando demasiados joins), escribir el SQL a mano permite un control total sobre el plan de ejecución.