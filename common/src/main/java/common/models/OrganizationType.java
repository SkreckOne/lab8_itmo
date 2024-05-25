package common.models;

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

}