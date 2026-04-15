package unlp.info.bd2.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import unlp.info.bd2.model.Purchase;
import unlp.info.bd2.utils.ToursException;

public class PurchaseRepository implements ToursRepository<Purchase>{

    @Autowired
    private SessionFactory sessionFactory;

    public void save(Purchase purchase) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.persist(purchase);
            session.flush();
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    

    public Purchase update(Purchase purchase) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            Purchase managed_purchase = session.merge(purchase);
            return managed_purchase;
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public void delete(Purchase purchase) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.remove(purchase);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<Purchase> findById(Long id) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            Purchase purchase = session
                .createQuery("from Purchase where id = :id", Purchase.class)
                .setParameter("id", id)
                .uniqueResult();
            return Optional.ofNullable(purchase);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<Purchase> findAll() throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Purchase> purchases = session
                .createQuery("from Purchase", Purchase.class)
                .list();
            return purchases;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<Purchase> findAll(String username) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Purchase> purchases = session
                .createQuery("from Purchase p where p.user.username = :username", Purchase.class)
                .setParameter("username", username)
                .list();
            return purchases;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<Purchase> getByCode(String code) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            Purchase purchase = session
                .createQuery("from Purchase where code = :code", Purchase.class)
                .setParameter("code", code)
                .uniqueResult();
            return Optional.ofNullable(purchase);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public long getCountOfPurchasesBetweenDates(Date start, Date end) throws ToursException{
        Session session = null;
        String query = "SELECT COUNT(p) FROM Purchase p WHERE p.date>=:start AND p.date<=:end";
        try {
            session = this.sessionFactory.getCurrentSession();
            long count = session
                .createQuery(query, Long.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .uniqueResult();
            return count;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    

}
