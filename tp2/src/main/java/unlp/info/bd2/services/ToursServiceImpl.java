package unlp.info.bd2.services;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;

import unlp.info.bd2.dto.RouteListDTO;
import unlp.info.bd2.model.*;
import unlp.info.bd2.repositories.*;
import jakarta.persistence.EntityManager;
import unlp.info.bd2.utils.ToursException;

@org.springframework.stereotype.Service
public class ToursServiceImpl implements ToursService {
    public PurchaseRepository purchaseRepo;
    public ReviewRepository reviewRepo;
    public RouteRepository routeRepo;
    public ServiceRepository serviceRepo;
    public SupplierRepository supplierRepo;
    public UserRepository userRepo;
    public StopRepository stopRepo;
    public EntityManager entityManager;

    public ToursServiceImpl(
        PurchaseRepository purchaseRepo, ReviewRepository reviewRepo,
        RouteRepository routeRepo, ServiceRepository serviceRepo,
        SupplierRepository supplierRepo, UserRepository userRepo,
        StopRepository stopRepo, EntityManager entityManager
    ){
        this.purchaseRepo = purchaseRepo;
        this.reviewRepo = reviewRepo;
        this.routeRepo = routeRepo;
        this.serviceRepo = serviceRepo;
        this.supplierRepo = supplierRepo;
        this.userRepo = userRepo;
        this.stopRepo = stopRepo;
        this.entityManager = entityManager;
    }
    
