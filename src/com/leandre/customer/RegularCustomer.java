package com.leandre.customer;

public class RegularCustomer extends Customer {

    public RegularCustomer(String customerId, String name, int age, String contact, String email, String address) {
        super("REG" + String.format("%03d", Customer.getCustomerCounter() + 1), name, age, contact, email, address);
    }

    @Override
    public void displayCustomerDetails() {
        System.out.println("Customer ID : " + getCustomerId());
        System.out.println("Name        : " + getName());
        System.out.println("Age         : " + getAge());
        System.out.println("Contact     : " + getContact());
        System.out.println("Email       : " + getEmail());
        System.out.println("Address     : " + getAddress());
    }

    @Override
    public String getCustomerType() {
        return "Regular";
    }
}