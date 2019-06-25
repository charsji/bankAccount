package com.hodo.jjaccount.rest;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ag.core.context.BaseContextHandler;
import com.github.wxiaoqi.security.auth.client.annotation.IgnoreClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.IgnoreUserToken;
import com.github.wxiaoqi.security.common.exception.base.BusinessException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.*;
import com.hodo.jjaccount.biz.HdBankAccountBiz;
import com.hodo.jjaccount.biz.HdBankPendingBiz;
import com.hodo.jjaccount.biz.HdBankRecordBiz;
import com.hodo.jjaccount.entity.*;
import com.hodo.jjaccount.util.jjUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.wxiaoqi.security.auth.client.annotation.CheckClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.CheckUserToken;

import javax.persistence.PrimaryKeyJoinColumn;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 待处理
 */
@RestController
@RequestMapping("hdBankPending")
@CheckClientToken
@CheckUserToken
public class HdBankPendingController extends BaseController<HdBankPendingBiz, HdBankPending, String> {
    @Autowired
    private HdBankAccountBiz hdBankAccountBiz;
    @Autowired
    private HdBankRecordBiz hdBankRecordBiz;

    /**
     * 页面展示
     */
    //添加录入人条件6.24
    @ApiOperation("分页获取数据2")
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<HdBankPending> list(@RequestParam
                                                   @ApiParam(value = "参数名称(subjects,page,limit,dateStart,dataEnd,remark," +
                                                           "bankName,mySubjects)") Map<String, Object> params) {
        //查询列表数据
        long total = 0;
        String tenantId = BaseContextHandler.getTenantID();
        String userId = BaseContextHandler.getUserID();
        params.put("create_by",userId);
//        if(StringUtil.isNotEmpty(tenantId)){
//            params.put("TENANTID",tenantId);
//        }
        List<HdBankPending> hdBankPendingList = new ArrayList<>();
        List<HdBankPending> allBankPendingList = new ArrayList<>();
        String page = jjUtil.handleParams(params, "page");
        String limit = jjUtil.handleParams(params, "limit");
        //除了公司其他都为模糊查询,所有都为模糊查询,除了时间
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
        hdBankPendingList = baseBiz.getBankPendingList(startTime, endTime,
                String.valueOf((pageInt - 1) * limitInt), limit, tenantId, params);
        allBankPendingList = baseBiz.getAllBankPending(startTime,
                endTime, tenantId, params);
        if (allBankPendingList != null && allBankPendingList.size() > 0) {
            total = allBankPendingList.size();
        }
        return new TableResultResponse<HdBankPending>(total, hdBankPendingList);
    }

