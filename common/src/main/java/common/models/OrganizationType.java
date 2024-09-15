package common.models;

import java.util.Vector;

public enum OrganizationType {
    PUBLIC,
    GOVERNMENT,
    TRUST;

    public static String names() {
        StringBuilder nameList = new StringBuilder();
        for (var organizationType : values()) {
            nameList.append(organizationType.name()).append(", ");
        }
        return nameList.substring(0, nameList.length()-2);
    }
    public static Vector<String> namesAsVector() {
        Vector<String> nameList =  new Vector<>();
        for (var organizationType : values()) {
            nameList.add(organizationType.name());
        }
        return nameList;
    }

}