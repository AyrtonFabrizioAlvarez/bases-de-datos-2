package unlp.info.bd2.repositories;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import unlp.info.bd2.model.Purchase;
import unlp.info.bd2.model.Route;

public interface PurchaseRepository extends CrudRepository<Purchase, Long>{

    //QUERYMETHODS QUE PIDEN DE PRUEBA
    public List<Purchase> findByUserUsername(String username);
    public boolean existsByRoute(Route route);

    //QUERYMETHODS QUE PIDEN EXPLICITAMENTE
    public List<Purchase> findByUserUsername(String username,  Pageable pageable); 
    public long countByDateBetween(Date from, Date to);

    //QUERYMETHOD QUE NO PIDEN PERO YA LO TENIA DEL TP1
    public Purchase getByCode(String code);

}
