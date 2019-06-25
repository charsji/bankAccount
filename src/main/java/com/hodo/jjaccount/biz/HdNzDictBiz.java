package com.hodo.jjaccount.biz;

import com.github.ag.core.context.BaseContextHandler;
import com.hodo.jjaccount.entity.HdNzDict;
import com.hodo.jjaccount.mapper.HdNzDictMapper;
import org.springframework.stereotype.Service;


import com.github.wxiaoqi.security.common.biz.BusinessBiz;

import java.util.List;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:28:42
 * @email 463540703@qq.com
 */
@Service
public class HdNzDictBiz extends BusinessBiz<HdNzDictMapper, HdNzDict> {

    public List<HdNzDict> getAllNzDictEx(String bankName, String tenantId,String userId) {
        return this.mapper.getAllNzDictEx(bankName, tenantId,userId);
    }

    public void batchSave(List<HdNzDict> hdNzDicts) throws Exception {
        for (HdNzDict hdNzDict : hdNzDicts) {
            hdNzDict.setCreateBy(BaseContextHandler.getUserID());
            hdNzDict.setTenantId(BaseContextHandler.getTenantID());
            super.insertSelective(hdNzDict);
        }
    }

    public List<HdNzDict> selectListAllByTenant(String tenantId,String userId) {
        return this.mapper.selectListAllByTenant(tenantId,userId);
    }

    //根据内转账号获取内转实体类
    public String getBankNameByNZId(String nzId) {
        return this.mapper.getBankNameByNZId(nzId,BaseContextHandler.getUserID());
    }
}