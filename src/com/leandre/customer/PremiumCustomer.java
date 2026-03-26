package com.leandre.customer;

public class PremiumCustomer extends Customer {
    private static final double DEFAULT_MINIMUM_BALANCE = 10_000;

    private final double minimumBalance;

    public PremiumCustomer(String customerId,
                           String name,
                           int age,
                           String contact,
                           String email,
                           String address) {
        this(customerId, name, age, contact, email, address, DEFAULT_MINIMUM_BALANCE);
    }

    public PremiumCustomer(String customerId, String name, int age, String contact, String email, String address, double minimumBalance) {
        super(customerId, name, age, contact, email, address);
        this.minimumBalance = minimumBalance;
    }

    @Override
    public void displayCustomerDetails() {
        System.out.println("Customer ID      : " + getCustomerId());
        System.out.println("Name             : " + getName());
        System.out.println("Age              : " + getAge());
        System.out.println("Contact          : " + getContact());
        System.out.println("Email            : " + getEmail());
        System.out.println("Address          : " + getAddress());
        System.out.println("Minimum Balance  : $" + minimumBalance);
        System.out.println("Type             : Premium Customer");
    }

    @Override
    public String getCustomerType() {
        return "Premium";
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    //TODO: research on why default value can't implement their setter method

    public boolean hasWaivedFees(double balance) {
        //TODO: Improve this code like checking if this premium user is eligible for fee waiver
        return balance >= minimumBalance;

    }
}