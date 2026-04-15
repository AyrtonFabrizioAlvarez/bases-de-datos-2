package unlp.info.bd2.repositories;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import unlp.info.bd2.model.Route;
import unlp.info.bd2.model.Stop;
import unlp.info.bd2.utils.ToursException;

public class RouteRepository implements ToursRepository<Route> {
    
    @Autowired
    private SessionFactory sessionFactory;

    public void save(Route route) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.persist(route);
            session.flush();
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Route update(Route route) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.merge(route);
            return route;
        } catch (Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public void delete(Route route) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            session.remove(route);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Optional<Route> findById(Long id) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            Route route = session
                .createQuery("from Route where id = :id", Route.class)
                .setParameter("id", id)
                .uniqueResult();
            return Optional.ofNullable(route);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<Route> findAll() throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Route> routes = session
                .createQuery("from Route", Route.class)
                .list();
            return routes;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<Route> getRoutesNotSelled() throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Route> routes = session
                .createQuery("SELECT r FROM Route r WHERE r NOT IN (SELECT p.route FROM Purchase p)", Route.class)
                .list();
            return routes;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<Route> getRoutesBelowPrice(float price) throws ToursException{
        Session session = null;
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Route> routes = session
                .createQuery("FROM Route r WHERE r.price < :price", Route.class)
                .setParameter("price", price)
                .list();
            return routes;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public List<Route> getRoutesWithStop(Stop stop) throws ToursException{
        Session session = null;
        String query = "FROM Route r WHERE :stop MEMBER OF r.stops";
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Route> routes = session
                .createQuery(query, Route.class)
                .setParameter("stop", stop)
                .list();
            return routes;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    public Long getMaxStopOfRoutes() throws ToursException{
        Session session = null;
        String query = "SELECT MAX(SIZE(r.stops)) FROM Route r";
        try {
            session = this.sessionFactory.getCurrentSession();
            Integer count = session
                .createQuery(query, Integer.class) // aca tuve que poner INTEGER porque MAX() retorna un entero
                .uniqueResult();
            return count.longValue(); // aca casteo a long para no romper ni cambiar el retorno
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }


    public List<Route> getTop3RoutesWithMaxRating() throws ToursException{
        Session session = null;
        String query = "SELECT p.route FROM Purchase p GROUP BY p.route ORDER BY MAX(p.review.rating) DESC LIMIT 3";
        try {
            session = this.sessionFactory.getCurrentSession();
            List<Route> routes = session
                .createQuery(query, Route.class)
                .list();
            return routes;
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
}
