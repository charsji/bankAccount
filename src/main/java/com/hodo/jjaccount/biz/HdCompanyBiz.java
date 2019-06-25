package com.hodo.jjaccount.biz;

import com.github.ag.core.context.BaseContextHandler;
import com.hodo.jjaccount.entity.HdCompany;
import com.hodo.jjaccount.mapper.HdCompanyMapper;
import org.springframework.stereotype.Service;


import com.github.wxiaoqi.security.common.biz.BusinessBiz;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:28:43
 * @email 463540703@qq.com
 */
@Service
public class HdCompanyBiz extends BusinessBiz<HdCompanyMapper, HdCompany> {
    //根据公司id获取公司名称
    public String getCompanyNameById(String companyId) {
        return this.mapper.getCompanyNameById(companyId, BaseContextHandler.getTenantID());
    }

    //根据公司名称查找该公司实体类
    public HdCompany getCompanyByName(String companyName) {
        return this.mapper.getCompanyByName(companyName, BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
    }


    //获取所有公司名称
    public List<HdCompany> getAllCompanyNames() {
        List<HdCompany> hdCompanies = this.mapper.getAllCompanyNames(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
        if (hdCompanies != null && hdCompanies.size() > 0) {
            for (HdCompany hdCompany : hdCompanies) {
                hdCompany.setValue(hdCompany.getCompanyName());
            }
        }
        return hdCompanies;
    }

    public List<HdCompany> getAllCompanyEx(String company, String tenantId,String userId) {
        return this.mapper.getAllCompanyEx(company, tenantId,userId);
    }

    @Transactional
    public void batchSave(List<HdCompany> hdCompanies) throws Exception {
        for (HdCompany hdCompany : hdCompanies) {
            hdCompany.setTenantId(BaseContextHandler.getTenantID());
            hdCompany.setCreateBy(BaseContextHandler.getUserID());
            super.insertSelective(hdCompany);
        }
    }
}