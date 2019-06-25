package com.hodo.jjaccount.common;

import com.github.wxiaoqi.security.common.msg.BaseResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @version 2017-06-14 22:40
 */
public class TableResultResponse<T> extends BaseResponse {

    TableData<T> data;
    BigDecimal sumIncome;
    BigDecimal sumPay;

    public TableResultResponse(int status, String msg) {
        super(status, msg);
    }

    public TableResultResponse(long total, List<T> rows, BigDecimal sumIncome,
                               BigDecimal sumPay) {
        this.data = new TableData<T>(total, rows);
        this.sumIncome = sumIncome;
        this.sumPay = sumPay;
    }

    public TableResultResponse() {
        this.data = new TableData<T>();
    }

    TableResultResponse<T> total(int total) {
        this.data.setTotal(total);
        return this;
    }

    TableResultResponse<T> total(List<T> rows) {
        this.data.setRows(rows);
        return this;
    }

    public TableData<T> getData() {
        return data;
    }

    public void setData(TableData<T> data) {
        this.data = data;
    }

    public BigDecimal getSumIncome() {
        return sumIncome;
    }

    public void setSumIncome(BigDecimal sumIncome) {
        this.sumIncome = sumIncome;
    }

    public BigDecimal getSumPay() {
        return sumPay;
    }

    public void setSumPay(BigDecimal sumPay) {
        this.sumPay = sumPay;
    }

    public class TableData<T> {
        long total;
        List<T> rows;

        public TableData(long total, List<T> rows) {
            this.total = total;
            this.rows = rows;
        }

        public TableData() {
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public List<T> getRows() {
            return rows;
        }

        public void setRows(List<T> rows) {
            this.rows = rows;
        }
    }
}
