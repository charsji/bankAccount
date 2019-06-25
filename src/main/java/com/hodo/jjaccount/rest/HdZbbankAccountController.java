package com.hodo.jjaccount.rest;

import com.github.ag.core.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.*;
import com.hodo.jjaccount.biz.*;
import com.hodo.jjaccount.common.TableResultResponse;
import com.hodo.jjaccount.entity.*;
import com.hodo.jjaccount.util.jjUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@RequestMapping("hdZbbankAccount")
@CheckClientToken
@CheckUserToken
public class HdZbbankAccountController extends BaseController<HdZbbankAccountBiz, HdZbbankAccount, String> {

    @Autowired
    private HdCompanyBiz hdCompanyBiz;
    @Autowired
    private HdZbbankPendingBiz hdZbbankPendingBiz;

//
//    @RequestMapping(value = "", method = RequestMethod.POST)
//    @ResponseBody
//    @ApiOperation("新增单个对象")
//    public ObjectRestResponse<HdZbbankAccount> add(@RequestBody HdZbbankAccount entity) {
//        baseBiz.insertEntity(entity);
//        return new ObjectRestResponse<HdZbbankAccount>().data(entity);
//    }


//    @ApiOperation("查询单个对象")
//    @RequestMapping(value = "/{id}")
//    public ObjectRestResponse<HdZbbankAccount> getone(@PathVariable String id){
//        ObjectRestResponse<HdZbbankAccount> entityObjectRestResponse = new ObjectRestResponse<>();
//        HdZbbankAccount entity = baseBiz.selectById(id);
//        //获取公司id,添加名称
//        String companyId = entity.getCompanyName();
//        if(StringUtil.isNotEmpty(companyId)){
//            String companyName = hdCompanyBiz.getCompanyNameById(companyId);
//            if(StringUtil.isNotEmpty(companyName)){
//                entity.setCompany_name(companyName);
//            }
//        }
//        return entityObjectRestResponse.data(entity);
//    }


//    /**
//     * 获取系统当前时间
//     * @return
//     */
//    @ApiOperation("获取系统当前时间")
//    @RequestMapping(value = "/getDate",method = RequestMethod.GET)
//    @ResponseBody
//    public ObjectRestResponse<String> doDate() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String date = sdf.format(new Date());
//        return new ObjectRestResponse<>().data(date);
//    }

//
//    @ApiOperation("修改摘要")
//    @RequestMapping(value = "/modifyRemark", method = RequestMethod.POST)
//    @ResponseBody
//    public ObjectRestResponse<HdZbbankAccount> modifyRemark(String id,String remark){
//        if(StringUtil.isNotEmpty(id)){
//            return  new ObjectRestResponse<>(1003,"id传递失败");
//        }else if (StringUtil.isNotEmpty(remark)){
//            return  new ObjectRestResponse<>(1003,"remark传递失败");
//        }
//        String message = null;
//        try {
//            HdZbbankAccount hza = baseBiz.selectById(id);
//            hza.setRemark(remark);
//            baseBiz.updateSelectiveById(hza);
//            message = "修改备注成功";
//        }catch (Exception e){
//            e.printStackTrace();
//            message = "修改备注失败";
//            return new ObjectRestResponse<>(500,message);
//        }
//        return new ObjectRestResponse<>(200,message);
//    }