    @Transactional
    public User createUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber) throws ToursException{
        User user = new User(username, password, fullName, email, birthdate, phoneNumber);
        try {
            this.userRepo.save(user);
            this.entityManager.flush();
            return user;
        } catch (Exception e){
            throw new ToursException("Constraint Violation");
        }
    }
    @Transactional
    public DriverUser createDriverUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber, String expedient) throws ToursException{
        DriverUser driver = new DriverUser(username, password, fullName, email, birthdate, phoneNumber, expedient);
        this.userRepo.save(driver);
        return driver;
    }
    @Transactional
    public TourGuideUser createTourGuideUser(String username, String password, String fullName, String email, Date birthdate, String phoneNumber, String education) throws ToursException{
        TourGuideUser tourGuides = new TourGuideUser(username, password, fullName, email, birthdate, phoneNumber, education);
        this.userRepo.save(tourGuides);
        return tourGuides;
    }
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) throws ToursException{
        return this.userRepo.findById(id);
    }
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) throws ToursException{
        return Optional.ofNullable(this.userRepo.findByUsername(username));
    }
    @Transactional
    public User updateUser(User user) throws ToursException{
        return this.userRepo.save(user);
    }
    @Transactional
    public void deleteUser(User user) throws ToursException{
        if (!user.isActive()){
            throw new ToursException("El usuario se encuentra desactivado");
        }
        if (user.hasAssociations()){
            user.setActive(false);
            this.userRepo.save(user);
        } else{
            this.userRepo.delete(user);
        }
    }
    @Transactional
    public Stop createStop(String name, String description) throws ToursException{
        Stop stop = new Stop(name, description);
        this.stopRepo.save(stop);
        return stop;
    }
    @Transactional(readOnly = true)
    public List<Stop> getStopByNameStart(String name) throws ToursException{
        Pageable pageable = PageRequest.of(0, 10);
        return this.stopRepo.findByNameStartingWith(name, pageable);
    }
    @Transactional
    public Route createRoute(String name, float price, float totalKm, int maxNumberOfUsers, List<Stop> stops) throws ToursException{
        Route route = new Route(name, price, totalKm, maxNumberOfUsers, stops);
        this.routeRepo.save(route);
        return route;
    }
    @Transactional(readOnly = true)
    public Optional<Route> getRouteById(Long id) throws ToursException{
        return this.routeRepo.findById(id);
    }
    @Transactional(readOnly = true)
    public List<Route> getRoutesBelowPrice(float price) throws ToursException{
        Pageable pageable = PageRequest.of(0,10);
        return this.routeRepo.findByPriceLessThan(price, pageable);
    }
    @Transactional
    public void assignDriverByUsername(String username, Long idRoute) throws ToursException{
        Route route = this.routeRepo.findById(idRoute).orElseThrow(() -> new ToursException("Ruta no encontrado"));
        DriverUser driver = this.userRepo.findDriverByUsername(username);
        route.addDriver(driver);
        driver.addRoute(route);
        this.routeRepo.save(route);
    }
    @Transactional
    public void assignTourGuideByUsername(String username, Long idRoute) throws ToursException{
        Route route = this.routeRepo.findById(idRoute).orElseThrow(() -> new ToursException("Ruta no encontrado"));
        TourGuideUser tourGuide = this.userRepo.findTourGuideByUsername(username);
        route.addTourGuide(tourGuide);
        tourGuide.addRoute(route);
        this.routeRepo.save(route);
    }
    @Transactional
    public Supplier createSupplier(String businessName, String authorizationNumber) throws ToursException{
        Supplier supplier = new Supplier(businessName, authorizationNumber);
        try {
            this.supplierRepo.save(supplier);
            this.entityManager.flush();
            return supplier;
        } catch (Exception e){
            throw new ToursException("Constraint Violation");
        }

    }
    @Transactional
    public Service addServiceToSupplier(String name, float price, String description, Supplier supplier) throws ToursException{
        Supplier managedSupplier = this.supplierRepo.findById(supplier.getId()).get();
        Service service = new Service(name, price, description);
        managedSupplier.addService(service);
        service.setSupplier(managedSupplier);
        this.serviceRepo.save(service);
        return service;
    }
    @Transactional
    public Service updateServicePriceById(Long id, float newPrice) throws ToursException{
        Service service = serviceRepo.findById(id).orElseThrow(() -> new ToursException("Service no encontrado"));
        service.setPrice(newPrice);
        serviceRepo.save(service);
        return service;
    }
    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierById(Long id) throws ToursException{
        return this.supplierRepo.findById(id);
    }
    @Transactional(readOnly = true)
    public Optional<Supplier> getSupplierByAuthorizationNumber(String authorizationNumber) throws ToursException{
        return Optional.ofNullable(this.supplierRepo.findByAuthorizationNumber(authorizationNumber));
    }
    @Transactional(readOnly = true)
    public Optional<Service> getServiceByNameAndSupplierId(String name, Long id) throws ToursException{
        return Optional.ofNullable(this.serviceRepo.findByNameAndSupplierId(name, id));
    }
    @Transactional
    public Purchase createPurchase(String code, Route route, User user) throws ToursException{
        Purchase purchase = new Purchase(code, route, user);
        try{
            this.purchaseRepo.save(purchase);
            this.entityManager.flush();
            return purchase;
        } catch (Exception e){
            throw new ToursException("Constraint Violation");
        }
    }
    @Transactional
    public Purchase createPurchase(String code, Date date, Route route, User user) throws ToursException{
        Purchase purchase = new Purchase(code, date, route, user);
        purchase.setTotalPrice(route.getPrice());
        user.addPurchase(purchase);
        this.purchaseRepo.save(purchase);
        return purchase;
    }
    @Transactional
    public ItemService addItemToPurchase(Service service, int quantity, Purchase purchase) throws ToursException{
        ItemService itemService = new ItemService(quantity);
        itemService.setService(service);
        service.addItemService(itemService);
        itemService.setPurchase(purchase);
        purchase.addItemService(itemService);
        this.entityManager.persist(itemService);
        this.purchaseRepo.save(purchase);
        return itemService;
    }
    @Transactional(readOnly = true)
    public Optional<Purchase> getPurchaseByCode(String code) throws ToursException{
        return Optional.ofNullable(this.purchaseRepo.getByCode(code));
    }
    @Transactional
    public void deletePurchase(Purchase purchase) throws ToursException{
        this.purchaseRepo.delete(purchase);
    }
    @Transactional
    public Review addReviewToPurchase(int rating, String comment, Purchase purchase) throws ToursException{
        Review review = new Review(rating, comment);
        review.setPurchase(purchase);
        purchase.setReview(review);
        this.reviewRepo.save(review);
        return review;
    }
    @Transactional
    public void deleteRoute(Route route) throws ToursException{
        // aca tengo que ir a buscar si esta ruta figura en alguna compra, de ser asi levanto excepcion?
        List<Route> routesNotSell = this.getRoutsNotSell();
        if (routesNotSell.contains(route)){
            this.routeRepo.delete(route);
        } else {
            throw new ToursException("No se puede eliminar una ruta con compras asociadas");
        }
    }

    // CONSULTAS HQL
    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchasesOfUsername(String username) throws ToursException{
        try {
            Pageable pageable = PageRequest.of(0, 10);
            return this.purchaseRepo.findByUserUsername(username, pageable);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public List<User> getUserSpendingMoreThan(float mount) throws ToursException{
        try {
            Pageable pageable = PageRequest.of(0, 10);
            return this.userRepo.getUserSpendingMoreThan(mount, pageable);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public List<Supplier> getTopNSuppliersInPurchases(int n) throws ToursException{
        try {
            Pageable pageable = PageRequest.of(0, n);
            return this.supplierRepo.getTopNSuppliersInPurchases(n, pageable);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public long getCountOfPurchasesBetweenDates(Date start, Date end) throws ToursException{
        try {
            return this.purchaseRepo.countByDateBetween(start, end);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public List<Route> getRoutesWithStop(Stop stop) throws ToursException{
        try {
            Pageable pageable = PageRequest.of(0, 10);
            return this.routeRepo.findByStops(stop, pageable);
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public Long getMaxStopOfRoutes() throws ToursException{
        try { 
            return this.routeRepo.getMaxStopOfRoutes().longValue();
        } catch (Exception e) {
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public List<Route> getRoutsNotSell() throws ToursException{
        try {
            Pageable pageable = PageRequest.of(0, 10);
            return this.routeRepo.getRoutesNotSell(pageable);
        }
        catch(Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public List<Route> getTop3RoutesWithMaxRating() throws ToursException{
        try {
            return this.routeRepo.getTop3RoutesWithMaxRating();
        }
        catch(Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public Service getMostDemandedService() throws ToursException{
        try {
            return this.serviceRepo.getMostDemandedService();
        }
        catch(Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }
    @Transactional(readOnly = true)
    public List<TourGuideUser> getTourGuidesWithRating1() throws ToursException{
        try {
            Pageable pageable = PageRequest.of(0, 10);
            return this.userRepo.getTourGuidesWithRating1(pageable);
        }
        catch(Exception e){
            throw new ToursException("Hubo un error: " + e.getMessage());
        }
    }

    //metodo que agrego para ver el tema del dto
    @Transactional(readOnly = true)
    public List<RouteListDTO> getRouteListDTO() {
        return this.routeRepo.getRouteListDTO();
    }
}
