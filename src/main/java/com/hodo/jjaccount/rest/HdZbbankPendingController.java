package com.hodo.jjaccount.rest;

import com.github.ag.core.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.ExcelUtil;
import com.github.wxiaoqi.security.common.util.MyBeanUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.UUIDUtils;
import com.hodo.jjaccount.biz.HdBankRecordBiz;
import com.hodo.jjaccount.biz.HdCompanyBiz;
import com.hodo.jjaccount.biz.HdZbbankAccountBiz;
import com.hodo.jjaccount.biz.HdZbbankPendingBiz;
import com.hodo.jjaccount.entity.*;
import com.hodo.jjaccount.util.jjUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.wxiaoqi.security.auth.client.annotation.CheckClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.CheckUserToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("hdZbbankPending")
@CheckClientToken
@CheckUserToken
public class HdZbbankPendingController extends BaseController<HdZbbankPendingBiz, HdZbbankPending, String> {

    @Autowired
    HdZbbankAccountBiz hdZbbankAccountBiz;
    @Autowired
    HdCompanyBiz hdCompanyBiz;
    @Autowired
    HdBankRecordBiz hdBankRecordBiz;

    //重写分页
    @ApiOperation("分页获取数据*")
    @RequestMapping(value = "/findRecords", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<HdZbbankPending> findRecords(@RequestParam Map<String, Object> params) {
        long total = 0;
        List<HdZbbankPending> hdZbbankPendingList = new ArrayList<>();
        List<HdZbbankPending> allZbbankPendingList = new ArrayList<>();
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
        String startTime = jjUtil.handleParams(params, "dateStart");
        String endTime = jjUtil.handleParams(params, "dateEnd");
        hdZbbankPendingList = baseBiz.getZbBankPendingList(startTime,
                endTime, String.valueOf((pageInt - 1) * limitInt), limit, BaseContextHandler.getTenantID(),
                params);
        allZbbankPendingList = baseBiz.getAllZbBankPendingList(
                startTime, endTime, BaseContextHandler.getTenantID(), params);
        if (allZbbankPendingList != null && allZbbankPendingList.size() > 0) {
            total = allZbbankPendingList.size();
        }
        return new TableResultResponse<HdZbbankPending>(total, hdZbbankPendingList);
    }

    /**
     * 批量删除银行待处理账单
     *
     * @return
     */
    @ApiOperation("批量删除总部银行待处理账单*")
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
            message = "总部银行待处理账单删除失败";
            return new ObjectRestResponse<>(500, message);
        }
        message = "您成功删除" + count + "条";
        return new ObjectRestResponse<>(200, message);
    }

    //导出
    @ApiOperation("导出*")
    @RequestMapping(value = "/exportXls", method = RequestMethod.GET)
    public void exportXls(String userName) {
        Map<String, Object> params = new HashedMap();
        //对方科目
        String subjects = request.getParameter("subjects");
        //摘要
        String remark = request.getParameter("remark");
        String startTime = request.getParameter("dateStart");
        String endTime = request.getParameter("dateEnd");
        if (StringUtil.isNotEmpty(subjects)) {
            params.put("subjects", subjects);
        }
        if (StringUtil.isNotEmpty(remark)) {
            params.put("remark", remark);
        }
        List<HdZbbankPending> allZbbankPendingList = baseBiz.getAllZbBankPendingList(
                startTime, endTime, BaseContextHandler.getTenantID(), params
        );
        ExcelUtil excelUtil = new ExcelUtil();
        try {
            excelUtil.print("总部银行待处理账单", HdZbbankPending.class, "总部银行待处理账单",
                    "导出人:" + userName, "总部银行待处理账单", allZbbankPendingList, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //划账入记账表
    @ApiOperation("划入记账单中*")
    @RequestMapping(value = "/doHua", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<HdZbbankPending> doHua(String pendId) {
        if (StringUtil.isEmpty(pendId)) {
            return new ObjectRestResponse<>(1003, "参数传递失败");
        }
        //待处理转成记账
        HdZbbankPending hdZbbankPending = baseBiz.selectById(pendId);
        if (hdZbbankPending == null) {
            return new ObjectRestResponse<>(1003, "数据查询异常");
        }

        HdZbbankAccount hdZbbankAccount = new HdZbbankAccount();
        try {
            MyBeanUtils.copyBeanNotNull2Bean(hdZbbankPending, hdZbbankAccount);
            //由于我方科目存入的是中文，所以需要转换成id
            String companyName = hdZbbankAccount.getCompanyName();
            if (StringUtil.isEmpty(companyName)) {
                return new ObjectRestResponse<>(1004, "账单公司为空");
            }
            HdCompany company = hdCompanyBiz.getCompanyByName(companyName);
            if (company == null) {
                return new ObjectRestResponse<>(1004, "账单公司获取异常");
            }
            hdZbbankAccount.setCompanyName(company.getId());
            hdZbbankAccountBiz.insertEntity(hdZbbankAccount);
            //待处理删除
            baseBiz.delete(hdZbbankPending);
            //处理记录保存
            HdBankRecord hdBankRecord = new HdBankRecord();
            hdBankRecord.setCompanyid(hdZbbankAccount.getCompanyName());
            //判断收支方向
            BigDecimal pay = hdZbbankAccount.getPay();
            BigDecimal income = hdZbbankAccount.getIncome();
            //默认为0,收入
            String flag = "0";
            BigDecimal money = BigDecimal.ZERO;
            if (pay != null && pay.compareTo(new BigDecimal(0)) != 0) {
                flag = "1";
                money = pay;
            } else {
                if (income != null && income.compareTo(new BigDecimal(0)) != 0) {
                    money = income;
                }
            }
            hdBankRecord.setFlag(flag);
            hdBankRecord.setCreateName(BaseContextHandler.getName());
            hdBankRecord.setCreateDate(new Date());
            hdBankRecord.setOpertype("总部待处理账单划入");
            //根据收支方向获取金额
            hdBankRecord.setMoney(money.toString());
            hdBankRecord.setNum(hdZbbankAccount.getNo() != null ? hdZbbankAccount.getNo().toString() : "");
            hdBankRecord.setId(UUIDUtils.generateUuid());
            hdBankRecordBiz.insertSelective(hdBankRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ObjectRestResponse<>(200, "成功划账");
    }

}