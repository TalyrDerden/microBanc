package com.home.microBanc.service;

public class ObjectAlreadyExistsException extends RuntimeException{
    public ObjectAlreadyExistsException(String message){
        super(message);
    }
}
