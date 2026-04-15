package unlp.info.bd2.services;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.hibernate.SessionFactory;

import jakarta.transaction.Transactional;
import unlp.info.bd2.model.*;
import unlp.info.bd2.repositories.*;
import unlp.info.bd2.utils.ToursException;

public class ToursServiceImpl implements ToursService {
    public PurchaseRepository purchaseRepo;
    public ReviewRepository reviewRepo;
    public RouteRepository routeRepo;
    public ServiceRepository serviceRepo;
    public SupplierRepository supplierRepo;
    public UserRepository userRepo;
    public StopRepository stopRepo;

    @Autowired
    private SessionFactory sessionFactory;

    public ToursServiceImpl(
        PurchaseRepository purchaseRepo, ReviewRepository reviewRepo,
        RouteRepository routeRepo, ServiceRepository serviceRepo,
        SupplierRepository supplierRepo, UserRepository userRepo,
        StopRepository stopRepo
    ){
        this.purchaseRepo = purchaseRepo;
        this.reviewRepo = reviewRepo;
        this.routeRepo = routeRepo;
        this.serviceRepo = serviceRepo;
        this.supplierRepo = supplierRepo;
        this.userRepo = userRepo;
        this.stopRepo = stopRepo;
    }
    
    @Transactional
    public User createUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber) throws ToursException{
        User user = new User(username, password, fullName, email, birthdate, phoneNumber);
        userRepo.save(user);
        return user;
    }
    @Transactional
    public DriverUser createDriverUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber, String expedient) throws ToursException{
        DriverUser driver = new DriverUser(username, password, fullName, email, birthdate, phoneNumber, expedient);
        userRepo.save(driver);
        return driver;
    }
    @Transactional
    public TourGuideUser createTourGuideUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber, String education) throws ToursException{
        TourGuideUser tourGuides = new TourGuideUser(username, password, fullName, email, birthdate, phoneNumber, education);
        userRepo.save(tourGuides);
        return tourGuides;
    }
    @Transactional
    public Optional<User> getUserById(Long id) throws ToursException{
        return userRepo.findById(id);
    }
    @Transactional
    public Optional<User> getUserByUsername(String username) throws ToursException{
        return userRepo.findByUserName(username);
    }
    @Transactional
    public User updateUser(User user) throws ToursException{
        return userRepo.update(user);
    }
    @Transactional
    public void deleteUser(User user) throws ToursException{
        if (!user.isActive()){
            throw new ToursException("El usuario se encuentra desactivado");
        }
        if (!user.canBeDeactivated()) {
            throw new ToursException("El usuario no puede ser desactivado");
        }
        if (user.hasAssociations()){
            user.setActive(false);
            userRepo.update(user);
        } else{
            userRepo.delete(user);
        }
    }
    @Transactional
    public Stop createStop(String name, String description) throws ToursException{
        Stop stop = new Stop(name, description);
        stopRepo.save(stop);
        return stop;
    }
    @Transactional
    public List<Stop> getStopByNameStart(String name) throws ToursException{
        return stopRepo.getByNameStart(name);
    }
    @Transactional
    public Route createRoute(String name, float price, float totalKm, int maxNumberOfUsers, List<Stop> stops) throws ToursException{
        Route route = new Route(name, price, totalKm, maxNumberOfUsers, stops);
        routeRepo.save(route);
        return route;
    }
    @Transactional
    public Optional<Route> getRouteById(Long id) throws ToursException{
        return routeRepo.findById(id);
    }
    @Transactional
    public List<Route> getRoutesBelowPrice(float price) throws ToursException{
        return routeRepo.getRoutesBelowPrice(price);
    }
    @Transactional
    public void assignDriverByUsername(String username, Long idRoute) throws ToursException{
        Route route = routeRepo.findById(idRoute).orElseThrow(() -> new ToursException("Ruta no encontrado"));
        DriverUser driver = userRepo.findDriverByUserName(username).orElseThrow(() -> new ToursException("Driver no encontrado"));
        route.addDriver(driver);
        driver.addRoute(route);
        routeRepo.update(route);
    }
    @Transactional
    public void assignTourGuideByUsername(String username, Long idRoute) throws ToursException{
        Route route = routeRepo.findById(idRoute).orElseThrow(() -> new ToursException("Ruta no encontrado"));
        TourGuideUser tourGuide = userRepo.findTourGuideByUserName(username).orElseThrow(() -> new ToursException("TourGuide no encontrado"));
        route.addTourGuide(tourGuide);
        tourGuide.addRoute(route);
        routeRepo.update(route);
    }
    @Transactional
    public Supplier createSupplier(String businessName, String authorizationNumber) throws ToursException{
        Supplier supplier = new Supplier(businessName, authorizationNumber);
        supplierRepo.save(supplier);
        return supplier;
    }
    @Transactional
    public Service addServiceToSupplier(String name, float price, String description, Supplier supplier) throws ToursException{
        Service service = new Service(name, price, description);
        // establezco bidireccionalidad manualmente
        supplier.addService(service);
        service.setSupplier(supplier);
        serviceRepo.save(service);
        return service;
    }
    @Transactional
    public Service updateServicePriceById(Long id, float newPrice) throws ToursException{
        Service service = serviceRepo.findById(id).orElseThrow(() -> new ToursException("Service no encontrado"));
        service.setPrice(newPrice);
        serviceRepo.update(service);
        return service;
    }
    @Transactional
    public Optional<Supplier> getSupplierById(Long id) throws ToursException{
        return supplierRepo.findById(id);
    }
    @Transactional
    public Optional<Supplier> getSupplierByAuthorizationNumber(String authorizationNumber) throws ToursException{
        return supplierRepo.getByAuthorizationNumber(authorizationNumber);
    }
    @Transactional
    public Optional<Service> getServiceByNameAndSupplierId(String name, Long id) throws ToursException{
        return serviceRepo.getByServiceByNameAndSupplierId(name, id);
    }
    @Transactional
    public Purchase createPurchase(String code, Route route, User user) throws ToursException{
        Purchase purchase = new Purchase(code, route, user);
        purchaseRepo.save(purchase);
        return purchase;
    }
    @Transactional
    public Purchase createPurchase(String code, Date date, Route route, User user) throws ToursException{
        Purchase purchase = new Purchase(code, date, route, user);
        purchase.setTotalPrice(route.getPrice());
        user.addPurchase(purchase);
        purchaseRepo.save(purchase);
        return purchase;
    }
    @Transactional
    public ItemService addItemToPurchase(Service service, int quantity, Purchase purchase) throws ToursException{
        Purchase managed_purchase = purchaseRepo.findById(purchase.getId()).get();
        ItemService itemService = new ItemService(quantity);
        // establezco relaciones bidireccionales
        itemService.setPurchase(managed_purchase);
        itemService.setService(service);
        service.addItemService(itemService);
        managed_purchase.addItemService(itemService);
        // calculo nuevo precio
        float old_price = managed_purchase.getTotalPrice();
        float new_amount = service.getPrice() * quantity;
        managed_purchase.setTotalPrice(old_price + new_amount);
        // esta chanchada me pasa por no tener un repo de itemservice y por no manejar la session aca
        this.sessionFactory.getCurrentSession().flush();
        return itemService;
    }
    @Transactional
    public Optional<Purchase> getPurchaseByCode(String code) throws ToursException{
        return purchaseRepo.getByCode(code);
    }
    @Transactional
    public void deletePurchase(Purchase purchase) throws ToursException{
        purchaseRepo.delete(purchase);
    }
    @Transactional
    public Review addReviewToPurchase(int rating, String comment, Purchase purchase) throws ToursException{
        Review review = new Review(rating, comment);
        review.setPurchase(purchase);
        purchase.setReview(review);
        reviewRepo.save(review);
        return review;
    }
    @Transactional
    public void deleteRoute(Route route) throws ToursException{
        // aca tengo que ir a buscar si esta ruta figura en alguna compra, de ser asi levanto excepcion?
        List<Route> routsNotSell = this.getRoutsNotSell();
        if (routsNotSell.contains(route)){
            routeRepo.delete(route);
        } else {
            throw new ToursException("No se puede eliminar una ruta con compras asociadas");
        }
    }

    // CONSULTAS HQL
    @Transactional
    public List<Purchase> getAllPurchasesOfUsername(String username) throws ToursException{
        try {
            return purchaseRepo.findAll(username);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional
    public List<User> getUserSpendingMoreThan(float mount) throws ToursException{
        try {
            return userRepo.usersSpendingMoreThan(mount);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional
    public List<Supplier> getTopNSuppliersInPurchases(int n) throws ToursException{
        try {
            return supplierRepo.getTopNSuppliersInPurchase(n);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional
    public long getCountOfPurchasesBetweenDates(Date start, Date end) throws ToursException{
        try {
            return purchaseRepo.getCountOfPurchasesBetweenDates(start, end);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional
    public List<Route> getRoutesWithStop(Stop stop) throws ToursException{
        try {
            return routeRepo.getRoutesWithStop(stop);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional
    public Long getMaxStopOfRoutes() throws ToursException{
        try { // cantidad de paradas que posee el recorrido con mayor cantidad de stops.
            return routeRepo.getMaxStopOfRoutes();
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional
    public List<Route> getRoutsNotSell() throws ToursException{
        try {
            return routeRepo.getRoutesNotSelled();
        }
        catch(Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional
    public List<Route> getTop3RoutesWithMaxRating() throws ToursException{
        try {
            return routeRepo.getTop3RoutesWithMaxRating();
        }
        catch(Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional
    public Service getMostDemandedService() throws ToursException{
        try {
            return serviceRepo.getMostDemandedService();
        }
        catch(Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional
    public List<TourGuideUser> getTourGuidesWithRating1() throws ToursException{
        try {
            return userRepo.getTourGuidesWithRating1();
        }
        catch(Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
}
