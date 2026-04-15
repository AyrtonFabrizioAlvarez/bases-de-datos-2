package unlp.info.bd2.model;

import java.util.ArrayList;
import java.util.Date;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;

@Entity(name = "Purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private float totalPrice = 0;

    @Column(nullable = false)
    private Date date;

    @ManyToOne(
        fetch = FetchType.EAGER,
        optional = false
    ) // seria necesario utilizar casacde.MERGE?
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(
        fetch = FetchType.EAGER,
        optional = false
    ) // seria necesario utilizar cascade.MERGE?
    @JoinColumn(name = "route_id")
    private Route route;

    @OneToOne(
        fetch = FetchType.EAGER, // aca iria lazy pro el caso donde no existe aun la review
        optional = true, // "optional = true" es por convencion pero de momento lo dejo para acostumbrarme
        cascade = CascadeType.ALL, // NO TIENE SENTIDO UNA REVIEW SI SE ELIMINA LA COMPRA A LA QUE PERTENECE, que pasa con el persist?
        orphanRemoval = true
    ) 
    @JoinColumn(name = "review_id")
    private Review review;

    @OneToMany(
        fetch = FetchType.LAZY, // por dominio no seria necesario acceder a todas por mas que sean una banda?
        mappedBy = "purchase",
        cascade = CascadeType.ALL, // SI SE ELIMINA UNA COMPRA SE ELIMINAN LOS ITEMSERVICE DE SU COLECCION, consultar refresh y detach
        orphanRemoval = true)
    private List<ItemService> itemServiceList;

    public Purchase(){

    }

    public Purchase(String code, Date date, Route route, User user){
        this.code = code;
        this.date = date;
        this.user = user;
        this.route = route;
        this.itemServiceList = new ArrayList<ItemService>();
    }
    public Purchase(String code, Route route, User user){
        this.code = code;
        this.user = user;
        this.route = route;
        this.itemServiceList = new ArrayList<ItemService>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public List<ItemService> getItemServiceList() {
        return itemServiceList;
    }

    public void setItemServiceList(List<ItemService> itemServiceList) {
        this.itemServiceList = itemServiceList;
    }

    public ItemService addItemService(ItemService item){
        this.itemServiceList.add(item);
        return item;
    }
}
