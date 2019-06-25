package com.hodo.jjaccount.biz;

import com.github.ag.core.context.BaseContextHandler;
import com.hodo.jjaccount.entity.HdCompany;
import com.hodo.jjaccount.entity.HdMatchCompany;
import com.hodo.jjaccount.mapper.HdMatchCompanyMapper;
import org.springframework.stereotype.Service;

import com.github.wxiaoqi.security.common.biz.BusinessBiz;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:28:43
 * @email 463540703@qq.com
 */
@Service
public class HdMatchCompanyBiz extends BusinessBiz<HdMatchCompanyMapper, HdMatchCompany> {

    //获取查询的公司
    public List<HdMatchCompany> getHdMatchCompanyList(String companyName, String page, String limit,
                                                      String tenantId, Map<String, Object> params) {
        return this.mapper.getHdMatchCompanyList(companyName, page, limit, tenantId, params);
    }

    public List<HdMatchCompany> getAllMatchCompany(String companyName, String tenantId, Map<String, Object> params) {
        return this.mapper.getAllMatchCompany(companyName, tenantId, params);
    }

    //根据外转的公司查询是否存在
    public List<HdMatchCompany> getMatchCompanyByTerm(String customerName) {
        return this.mapper.getMatchCompanyByTerm(customerName, BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
    }

    public void batchSave(List<HdMatchCompany> hdMatchCompanies) {
        for (HdMatchCompany hdMatchCompany : hdMatchCompanies) {
            hdMatchCompany.setTenantId(BaseContextHandler.getTenantID());
            hdMatchCompany.setCreateBy(BaseContextHandler.getUserID());
            super.insertSelective(hdMatchCompany);
        }
    }
}