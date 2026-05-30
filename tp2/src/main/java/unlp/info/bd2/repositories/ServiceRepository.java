package unlp.info.bd2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import unlp.info.bd2.model.Service;

public interface ServiceRepository extends CrudRepository<Service, Long> {
    
    //QUERY QUE PIDEN EXPPLICITAMENTE
    @Query("SELECT i.service FROM ItemService i GROUP BY i.service ORDER BY SUM(i.quantity) DESC LIMIT 1")
    public Service getMostDemandedService();

    //QUERYMETHOD QUE NO PIDEN PERO YA LO TENIA DEL TP1
    public Service findByNameAndSupplierId(String name, Long id);
}
