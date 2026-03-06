package com.leandre.customer;

public class RegularCustomer extends Customer {

    public RegularCustomer(String customerId, String name, int age, String contact, String address) {
        super("REG" + String.format("%03d",age), name, age, contact, address);
    }

    @Override
    public void displayCustomerDetails() {
        System.out.println("Customer ID : " + getCustomerId());
        System.out.println("Age         : " + getAge());
        System.out.println("Contact     : " + getContact());
        System.out.println("Address     : " + getAddress());
    }

    @Override
    public String getCustomerType() {
        return "Regular";
    }
}
