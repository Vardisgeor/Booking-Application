package com.example.firebnb;

import java.util.HashSet;
import java.util.Set;


public class Account {
    public static Set<Account> accounts = new HashSet<>();
    private String password;
    private String name;
    public Account() {}

    public static void updateAcc(Set<Account> acc){
        accounts = acc;
    }


    public Account(String name, String password){
        this.password = password;
        this.name = name;
        accounts.add(this);
    }

    public static boolean nameExist(String name)
    {
        for(Account i: accounts){
            if(i.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public static Account Login(String name, String pas)
    {
        for (Account i: accounts) {
            if(i.getName().equals(name) && i.getPassword().equals(pas)){
                return i;
            }
        }
        return null;

    }

    public void setName(String name) {
        this.name = name;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getName(){
        return name;
    }


    public String getPassword(){
        return password;
    }

}