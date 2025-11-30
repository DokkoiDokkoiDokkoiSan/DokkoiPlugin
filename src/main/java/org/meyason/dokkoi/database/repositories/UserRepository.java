package org.meyason.dokkoi.database.repositories;

// 全部SpaceServerUniverseCoreV2から移植しただけ

import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.meyason.dokkoi.database.models.User;
import org.meyason.dokkoi.exception.UserNotFoundException;


import java.util.Date;
import java.util.UUID;

public class UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User createUser(Player player) {
        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        User user = new User(null, uuid, name, new Date(), new Date());

        Session session = this.sessionFactory.getCurrentSession();

        try {
            session.beginTransaction();
            session.persist(user);//save
            session.getTransaction().commit();
        } finally {
            session.close();
        }
        return user;
    }

    public User getUser(Long id) throws UserNotFoundException {
        Session session = this.sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            User data = session.get(User.class, id);
            session.getTransaction().commit();
            if (data == null) {
                throw new UserNotFoundException("ユーザーデータが存在しませんでした。 ID:" + id);
            }
            return data;
        } finally {
            session.close();
        }
    }

    public User getUserFromUUID(UUID uuid) throws UserNotFoundException {
        Session session = this.sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            User data = session.createSelectionQuery("from User where uuid = ?1", User.class)
                    .setParameter(1, uuid.toString()).getSingleResultOrNull();
            session.getTransaction().commit();
            if (data == null) {
                throw new UserNotFoundException("ユーザーデータが存在しませんでした。 UUID:" + uuid);
            }
            return data;
        } finally {
            session.close();
        }
    }

    public User getUserFromPlayerName(String name) throws UserNotFoundException {
        Session session = this.sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            User data = session.createSelectionQuery("from User where name = ?1", User.class)
                    .setParameter(1, name).getSingleResultOrNull();
            session.getTransaction().commit();
            session.close();
            if (data == null) {
                throw new UserNotFoundException("ユーザーデータが存在しませんでした。 name:" + name);
            }
            return data;
        } finally {
            session.close();
        }
    }

    public boolean existsUser(Long id) {
        try {
            getUser(id);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    public boolean existsUserFromUUID(UUID uuid) {
        try {
            getUserFromUUID(uuid);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    public boolean existsUserFromPlayerName(String name) {
        try {
            getUserFromPlayerName(name);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    public Long getPrimaryKeyFromUUID(UUID uuid) throws UserNotFoundException {
        User user = this.getUserFromUUID(uuid);
        return user.getId();
    }

    public Long getPrimaryKeyFromPlayerName(String name) throws UserNotFoundException {
        User user = this.getUserFromPlayerName(name);
        return user.getId();
    }

    public void updateUser(User user) {
        Session session = this.sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.merge(user);//update
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    public void deleteUser(User user) {
        Session session = this.sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.remove(user); //delete
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
}
