package com.project.bookstore.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate orderDate;
    private LocalDate shippingDate;

    private double totalPrice;

    private boolean completed;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderPayment orderPayment;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<BasketItem> orderedItems;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(LocalDate shippingDate) {
        this.shippingDate = shippingDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public OrderPayment getPayment() {
        return orderPayment;
    }

    public void setPayment(OrderPayment orderPayment) {
        this.orderPayment = orderPayment;
    }

    public OrderAddress getAddress() {
        return orderAddress;
    }

    public void setAddress(OrderAddress orderAddress) {
        this.orderAddress = orderAddress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<BasketItem> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(List<BasketItem> orderedItems) {
        this.orderedItems = orderedItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderPayment getOrderPayment() {
        return orderPayment;
    }

    public void setOrderPayment(OrderPayment orderPayment) {
        this.orderPayment = orderPayment;
    }

    public OrderAddress getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(OrderAddress orderAddress) {
        this.orderAddress = orderAddress;
    }
}
