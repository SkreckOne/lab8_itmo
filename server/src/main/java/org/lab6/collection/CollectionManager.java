package org.lab6.collection;


import common.console.Console;
import common.models.Organization;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class CollectionManager {
    private final PriorityQueue<Organization> collection = new PriorityQueue<>();

    private final DumpManager dumpManager;
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;
    private long currentId = 1;


    public CollectionManager(DumpManager dumpManager) {
        this.dumpManager = dumpManager;
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
        dumpManager.readCollection(collection);
        lastInitTime = LocalDateTime.now();
        for (Organization organization : collection) {
            long id = organization.getId();
            if (id > currentId) {
                currentId = id;
            }
        }
        return true;
    }

    public long getFreeId() {
        while (getById(currentId, null) != null)
            if (++currentId < 0)
                currentId = 1;
        return currentId;
    }

    public Organization getById(long id, String username) {
        return collection.stream()
                .filter(org -> username == null || org.getOwnerUsername().equals(username))
                .filter(organization -> organization.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Organization getByFullname(String fullname, String username) {
        return collection.stream()
                .filter(org -> username == null || org.getOwnerUsername().equals(username))
                .filter(organization -> organization.getFullName().equals(fullname))
                .findFirst()
                .orElse(null);
    }

    public boolean add(Organization organization) {
        if (contains(organization)) return false;
        collection.add(organization);
        return true;
    }

    public boolean contains(Organization organization) {
        return organization == null || getById(organization.getId(), organization.getOwnerUsername()) != null || getByFullname(organization.getFullName(), organization.getOwnerUsername()) != null;
    }

    public void saveCollection() {
        dumpManager.writeCollection(collection);
        lastSaveTime = LocalDateTime.now();
    }

    public void clear(String username) {
        collection.removeIf(org -> org.getOwnerUsername().equals(username));
    }

    public PriorityQueue<Organization> getCollection(String username) {
        return collection.stream()
                .filter(org -> org.getOwnerUsername().equals(username))
                .collect(Collectors.toCollection(PriorityQueue::new));
    }


    public String collectionType() {
        return collection.getClass().getName();
    }

    public long collectionSize(String username) {
        return collection.stream()
                .filter(org -> org.getOwnerUsername().equals(username))
                .count();
    }

    public boolean remove(long id, String username) {
        Organization orgToRem = getById(id, username);
        if (orgToRem == null) return false;
        collection.remove(orgToRem);
        return true;
    }

    public Organization getFirstElement(String username) {
        return collection.stream()
                .filter(org -> username == null || org.getOwnerUsername().equals(username))
                .min(Organization::compareTo)
                .orElse(null);
    }

    public boolean removeLower(Organization organization, String username) {
        return collection.removeIf(org -> (username == null || org.getOwnerUsername().equals(username)) && org.compareTo(organization) < 0);
    }

    public PriorityQueue<Organization> getGreaterThan(String username, String fullname) {
        return collection.stream()
                .filter(org -> username == null || org.getOwnerUsername().equals(username))
                .filter(organization -> organization.getFullName().compareTo(fullname) > 0)
                .collect(Collectors.toCollection(PriorityQueue::new));
    }

    public PriorityQueue<Organization> lowerGreaterThan(String username, String fullname) {
        return collection.stream()
                .filter(org -> username == null || org.getOwnerUsername().equals(username))
                .filter(organization -> organization.getFullName().compareTo(fullname) < 0)
                .collect(Collectors.toCollection(PriorityQueue::new));
    }

    public PriorityQueue<Organization> getCollectionDescending(String username) {
        Comparator<Organization> reversedComparator = Comparator.reverseOrder();
        return collection.stream()
                .filter(org -> username == null || org.getOwnerUsername().equals(username))
                .collect(Collectors.toCollection(() -> new PriorityQueue<>(reversedComparator)));
    }
}