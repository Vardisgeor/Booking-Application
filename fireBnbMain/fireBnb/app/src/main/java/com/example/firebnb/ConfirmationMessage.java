package com.example.firebnb;

import java.io.Serializable;
public class ConfirmationMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;

    public ConfirmationMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
