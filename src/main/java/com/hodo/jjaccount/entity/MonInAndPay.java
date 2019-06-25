package com.hodo.jjaccount.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class MonInAndPay {
    @Excel(name = "日期")
    private String yearMon;
    @Excel(name = "收入", width = 16.0D)
    private String incomes;
    @Excel(name = "支出", width = 16.0D)
    private String pays;
    @Excel(name = "合计", width = 16.0D)
    private String balance;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getYearMon() {
        return yearMon;
    }

    public void setYearMon(String yearMon) {
        this.yearMon = yearMon;
    }

    public String getIncomes() {
        return incomes;
    }

    public void setIncomes(String incomes) {
        this.incomes = incomes;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }


}
