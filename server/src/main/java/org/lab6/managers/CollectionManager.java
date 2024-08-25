package org.lab6.managers;


import common.console.Console;
import common.models.Organization;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private final PriorityQueue<Organization> collection = new PriorityQueue<>();

    private final DatabaseManager dbManager;
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;

    public CollectionManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    public void validateAll(Console console) {
        collection.forEach(organization -> {
            if (!organization.validate()) {
                console.printError("Дракон с id=" + organization.getId() + " имеет невалидные поля.");
            }
        });
        console.println("Выполнена проверка корректности загруженных данных");
    }

    public boolean init() {
        collection.clear();
        dbManager.readCollection(collection);
        lastInitTime = LocalDateTime.now();
        return true;
    }

    public Organization getById(long id) {
        return collection.stream()
                .filter(organization -> organization.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Organization getByFullname(String fullname) {
        return collection.stream()
                .filter(organization -> organization.getFullName().equals(fullname))
                .findFirst()
                .orElse(null);
    }

    public boolean add(Organization organization) {
        System.out.println(contains(organization));
        System.out.println(getByFullname(organization.getFullName()));
        if (contains(organization)) return false;
        if (dbManager.writerProvider(organization)){
            Map<String, Object> data = dbManager.getIdAndDate(organization.getFullName());
            organization.setCreationDate((Date) data.get("creation_date"));
            organization.setId((Integer) data.get("organisation_id"));
            collection.add(organization);
            return true;
        }else{return false;}

    }

    public boolean contains(Organization organization) {
        return organization == null || getByFullname(organization.getFullName()) != null;
    }

    public void saveCollection() {
        dbManager.writeCollection(collection);
        lastSaveTime = LocalDateTime.now();
    }

    public void clear(Integer id) {
        collection.removeIf(organization -> (organization.getOwnerId() == id && dbManager.deleteOrganization(organization)));
    }

    public PriorityQueue<Organization> getCollection() {
        return collection;
    }


    public String collectionType() {
        return collection.getClass().getName();
    }

    public long collectionSize() {
        return collection.size();
    }

    public boolean remove(long id, int userId) {
        Organization orgToRem = getById(id);
        if (orgToRem == null) return false;

        return collection.removeIf(org -> org.equals(orgToRem) && org.getOwnerId()==userId && dbManager.deleteOrganization(org));
    }

    public Organization getFirstElement(int user_id) {
        return collection.stream().filter(organization -> organization.getOwnerId() == user_id).findFirst().orElse(null);
    }

    public boolean removeLower(Organization organization) {
        return collection.removeIf(org -> (org.compareTo(organization) < 0 && org.getOwnerId()==organization.getOwnerId() && dbManager.deleteOrganization(org)));
    }

    public PriorityQueue<Organization> getGreaterThan(String fullname) {
        return collection.stream()
                .filter(organization -> organization.getFullName().compareTo(fullname) > 0)
                .collect(Collectors.toCollection(PriorityQueue::new));
    }

    public PriorityQueue<Organization> lowerGreaterThan(String fullname) {
        return collection.stream()
                .filter(organization -> organization.getFullName().compareTo(fullname) < 0)
                .collect(Collectors.toCollection(PriorityQueue::new));
    }

    public PriorityQueue<Organization> getCollectionDescending() {
        Comparator<Organization> reversedComparator = Comparator.reverseOrder();
        return collection.stream()
                .collect(Collectors.toCollection(() -> new PriorityQueue<>(reversedComparator)));
    }
}