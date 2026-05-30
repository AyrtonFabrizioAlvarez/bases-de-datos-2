package unlp.info.bd2.model;

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
@Entity(name = "ItemService")
public class ItemService {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(
        fetch = FetchType.EAGER,
        optional = false) // NO ME INTERESA HACER CASCADA A UNA COMPRA A PARTIR DE UN ITEMSERVICE DE SU COLECCION
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @ManyToOne(
        fetch = FetchType.EAGER,
        optional = false) // ESTE OPTIONAL ESTA MAL? YA QUE SI SE ELIMINA UN SERVICE Y NO TENER CASCADE DE MOMENTO QUEDARIA FK EN NULL
    @JoinColumn(name = "service_id")
    private Service service;

    public ItemService(){

    }

    public ItemService(int quantity){
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
