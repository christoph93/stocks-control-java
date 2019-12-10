package com.stoncks.document;

import com.stoncks.entity.Position;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Dividend {

    @Id
    private String id;

    private String symbol, description;
    private Date payDate;
    private double grossValue, taxValue, netValue;


    public Dividend(String symbol, String description, Date payDate, double grossValue, double taxValue, double netValue) {
        this.symbol = symbol;
        this.description = description;
        this.payDate = payDate;
        this.grossValue = grossValue;
        this.taxValue = taxValue;
        this.netValue = netValue;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public double getGrossValue() {
        return grossValue;
    }

    public void setGrossValue(double grossValue) {
        this.grossValue = grossValue;
    }

    public double getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(double taxValue) {
        this.taxValue = taxValue;
    }

    public double getNetValue() {
        return netValue;
    }

    public void setNetValue(double netValue) {
        this.netValue = netValue;
    }

    @Override
    public String toString() {
        return "Dividend{" +
                "id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", description='" + description + '\'' +
                ", payDate=" + payDate +
                ", grossValue=" + grossValue +
                ", taxValue=" + taxValue +
                ", netValue=" + netValue +
                '}';
    }
}
