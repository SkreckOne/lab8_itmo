package common.models;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {
    private long x;
    private long y;
    private long z;

    public Location(long x, long y, long z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString(){
        return "Location(" + x + ", " + y + ", " + z + ")";
    }

    public long getX(){return this.x;}
    public long getY(){return this.y;}
    public long getZ(){return this.z;}
    public void setX(long x){this.x = x;}
    public void setY(long y){this.y = y;}
    public void setZ(long z){this.z = z;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(x, location.x) && Objects.equals(y, location.y) && Objects.equals(z, location.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

}