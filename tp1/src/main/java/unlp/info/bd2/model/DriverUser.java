package unlp.info.bd2.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;

@Entity(name = "DriverUser")
// SINGLE_TABLE
@DiscriminatorValue("DRIVER")
// JOINED
//@PrimaryKeyJoinColumn(name = "id")
// TABLE_PER_CLASS
public class DriverUser extends User {

    @Column(nullable = true) // en single_table tiene que poder ser null
    private String expedient;

    @ManyToMany(
        fetch = FetchType.LAZY,
        mappedBy = "driverList"
    )
    private List<Route> routes;

    public DriverUser(){
        super();
    }

    public DriverUser(String username, String password, String name, String email, Date birthdate, String phoneNumber, String expedient){
        super(username, password, name, email, birthdate, phoneNumber);
        this.expedient = expedient;
        this.routes = new ArrayList<Route>();
    }


    public String getExpedient() {
        return expedient;
    }

    public void setExpedient(String expedient) {
        this.expedient = expedient;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRouts(List<Route> routs) {
        this.routes = routs;
    }

    public void addRoute(Route route){
        this.routes.add(route);
    }
}
