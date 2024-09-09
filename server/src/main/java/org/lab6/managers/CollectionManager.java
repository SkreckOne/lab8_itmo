package org.lab6.managers;

import common.console.Console;
import common.models.Organization;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class CollectionManager {
    private final PriorityQueue<Organization> collection = new PriorityQueue<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();  // Add a ReadWriteLock

    private final DatabaseManager dbManager;
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;

    public CollectionManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public LocalDateTime getLastInitTime() {
        lock.readLock().lock();
        try {
            return lastInitTime;
        } finally {
            lock.readLock().unlock();
        }
    }

    public LocalDateTime getLastSaveTime() {
        lock.readLock().lock();
        try {
            return lastSaveTime;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void validateAll(Console console) {
        lock.readLock().lock();
        try {
            collection.forEach(organization -> {
                if (!organization.validate()) {
                    console.printError("Organization with id=" + organization.getId() + " has invalid fields.");
                }
            });
            console.println("Completed validation of loaded data.");
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean init() {
        lock.writeLock().lock();
        try {
            collection.clear();
            dbManager.readCollection(collection);
            lastInitTime = LocalDateTime.now();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Organization getById(long id) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(organization -> organization.getId() == id)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Organization getByFullname(String fullname) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(organization -> organization.getFullName().equals(fullname))
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean add(Organization organization) {
        lock.writeLock().lock();
        try {
            if (checkIfContain(organization)) return false;
            if (dbManager.writerProvider(organization)) {
                Map<String, Object> data = dbManager.getIdAndDate(organization.getFullName());
                organization.setCreationDate((Date) data.get("creation_date"));
                organization.setId((Integer) data.get("organisation_id"));
                collection.add(organization);
                return true;
            } else {
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean checkIfContain(Organization organization) {
        lock.readLock().lock();
        try {
            return organization == null || getByFullname(organization.getFullName()) != null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void saveCollection() {
        lock.writeLock().lock();
        try {
            dbManager.writeCollection(collection);
            lastSaveTime = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }
    public void clear(Integer id) {
        lock.writeLock().lock();
        try {
            collection.removeIf(organization ->
                    (organization.getOwnerId() == id && dbManager.deleteOrganization(organization))
            );
        } finally {
            lock.writeLock().unlock();
        }
    }

    public PriorityQueue<Organization> getCollection() {
        lock.readLock().lock();
        try {
            return new PriorityQueue<>(collection);
        } finally {
            lock.readLock().unlock();
        }
    }

    public String collectionType() {
        lock.readLock().lock();
        try {
            return collection.getClass().getName();
        } finally {
            lock.readLock().unlock();
        }
    }

    public long collectionSize() {
        lock.readLock().lock();
        try {
            return collection.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean remove(long id, int userId) {
        lock.writeLock().lock();
        try {
            Organization orgToRem = getById(id);
            if (orgToRem == null) return false;

            return collection.removeIf(org ->
                    org.equals(orgToRem) && org.getOwnerId() == userId && dbManager.deleteOrganization(org)
            );
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Organization getFirstElement(int userId) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(organization -> organization.getOwnerId() == userId)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean removeLower(Organization organization) {
        lock.writeLock().lock();
        try {
            return collection.removeIf(org ->
                    (org.compareTo(organization) < 0 && org.getOwnerId() == organization.getOwnerId() && dbManager.deleteOrganization(org))
            );
        } finally {
            lock.writeLock().unlock();
        }
    }

    public PriorityQueue<Organization> getGreaterThan(String fullname) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(organization -> organization.getFullName().compareTo(fullname) > 0)
                    .collect(Collectors.toCollection(PriorityQueue::new));
        } finally {
            lock.readLock().unlock();
        }
    }

    public PriorityQueue<Organization> lowerGreaterThan(String fullname) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(organization -> organization.getFullName().compareTo(fullname) < 0)
                    .collect(Collectors.toCollection(PriorityQueue::new));
        } finally {
            lock.readLock().unlock();
        }
    }

    public PriorityQueue<Organization> getCollectionDescending() {
        lock.readLock().lock();
        try {
            Comparator<Organization> reversedComparator = Comparator.reverseOrder();
            return collection.stream()
                    .collect(Collectors.toCollection(() -> new PriorityQueue<>(reversedComparator)));
        } finally {
            lock.readLock().unlock();
        }
    }
}