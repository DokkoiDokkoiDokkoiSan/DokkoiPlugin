package org.meyason.dokkoi.database;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.logging.LogFactory;
import org.flywaydb.core.internal.logging.javautil.JavaUtilLogCreator;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import static org.hibernate.cfg.AvailableSettings.*;

import org.meyason.dokkoi.database.models.*;

public class DatabaseConnector {


    private String jdbc;
    private String database;
    private String user;
    private String password;

    private SessionFactory sessionFactory;

    public DatabaseConnector(String host, int port, String database, String user, String password) {
        this.database = database;
        this.user = user;
        this.password = password;
        this.jdbc = "jdbc:mysql://" + host + ":" + port;
        init();
    }

    private void init() {
        migrate();
        this.sessionFactory = buildSessionFactory();
    }

    private void migrate() {
        LogFactory.setLogCreator(new JavaUtilLogCreator());
        Flyway.configure(getClass().getClassLoader())
                .dataSource(jdbc + "/" + this.database, this.user, this.password)
                .baselineVersion("2023.09.20.01")
                .locations("classpath:db/migration/")
                .schemas("flyway_schema")
                .load().migrate();
    }

    private SessionFactory buildSessionFactory() {
        Configuration config = new Configuration();
        config = registerAnnotatedClasses(config);
        config = registerProperties(config);
        return config.buildSessionFactory();
    }

    private Configuration registerAnnotatedClasses(Configuration configuration) {
        return configuration.addAnnotatedClass(User.class)
                .addAnnotatedClass(Money.class);
    }

    private Configuration registerProperties(Configuration configuration) {
        return configuration.setProperty(DRIVER, "com.mysql.cj.jdbc.Driver")
                .setProperty(URL, jdbc + "/" + this.database)
                .setProperty(USER, this.user)
                .setProperty(PASS, this.password)
                .setProperty(POOL_SIZE, "1")
                .setProperty(SHOW_SQL, "false")
                .setProperty(FORMAT_SQL, "false")
                .setProperty(JDBC_TIME_ZONE, "Asia/Tokyo")
                .setProperty("hibernate.current_session_context_class", "thread")
                .setProperty("hibernate.hbm2ddl.auto", "none");
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void close() {
        if (this.sessionFactory != null) {
            this.sessionFactory.close();
        }
    }
}
