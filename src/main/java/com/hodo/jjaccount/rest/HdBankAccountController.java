package com.hodo.jjaccount.rest;

import com.github.ag.core.context.BaseContextHandler;
import com.github.wxiaoqi.security.auth.client.annotation.IgnoreClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.IgnoreUserToken;
import com.github.wxiaoqi.security.common.exception.base.BusinessException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;

import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.*;
import com.hodo.jjaccount.biz.HdBankAccountBiz;
import com.hodo.jjaccount.biz.HdBankPendingBiz;
import com.hodo.jjaccount.biz.HdBankRecordBiz;
import com.hodo.jjaccount.biz.HdCompanyBiz;
import com.hodo.jjaccount.common.TableResultResponse;
import com.hodo.jjaccount.entity.HdBankAccount;
import com.hodo.jjaccount.entity.HdBankPending;
import com.hodo.jjaccount.entity.HdBankRecord;
import com.hodo.jjaccount.entity.Statistics;
import com.hodo.jjaccount.util.jjUtil;
import io.swagger.annotations.*;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.wxiaoqi.security.auth.client.annotation.CheckClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.CheckUserToken;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("hdBankAccount")
//@CheckClientToken
//@CheckUserToken
public class HdBankAccountController extends BaseController<HdBankAccountBiz, HdBankAccount, String> {
    @Autowired
    private HdCompanyBiz hdCompanyBiz;
    @Autowired
    private HdBankPendingBiz hdBankPendingBiz;

    @Autowired
    private HdBankRecordBiz hdBankRecordBiz;

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("新增单个对象")
    public ObjectRestResponse<HdBankAccount> add(@RequestBody HdBankAccount entity) {
        baseBiz.insertEntity(entity);
        return new ObjectRestResponse<HdBankAccount>().data(entity);
    }

    //重写添加公司名称字段
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("查询单个对象")
    public ObjectRestResponse<HdBankAccount> get(@PathVariable String id) {
        ObjectRestResponse<HdBankAccount> entityObjectRestResponse = new ObjectRestResponse<>();
        HdBankAccount entity = baseBiz.selectById(id);
        //获取公司id,添加名称
        String companyId = entity.getCompanyName();
        if (StringUtil.isNotEmpty(companyId)) {
            String companyName = hdCompanyBiz.getCompanyNameById(companyId);
            if (StringUtil.isNotEmpty(companyName)) {
                entity.setCompany_name(companyName);
            }
        }
        entityObjectRestResponse.data(entity);
        return entityObjectRestResponse;
    }

//    @ApiOperation(value = "分页获取数据2")
//    @RequestMapping(value = "/findRecords", method = RequestMethod.GET)
//    @ResponseBody
//    public TableResultResponse<HdBankAccount> page(@RequestParam @ApiParam(value = "参数名称(company,page,limit,remark," +
//            "dateStart,dataEnd)") Map<String, Object> params) {
//        //根据公司名称判断是否展示数据
//        String tenantId = BaseContextHandler.getTenantID();
////        if(StringUtil.isNotEmpty(tenantId)){
////            params.put("TENANTID",tenantId);
////        }
//
//        long total = 0;
//        String companyName = jjUtil.handleParams(params, "company");
//        if (StringUtil.isEmpty(companyName)) {
//            return new TableResultResponse<>();
//        }
//        //分页参数
//        String page = jjUtil.handleParams(params, "page");
//        String limit = jjUtil.handleParams(params, "limit");
//        //根据条件查询所有的数据
//        if (StringUtil.isEmpty(page)) {
//            page = "1";
//        }
//        if (StringUtil.isEmpty(limit)) {
//            limit = "10";
//        }
//        int pageInt = Integer.parseInt(page);
//        int limitInt = Integer.parseInt(limit);
//        //模糊查询
//        String remark = jjUtil.handleParams(params, "remark");
//        //查询时间分离
//        String startTime = jjUtil.handleParams(params, "dateStart");
//        String endTime = jjUtil.handleParams(params, "dateEnd");
//        List<HdBankAccount> hdBankAccountList = baseBiz.getBankAccountList(companyName, remark, startTime,
//                endTime, String.valueOf((pageInt - 1) * limitInt), limit, tenantId, params);
//        //重新计算结存金额
//        if (hdBankAccountList != null && hdBankAccountList.size() > 0) {
//            try {
//                hdBankAccountList = baseBiz.calBalance(hdBankAccountList, companyName);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        List<HdBankAccount> allBankAccountList = baseBiz.getAllBankAccount(companyName, remark, startTime,
//                endTime, tenantId, params);
//        BigDecimal sumIncome = BigDecimal.ZERO;
//        BigDecimal sumPay = BigDecimal.ZERO;
//        if (allBankAccountList != null && allBankAccountList.size() > 0) {
//            for (HdBankAccount hdBankAccount : allBankAccountList) {
//                sumIncome = sumIncome.add(hdBankAccount.getIncome());
//                sumPay = sumPay.add(hdBankAccount.getPay());
//            }
//            total = allBankAccountList.size();
//        }
//
//        return new TableResultResponse<HdBankAccount>(total, hdBankAccountList, sumIncome, sumPay);
//    }

