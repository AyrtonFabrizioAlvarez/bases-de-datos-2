package unlp.info.bd2.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import unlp.info.bd2.model.DriverUser;
import unlp.info.bd2.model.TourGuideUser;
import unlp.info.bd2.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

    //QUERYMETHOD QUE PIDEN DE PRUEBA
    public User findByEmail(String email);
    
    //QUERY QUE PIDEN EXPLICITAMENTE
    @Query("SELECT DISTINCT p.user FROM Purchase p WHERE p.totalPrice >= :amount")
    public List<User> getUserSpendingMoreThan(@Param("amount") float amount, Pageable pageable);

    @Query("SELECT DISTINCT tg FROM Purchase p JOIN p.route.tourGuideList tg WHERE p.review.rating = 1")
    public List<TourGuideUser> getTourGuidesWithRating1(Pageable pageable);
    

    //QUERYMETHOD QUE NO PIDEN PERO YA LO TENIA DEL TP1
    public User findByUsername(String username); //aca funciona el polimorfismo o es uno por cada subclase?

    @Query("from DriverUser where username = :username")
    public DriverUser findDriverByUsername(String username);

    @Query("from TourGuideUser where username = :username")
    public TourGuideUser findTourGuideByUsername(String username);

}
