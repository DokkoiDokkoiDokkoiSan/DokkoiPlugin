package org.meyason.dokkoi;

import org.hibernate.SessionFactory;

import org.meyason.dokkoi.database.DatabaseManager;
import org.meyason.dokkoi.database.DatabaseConnector;

public class DokkoiDatabaseAPI {

    private static DokkoiDatabaseAPI instance;

    private final SessionFactory sessionFactory;

    private final DatabaseManager databaseManager;

    private final DatabaseConnector databaseConnector;

    public DokkoiDatabaseAPI(DatabaseConnector databaseConnector){
        this.databaseConnector = databaseConnector;
        this.sessionFactory = databaseConnector.getSessionFactory();
        this.databaseManager = new DatabaseManager(sessionFactory);
        instance = this;
    }

    public static DokkoiDatabaseAPI getInstance() {
        return instance;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
