package unlp.info.bd2.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import unlp.info.bd2.model.Supplier;

public interface SupplierRepository extends CrudRepository<Supplier, Long> {

    //QUERY QUE PIDEN EXPLICITAMENTE
    @Query("SELECT s.supplier FROM Service s JOIN s.itemServiceList it JOIN it.purchase p GROUP BY s.supplier ORDER BY COUNT(p) DESC")
    public List<Supplier> getTopNSuppliersInPurchases(@Param("n") int n, Pageable pageable);
    
    //QUERYMETHOD QUE NO PIDEN PERO YA LO TENIA DEL TP1
    public Supplier findByAuthorizationNumber(String authorizationNumber);

}