    //重写分页用sql
    //添加录入人条件6.24
    @ApiOperation(value = "分页获取数据2")
    @RequestMapping(value = "/findRecords", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<HdBankAccount> page(@RequestParam @ApiParam(value = "参数名称(company,page,limit,remark," +
            "dateStart,dataEnd)") Map<String, Object> params) {
        long total = 0;
        String tenantId = BaseContextHandler.getTenantID();
        //tenantId = "aad85cb03e9f42a89d5641fe672d5c42";
        //增加录入人查询条件
        String userId = BaseContextHandler.getUserID();
        params.put("create_by",userId);
        String page = jjUtil.handleParams(params, "page");
        String limit = jjUtil.handleParams(params, "limit");
        String companyName = jjUtil.handleParams(params, "company");
        if (StringUtil.isNotEmpty(page)) {
            page = "1";
        }
        if (StringUtil.isNotEmpty(limit)) {
            limit = "10";
        }
        String startTime = jjUtil.handleParams(params, "dateStart");
        String endTime = jjUtil.handleParams(params, "dateEnd");
        int pageInt = Integer.parseInt(page);
        int limitInt = Integer.parseInt(limit);
        List<HdBankAccount> hdBankAccountList = baseBiz.getBankAccountListBySql(
                startTime, endTime, String.valueOf((pageInt - 1) * limitInt), limit, params, companyName, tenantId
        );
        List<HdBankAccount> allBankAccountList = baseBiz.getAllBankAccountBySql(startTime,
                endTime, params, companyName, tenantId);
        BigDecimal sumIncome = BigDecimal.ZERO;
        BigDecimal sumPay = BigDecimal.ZERO;
        if (allBankAccountList != null && allBankAccountList.size() > 0) {
            for (HdBankAccount hdBankAccount : allBankAccountList) {
                sumIncome = sumIncome.add(hdBankAccount.getIncome());
                sumPay = sumPay.add(hdBankAccount.getPay());
            }
            total = allBankAccountList.size();
        }
        return new TableResultResponse<HdBankAccount>(total, hdBankAccountList, sumIncome, sumPay);
    }

    /**
     * 银行财务冲账
     */
    @ApiOperation("银行财务冲账")
    @RequestMapping(value = "/doBatchCZ", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<HdBankAccount> doBatchCZ(String ids) {
        String message = null;
        message = "银行账务信息冲账成功";
        int count = 0;
        try {
            for (String id : ids.split(",")) {
                HdBankAccount hdBankAccount = baseBiz.selectById(id);
                HdBankAccount t = new HdBankAccount();
                MyBeanUtils.copyBeanNotNull2Bean(hdBankAccount, t);
                t.setId(UUIDUtils.generateUuid());
                t.setIncome(hdBankAccount.getIncome().multiply(new BigDecimal(-1)));
                t.setPay(hdBankAccount.getPay().multiply(new BigDecimal(-1)));

                //String company = hdCompanyBiz.getCompanyNameById(hdBankAccount.getCompanyName());
//                String sql = "SELECT COMPANY_NAME from HD_COMPANY WHERE id = '" + hdBankAccount.getCompanyName() + "'";
//                List<?> list = hdBankAccountService.findListbySql(sql);
//                String company = String.valueOf(list.get(0));
                //t.setCompanyName(company);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                t.setAccountDate(sdf.parse(sdf.format(new Date())));
                t.setRemark("用于" + sdf.format(hdBankAccount.getAccountDate()) + ",凭证编号为：" + hdBankAccount.getNo() + "的账务冲账！");
                baseBiz.insertEntity(t);
                count++;

            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "银行账务信息冲账失败";
            return new ObjectRestResponse<>(500, message);
        }
        message = "您成功冲账" + count + "条记录！";
        return new ObjectRestResponse<>(200, message);
    }

    //查询所有的摘要
    //添加录入人条件6.24
    @ApiOperation("查询所有的摘要")
    @RequestMapping(value = "/doSearch", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<String> doSearch() {
        String str = baseBiz.Search();
        return new ObjectRestResponse<>(200, "获取成功").data(str);
    }

    /**
     * 获取系统当前时间
     *
     * @return
     */
    @ApiOperation("获取系统当前时间")
    @RequestMapping(value = "/doDate", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<String> doDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前时间
        String day = sdf.format(new Date());
        return new ObjectRestResponse<>().data(day);
    }

    /**
     * 批量删除银行账务信息表
     *
     * @return
     */
    @ApiOperation("批量删除银行账务信息表")
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
            message = "银行账务信息表删除失败";
            return new ObjectRestResponse<>(500, message);
        }
        message = "您成功删除" + count + "条";
        return new ObjectRestResponse<>(200, message);
    }


    /**
     * 导出excel
     *
     * @throws ParseException
     */
    //添加录入人条件6.24
    @ApiOperation("导出")
    @RequestMapping(value = "/exportXls", method = RequestMethod.GET)
    @ResponseBody
//    @IgnoreUserToken
//    @IgnoreClientToken
    public void exportXls(String userName) throws ParseException {
        //companyName为中文名
        String companyName = request.getParameter("company");
        String remark = request.getParameter("remark");
        String startTime = request.getParameter("dateStart");
        String endTime = request.getParameter("dateEnd");
        String tenantId = BaseContextHandler.getTenantID();
        //tenantId = "aad85cb03e9f42a89d5641fe672d5c42";
        String userId = BaseContextHandler.getUserID();
        Map<String, Object> params = new HashedMap();
//        if(StringUtil.isNotEmpty(companyName)){
//            params.put("COMPANY_NAME",companyName);
//        }
        params.put("create_by",userId);
        if (StringUtil.isNotEmpty(remark)) {
            params.put("remark", remark);
        }
//        if(StringUtil.isNotEmpty(tenantId)){
//            params.put("TENANTID",tenantId);
//        }
        List<HdBankAccount> allBankAccountList = baseBiz.getAllBankAccountBySql(startTime,
                endTime, params, companyName, tenantId);
        //计算结存金额
//        if (allBankAccountList != null && allBankAccountList.size() > 0) {
//            try {
//                allBankAccountList = baseBiz.calBalance(allBankAccountList, companyName);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        ExcelUtil excelUtil = new ExcelUtil<HdBankAccount>();
        try {
            excelUtil.print("银行账单记录列表", HdBankAccount.class, "银行账单记录列表", "导出人:" + userName, "银行账单记录列表", allBankAccountList, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 修改摘要
     */
    @ApiOperation("修改摘要")
    @RequestMapping(value = "/updateRemark", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<HdBankAccount> updateRemark(String id, String remark) {
        if (StringUtil.isEmpty(id)) {
            return new ObjectRestResponse<>(1003, "参数传递失败");
        }
        if (StringUtil.isEmpty(remark)) {
            return new ObjectRestResponse<>(1003, "参数传递失败");
        }
        String message = null;
        message = "修改备注成功";
        try {
            HdBankAccount hdBankAccount = baseBiz.selectById(id);
            hdBankAccount.setRemark(remark);
            baseBiz.updateSelectiveById(hdBankAccount);
        } catch (Exception e) {
            e.printStackTrace();
            message = "修改备注失败";
            return new ObjectRestResponse<>(500, message);
        }

        return new ObjectRestResponse<>(200, message);
    }


    //添加录入人条件6.24
    /**
     * 同步功能
     */
    @RequestMapping(value = "/synData", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<Map<String, Object>> synData(String query_begin,
                                                           String query_end, String query_to) {
        Integer saveCountBefore = baseBiz.getSaveCount(query_begin, query_end);
        Map<String, Object> result = new HashedMap();
        Date accountDay = null;
        if (StringUtil.isNotEmpty(query_to)) {
            try {
                accountDay = DateUtils.date_sdf.parse(query_to);
            } catch (ParseException e) {
                e.printStackTrace();
                return new ObjectRestResponse<>(200, "同步至某日日期格式错误");
            }
        }
        List<HdBankAccount> accountList = new ArrayList<>();
        List<HdBankPending> pendingList = new ArrayList<>();
        //查询本地所有的sheetid,查询所有的rid
        List<String> sheetIdInBankAccount = baseBiz.getSheetIdsInBankAccount() != null ? baseBiz.getSheetIdsInBankAccount() :
                new ArrayList<>();
        List<String> sheetIdInBankPend = baseBiz.getSheetIdsInBankPend() != null ? baseBiz.getSheetIdsInBankPend() :
                new ArrayList<>();
        List<String> rIdInBankAccount = baseBiz.getRIdsInBankAccount() != null ? baseBiz.getRIdsInBankAccount() :
                new ArrayList<>();
        List<String> rIdInBankPend = baseBiz.getRIdsInBankPend() != null ? baseBiz.getRIdsInBankPend() :
                new ArrayList<>();
        baseBiz.synNZData(accountList, pendingList, sheetIdInBankAccount,
                sheetIdInBankPend, query_begin, query_end);
        //一笔生成两笔需要相加除以2
        Integer due = (accountList.size() + pendingList.size()) / 2;
        System.out.println(accountList.size() + "------互转" + pendingList.size());
        baseBiz.synNZIncome(accountList, pendingList, sheetIdInBankAccount,
                sheetIdInBankPend, query_begin, query_end);
        System.out.println(accountList.size() + "------内转外" + pendingList.size());
        baseBiz.synNZPay(accountList, pendingList, sheetIdInBankAccount,
                sheetIdInBankPend, query_begin, query_end);
        System.out.println(accountList.size() + "------外转内" + pendingList.size());

        baseBiz.synWZ(accountList, pendingList, rIdInBankAccount,
                rIdInBankPend, query_begin, query_end);
        System.out.println(accountList.size() + "------外转" + pendingList.size());

        //System.out.println(accountList.size()+"------4"+pendingList.size());
        //int saveCount=0;
        int synCount = 0;
        System.out.println("賬單保存開始");
        if (accountList != null && accountList.size() > 0) {
            synCount = accountList.size();
            //重新封裝賬單加序號
            //按照時間排個序
            Collections.sort(accountList, new Comparator<HdBankAccount>() {
                @Override
                public int compare(HdBankAccount o1, HdBankAccount o2) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date dt1 = o1.getAccountDate();
                        Date dt2 = o2.getAccountDate();
                        if (dt1.getTime() > dt2.getTime()) {
                            return 1;
                        } else if (dt1.getTime() < dt2.getTime()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
            baseBiz.handBankAccountList(accountList, accountDay);
            baseBiz.batchSave(accountList);

//            for(HdBankAccount hdBankAccount:accountList){
//                try{
//                    if(accountDay!=null){
//                        hdBankAccount.setAccountDate(accountDay);
//                    }
//                    baseBiz.insertEntity(hdBankAccount);
//                    saveCount++;
//                }catch (Exception e){
//                    System.out.println("錯誤信息"+e.getMessage());
//                    e.printStackTrace();
//                    continue;
//                }
//            }
        }
        //System.out.println(synCount+"-----------"+saveCount+"待處理保存開始:");
        if (pendingList != null && pendingList.size() > 0) {
            synCount += pendingList.size();
            for (HdBankPending hdBankPending : pendingList) {
//                try{
                //添加用户
                String userId = BaseContextHandler.getUserID();
                hdBankPending.setCreateBy(userId);
                if (accountDay != null) {
                    hdBankPending.setAccountDate(accountDay);
                }
                EntityUtils.setCreatAndUpdatInfo(hdBankPending);
                //hdBankPendingBiz.insertSelective(hdBankPending);
                //saveCount++;
//                }catch (Exception e){
//                    System.out.println("待處理錯誤信息"+e.getMessage());
//                    e.printStackTrace();
//                    continue;
//                }
            }
            hdBankPendingBiz.batchSave(pendingList);
        }
//        return new ObjectRestResponse<>(200,"总获取数量"+num+
//                "同步成功数量"+successNum+"用时:"+(end-start)+"毫秒");
        //同步后
        Integer saveCountAfter = baseBiz.getSaveCount(query_begin, query_end);
        System.out.println(saveCountAfter + "@@@@@@@@@@" + synCount);
        //实际同步数量
        Integer saveCount = saveCountAfter - due - saveCountBefore;
        if (synCount == 0) {
            saveCount = 0;
        }
        result.put("synCount", synCount - due);
        result.put("save", saveCount);
        return new ObjectRestResponse<>(200, "同步成功").data(result);
    }

    @RequestMapping(value = "/getStatisticsList", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<Statistics> getStatisticsList(String dateStart,
                                                             String dateEnd) {
        long total = 0;
        String tenantId = BaseContextHandler.getTenantID();
        String userId = BaseContextHandler.getUserID();
        if (StringUtil.isEmpty(dateStart)) {
            return new TableResultResponse<Statistics>(total, new ArrayList<Statistics>(), BigDecimal.ZERO, BigDecimal.ZERO);
        }
        if (StringUtil.isEmpty(dateEnd)) {
            return new TableResultResponse<Statistics>(total, new ArrayList<Statistics>(), BigDecimal.ZERO, BigDecimal.ZERO);
        }
        BigDecimal sumIncome = BigDecimal.ZERO;
        BigDecimal sumPay = BigDecimal.ZERO;
        List<Statistics> statisticsList = baseBiz.statics(dateStart, dateEnd, tenantId,userId);
        return new TableResultResponse<Statistics>(total, statisticsList, sumIncome, sumPay);
    }

    /*********************************标准版本二次修改*********************************/
    //修改账单日期
    @RequestMapping(value = "/changeAccountDate", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<HdBankAccount> changeAccountDate(String ids,String accountDate){
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
            HdBankAccount hdBankAccount = baseBiz.selectById(id);
            //修改记账日期，同时修改摘要，修改编号，记录操作日志
            if(hdBankAccount==null){
                return new ObjectRestResponse<>(500, "数据获取异常");
            }
            hdBankAccount.setAccountDate(accountDay);
            if(StringUtil.isNotEmpty(hdBankAccount.getRemark())){
                remark = hdBankAccount.getRemark();
            }
            remark = remark+";九恒星记账日期:"+hdBankAccount.getAccountDate();
            hdBankAccount.setRemark(remark);
            baseBiz.insertEntity(hdBankAccount);
            HdBankRecord hdbankRecord = hdBankRecordBiz.getHdBankRecord(hdBankAccount,"账单修改九恒星日期");
            hdBankRecordBiz.insertSelective(hdbankRecord);
        }
        return new ObjectRestResponse<>(200,"修改成功");
    }

    //账单还原，只能单个还原，无法判断，银行和摘要无法匹配，如果是九恒星同步的可以还原吗，还原回去要一模一样吗
    //除非吧摘要存起来，对方科目，我方科目，公司的关系
}