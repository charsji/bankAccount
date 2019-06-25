package com.hodo.jjaccount.biz;

import com.github.ag.core.context.BaseContextHandler;
import com.hodo.jjaccount.entity.HdBankPending;
import com.hodo.jjaccount.entity.HdBankRecord;
import com.hodo.jjaccount.mapper.HdBankPendingMapper;
import org.springframework.stereotype.Service;


import com.github.wxiaoqi.security.common.biz.BusinessBiz;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:29:23
 * @email 463540703@qq.com
 */
@Service
public class HdBankPendingBiz extends BusinessBiz<HdBankPendingMapper, HdBankPending> {

    public List<HdBankPending> getBankPendingList(String startTime, String endTime,
                                                  String page, String limit, String tenantId, Map<String, Object> params) {
        return this.mapper.getBankPendingList(startTime, endTime, page, limit, tenantId, params);
    }

    public List<HdBankPending> getAllBankPending(String startTime, String endTime, String tenantId,
                                                 Map<String, Object> params) {
        return this.mapper.getAllBankPending(startTime, endTime, tenantId, params);
    }

    public void batchSave(List<HdBankPending> hdBankPendingList) {

        while (hdBankPendingList.size() > 1000) {
            List<HdBankPending> hdBankPendings = hdBankPendingList.subList(0, 999);
            this.mapper.addList(hdBankPendings);
            hdBankPendingList.removeAll(hdBankPendings);
        }
        this.mapper.addList(hdBankPendingList);
    }

}