    /**
     * 批量删除银行待处理账单
     *
     * @return
     */
    @ApiOperation("批量删除银行待处理账单")
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
            message = "银行待处理账单删除失败";
            return new ObjectRestResponse<>(500, message);
        }
        message = "您成功删除" + count + "条";
        return new ObjectRestResponse<>(200, message);
    }

    /**
     * 划账
     */
    @ApiOperation("划账")
    @RequestMapping(value = "/doHua", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<HdBankPending> doHua(String companyName,
                                                   String remark, String ids) {
        String message = null;
        List<HdBankRecord> hdBankRecordList = new ArrayList<>();
        if (StringUtil.isEmpty(companyName)) {
            return new ObjectRestResponse<>(1003, "参数传递失败");
        }
//        if(StringUtil.isEmpty(remark)){
//            return new ObjectRestResponse<>(1003,"参数传递失败");
//        }
        if (StringUtil.isEmpty(ids)) {
            return new ObjectRestResponse<>(1003, "参数传递失败");
        }
        //公司是否存在前端已做控制
        try {
            for (String id : ids.split(",")) {
                HdBankAccount bank = new HdBankAccount();
                HdBankPending pending = baseBiz.selectById(id);
                MyBeanUtils.copyBeanNotNull2Bean(pending, bank);
                bank.setId(null);
                bank.setCompanyName(companyName);
                if (StringUtil.isNotEmpty(remark)) {
                    bank.setRemark(remark);
                }
                //银行账单录入
                hdBankAccountBiz.insertEntityFreeTime(bank);
                //待处理账单删除
                baseBiz.deleteById(id);
                //批量插入记录表
                hdBankRecordList.add(hdBankRecordBiz.getHdBankRecord(bank, "划账"));
            }
            hdBankRecordBiz.batchSave(hdBankRecordList);
        } catch (Exception e) {
            e.printStackTrace();
            message = "划账失败";
            return new ObjectRestResponse<>(500, message);
        }

        return new ObjectRestResponse<>(200, message);
    }

    /**
     * 分账
     */
    @ApiOperation("分账")
    @RequestMapping(value = "/doFen", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<HdBankPending> doFen(String id, String hdFenStr) {
        String message = null;
        List<HdBankRecord> hdBankRecords = new ArrayList<>();
        if (StringUtil.isEmpty(id)) {
            return new ObjectRestResponse<>(1003, "传入参数失败");
        }
        if (StringUtil.isEmpty(hdFenStr)) {
            return new ObjectRestResponse<>(1003, "传入参数失败");
        }
        //List<Object> strList = JSONArray.parseArray(str);
        List<HdFenListEntity> hdFenList = new ArrayList<>();
        hdFenList = JSONArray.parseArray(hdFenStr, HdFenListEntity.class);
        //检查公司是否为空
        for (HdFenListEntity hdFen : hdFenList) {
            if (StringUtil.isEmpty(hdFen.getCompanyId())) {
                return new ObjectRestResponse<>(1004, "分账有公司名为空");
            }
        }
        //先给金额排个序
        Collections.sort(hdFenList, new Comparator<HdFenListEntity>() {
            @Override
            public int compare(HdFenListEntity o1, HdFenListEntity o2) {
                int a = o1.getMoney().compareTo(o2.getMoney());
                if (a == 1) {
                    return 1;
                } else if (a == -1) {
                    return -1;
                } else {
                    return 0;
                }
            }

        });
        try {
            HdBankPending hdBankPending = baseBiz.selectById(id);
            if (hdBankPending == null) {
                return new ObjectRestResponse<>(1004, "数据查询异常");
            }
            //先录入一笔最大的金额进公司
            HdFenListEntity maxFenListEntity = hdFenList.get(hdFenList.size() - 1);
            HdBankAccount maxBankAccount = new HdBankAccount();
            MyBeanUtils.copyBeanNotNull2Bean(hdBankPending, maxBankAccount);
            maxBankAccount.setCompanyName(maxFenListEntity.getCompanyId());
            maxBankAccount.setId(null);
            hdBankAccountBiz.insertEntityFreeTime(maxBankAccount);
            hdBankRecords.add(hdBankRecordBiz.getHdBankRecord(maxBankAccount, "分账"));
            //最后一笔相反操作
            hdFenList.remove(hdFenList.size() - 1);
            //记录操作信息
            //先判断是为支出还是收入
            BigDecimal bPay = hdBankPending.getPay() != null ? hdBankPending.getPay() : BigDecimal.ZERO;
            if (bPay.compareTo(BigDecimal.ZERO) == 0) {
                //支出为0即为收入
                for (HdFenListEntity e : hdFenList) {
                    HdBankAccount bankIncome = new HdBankAccount();
                    HdBankAccount bankPay = new HdBankAccount();
                    MyBeanUtils.copyBeanNotNull2Bean(hdBankPending, bankIncome);
                    MyBeanUtils.copyBeanNotNull2Bean(hdBankPending, bankPay);
                    bankIncome.setCompanyName(e.getCompanyId());
                    bankIncome.setIncome(e.getMoney());
                    bankIncome.setRid(hdBankPending.getRid());
                    bankIncome.setId(null);
                    hdBankAccountBiz.insertEntityFreeTime(bankIncome);
                    //记录操作信息
                    hdBankRecords.add(hdBankRecordBiz.getHdBankRecord(bankIncome, "分账"));
                    bankPay.setCompanyName(maxFenListEntity.getCompanyId());
                    bankPay.setIncome(new BigDecimal("0.00"));
                    bankPay.setPay(e.getMoney());
                    bankPay.setRid(hdBankPending.getRid());
                    bankPay.setId(null);
                    hdBankAccountBiz.insertEntityFreeTime(bankPay);
                    //记录操作信息
                    hdBankRecords.add(hdBankRecordBiz.getHdBankRecord(bankPay, "分账"));
                }
            } else {
                //支出不为0即为支出
                for (HdFenListEntity e : hdFenList) {
                    HdBankAccount bankIncome = new HdBankAccount();
                    HdBankAccount bankPay = new HdBankAccount();
                    MyBeanUtils.copyBeanNotNull2Bean(hdBankPending, bankIncome);
                    MyBeanUtils.copyBeanNotNull2Bean(hdBankPending, bankPay);
                    bankIncome.setCompanyName(e.getCompanyId());
                    bankIncome.setPay(e.getMoney());
                    bankIncome.setRid(hdBankPending.getRid());
                    bankIncome.setId(null);
                    hdBankAccountBiz.insertEntityFreeTime(bankIncome);
                    //记录操作信息
                    hdBankRecords.add(hdBankRecordBiz.getHdBankRecord(bankIncome, "分账"));
                    bankPay.setCompanyName(maxFenListEntity.getCompanyId());
                    bankPay.setPay(new BigDecimal("0.00"));
                    bankPay.setRid(hdBankPending.getRid());
                    bankPay.setIncome(e.getMoney());
                    bankPay.setId(null);
                    hdBankAccountBiz.insertEntityFreeTime(bankPay);
                    //记录操作信息
                    hdBankRecords.add(hdBankRecordBiz.getHdBankRecord(bankPay, "分账"));
                }
            }
            //删除原分账记录
            baseBiz.deleteById(id);
            //批量记录操作信息
            hdBankRecordBiz.batchSave(hdBankRecords);


        } catch (Exception e) {
            e.printStackTrace();
            message = "分账失败";
            return new ObjectRestResponse<>(500, message);
        }

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
    public void exportXls(String userName) throws ParseException {
        //companyName为中文名
        Map<String, Object> params = new HashedMap();
        String subjects = request.getParameter("subjects");
        String remark = request.getParameter("remark");
        String bankName = request.getParameter("bankName");
        String mySubjects = request.getParameter("mySubjects");
        if (StringUtil.isNotEmpty(subjects)) {
            params.put("subjects", subjects);
        }
        if (StringUtil.isNotEmpty(remark)) {
            params.put("remark", remark);
        }
        if (StringUtil.isNotEmpty(bankName)) {
            params.put("bankName", bankName);
        }
        if (StringUtil.isNotEmpty(mySubjects)) {
            params.put("mySubjects", mySubjects);
        }
        String tenantId = BaseContextHandler.getTenantID();
        String userId = BaseContextHandler.getUserID();
//        if(StringUtil.isNotEmpty(tenantId)){
//            params.put("TENANTID",tenantId);
//        }
        params.put("create_by",userId);
        String startTime = request.getParameter("dateStart");
        String endTime = request.getParameter("dateEnd");


        List<HdBankPending> allBankPendingList = baseBiz.getAllBankPending(startTime,
                endTime, tenantId, params);
        ExcelUtil excelUtil = new ExcelUtil<HdBankPending>();
        try {
            excelUtil.print("银行待处理记录列表", HdBankPending.class, "银行待处理记录列表", "导出人:" + userName, "银行待处理记录列表", allBankPendingList, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*********************************标准版本二次修改*********************************/
    //修改日期，无须修改编号，录入记录，同时修改摘要
    @RequestMapping(value = "/changeAccountDate", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<HdBankPending> changeAccountDate(String ids,String accountDate){
        if(StringUtil.isEmpty(ids)){
            return new ObjectRestResponse<>(1003,"参数传递失败");
        }
        if(StringUtil.isEmpty(accountDate)){
            return new ObjectRestResponse<>(1003,"参数传递失败");
        }
        Date accountDay = null;
        String remark = "";
        if (StringUtil.isNotEmpty(accountDate)) {
            try {
                accountDay = DateUtils.date_sdf.parse(accountDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return new ObjectRestResponse<>(200, "同步至某日日期格式错误");
            }
        }
        for(String id : ids.split(",")){
            HdBankPending hdBankPending = baseBiz.selectById(id);
            if(hdBankPending==null){
                return new ObjectRestResponse<>(500, "数据获取异常");
            }
            hdBankPending.setAccountDate(accountDay);
            if(StringUtil.isNotEmpty(hdBankPending.getRemark())){
                remark = hdBankPending.getRemark();
            }
            remark = remark+";九恒星记账日期:"+hdBankPending.getAccountDate();
            hdBankPending.setRemark(remark);
            baseBiz.insertSelective(hdBankPending);
            HdBankRecord hdbankRecord = hdBankRecordBiz.getHdBankRecord(hdBankPending,"待处理修改九恒星日期");
            hdBankRecordBiz.insertSelective(hdbankRecord);
        }
        return new ObjectRestResponse<>(200,"修改成功");
    }


}