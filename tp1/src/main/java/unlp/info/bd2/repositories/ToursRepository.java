package unlp.info.bd2.repositories;

import java.util.List;
import java.util.Optional;

import unlp.info.bd2.utils.ToursException;


public interface ToursRepository<T> {
    void save(T anObject) throws ToursException;
    T update(T anObject) throws ToursException;
    void delete(T anObject) throws ToursException;
    Optional<T> findById(Long id) throws ToursException;
    List<T> findAll() throws ToursException;
}
