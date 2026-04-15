package unlp.info.bd2.model;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Cascade;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity(name = "Route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private float totalKm;

    @Column(nullable = false)
    private int maxNumberUsers;

    @ManyToMany(
        fetch = FetchType.LAZY, //  consultar pro dominio la necesidad de tener las paradas
        cascade = {CascadeType.PERSIST, CascadeType.MERGE} // es necesario persist? depende de si una parada existe antes que las rutas?
    )
    @JoinTable(
        name = "ROUTE_STOPS",
        joinColumns = @JoinColumn(name = "route_id"),
        inverseJoinColumns = @JoinColumn(name = "stop_id") //CONSULTAR BIEN LA DIFERENCIA ENTRE JOIN E INVERSEJOIN
    )
    private List<Stop> stops;

    @ManyToMany(
        fetch = FetchType.LAZY
        //es necesario considerar persist y merge en la cascada?
    )
    @JoinTable(
        name = "ROUTE_DRIVER",
        joinColumns = @JoinColumn(name = "route_id"),
        inverseJoinColumns = @JoinColumn(name = "driver_id")
    )
    private List<DriverUser> driverList;

    @ManyToMany(
        fetch = FetchType.LAZY
        //es necesario considerar persist y merge en la cascada?
    )
    @JoinTable(
        name = "ROUTE_GUIDE",
        joinColumns = @JoinColumn(name = "route_id"),
        inverseJoinColumns = @JoinColumn(name = "guide_id")
    )
    private List<TourGuideUser> tourGuideList;

    public Route(){
        
    }
    public Route(String name, float price, float totalKm, int maxNumberUsers, List<Stop> stops){
        this.name = name;
        this.price = price;
        this.totalKm = totalKm;
        this.maxNumberUsers = maxNumberUsers;
        this.stops = stops;
        this.driverList = new ArrayList<DriverUser>();
        this.tourGuideList = new ArrayList<TourGuideUser>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(float totalKm) {
        this.totalKm = totalKm;
    }

    public int getMaxNumberUsers() {
        return maxNumberUsers;
    }

    public void setMaxNumberUsers(int maxNumberUsers) {
        this.maxNumberUsers = maxNumberUsers;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public void addDriver(DriverUser driver){
        this.driverList.add(driver);
    }

    public List<DriverUser> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<DriverUser> driverList) {
        this.driverList = driverList;
    }

    public void addTourGuide(TourGuideUser guide){
        this.tourGuideList.add(guide);
    }

    public List<TourGuideUser> getTourGuideList() {
        return tourGuideList;
    }

    public void setTourGuideList(List<TourGuideUser> tourGuideList) {
        this.tourGuideList = tourGuideList;
    }

}
