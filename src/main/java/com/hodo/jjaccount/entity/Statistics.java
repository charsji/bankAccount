package com.hodo.jjaccount.entity;

import java.math.BigDecimal;

public class Statistics {
    private String companyName;
    private BigDecimal beginBalance;
    private BigDecimal currentIncome;
    private BigDecimal currentPay;
    private BigDecimal endBalance;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getBeginBalance() {
        return beginBalance;
    }

    public void setBeginBalance(BigDecimal beginBalance) {
        this.beginBalance = beginBalance;
    }

    public BigDecimal getCurrentIncome() {
        return currentIncome;
    }

    public void setCurrentIncome(BigDecimal currentIncome) {
        this.currentIncome = currentIncome;
    }

    public BigDecimal getCurrentPay() {
        return currentPay;
    }

    public void setCurrentPay(BigDecimal currentPay) {
        this.currentPay = currentPay;
    }

    public BigDecimal getEndBalance() {
        return endBalance;
    }

    public void setEndBalance(BigDecimal endBalance) {
        this.endBalance = endBalance;
    }
}
