package common.models;



import common.models.interfaces.Validatable;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class Organization implements Serializable, Validatable, Comparable<Organization> {
    private static final long serialVersionUID = 10L;
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long annualTurnover; //Поле не может быть null, Значение поля должно быть больше 0
    private String fullName; //Длина строки не должна быть больше 1322, Значение этого поля должно быть уникальным, Поле не может быть null
    private OrganizationType type; //Поле не может быть null
    private Address postalAddress; //Поле может быть null
    private String owner_username;
    private long owner_id;

    public Organization(int id, String name, Coordinates coordinates, Long annualTurnover, String fullName, OrganizationType type, Address postalAddress, String owner_username, long owner_id){
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = new Date();
        this.annualTurnover = annualTurnover;
        this.fullName = fullName;
        this.type = type;
        this.postalAddress = postalAddress;
        this.owner_username = owner_username;
        this.owner_id = owner_id;
    }

    public Organization() {}



    @Override
    public boolean validate(){
        if (id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null || !coordinates.validate()) return false;
        if (creationDate == null) return false;
        if (annualTurnover == null || annualTurnover <= 0) return false;
        if (fullName == null || fullName.length() >= 1322) return false;
        if (type == null) return false;
        if (postalAddress == null || !postalAddress.validate()) return false;
        return true;
    }



    @Override
    public String toString() {
        // Define a format for each row of the table
        String rowFormat = "| %-16s | %-50s |\n";

        // Construct the table with String.format, using the row format
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

    public long getId(){return this.id;}
    public String getName(){return this.name;}
    public Coordinates getCoordinates(){return this.coordinates;}
    public Date getCreationDate(){return this.creationDate;}
    public Long getAnnualTurnover(){return this.annualTurnover;}
    public String getFullName(){return this.fullName;}
    public OrganizationType getType(){return this.type;}
    public Address getPostalAddress(){return this.postalAddress;}
    public String getOwnerUsername(){return this.owner_username;}

    public void setName(String name){this.name = name;}
    public void setCoordinates(Coordinates coordinates){this.coordinates = coordinates;}
    public void setCreationDate(Date creationDate){this.creationDate = creationDate;}
    public void setId(long id){this.id = id;}
    public void setFullName(String fullName){this.fullName = fullName;}
    public void setType(OrganizationType type){this.type = type;}
    public void setPostalAddress(Address postalAddress){this.postalAddress = postalAddress;}
    public void setAnnualTurnover(Long annualTurnover){this.annualTurnover = annualTurnover;}
    public void setOwnerUsername(String username){this.owner_username = username;}

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
