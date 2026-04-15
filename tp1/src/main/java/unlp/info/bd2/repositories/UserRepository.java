package unlp.info.bd2.repositories;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import unlp.info.bd2.model.DriverUser;
import unlp.info.bd2.model.TourGuideUser;
import unlp.info.bd2.model.User;
import unlp.info.bd2.utils.ToursException;

public class UserRepository implements ToursRepository<User> {
    
    @Autowired
    private SessionFactory sessionFactory;

    public void save(User user) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.persist(user);
            session.flush();
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    
    public User update(User user) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            User managed_user = session.merge(user);
            return managed_user;
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public void delete(User user) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.remove(user);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<User> findById(Long id) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            User user = session
                .createQuery("from User where id = :id", User.class)
                .setParameter("id", id)
                .uniqueResult();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<User> findByUserName(String username) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            User user = session
                .createQuery("from User where username = :username", User.class)
                .setParameter("username", username)
                .uniqueResult();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<DriverUser> findDriverByUserName(String username) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            DriverUser driver = session
                .createQuery("from User where username = :username", DriverUser.class)
                .setParameter("username", username)
                .uniqueResult();
            return Optional.ofNullable(driver);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<TourGuideUser> findTourGuideByUserName(String username) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            TourGuideUser tourGuide = session
                .createQuery("from User where username = :username", TourGuideUser.class)
                .setParameter("username", username)
                .uniqueResult();
            return Optional.ofNullable(tourGuide);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<User> findAll() throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            List<User> users = session
                .createQuery("from User", User.class)
                .list();
            return users;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<User> usersSpendingMoreThan(float mount) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            List<User> users = session
                .createQuery("from User u where u IN (Select p.user from Purchase p where p.totalPrice >= :mount)", User.class)
                .setParameter("mount", mount)
                .list();
            return users;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<TourGuideUser> getTourGuidesWithRating1() throws ToursException{
        Session session = null;
        //String query = "SELECT p.route.tourGuideList FROM Purchase p WHERE p.review.rating = 1";
        String query = "SELECT DISTINCT tg FROM Purchase p JOIN p.route.tourGuideList tg WHERE p.review.rating = 1";
        try {
            session = this.sessionFactory.getCurrentSession();
            List<TourGuideUser> users = session
                .createQuery(query, TourGuideUser.class)
                .list();
            return users;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
}
