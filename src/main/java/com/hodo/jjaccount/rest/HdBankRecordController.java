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
import com.hodo.jjaccount.biz.HdBankRecordBiz;
import com.hodo.jjaccount.biz.HdCompanyBiz;
import com.hodo.jjaccount.entity.HdBankPending;
import com.hodo.jjaccount.entity.HdBankRecord;
import com.hodo.jjaccount.entity.HdMatchCompany;
import com.hodo.jjaccount.util.jjUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.wxiaoqi.security.auth.client.annotation.CheckClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.CheckUserToken;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("hdBankRecord")
@CheckClientToken
@CheckUserToken
public class HdBankRecordController extends BaseController<HdBankRecordBiz, HdBankRecord, String> {
    @Autowired
    private HdCompanyBiz hdCompanyBiz;
    @Autowired
    private HdBankAccountBiz hdBankAccountBiz;

    /**
     * 批量删除银行操作记录
     *
     * @return
     */
    @ApiOperation("批量删除银行操作记录")
    @RequestMapping(value = "/doBatchDel", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<HdBankPending> doBatchDel(String ids) {
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
            message = "银行操作记录删除失败";
            return new ObjectRestResponse<>(500, message);
        }
        message = "您成功删除" + count + "条";
        return new ObjectRestResponse<>(200, message);
    }

    //重写page
    //添加录入人条件6.24
    @ApiOperation("分页获取数据2")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<HdBankRecord> list(@RequestParam Map<String, Object> params) {
        //公司精准查询，有时间查询，其他模糊查询
        long total = 0;
        String tenantId = BaseContextHandler.getTenantID();
        String userId = BaseContextHandler.getUserID();
//        if(StringUtil.isNotEmpty(tenantId)){
//            params.put("TENANTID",tenantId);
//        }
        params.put("create_by",userId);
        String companyName = jjUtil.handleParams(params, "company");
        String page = jjUtil.handleParams(params, "page");
        String limit = jjUtil.handleParams(params, "limit");
        //查询时间分离
        String startTime = jjUtil.handleParams(params, "dateStart");
        String endTime = jjUtil.handleParams(params, "dateEnd");
        if (StringUtil.isEmpty(page)) {
            page = "1";
        }
        if (StringUtil.isEmpty(limit)) {
            limit = "10";
        }
        int pageInt = Integer.parseInt(page);
        int limitInt = Integer.parseInt(limit);
        List<HdBankRecord> hdBankRecordList = baseBiz.getHdBankRecordList(companyName, startTime, endTime,
                String.valueOf((pageInt - 1) * limitInt), limit, tenantId, params);
        List<HdBankRecord> allBankRecordList = baseBiz.getAllBankRecord(companyName, startTime, endTime, tenantId, params);
//        List<HdBankRecord> allMatchCompanyList = baseBiz.getAllBankRecord(companyName,startTime,endTime,params);
        if (hdBankRecordList != null && hdBankRecordList.size() > 0) {
            for (HdBankRecord hdBankRecord : hdBankRecordList) {
                String companyId = hdBankRecord.getCompanyid();
                if (StringUtil.isNotEmpty(companyId)) {
                    String company_name = hdCompanyBiz.getCompanyNameById(companyId);
                    hdBankRecord.setCompanyid(company_name);
                }
            }
        }
        if (allBankRecordList != null && allBankRecordList.size() > 0) {
            total = allBankRecordList.size();
        }
        return new TableResultResponse<HdBankRecord>(total, hdBankRecordList);
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
    public void exportXls(String userName) throws ParseException {
        //日期，摘要，公司，金额
        String startTime = request.getParameter("dateStart");
        String endTime = request.getParameter("dateEnd");
        String companyName = request.getParameter("company");
        String money = request.getParameter("money");
        String remark = request.getParameter("remark");
        Map<String, Object> params = new HashedMap();
        if (StringUtil.isNotEmpty(money)) {
            params.put("money", money);
        }
        if (StringUtil.isNotEmpty(remark)) {
            params.put("remark", remark);
        }
        String tenantId = BaseContextHandler.getTenantID();
        String userId = BaseContextHandler.getUserID();
//        if(StringUtil.isNotEmpty(tenantId)){
//            params.put("TENANTID",tenantId);
//        }
        params.put("create_by",userId);
        List<HdBankRecord> allBankRecordList = baseBiz.getAllBankRecord(companyName, startTime, endTime, tenantId, params);
        //公司名称和收支方向
        if (allBankRecordList != null && allBankRecordList.size() > 0) {
            for (HdBankRecord hdBankRecord : allBankRecordList) {
                if (hdBankRecord.getFlag() != null) {
                    if ("0".equals(hdBankRecord.getFlag())) {
                        hdBankRecord.setFlag("收入");
                    } else {
                        hdBankRecord.setFlag("支出");
                    }
                }
                if (hdBankRecord.getCompanyid() != null) {
                    String company_name = hdCompanyBiz.getCompanyNameById(hdBankRecord.getCompanyid());
                    if (StringUtil.isNotEmpty(company_name)) {
                        hdBankRecord.setCompanyid(company_name);
                    }
                }
            }
        }

        ExcelUtil excelUtil = new ExcelUtil<HdBankRecord>();
        try {
            excelUtil.print("银行处理记录列表", HdBankRecord.class, "银行处理记录列表", "导出人:" + userName, "银行处理记录列表", allBankRecordList, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}