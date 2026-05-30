package unlp.info.bd2.dto;

public class RouteListDTO {
    public String nombre;
    public Long cantVentas;
    public Double precioProm;

    public RouteListDTO(String nombre, Long cantVentas, Double precioProm){
        this.nombre = nombre;
        this.cantVentas = cantVentas;
        this.precioProm = precioProm;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setCantVentas(Long cantVentas){
        this.cantVentas = cantVentas;
    }
    public void setPrecioProm(Double precioProm){
        this.precioProm = precioProm;
    }
    public String getNombre(){
        return this.nombre;
    }
    public Long getCantVentas(){
        return this.cantVentas;
    }
    public Double getPrecioProm(){
        return this.precioProm;
    }


}