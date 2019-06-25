package com.hodo.jjaccount.biz;

import com.hodo.jjaccount.entity.HdBankPending;
import com.hodo.jjaccount.entity.HdZbbankPending;
import com.hodo.jjaccount.mapper.HdZbbankPendingMapper;
import org.springframework.stereotype.Service;

import com.github.wxiaoqi.security.common.biz.BusinessBiz;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-05-08 10:28:39
 * @email 463540703@qq.com
 */
@Service
public class HdZbbankPendingBiz extends BusinessBiz<HdZbbankPendingMapper, HdZbbankPending> {

    public List<HdZbbankPending> getZbBankPendingList(String startTime, String endTime,
                                                      String page, String limit, String tenantId,
                                                      Map<String, Object> params) {
        return this.mapper.getZbBankPendingList(startTime, endTime, page,
                limit, tenantId, params);
    }

    public List<HdZbbankPending> getAllZbBankPendingList(String startTime, String endTime, String tenantId,
                                                         Map<String, Object> params) {
        return this.mapper.getAllZbBankPendingList(startTime, endTime, tenantId, params);
    }

    public void batchSave(List<HdZbbankPending> hdBankPendingList) {

        while (hdBankPendingList.size() > 1000) {
            List<HdZbbankPending> hdBankPendings = hdBankPendingList.subList(0, 999);
            this.mapper.addList(hdBankPendings);
            hdBankPendingList.removeAll(hdBankPendings);
        }
        this.mapper.addList(hdBankPendingList);
    }

}