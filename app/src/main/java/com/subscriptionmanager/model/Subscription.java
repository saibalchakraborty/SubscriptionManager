package com.subscriptionmanager.model;

import java.io.Serializable;
import java.util.Date;

public class Subscription implements Serializable, Comparable {
    private long id;
    private String subscription;
    private Date startDate;
    private Date endDate;
    private Date notifyDate;
    private double cost;

    public Subscription(long id, String subscription, Date startDate, Date endDate, Date notifyDate, double cost) {
        this.id = id;
        this.subscription = subscription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notifyDate = notifyDate;
        this.cost = cost;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(Date notifyDate) {
        this.notifyDate = notifyDate;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", subscription='" + subscription + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", notifyDate=" + notifyDate +
                ", cost=" + cost +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        Subscription subscription = (Subscription) o;
        if(id < subscription.getId()){
            return 1;
        }
        else {
            return -1;
        }
    }
}