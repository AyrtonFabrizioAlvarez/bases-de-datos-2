package unlp.info.bd2.services;
import unlp.info.bd2.model.*;
import unlp.info.bd2.utils.ToursException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ToursService {

    User createUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber) throws ToursException;
    DriverUser createDriverUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber, String expedient) throws ToursException;
    TourGuideUser createTourGuideUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber, String education) throws ToursException;
    Optional<User> getUserById(Long id) throws ToursException;
    Optional<User> getUserByUsername(String username) throws ToursException;
    User updateUser(User user) throws ToursException;
    void deleteUser(User user) throws ToursException;
    Stop createStop(String name, String description) throws ToursException;
    List<Stop> getStopByNameStart(String name) throws ToursException;
    Route createRoute(String name, float price, float totalKm, int maxNumberOfUsers, List<Stop> stops) throws ToursException;
    Optional<Route> getRouteById(Long id) throws ToursException;
    List<Route> getRoutesBelowPrice(float price) throws ToursException;
    void assignDriverByUsername(String username, Long idRoute) throws ToursException;
    void assignTourGuideByUsername(String username, Long idRoute) throws ToursException;
    Supplier createSupplier(String businessName, String authorizationNumber) throws ToursException;
    Service addServiceToSupplier(String name, float price, String description, Supplier supplier) throws ToursException;
    Service updateServicePriceById(Long id, float newPrice) throws ToursException;
    Optional<Supplier> getSupplierById(Long id) throws ToursException;
    Optional<Supplier> getSupplierByAuthorizationNumber(String authorizationNumber) throws ToursException;
    Optional<Service> getServiceByNameAndSupplierId(String name, Long id) throws ToursException;
    Purchase createPurchase(String code, Route route, User user) throws ToursException;
    Purchase createPurchase(String code, Date date, Route route, User user) throws ToursException;
    ItemService addItemToPurchase(Service service, int quantity, Purchase purchase) throws ToursException;
    Optional<Purchase> getPurchaseByCode(String code) throws ToursException;
    void deletePurchase(Purchase purchase) throws ToursException;
    Review addReviewToPurchase(int rating, String comment, Purchase purchase) throws ToursException;

    void deleteRoute(Route route) throws ToursException;

    // CONSULTAS HQL
    List<Purchase> getAllPurchasesOfUsername(String username) throws ToursException;
    List<User> getUserSpendingMoreThan(float mount) throws ToursException;
    List<Supplier> getTopNSuppliersInPurchases(int n) throws ToursException;
    long getCountOfPurchasesBetweenDates(Date start, Date end) throws ToursException;
    List<Route> getRoutesWithStop(Stop stop) throws ToursException;
    Long getMaxStopOfRoutes() throws ToursException;
    List<Route> getRoutsNotSell() throws ToursException;
    List<Route> getTop3RoutesWithMaxRating() throws ToursException;
    Service getMostDemandedService() throws ToursException;
    List<TourGuideUser> getTourGuidesWithRating1() throws ToursException;

}
