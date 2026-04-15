package unlp.info.bd2.repositories;

import java.util.Optional;
import java.util.List;
import unlp.info.bd2.model.Stop;
import unlp.info.bd2.utils.ToursException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class StopRepository implements ToursRepository<Stop> {
    
        
    @Autowired
    private SessionFactory sessionFactory;

    public void save(Stop stop) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.persist(stop);
            session.flush();
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    
    public Stop update(Stop stop) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.merge(stop);
            return stop;
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public void delete(Stop stop) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.remove(stop);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<Stop> findById(Long id) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            Stop stop = session
                .createQuery("from Stop where id = :id", Stop.class)
                .setParameter("id", id)
                .uniqueResult();
            return Optional.ofNullable(stop);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<Stop> findAll() throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Stop> stops = session
                .createQuery("from Stop", Stop.class)
                .list();
            return stops;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<Stop> getByNameStart(String name) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Stop> stops = session
                .createQuery("from Stop where name LIKE :name", Stop.class)
                .setParameter("name", name + "%")
                .list();
            return stops;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
}
