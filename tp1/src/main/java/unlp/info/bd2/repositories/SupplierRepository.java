package unlp.info.bd2.repositories;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import unlp.info.bd2.model.Supplier;
import unlp.info.bd2.utils.ToursException;

public class SupplierRepository implements ToursRepository<Supplier> {
    
    @Autowired
    private SessionFactory sessionFactory;

    public void save(Supplier supplier) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.persist(supplier);
            session.flush();
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    
    public Supplier update(Supplier supplier) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.merge(supplier);
            return supplier;
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public void delete(Supplier supplier) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.remove(supplier);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<Supplier> findById(Long id) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            Supplier supplier = session
                .createQuery("from Supplier where id = :id", Supplier.class)
                .setParameter("id", id)
                .uniqueResult();
            return Optional.ofNullable(supplier);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<Supplier> findAll() throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Supplier> suppliers = session
                .createQuery("from Supplier", Supplier.class)
                .list();
            return suppliers;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<Supplier> getByAuthorizationNumber(String authorizationNumber) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            Supplier supplier = session
                .createQuery("from Supplier where authorizationNumber = :authorizationNumber", Supplier.class)
                .setParameter("authorizationNumber", authorizationNumber)
                .uniqueResult();
            return Optional.ofNullable(supplier);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<Supplier> getTopNSuppliersInPurchase(int n) throws ToursException{
        Session session = null;
        String query = "SELECT s.supplier FROM Service s JOIN s.itemServiceList it JOIN it.purchase p GROUP BY s.supplier ORDER BY COUNT(p) DESC LIMIT :n";
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Supplier> suppliers = session
                .createQuery(query, Supplier.class)
                .setParameter("n", n)
                .list();
            return suppliers;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
}
