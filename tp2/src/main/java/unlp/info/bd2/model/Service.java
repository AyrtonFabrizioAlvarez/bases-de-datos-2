package unlp.info.bd2.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cascade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity(name = "Service")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private float price;

    @Column(nullable = true)
    private String description;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "service") // QUE PASA CON UN ITEMSERVICE SI ELIMINO UN SERVICIO??? 
    private List<ItemService> itemServiceList;
    
    @ManyToOne(
        fetch = FetchType.EAGER,
        optional = false)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    public Service(){

    }
    public Service(String name, float price, String description){
        this.name = name;
        this.price = price;
        this.description = description;
        this.itemServiceList = new ArrayList<ItemService>();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ItemService> getItemServiceList() {
        return itemServiceList;
    }

    public ItemService addItemService(ItemService item){
        this.itemServiceList.add(item);
        return item;
    }

    public void setItemServiceList(List<ItemService> itemServiceList) {
        this.itemServiceList = itemServiceList;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
