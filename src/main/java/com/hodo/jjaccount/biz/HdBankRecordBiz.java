package com.hodo.jjaccount.biz;

import com.github.ag.core.context.BaseContextHandler;
import com.hodo.jjaccount.entity.HdBankAccount;
import com.hodo.jjaccount.entity.HdBankPending;
import com.hodo.jjaccount.entity.HdBankRecord;
import com.hodo.jjaccount.entity.HdCompany;
import com.hodo.jjaccount.mapper.HdBankRecordMapper;
import org.springframework.stereotype.Service;

import com.github.wxiaoqi.security.common.biz.BusinessBiz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:29:07
 * @email 463540703@qq.com
 */
@Service
public class HdBankRecordBiz extends BusinessBiz<HdBankRecordMapper, HdBankRecord> {
    /**
     * 封装操作记录
     */
    public HdBankRecord getHdBankRecord(HdBankAccount hdBankAccount, String type) {
        boolean flag = true;
        if (hdBankAccount.getPay() != null) {
            BigDecimal bpay = hdBankAccount.getPay() != null ? hdBankAccount.getPay() : BigDecimal.ZERO;
            flag = bpay.compareTo(BigDecimal.ZERO) == 0;
        }
        HdBankRecord hdBankRecord = new HdBankRecord();
        hdBankRecord.setCompanyid(hdBankAccount.getCompanyName());
        hdBankRecord.setFlag(flag ? "0" : "1");
        hdBankRecord.setNum(hdBankAccount.getNo() != null ? hdBankAccount.getNo().toString() :
                "");
        hdBankRecord.setMoney(flag ? hdBankAccount.getIncome() != null ? hdBankAccount.getIncome().toString() :
                "" : hdBankAccount.getPay() != null ? hdBankAccount.getPay().toString() :
                "");
        hdBankRecord.setRemark(hdBankAccount.getRemark());
        hdBankRecord.setOpertype(type);
        hdBankRecord.setCreateDate(new Date());
        hdBankRecord.setCreateName(BaseContextHandler.getName());
        hdBankAccount.setCreateBy(BaseContextHandler.getUserID());
        return hdBankRecord;
    }

    public void batchSave(List<HdBankRecord> hdBankRecords) {
        for (HdBankRecord hdBankRecord : hdBankRecords) {
            hdBankRecord.setTenantId(BaseContextHandler.getTenantID());
        }
        while (hdBankRecords.size() > 100) {
            List<HdBankRecord> hdBankRecordList = hdBankRecords.subList(0, 99);
            this.mapper.addList(hdBankRecordList);
            hdBankRecords.removeAll(hdBankRecordList);
        }
        this.mapper.addList(hdBankRecords);
    }

    public List<HdBankRecord> getHdBankRecordList(String companyName, String startTime,
                                                  String endTime, String page, String limit,
                                                  String tenantId, Map<String, Object> params) {
        return this.mapper.getHdBankRecordList(companyName, startTime, endTime,
                page, limit, tenantId, params);
    }

    public List<HdBankRecord> getAllBankRecord(String companyName, String startTime,
                                               String endTime, String tenantId,
                                               Map<String, Object> params) {
        return this.mapper.getAllBankRecord(companyName, startTime, endTime,
                tenantId, params);
    }

    public HdBankRecord getHdBankRecord(HdBankPending HdBankPending, String type) {
        boolean flag = true;
        if (HdBankPending.getPay() != null) {
            BigDecimal bpay = HdBankPending.getPay() != null ? HdBankPending.getPay() : BigDecimal.ZERO;
            flag = bpay.compareTo(BigDecimal.ZERO) == 0;
        }
        HdBankRecord hdBankRecord = new HdBankRecord();
        hdBankRecord.setCompanyid(HdBankPending.getCompanyName());
        hdBankRecord.setFlag(flag ? "0" : "1");
        hdBankRecord.setNum(HdBankPending.getNo() != null ? HdBankPending.getNo().toString() :
                "");
        hdBankRecord.setMoney(flag ? HdBankPending.getIncome() != null ? HdBankPending.getIncome().toString() :
                "" : HdBankPending.getPay() != null ? HdBankPending.getPay().toString() :
                "");
        hdBankRecord.setRemark(HdBankPending.getRemark());
        hdBankRecord.setOpertype(type);
        hdBankRecord.setCreateDate(new Date());
        hdBankRecord.setCreateName(BaseContextHandler.getName());
        return hdBankRecord;
    }
}