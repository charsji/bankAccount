package com.hodo.jjaccount.rest;

import com.github.ag.core.context.BaseContextHandler;
import com.github.wxiaoqi.security.auth.client.annotation.IgnoreClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.IgnoreUserToken;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;

import com.github.wxiaoqi.security.common.util.ExcelUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.hodo.jjaccount.biz.HdBankAccountBiz;
import com.hodo.jjaccount.biz.HdCompanyBiz;
import com.hodo.jjaccount.biz.HdMatchCompanyBiz;
import com.hodo.jjaccount.entity.*;
import com.hodo.jjaccount.util.jjUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.wxiaoqi.security.auth.client.annotation.CheckClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.CheckUserToken;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("hdMatchCompany")
@CheckClientToken
@CheckUserToken
public class HdMatchCompanyController extends BaseController<HdMatchCompanyBiz, HdMatchCompany, String> {
    @Autowired
    private HdBankAccountBiz hdBankAccountBiz;
    @Autowired
    private HdCompanyBiz hdCompanyBiz;

    //公司名匹配查询重写page
    //添加录入人条件6.24
    @ApiOperation("分页获取数据2")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<HdMatchCompany> list(@RequestParam Map<String, Object> params) {
        //公司名精准查询,内部抬头外部抬头模糊查询
        long total = 0;
        String tenantId = BaseContextHandler.getTenantID();
        String userId = BaseContextHandler.getUserID();
//        if(StringUtil.isNotEmpty(tenantId)){
//            params.put("TENANTID",tenantId);
//        }
        String companyName = jjUtil.handleParams(params, "companyName");
        String page = jjUtil.handleParams(params, "page");
        String limit = jjUtil.handleParams(params, "limit");
        if (StringUtil.isEmpty(page)) {
            page = "1";
        }
        if (StringUtil.isEmpty(limit)) {
            limit = "10";
        }
        int pageInt = Integer.parseInt(page);
        int limitInt = Integer.parseInt(limit);
        List<HdMatchCompany> hdMatchCompanyList = baseBiz.getHdMatchCompanyList(companyName, String.valueOf((pageInt - 1) * limitInt), limit, tenantId, params);
        List<HdMatchCompany> allMatchCompanyList = baseBiz.getAllMatchCompany(companyName, tenantId, params);
        if (hdMatchCompanyList != null && hdMatchCompanyList.size() > 0) {
            for (HdMatchCompany hdMatchCompany : hdMatchCompanyList) {
                String companyId = hdMatchCompany.getCompanyName();
                if (StringUtil.isNotEmpty(companyId)) {
                    String company_name = hdCompanyBiz.getCompanyNameById(companyId);
                    hdMatchCompany.setCompanyName(company_name);
                }
            }
        }

        if (allMatchCompanyList != null && allMatchCompanyList.size() > 0) {
            total = allMatchCompanyList.size();
        }
        return new TableResultResponse<>(total, hdMatchCompanyList);
    }

    /**
     * 批量删除公司名匹配表
     *
     * @return
     */
    @ApiOperation("批量删除公司名匹配表")
    @RequestMapping(value = "/doBatchDel", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<String> doBatchDel(String ids) {
        if (StringUtil.isEmpty(ids)) {
            return new ObjectRestResponse<>(1003, "参数传递失败");
        }
        String message = null;
        int count = 0;
        try {
            for (String id : ids.split(",")) {
                baseBiz.deleteById(id);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "公司名匹配表删除失败";
            return new ObjectRestResponse<>(500, message);
        }
        message = "您成功删除" + count + "条";
        return new ObjectRestResponse<>(200, message);
    }

    /**
     * 导出
     */
    //添加录入人条件6.24
    @ApiOperation("导出")
    @RequestMapping(value = "/exportXls", method = RequestMethod.GET)
    @ResponseBody
//    @IgnoreUserToken
//    @IgnoreClientToken
    public void exportXls(String userName,
                          String companyName, String CUSTOMER_NAME, String INNER_COMPANY) throws ParseException {
        Map<String, Object> params = new HashedMap();
        if (StringUtil.isNotEmpty(CUSTOMER_NAME)) {
            params.put("customer_name", CUSTOMER_NAME);
        }
        if (StringUtil.isNotEmpty(INNER_COMPANY)) {
            params.put("inner_company", INNER_COMPANY);
        }
        String tenantId = BaseContextHandler.getTenantID();
        String userId = BaseContextHandler.getUserID();
//        if(StringUtil.isNotEmpty(tenantId)){
//            params.put("TENANTID",tenantId);
//        }
        params.put("create_by",userId);
        List<HdMatchCompany> allMatchCompanyList = baseBiz.getAllMatchCompany(companyName, tenantId, params);
        if (allMatchCompanyList != null && allMatchCompanyList.size() > 0) {
            for (HdMatchCompany hdMatchCompany : allMatchCompanyList) {
                if (hdMatchCompany.getCompanyName() != null) {
                    String company_name = hdCompanyBiz.getCompanyNameById(hdMatchCompany.getCompanyName());
                    if (StringUtil.isNotEmpty(company_name)) {
                        hdMatchCompany.setCompanyName(company_name);
                    }
                }
            }
        }

        ExcelUtil excelUtil = new ExcelUtil<HdMatchCompany>();
        try {
            excelUtil.print("公司匹配信息管理列表", HdMatchCompany.class, "公司匹配信息管理列表", "导出人:" + userName, "公司匹配信息管理列表", allMatchCompanyList, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //导入不用写,在微服务中
    //添加录入人条件6.24
    @ApiOperation("导入")
    @RequestMapping(value = "/importXls", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<HdMatchCompany> importXls(
            @RequestParam(value = "file", required = true) MultipartFile file
    ) {

        //外部抬头，公司必填，内部抬头不必填，公司需匹配不能乱填
        List<HdMatchCompany> hdMatchCompanies;
        ExcelUtil excelUtil = new ExcelUtil<HdMatchCompany>();
        try {
            hdMatchCompanies = excelUtil.importXls(file, HdMatchCompany.class);
            int count = 0;
            if (hdMatchCompanies != null && hdMatchCompanies.size() > 0) {

                //检查外部抬头，公司是否为空，公司能否匹配
                for (HdMatchCompany hdMatchCompany : hdMatchCompanies) {
                    count++;
                    if (StringUtil.isEmpty(hdMatchCompany.getCustomerName())) {
                        return new ObjectRestResponse<>(1004, "第" + count + "条外部抬头为空");
                    }
                    if (StringUtil.isEmpty(hdMatchCompany.getCompanyName())) {
                        return new ObjectRestResponse<>(1004, "第" + count + "条公司为空");
                    }
                    HdCompany hdCompany = hdCompanyBiz.getCompanyByName(hdMatchCompany.getCompanyName());
                    if (hdCompany == null) {
                        return new ObjectRestResponse<>(1004, "第" + count + "条公司无法与系统内部匹配");
                    }
                    hdMatchCompany.setCompanyName(hdCompany.getId());

                }
                baseBiz.batchSave(hdMatchCompanies);
            }
        } catch (Exception e) {
            return new ObjectRestResponse<>(500, "导入失败");
        }

        return new ObjectRestResponse<>(200, "导入成功");
    }
}