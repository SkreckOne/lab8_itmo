package common.models;



import common.models.interfaces.Validatable;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class Organization implements Serializable, Validatable, Comparable<Organization> {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long annualTurnover; //Поле не может быть null, Значение поля должно быть больше 0
    private String fullName; //Длина строки не должна быть больше 1322, Значение этого поля должно быть уникальным, Поле не может быть null
    private OrganizationType type; //Поле не может быть null
    private Address postalAddress; //Поле может быть null
    private int owner_id;

    public Organization(int id, String name, Coordinates coordinates, Date date , Long annualTurnover, String fullName, OrganizationType type, Address postalAddress, int owner_id){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = date;
        this.annualTurnover = annualTurnover;
        this.fullName = fullName;
        this.type = type;
        this.postalAddress = postalAddress;
        this.owner_id = owner_id;
    }


    public Organization(String name, Coordinates coordinates, Long annualTurnover, String fullName, OrganizationType type, Address postalAddress, int owner_id){
        this.name = name;
        this.coordinates = coordinates;
        this.annualTurnover = annualTurnover;
        this.fullName = fullName;
        this.type = type;
        this.postalAddress = postalAddress;
        this.owner_id = owner_id;
    }

    @Override
    public boolean validate(){
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null || !coordinates.validate()) return false;
        if (annualTurnover == null || annualTurnover <= 0) return false;
        if (fullName == null || fullName.length() >= 1322) return false;
        if (type == null) return false;
        if (postalAddress == null || !postalAddress.validate()) return false;
        return true;
    }

    public boolean validateWithMessages(StringBuilder errorMessage) {
        if (name == null || name.isEmpty()) {
            errorMessage.append("Name cannot be empty.\n");
            return false;
        }
        if (coordinates == null || !coordinates.validate()) {
            errorMessage.append("Coordinates are invalid.\n");
            return false;
        }
        if (annualTurnover == null || annualTurnover <= 0) {
            errorMessage.append("Annual Turnover must be greater than 0.\n");
            return false;
        }
        if (fullName == null || fullName.length() >= 1322) {
            errorMessage.append("Full Name must be less than 1322 characters and cannot be null.\n");
            return false;
        }
        if (type == null) {
            errorMessage.append("Organization type cannot be null.\n");
            return false;
        }
        if (postalAddress == null || !postalAddress.validate()) {
            errorMessage.append("Postal Address is invalid.\n");
            return false;
        }
        return true;
    }



    @Override
    public String toString() {
        String rowFormat = "| %-16s | %-50s |\n";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(rowFormat, "Field", "Value"));
        sb.append(String.format(rowFormat, "ID", id));
        sb.append(String.format(rowFormat, "Name", name));
        sb.append(String.format(rowFormat, "Coordinates", coordinates));
        sb.append(String.format(rowFormat, "Creation Date", creationDate.getTime()));
        sb.append(String.format(rowFormat, "Annual Turnover", annualTurnover));
        sb.append(String.format(rowFormat, "Full Name", fullName));
        sb.append(String.format(rowFormat, "Type", type));
        sb.append(String.format(rowFormat, "Postal Address", postalAddress));

        return sb.toString();
    }

    public int getId(){return this.id;}
    public String getName(){return this.name;}
    public Coordinates getCoordinates(){return this.coordinates;}
    public Date getCreationDate(){return this.creationDate;}
    public Long getAnnualTurnover(){return this.annualTurnover;}
    public String getFullName(){return this.fullName;}
    public OrganizationType getType(){return this.type;}
    public Address getPostalAddress(){return this.postalAddress;}
    public int getOwnerId() {return this.owner_id;}

    public void setName(String name){this.name = name;}
    public void setCoordinates(Coordinates coordinates){this.coordinates = coordinates;}
    public void setCreationDate(Date creationDate){this.creationDate = creationDate;}
    public void setId(int id){this.id = id;}
    public void setFullName(String fullName){this.fullName = fullName;}
    public void setType(OrganizationType type){this.type = type;}
    public void setPostalAddress(Address postalAddress){this.postalAddress = postalAddress;}
    public void setAnnualTurnover(Long annualTurnover){this.annualTurnover = annualTurnover;}
    public void setOwnerId (int id) {this.owner_id = id;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && type == that.type && Objects.equals(postalAddress, that.postalAddress) && Objects.equals(fullName, that.fullName) && Objects.equals(creationDate, that.creationDate)
                && Objects.equals(annualTurnover, that.annualTurnover);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, postalAddress, fullName, creationDate, annualTurnover);
    }

    @Override
    public int compareTo(Organization organization) {
        return this.getFullName().compareTo(organization.getFullName());
    }
}
