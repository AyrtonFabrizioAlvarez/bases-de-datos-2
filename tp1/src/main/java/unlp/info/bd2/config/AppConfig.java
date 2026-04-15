package unlp.info.bd2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import unlp.info.bd2.repositories.*;
import unlp.info.bd2.services.*;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public ToursService createService() {
        PurchaseRepository purchaseRepository = this.createPurchaseRepository();
        ReviewRepository reviewRepository = this.createReviewRepository();
        RouteRepository routeRepository = this.createRouteRepository();
        ServiceRepository serviceRepository = this.createServiceRepository();
        SupplierRepository supplierRepository = this.createSupplierRepository();
        UserRepository userRepository = this.createUserRepository();
        StopRepository stopRepository = this.createStopRepository();
        return new ToursServiceImpl(
            purchaseRepository,
            reviewRepository,
            routeRepository,
            serviceRepository,
            supplierRepository,
            userRepository,
            stopRepository
        );
    }

    @Bean
    @Primary
    public PurchaseRepository createPurchaseRepository() {
        return new PurchaseRepository();
    }
    @Bean
    @Primary
    public StopRepository createStopRepository() {
        return new StopRepository();
    }
    @Bean
    @Primary
    public ReviewRepository createReviewRepository() {
        return new ReviewRepository();
    }
    @Bean
    @Primary
    public RouteRepository createRouteRepository() {
        return new RouteRepository();
    }
    @Bean
    @Primary
    public ServiceRepository createServiceRepository() {
        return new ServiceRepository();
    }
    @Bean
    @Primary
    public SupplierRepository createSupplierRepository() {
        return new SupplierRepository();
    }
    @Bean
    @Primary
    public UserRepository createUserRepository() {
        return new UserRepository();
    }    
}
