package unlp.info.bd2.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import unlp.info.bd2.dto.RouteListDTO;
import unlp.info.bd2.model.Route;
import unlp.info.bd2.model.Stop;

public interface RouteRepository extends CrudRepository<Route, Long> {

    //QUERYMETHODS QUE PIDEN DE PRUEBA
    public List<Route> findByPriceLessThanOrderByNameAsc(float price);
    
    //QUERYMETHODS QUE PIDEN EXPLICITAMENTE
    public List<Route> findByStops (Stop stop, Pageable pageable);

    
    //QUERYMETHOD QUE NO PIDEN PERO YA LO TENIA DEL TP1
    public List<Route> findByPriceLessThan(float price, Pageable pageable);
    
   
    //QUERY QUE PIDEN EXPPLICITAMENTE
    @Query("SELECT MAX(SIZE(r.stops)) FROM Route r")
    public Integer getMaxStopOfRoutes();

    @Query("SELECT r FROM Route r WHERE NOT EXISTS (SELECT p FROM Purchase p WHERE p.route = r)")
    public List<Route> getRoutesNotSell(Pageable pageable);

    @Query("SELECT p.route FROM Purchase p GROUP BY p.route ORDER BY MAX(p.review.rating) DESC LIMIT 3")
    public List<Route> getTop3RoutesWithMaxRating();

    //QUERY METHOD QUE SE UTILIZARIA PARA HIDRATAR EL DTO
    @Query("SELECT new unlp.info.bd2.dto.RouteListDTO(r.name, COUNT(p), AVG(p.totalPrice)) FROM Purchase p JOIN p.route r GROUP BY r.name ")
    public List<RouteListDTO> getRouteListDTO();

}
