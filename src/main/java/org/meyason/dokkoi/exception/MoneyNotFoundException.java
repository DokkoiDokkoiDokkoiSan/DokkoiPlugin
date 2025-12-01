package org.meyason.dokkoi.exception;

public class MoneyNotFoundException extends RuntimeException {
    public MoneyNotFoundException(String message) {
        super(message);
    }
}
