package org.meyason.dokkoi.database;

import org.hibernate.SessionFactory;
import org.meyason.dokkoi.database.repositories.*;

public class DatabaseManager {

    private final UserRepository userRepository;
    private final MoneyRepository moneyRepository;

    public DatabaseManager(SessionFactory sessionFactory) {
        this.userRepository = new UserRepository(sessionFactory);
        this.moneyRepository = new MoneyRepository(sessionFactory);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public MoneyRepository getMoneyRepository() {
        return moneyRepository;
    }
}
