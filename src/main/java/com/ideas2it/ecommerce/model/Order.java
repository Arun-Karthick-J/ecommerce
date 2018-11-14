package com.ideas2it.ecommerce.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * Order contains customer details, product, delivery address, quantity and
 * price of the product at the time of purchase.
 * </p>
 * 
 * @author Anantharaj.S
 */
public class Order {
    private Integer id;
    private Customer customer;
    private LocalDate orderDate;
    private Float price;
    private Address address;
    private List<OrderItem> orderItems;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate getOrderDate() {
        return orderDate;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