    @ApiOperation(value = "分页获取数据*")
    @RequestMapping(value = "/findRecords", method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<HdZbbankAccount> page(@RequestParam @ApiParam(value = "参数名称(page,limit,dateStart,dataEnd)") Map<String, Object> params) {
        //根据公司名称判断是否展示数据
        String tenantId = BaseContextHandler.getTenantID();
        //String tenantId = "fa3421229bf1497192493d485f9b7898";
        String companyName = "红豆集团有限公司";
//       String companyName = jjUtil.handleParams(params, "company");
//        if (StringUtil.isEmpty(companyName)) {
//            return new TableResultResponse<>();
//        }

        //分页参数
        String page = jjUtil.handleParams(params, "page");
        String limit = jjUtil.handleParams(params, "limit");
        //根据条件查询所有的数据
        if (StringUtil.isEmpty(page)) {
            page = "1";
        }
        if (StringUtil.isEmpty(limit)) {
            limit = "10";
        }
        int pageInt = Integer.parseInt(page);
        int limitInt = Integer.parseInt(limit);
        //模糊查询
//        String remark = jjUtil.handleParams(params, "remark");
        //查询时间分离
        String startTime = jjUtil.handleParams(params, "dateStart");
        String endTime = jjUtil.handleParams(params, "dateEnd");
        List<HdZbbankAccount> hdZbbankAccountList = baseBiz.getBankAccountList(companyName, startTime,
                endTime, String.valueOf((pageInt - 1) * limitInt), limit, tenantId, params);
        //重新计算结存金额
        if (hdZbbankAccountList != null && hdZbbankAccountList.size() > 0) {
            try {
                hdZbbankAccountList = baseBiz.calBalance(hdZbbankAccountList, companyName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //获取符合条件的银行账户总数
        List<HdZbbankAccount> allBankAccountList = baseBiz.getAllBankAccount(companyName, startTime,
                endTime, tenantId, params);
        Integer totalSize = 0;
        BigDecimal sumIncome = BigDecimal.ZERO;
        BigDecimal sumPay = BigDecimal.ZERO;
        if (allBankAccountList != null && allBankAccountList.size() > 0) {
            for (HdZbbankAccount hzba : allBankAccountList) {
                sumIncome = sumIncome.add(hzba.getIncome());
                sumPay = sumPay.add(hzba.getPay());
            }
            //银行账户总数
            totalSize = allBankAccountList.size();
        }
        for (HdZbbankAccount hzba : hdZbbankAccountList) {
            hzba.setCompanyName("红豆集团有限公司");
        }
        return new TableResultResponse<>(totalSize, hdZbbankAccountList, sumIncome, sumPay);
    }


    @ApiOperation("同步功能*")
    @ResponseBody
    @RequestMapping(value = "/synData", method = RequestMethod.GET)
    public ObjectRestResponse<Map<String, Object>> synData(String begin_date, String end_date) {
        System.out.println("同步开始");
        Integer saveCountBefore = baseBiz.getSaveCount();
        Map<String, Object> result = new HashedMap();
        List<HdZbbankAccount> accountList = new ArrayList<>();
        List<HdZbbankPending> pendingList = new ArrayList<>();
        //查询本地所有的sheetid,查询所有的rid
        List<String> sheetIdInBankAccount = baseBiz.getSheetIdsInZBBankAccount() != null ? baseBiz.getSheetIdsInZBBankAccount() : new ArrayList<>();
        System.out.println(sheetIdInBankAccount.size() + "aaaa");
        List<String> rIdInBankAccount = baseBiz.getRIdsInZBBankAccount() != null ? baseBiz.getRIdsInZBBankAccount() : new ArrayList<>();
        System.out.println(rIdInBankAccount.size() + "bbbb");
        //补充5.22待处理中出现就不同步
        List<String> sheetIdInBankPend = baseBiz.getSheetIdsInBankPend() != null ? baseBiz.getSheetIdsInBankPend() :
                new ArrayList<>();
        List<String> rIdInBankPend = baseBiz.getRIdsInBankPend() != null ? baseBiz.getRIdsInBankPend() :
                new ArrayList<>();
        //查询内转收入记录
        baseBiz.synNZIncome(accountList, pendingList, sheetIdInBankAccount, sheetIdInBankPend, begin_date, end_date);
        System.out.println(accountList.size() + "11111");
        //查询内转支出记录
        baseBiz.synNZPay(accountList, pendingList, sheetIdInBankAccount, sheetIdInBankPend, begin_date, end_date);
        System.out.println(accountList.size() + "22222");
        //查询外转记录
        baseBiz.synWZ(accountList, pendingList, rIdInBankAccount, rIdInBankPend, begin_date, end_date);
        System.out.println(accountList.size() + "33333");
        int synCount = 0;
        System.out.println("账单保存开始");
        if (accountList != null && accountList.size() > 0) {
            synCount = accountList.size();
            //重新封装加序号
            //按时间排序
            Collections.sort(accountList, new Comparator<HdZbbankAccount>() {
                @Override
                public int compare(HdZbbankAccount o1, HdZbbankAccount o2) {
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
            //重新封装accountlist（加上序号NO）
            baseBiz.handBankAccountList(accountList);
            //批量保存数据
            baseBiz.batchSave(accountList);
        }
        if (pendingList != null && pendingList.size() > 0) {
            for (HdZbbankPending hdZbbankPending : pendingList) {
                EntityUtils.setCreatAndUpdatInfo(hdZbbankPending);
            }
            System.out.println(pendingList.size() + "!");
            //批量保存待处理
            hdZbbankPendingBiz.batchSave(pendingList);
        }

        Integer saveCountAfter = baseBiz.getSaveCount();
        System.out.println("执行到这里---------！！！！---------");
        Integer saveCount = saveCountAfter - saveCountBefore;
        if (synCount == 0) {
            saveCount = 0;
        }
        result.put("synCount", synCount);
        result.put("save", saveCount);
        System.out.println("同步结束");
        return new ObjectRestResponse<>(200, "同步成功").data(result);
    }


    /**
     * 导出excel
     *
     * @throws ParseException
     */
    @ApiOperation("导出选择日期内数据表*")
    @RequestMapping(value = "/exportXls", method = RequestMethod.GET)
    @ResponseBody
//    @IgnoreUserToken
//    @IgnoreClientToken
    public void exportXls(String userName) throws ParseException {
        //companyName为中文名
        String companyName = request.getParameter("company");
        String startTime = request.getParameter("dateStart");
        String endTime = request.getParameter("dateEnd");
        String tenantId = BaseContextHandler.getTenantID();
        //String tenantId = "fa3421229bf1497192493d485f9b7898";
        String remark = request.getParameter("remark");
        Map<String, Object> params = new HashedMap();
        if (StringUtil.isNotEmpty(remark)) {
            params.put("remark", remark);
        }
        List<HdZbbankAccount> allBankAccountList = baseBiz.getAllBankAccount(companyName, startTime, endTime, tenantId, params);
        //计算结存金额
        if (allBankAccountList != null && allBankAccountList.size() > 0) {
            try {
                allBankAccountList = baseBiz.calBalance(allBankAccountList, companyName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ExcelUtil excelUtil = new ExcelUtil<HdZbbankAccount>();
        try {
            excelUtil.print("银行账单记录列表", HdZbbankAccount.class, "银行账单记录列表", "导出人:" + userName, "银行账单记录列表", allBankAccountList, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @ApiOperation("导出年月合计数据表*")
    @RequestMapping(value = "/exportXlsAccount", method = RequestMethod.GET)
    @ResponseBody
    public void exportXlsAccount(String userName, String year) throws ParseException {
        String tenantId = BaseContextHandler.getTenantID();
        //计算年月合计
        year = baseBiz.handleMIAPDate(year);
        List<MonInAndPay> miapList = baseBiz.geteveryMonthPayAndIncome(tenantId, year);
        baseBiz.handleMIAP(miapList);
        //导出表格
        ExcelUtil excelUtil = new ExcelUtil<HdZbbankAccount>();
        try {

            excelUtil.print(year + "年银行账单年月合计表", MonInAndPay.class, "银行账单年月合计表", "导出人:" + userName, "银行账单年月合计表", miapList, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}