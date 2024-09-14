package org.lab6.utils;


import common.transfer.Session;

public class SessionHandler {
    public static Session session = null;

    public static Session getSession() {
        return session;
    }

    public static void setSession(Session currentUser) {
        SessionHandler.session = currentUser;
    }
}