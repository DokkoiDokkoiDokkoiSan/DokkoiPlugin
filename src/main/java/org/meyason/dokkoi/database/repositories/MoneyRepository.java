package org.meyason.dokkoi.database.repositories;

//これもSpaceServerUniverseCoreV2からのコピペ改変

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.meyason.dokkoi.database.models.Money;
import org.meyason.dokkoi.database.models.User;
import org.meyason.dokkoi.exception.MoneyNotFoundException;

import java.util.Date;

public class MoneyRepository {

    private final SessionFactory sessionFactory;

    public MoneyRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Money createMoney(User user) {
        Long user_id = user.getId();
        Money money = new Money(null, user_id, 10L, new Date(), new Date());

        Session session = this.sessionFactory.getCurrentSession();

        try {
            session.beginTransaction();
            session.persist(money);//save
            session.getTransaction().commit();
        } finally {
            session.close();
        }
        return money;
    }

    public Money getMoney(Long id) throws MoneyNotFoundException {
        Session session = this.sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Money data = session.get(Money.class, id);
            session.getTransaction().commit();

            if (data == null) {
                throw new MoneyNotFoundException("お金データが存在しませんでした。 ID:" + id);
            }
            return data;
        } finally {
            session.close();
        }
    }

    public Money getMoneyFromUserId(Long user_id) throws MoneyNotFoundException {
        Session session = this.sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Money data = session.createSelectionQuery("from Money where user_id = ?1", Money.class)
                    .setParameter(1, user_id).getSingleResultOrNull();
            session.getTransaction().commit();

            if (data == null) {
                throw new MoneyNotFoundException("お金データが存在しませんでした。 user_id:" + user_id);
            }
            return data;
        } finally {
            session.close();
        }
    }

    public boolean existsMoney(Long id) {
        try {
            getMoney(id);
            return true;
        } catch (MoneyNotFoundException e) {
            return false;
        }
    }

    public boolean existsMoneyFromUserId(Long user_id) {
        try {
            getMoneyFromUserId(user_id);
            return true;
        } catch (MoneyNotFoundException e) {
            return false;
        }
    }

    public Long getPrimaryKeyFromUserId(Long user_id) throws MoneyNotFoundException {
        Money money = this.getMoneyFromUserId(user_id);
        return money.getId();
    }

    public void updateMoneyFromLP(User user, Long money) throws MoneyNotFoundException {
        Money moneyData = this.getMoneyFromUserId(user.getId());
        moneyData.setMoney(money);
        updateMoney(moneyData);
    }

    public void updateMoney(Money money) {
        Session session = this.sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.merge(money);//update
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    public void deleteMoney(Money money) {
        Session session = this.sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            session.remove(money); //delete
            session.getTransaction().commit();
        } finally {
            session.close();
        }

    }
}
