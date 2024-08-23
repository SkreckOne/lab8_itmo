package common.models;



import common.models.interfaces.Validatable;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Objects;

public class Coordinates implements Serializable, Validatable {
    private Integer x; //Значение поля должно быть больше -465, Поле не может быть null
    private long y; //Значение поля должно быть больше -493

    public Coordinates(Integer x, long y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean validate(){
        if (x == null || x < -465) return false;
        if (y < -493) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Coordinates("+x + ", " + y+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Integer getX(){return this.x;}
    public long getY(){return this.y;}
    public void setX(Integer x){this.x = x;}
    public void setY(long y){this.y = y;}
}
