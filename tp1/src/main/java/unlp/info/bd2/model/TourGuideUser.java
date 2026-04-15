package unlp.info.bd2.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;

@Entity(name = "TourGuideUser")
// SINGLE_TABLE
@DiscriminatorValue("GUIDE")
// JOINED
//@PrimaryKeyJoinColumn(name = "id")
// TABLE_PER_CLASS
public class TourGuideUser extends User {

    @Column(nullable = true) // en single_table tiene que poder ser null
    private String education;

    @ManyToMany(
        fetch = FetchType.LAZY,
        mappedBy = "tourGuideList"
    )
    private List<Route> routes;

    public TourGuideUser(){
        super();
    }
    public TourGuideUser(String username, String password, String name, String email, Date birthdate, String phoneNumber, String education){
        super(username, password, name, email, birthdate, phoneNumber);
        this.education = education;
        this.routes = new ArrayList<Route>();
    }


    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route){
        this.routes.add(route);
    }

    public boolean canBeDeactivated() {
        // si tiene rutas asignadas, no puede ser desactivado
        return this.routes == null || this.routes.isEmpty();
    }

}
