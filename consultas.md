# CONSULTAS TP1

### ID'S

- Siempre vamos a necesitar utilizar la estrategia de generacion SEQUENCE para obtener el id antes del insert y de esa manera gestionar casos donde lo necesitemos antes? MANEJARNOS CON IDENTITY

## RELACIONES

- especificamente surgió en la pregunta 18.
  - vimos que hay una relacion 1a1 OPCIONAL entre PURCHASE - REVIEW, y no me queda claro si en este caso cualquiera podría ser la "dueña" de la relacion y que implica esto realmente para todos los casos. ¿Sería quien tiene la FK "nada mas"? ¿En el caso de 1a1 entonces sería indistinto cual es la dueña?
  - Entiendo que podria tener una columna review_id en null, pero no sería mejor directamente no tener una columna null y directamente tener la review con su purchase_id respectivo al crearse la review misma?

- Al momento de un ManyToMany donde hacemos JoinTable como tabla intermedia, existe diferencia entre JoinColumn e InverseJoinColumn? cual es? ver modelo de Route-Stop

## CASCADES

- Al momento de definir las cascades, en el caso de Purchase-Review, entiendo que por negocio no estaria bien que al eliminar una compra se mantenga un review ya que no tendria sentido al no saber a que compra pertenece. En estos casos, es mejor directamente usar cascade.ALL y que directamente se usen todos los valores? porque no me termina de quedar muy claro los roles de MERGE, REFRESH y DETACH o los escenarios donde estos valores se vuelven "necesarios"

- Si elimino un Service, esto traeria problema con los ItemService que lo referenciaban. Como se gestiona esto?
  - (ver Service-ItemService y ItemService-Service)


## HERENCIA

- Consultar la tabla del punto 33 para asegurarme justificaciones y casos border como cuando la clase padre de table_per_class es concreta
- Si bien entiendo pro restricciones, normalizacion y mantenimiento que nos sirve mas la estrategia JOINED que SINGLE_TABLE, hay un punto de profundidad de la jerarquia donde deje de servirnos por sumar demasiados JOIN? o es preferible ganarlo en restricciones, normalizacion y mantenimiento?


## REPOSITORY

- como se maneja esto? una INTERFAZ TOUR que establece las firmas de metodos comunes CRUD con tipos genericos T?
  - en este caso que veo que es repetir codigo salvo por como tipifico cada tip ode retorno o de parametro de las funciones, eso esta bien o delata que necesito una ABSTRACT en lugar de INTERFAZ donde hay cierta implementacion que se corresponde?

## TRANSACTION / SERVICE

- Establecemos que para mantener los principios ACID de transacciones con logica compleja y la separacion de capas de la arquitectura manejamos begin(), commit() y rollback() en el servicio, el tema es que en el ejemplo de EMPLEADOS utiliza en lugar de esto ya un decorador de SPRING, comos e soluciona?
  - la idea es inyectar la "currentSession" directamente en el repository y que la "sessionFactory" la tenga el service?