package unlp.info.bd2.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import unlp.info.bd2.model.Stop;

public interface StopRepository extends CrudRepository<Stop, Long> {
     
    //QUERYMETHOD QUE NO PIDEN PERO YA LO TENIA DEL TP1
    public List<Stop> findByNameStartingWith(String name, Pageable pageable);
    
}
