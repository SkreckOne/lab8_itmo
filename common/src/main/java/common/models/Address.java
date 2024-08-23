package common.models;


import common.models.interfaces.Validatable;

import java.io.Serializable;
import java.util.Objects;

public class Address implements Serializable, Validatable {
    private String zipCode; //Длина строки должна быть не меньше 8, Поле не может быть null
    private Location town; //Поле не может быть null

    public Address(String zipCode, Location town){
        this.zipCode = zipCode;
        this.town = town;
    }

    @Override
    public boolean validate(){
        if (zipCode == null || zipCode.length() < 8) return false;
        if (town == null) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Address(" + zipCode +", " + town + ")";
    }

    public String getZipCode(){return this.zipCode;}
    public Location getTown(){return this.town;}
    public void setTown(Location town){this.town = town;}
    public void setZipCode(String zipCode){this.zipCode = zipCode;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(town, address.town) && Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(town, zipCode);
    }
}