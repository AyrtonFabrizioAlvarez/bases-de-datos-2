package unlp.info.bd2.repositories;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import unlp.info.bd2.model.Service;
import unlp.info.bd2.utils.ToursException;

public class ServiceRepository implements ToursRepository<Service> {
    
    @Autowired
    private SessionFactory sessionFactory;

    public void save(Service service) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.persist(service);
            session.flush();
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    
    public Service update(Service service) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.merge(service);
            return service;
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public void delete(Service service) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.remove(service);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<Service> findById(Long id) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            Service service = session
                .createQuery("from Service where id = :id", Service.class)
                .setParameter("id", id)
                .uniqueResult();
            return Optional.ofNullable(service);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<Service> findAll() throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Service> services = session
                .createQuery("from Service", Service.class)
                .list();
            return services;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<Service> getByServiceByNameAndSupplierId(String name, Long supplier_id) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            Service service = session
                .createQuery("from Service where name = :name and supplier.id = :supplier_id", Service.class)
                .setParameter("name", name)
                .setParameter("supplier_id", supplier_id)
                .uniqueResult();
            return Optional.ofNullable(service);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    
    public Service getMostDemandedService() throws ToursException{
        //servicio que más veces fue incluido en compras, teniendo en cuenta la cantidad.
        Session session = null;
        String query = "SELECT i.service FROM ItemService i GROUP BY i.service ORDER BY SUM(i.quantity) DESC LIMIT 1";
        try {
            session = this.sessionFactory.getCurrentSession();
            Service service = session
                .createQuery(query, Service.class)
                .uniqueResult();
            return service;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
}
