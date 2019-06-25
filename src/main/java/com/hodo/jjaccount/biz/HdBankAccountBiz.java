package com.hodo.jjaccount.biz;

import com.github.ag.core.context.BaseContextHandler;
import com.github.wxiaoqi.security.admin.client.entity.SimpleDepart;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.MyBeanUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.UUIDUtils;
import com.hodo.jjaccount.entity.*;
import com.hodo.jjaccount.mapper.HdBankAccountMapper;
import com.hodo.jjaccount.util.DbUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.wxiaoqi.security.common.biz.BusinessBiz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:29:23
 * @email 463540703@qq.com
 */
@Service
public class HdBankAccountBiz extends BusinessBiz<HdBankAccountMapper, HdBankAccount> {
    @Autowired
    private HdCompanyBiz hdCompanyBiz;
    @Autowired
    private HdNzDictBiz hdNzDictBiz;
    @Autowired
    private HdMatchCompanyBiz hdMatchCompanyBiz;

//    public List<HdBankAccount> getBankAccountList(String companyName,
//                                                  String remark, String startTime,
//                                                  String endTime, String page, String limit, String tenantId,
//                                                  Map<String, Object> params) {
//        return this.mapper.getBankAccountList(companyName, remark, startTime,
//                endTime, page, limit, tenantId, params);
//    }

    public List<HdBankAccount> getBankAccountListBySql(String startTime,
                                                       String endTime, String page, String limit,
                                                       Map<String, Object> params, String companayName,
                                                       String tenantId) {
        return this.mapper.getBankAccountListBySql(startTime,
                endTime, page, limit, params, companayName, tenantId);
    }

    //    public List<HdBankAccount> getAllBankAccount(String companyName,
//                                                 String remark, String startTime,
//                                                 String endTime, String tenantId,
//                                                 Map<String, Object> params) {
//        return this.mapper.getAllBankAccount(companyName, remark, startTime,
//                endTime, tenantId, params);
//    }
    public List<HdBankAccount> getAllBankAccountBySql(String startTime, String endTime,
                                                      Map<String, Object> params, String companyName,
                                                      String tenantId) {
        return this.mapper.getAllBankAccountBySql(startTime,
                endTime, params, companyName, tenantId);
    }


    public List<HdBankAccount> calBalance(List<HdBankAccount> banks, String companyName) throws Exception {
        List<HdBankAccount> lists = new ArrayList<HdBankAccount>();
//        String sqlBalance = "";
//        String sqlIncomAndPay = "";
//        String sql = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < banks.size(); i++) {
            String date = sdf.format(banks.get(i).getAccountDate());
            // 拷贝实体类
            HdBankAccount bank = new HdBankAccount();
            MyBeanUtils.copyBeanNotNull2Bean(banks.get(i), bank);
            // 根据公司查结存金额
//            sqlBalance = "SELECT BALANCE FROM HD_BANK_ACCOUNT WHERE COMPANY_NAME = '" + companyName + "' and "
//                    + "ACCOUNT_DATE < to_date('2017-01-01','yyyy-MM-dd') and income=0 and pay =0 ORDER BY ACCOUNT_DATE ASC";
            //List<?> balanceList = commonDao.findListbySql(sqlBalance);
            String balanceStr = this.mapper.selBalanceBysql(companyName, BaseContextHandler.getTenantID());
            BigDecimal balance = BigDecimal.ZERO;
            if (StringUtil.isNotEmpty(balanceStr)) {
                balance = new BigDecimal(balanceStr);
            }
            //System.out.println(balanceStr+"期初");
            // 计算在此条日期前的总收入和总支出
//            sqlIncomAndPay = "select nvl(sum(income),0.00),nvl(sum(pay),0.00)  from HD_BANK_ACCOUNT where "
//                    + "COMPANY_NAME = '" + companyName + "' AND ACCOUNT_DATE<to_date('" + date + "','yyyy-MM-dd')";
            //List<?> incomeAndPayList = commonDao.findListbySql(sqlIncomAndPay);
            IncomeAndPay incomeAndPay = this.mapper.selIncomeAndPayBysql(companyName, date, BaseContextHandler.getTenantID());
            BigDecimal income = BigDecimal.ZERO;
            BigDecimal pay = BigDecimal.ZERO;
            if (incomeAndPay != null) {
                income = new BigDecimal(incomeAndPay.getIncomes());
                pay = new BigDecimal(incomeAndPay.getPays());
                //System.out.println(incomeAndPay.getIncomes()+"期前"+incomeAndPay.getPays());
            }

            // 找到这个日期里到这条凭证编号之前的总收入和总支出
//            sql = "select nvl(sum(income),0.00),nvl(sum(pay),0.00) from HD_BANK_ACCOUNT where " + "COMPANY_NAME = '"
//                    + companyName + "' AND ACCOUNT_DATE=to_date('" + date + "','yyyy-MM-dd') AND" + " no <="
//                    + banks.get(i).getNo() + " ORDER BY no";
            //List<?> list = commonDao.findListbySql(sql);
            IncomeAndPay incomeAndPayOnNo = this.mapper.selIncomeAndPayOnNoBysql(companyName, BaseContextHandler.getTenantID(),
                    date, banks.get(i).getNo().toString());

            BigDecimal newincome = BigDecimal.ZERO;
            BigDecimal newpay = BigDecimal.ZERO;
            if (incomeAndPayOnNo != null) {
                newincome = new BigDecimal(incomeAndPayOnNo.getIncomes());
                newpay = new BigDecimal(incomeAndPayOnNo.getPays());
                //System.out.println(incomeAndPayOnNo.getIncomes()+"期中"+incomeAndPayOnNo.getPays());
            }

            // 计算结存金额
            BigDecimal Balance1 = (income.subtract(pay)).add(balance);
            BigDecimal finalBalance = (Balance1.add(newincome)).subtract(newpay);
            // System.out.println(finalBalance);
            bank.setBalance(finalBalance);
            //公司id转成公司名称
            String companyId = bank.getCompanyName();
            if (StringUtil.isNotEmpty(companyId)) {
                String company_name = hdCompanyBiz.getCompanyNameById(companyId);
                if (StringUtil.isNotEmpty(company_name)) {
                    bank.setCompanyName(company_name);
                }
            }
            lists.add(bank);
        }
        return lists;
    }

    public String Search() {
        //String sql = "SELECT DISTINCT REMARK from HD_BANK_ACCOUNT WHERE REMARK IS NOT NULL";
        List<String> list = this.mapper.searchRemark(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
        String str = "";
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                str += (String) list.get(i) + ",";
            }
        }
        return str;
    }

    //重新新增增加序号,在从当前时间获取最大值
    public void insertEntity(HdBankAccount entity) {
        EntityUtils.setCreatAndUpdatInfo(entity);
        BigDecimal income = entity.getIncome();
        BigDecimal pay = entity.getPay();
        if (income == null || "".equals(income)) {
            entity.setIncome(BigDecimal.ZERO);
        }
        if (pay == null || "".equals(pay)) {
            entity.setPay(BigDecimal.ZERO);
        }
        getNoMaxCurrentTime(entity);
        super.insertSelective(entity);
    }

    //封裝handBankAccountList
    public void handBankAccountList(List<HdBankAccount> accountList, Date accountDay) {
        Map<String, Map<Integer, Object>> mapResult = new HashedMap();
        for (HdBankAccount hdBankAccount : accountList) {
            try {
                //添加用户
                String userId = BaseContextHandler.getUserID();
                hdBankAccount.setCreateBy(userId);
                if (accountDay != null) {
                    hdBankAccount.setAccountDate(accountDay);
                }
                EntityUtils.setCreatAndUpdatInfo(hdBankAccount);
                BigDecimal income = hdBankAccount.getIncome();
                BigDecimal pay = hdBankAccount.getPay();
                if (income == null || "".equals(income)) {
                    hdBankAccount.setIncome(BigDecimal.ZERO);
                }
                if (pay == null || "".equals(pay)) {
                    hdBankAccount.setPay(BigDecimal.ZERO);
                }
                //逐个添加No
                if (mapResult.size() > 0) {
                    System.out.println(hdBankAccount.getCompanyName());

                    if (mapResult.keySet().contains(hdBankAccount.getCompanyName())) {
                        //如果包含该公司在最大基础上+1
                        //获取最大的key

                        Set<Integer> numSet = mapResult.get(hdBankAccount.getCompanyName()).keySet();
                        Integer maxNum = Collections.max(numSet) + 1;
                        hdBankAccount.setNo(maxNum);
                        mapResult.get(hdBankAccount.getCompanyName()).put(maxNum, hdBankAccount);
                    } else {

                        Map<Integer, Object> valueResult = new HashedMap();
                        //获取最大值
                        getNoMaxCurrentTime(hdBankAccount);
                        valueResult.put(hdBankAccount.getNo(), hdBankAccount);
                        mapResult.put(hdBankAccount.getCompanyName(), valueResult);
                    }
                } else {

                    Map<Integer, Object> valueResult = new HashedMap();
                    //获取最大值
                    getNoMaxCurrentTime(hdBankAccount);
                    valueResult.put(hdBankAccount.getNo(), hdBankAccount);
                    mapResult.put(hdBankAccount.getCompanyName(), valueResult);
                }
            } catch (Exception e) {
                System.out.println("錯誤信息" + e.getMessage());
                e.printStackTrace();
                continue;
            }
        }
    }

    //批量增加
    public void batchSave(List<HdBankAccount> accountList) {
        while (accountList.size() > 1000) {
            List<HdBankAccount> hdBankRecordList = accountList.subList(0, 999);
            this.mapper.addList(hdBankRecordList);
            accountList.removeAll(hdBankRecordList);
        }
        this.mapper.addList(accountList);
    }

    //重新新增增加序号,从所选时间获取最大值
    public void insertEntityFreeTime(HdBankAccount entity) {
        EntityUtils.setCreatAndUpdatInfo(entity);
        BigDecimal income = entity.getIncome();
        BigDecimal pay = entity.getPay();
        if (income == null || "".equals(income)) {
            entity.setIncome(BigDecimal.ZERO);
        }
        if (pay == null || "".equals(pay)) {
            entity.setPay(BigDecimal.ZERO);
        }
        getNoMaxFreeTime(entity);
        super.insertSelective(entity);
    }


    public synchronized void getNoMaxFreeTime(HdBankAccount entity) {
        Integer maxNo = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int No = 1;
        if (StringUtil.isNotEmpty(entity.getCompanyName())) {
            //如果无法获取时间以当前时间获取最大值
            String accountDateStr = sdf.format(entity.getAccountDate());
            if (entity.getAccountDate() != null) {
                maxNo = this.mapper.selectMaxNoFreeTime(entity.getCompanyName(), BaseContextHandler.getTenantID(),
                        accountDateStr);
            } else {
                maxNo = this.mapper.selectMaxNo(entity.getCompanyName(), BaseContextHandler.getTenantID());
            }

        }
        if (maxNo != null) {
            No = maxNo + 1;
        }
        entity.setNo(No);
    }

    /**
     * 获取hd_account表中最大编号用于比较插入
     *
     * @return
     */
    public synchronized void getNoMaxCurrentTime(HdBankAccount entity) {
        Integer maxNo = null;
        int No = 1;
        if (StringUtil.isNotEmpty(entity.getCompanyName())) {
            maxNo = this.mapper.selectMaxNo(entity.getCompanyName(), BaseContextHandler.getTenantID());
        }
        if (maxNo != null) {
            No = maxNo + 1;
        }
        entity.setNo(No);
    }

    /**
     * 封装文件名
     *
     * @param filename
     * @return
     */
    public String getFilename(String filename) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(new Date());
        return filename + "&" + dateString;
    }

    public List<String> getSheetIdsInBankAccount() {
        return this.mapper.getSheetIdsInBankAccount(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
    }

    public List<String> getSheetIdsInBankPend() {
        return this.mapper.getSheetIdsInBankPend(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
    }

    public List<String> getRIdsInBankPend() {
        return this.mapper.getRIdsInBankPend(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
    }

    public List<String> getRIdsInBankAccount() {
        return this.mapper.getRIdsInBankAccount(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
    }

    //内部互转同步
    public Integer synNZData(List<HdBankAccount> accountList,
                             List<HdBankPending> pendingList,
                             List<String> sheetIdInBankAccount,
                             List<String> sheetIdInBankPend,
                             String query_begin, String query_end) {
        int num = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rsn = null;
        try {
            conn = DbUtil.getConnection();
            //查询所有内部账单号然后拼接查询条件
            String sql = sqlBuild(sqlBuildParam1());
            ps = conn.prepareStatement(sql);
            ps.setString(1, query_begin);
            ps.setString(2, query_end);
            ResultSet rs = ps.executeQuery();
            String companyA = "";
            String companyB = "";
            while (rs.next()) {
                if (!sheetIdInBankAccount.contains(rs.getString(1)) &&
                        !sheetIdInBankPend.contains(rs.getString(1))) {
                    String explain = "";
                    if (StringUtil.isNotEmpty(rs.getString(5))) {
                        explain = rs.getString(5).trim();
                    }
                    //System.out.println("互相内转explain:" + explain+",income:" + rs.getString(6));
                    if (explain != null) {
                        if (getCompanyExplain(explain).contains("付")) {
                            companyA = getCompanyName(getCompanyExplain(explain), "pay");
                            companyB = getCompanyName(getCompanyExplain(explain), "income");
                            //查询A公司和B公司
                            HdCompany hdCompanyA = hdCompanyBiz.getCompanyByName(companyA);
                            HdCompany hdCompanyB = hdCompanyBiz.getCompanyByName(companyB);
                            if (hdCompanyA != null) {
                                //支出公司存在,加入账单数组
                                accountList.add(
                                        saveAccount(rs.getDate(4),
                                                explain, new BigDecimal("0"), new BigDecimal(rs.getString(6)),
                                                hdCompanyA.getId(), rs.getString(1), rs.getString(40),"1"));
                            } else {
                                //支出公司不存在，加入待处理数组
                                pendingList.add(savePend(rs.getDate(4),
                                        explain, new BigDecimal("0"), new BigDecimal(rs.getString(6)),
                                        "待处理", rs.getString(1),
                                        rs.getString(8), rs.getString(40), rs.getString(41),"1"));
                            }
                            if (hdCompanyB != null) {
                                //收入公司存在，加入账单数组
                                accountList.add(saveAccount(rs.getDate(4),
                                        explain, new BigDecimal(rs.getString(6)), new BigDecimal("0"),
                                        hdCompanyB.getId(), rs.getString(1), rs.getString(41),"1"));
                            } else {
                                //收入公司不存在，加入待处理数组
                                pendingList.add(savePend(rs.getDate(4),
                                        explain, new BigDecimal(rs.getString(6)), new BigDecimal("0"),
                                        "待处理", rs.getString(1),
                                        rs.getString(10), rs.getString(40), rs.getString(41),"1"));
                            }
                        } else {
                            //摘要第一个逗号前字符串不带付字
                            pendingList.add(savePend(rs.getDate(4), explain, new BigDecimal("0"), new BigDecimal(rs.getString(6)), "待处理", rs.getString(1), rs.getString(8), rs.getString(40), rs.getString(41),"1"));
                            pendingList.add(savePend(rs.getDate(4), explain, new BigDecimal(rs.getString(6)), new BigDecimal("0"), "待处理", rs.getString(1), rs.getString(10), rs.getString(41), rs.getString(40),"1"));
                        }

                    } else {
                        //摘要为空
                        pendingList.add(savePend(rs.getDate(4), explain, new BigDecimal("0"), new BigDecimal(rs.getString(6)), "待处理", rs.getString(1), rs.getString(8), rs.getString(40), rs.getString(41),"1"));
                        pendingList.add(savePend(rs.getDate(4), explain, new BigDecimal(rs.getString(6)), new BigDecimal("0"), "待处理", rs.getString(1), rs.getString(10), rs.getString(41), rs.getString(40),"1"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rsn != null) {
                try {
                    rsn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return num;
    }

    //内部转外部同步,支出
    public Integer synNZPay(List<HdBankAccount> accountList,
                            List<HdBankPending> pendingList,
                            List<String> sheetIdInBankAccount,
                            List<String> sheetIdInBankPend,
                            String query_begin, String query_end) {
        int num = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rsn = null;
        try {
            conn = DbUtil.getConnection();
            String sqlPay = sqlBuild(sqlBuildParam2());

            ps = conn.prepareStatement(sqlPay);
            ps.setString(1, query_begin);
            ps.setString(2, query_end);
            ResultSet rs = ps.executeQuery();
            String companyName = "";
            while (rs.next()) {
                if (!sheetIdInBankAccount.contains(rs.getString(1)) &&
                        !sheetIdInBankPend.contains(rs.getString(1))) {
                    String explain = "";
                    if (StringUtil.isNotEmpty(rs.getString(5))) {
                        explain = rs.getString(5).trim();
                    }
                    if (explain != null) {
                        //System.out.println("内转支出explain:" + rs.getString(5) + ",money:" + rs.getString(6));
                        //获取收支公司名，第一个逗号之前的字符串带付字
                        //根据是否带付字获取支出公司名称：
                        //有付字，截取付字之前的为支出公司名称；无付字，直接为支出公司名称
                        if (getCompanyExplain(explain).contains("付")) {
                            companyName = getCompanyName(getCompanyExplain(explain), "pay");
                        } else {
                            companyName = getCompanyExplain(explain);
                        }
                        //匹配支出公司
                        HdCompany hdCompany = hdCompanyBiz.getCompanyByName(companyName);
                        if (hdCompany != null) {
                            //支出公司存在，加入账单数组
                            if (StringUtil.isNotEmpty(rs.getString(21))) {
                                explain += rs.getString(21);
                            } else {
                                explain += rs.getString(41);
                            }
                            accountList.add(saveAccount(rs.getDate(4),
                                    explain, new BigDecimal("0"), new BigDecimal(rs.getString(6)),
                                    hdCompany.getId(), rs.getString(1), rs.getString(40),"1"));
                        } else {
                            //支出公司不存在，加入待处理数组
                            pendingList.add(savePend(rs.getDate(4),
                                    explain + "," + rs.getString(41),
                                    new BigDecimal("0"), new BigDecimal(rs.getString(6))
                                    , "待处理", rs.getString(1),
                                    rs.getString(8), rs.getString(40),
                                    rs.getString(41),"1"));
                        }
                    } else {
                        pendingList.add(savePend(rs.getDate(4),
                                explain + "," + rs.getString(41),
                                new BigDecimal("0"), new BigDecimal(rs.getString(6))
                                , "待处理", rs.getString(1),
                                rs.getString(8), rs.getString(40),
                                rs.getString(41),"1"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rsn != null) {
                try {
                    rsn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }

        return num;
    }

    //内部转外部同步,收入
    public Integer synNZIncome(List<HdBankAccount> accountList,
                               List<HdBankPending> pendingList,
                               List<String> sheetIdInBankAccount,
                               List<String> sheetIdInBankPend,
                               String query_begin, String query_end) {
        int num = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rsn = null;
        try {
            conn = DbUtil.getConnection();
            String sqlIncome = sqlBuild(sqlBuildParam3());
            ps = conn.prepareStatement(sqlIncome);
            ps.setString(1, query_begin);
            ps.setString(2, query_end);
            ResultSet rs = ps.executeQuery();
            String companyName = "";
            while (rs.next()) {
                if (!sheetIdInBankAccount.contains(rs.getString(1)) &&
                        !sheetIdInBankPend.contains(rs.getString(1))) {
                    String explain = "";
                    if (StringUtil.isNotEmpty(rs.getString(5))) {
                        explain = rs.getString(5).trim();
                    }
                    //System.out.println("内转收入explain:" + explain+",income:" + rs.getString(6));
                    if (explain != null) {
                        //获取收支公司名，第一个逗号之前的字符串带付字
                        //根据是否带付字获取收入公司名称：
                        //有付字，截取付字之前的为收入公司名称；无付字，直接为收入公司名称
                        if (getCompanyExplain(explain).contains("付")) {
                            companyName = getCompanyName(getCompanyExplain(explain), "income");
                        } else {
                            companyName = getCompanyExplain(explain);
                        }
                        HdCompany hdCompany = hdCompanyBiz.getCompanyByName(companyName);
                        if (hdCompany != null) {
                            //收入公司存在，加入账单数组
                            accountList.add(saveAccount(rs.getDate(4), explain + "," + rs.getString(40), new BigDecimal(rs.getString(6)),
                                    new BigDecimal("0"), hdCompany.getId(),
                                    rs.getString(1), rs.getString(41),"1"));
                        } else {
                            //收入公司不存在，加入待处理数组
                            pendingList.add(savePend(rs.getDate(4), explain + "," + rs.getString(40), new BigDecimal(rs.getString(6)),
                                    new BigDecimal("0"), "待处理",
                                    rs.getString(1), rs.getString(10),
                                    rs.getString(41), rs.getString(40),"1"));
                        }
                    } else {
                        pendingList.add(savePend(rs.getDate(4), explain + "," + rs.getString(40),
                                new BigDecimal(rs.getString(6)),
                                new BigDecimal("0"), "待处理",
                                rs.getString(1), rs.getString(10),
                                rs.getString(41), rs.getString(40),"1"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rsn != null)
                try {
                    rsn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (ps != null)
                try {
                    ps.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return num;
    }

    //外转
    public Integer synWZ(List<HdBankAccount> accountList,
                         List<HdBankPending> pendingList,
                         List<String> rIdInBankAccount,
                         List<String> rIdInBankPend,
                         String query_begin, String query_end) {
        int num = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        HdBankAccount hdBankAccountAdd = null;
        try {
            String sqlWZ = sqlBuildWZ(sqlBuildWZParam(), query_begin, query_end);
            conn = DbUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlWZ);
            while (rs.next()) {
                String info = "";
                String companyId = "";
                String explain = "";
                String opAcntName = "";
                String acntName = "";
                if (StringUtil.isNotEmpty(rs.getString(1))) {
                    explain = rs.getString(1).trim();
                }
                if (StringUtil.isNotEmpty(rs.getString(6))) {
                    opAcntName = rs.getString(6).trim();
                }
                if (StringUtil.isNotEmpty(rs.getString(8))) {
                    acntName = rs.getString(8).trim();
                }
                if (!rIdInBankAccount.contains(rs.getString(3)) &&
                        !rIdInBankPend.contains(rs.getString(3))) {
                    //1、先按摘要匹配
                    info = explain;
                    //System.out.println("外转账单explain:" + explain+",money:" + rs.getString(5));
                    if (explain != null) {
                        //九恒星中中国银行摘要会自动变成OBSS011310531050GIRO000001209448内二//内二;内二
                        //针对此情况提取匹配公司
                        //摘要匹配公司
                        if (rs.getString(7).equals("中国银行") && explain.contains(";")) {
                            String[] strs = explain.split(";");
                            if (strs.length > 1) {
                                explain = explain.split(";")[1];
                                String companyName = getCompanyName(getCompanyExplain(explain), "pay");
                                HdCompany hdCompany = hdCompanyBiz.getCompanyByName(companyName);
                                if (hdCompany != null) {
                                    companyId = hdCompany.getId();
                                }
                            } else {
                                //explain = "";
                            }
                        } else {
                            String companyName = getCompanyName(getCompanyExplain(explain), "pay");
                            HdCompany hdCompany = hdCompanyBiz.getCompanyByName(companyName);
                            if (hdCompany != null) {
                                companyId = hdCompany.getId();
                            }
                        }
                        info += ":" + companyId;
                        if (StringUtil.isNotEmpty(companyId)) {
                            // 如果匹配成功
                            hdBankAccountAdd = getHdBankEntity(rs, companyId);
                            if (hdBankAccountAdd != null) {
                                accountList.add(hdBankAccountAdd);
                            }
                            //一旦匹配成功，无论结果如何，此次循环结束
                            continue;
                        }
                        //2、再按外单位匹配
                        info += ";" + opAcntName + ":" + acntName;
                        if (opAcntName != null) {
                            companyId = getMatchCompanyId(opAcntName, acntName);
                            info += ":" + companyId;
                            if (StringUtil.isNotEmpty(companyId)) {
                                // 如果匹配外部维护公司
                                hdBankAccountAdd = getHdBankEntity(rs, companyId);
                                if (hdBankAccountAdd != null) {
                                    accountList.add(hdBankAccountAdd);
                                }
                                //一旦匹配成功，无论结果如何，此次循环结束
                                continue;
                            }
                        }
                        info += "匹配失败";
                        // 进入待转数据
                        HdBankPending hdBankPending = new HdBankPending();
                        hdBankPending.setAccountDate(rs.getDate(4));
                        hdBankPending.setSynAccountDate(rs.getDate(4));
                        hdBankPending.setRid(rs.getString(3));
                        hdBankPending.setRemark(explain + "," + opAcntName);
                        hdBankPending.setBankname(rs.getString(7));
                        hdBankPending.setMysubjects(rs.getString(8));
                        hdBankPending.setCompanyName("待处理");
                        String dirflag = rs.getString(2);
                        hdBankPending.setSubjects(opAcntName);
                        //6.25
                        hdBankPending.setAccountType("0");
                        // 数值为支出
                        if ("1".equals(dirflag)) {
                            hdBankPending.setIncome(new BigDecimal("0.00"));
                            hdBankPending.setPay(new BigDecimal(rs.getString(5) == null ? "0.00" : rs.getString(5)));
                        }
                        // 数值为收入
                        if ("2".equals(dirflag)) {
                            hdBankPending.setPay(new BigDecimal("0.00"));
                            hdBankPending.setIncome(new BigDecimal(rs.getString(5) == null ? "0.00" : rs.getString(5)));
                        }
                        //FileOperation.contentToTxt("c://info.txt", info);
                        pendingList.add(hdBankPending);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (stmt != null)
                    stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return num;
    }


    // 判断是否匹配外部账户维护表
    private String getMatchCompanyId(String opAcntName, String acntname) {
        List<HdMatchCompany> matchComList = new ArrayList<HdMatchCompany>();
        //String hql = "from HdMatchCompanyEntity where customerName = '"+opAcntName+"'";
        String matchComName = "";
        //根据外部单位查找内部匹配公司
        matchComList = hdMatchCompanyBiz.getMatchCompanyByTerm(opAcntName);
        if (matchComList != null && matchComList.size() > 0) {
            //循环找到对应的内公司名称
            for (HdMatchCompany matchCom : matchComList) {
                //内部抬头不为空
                if (StringUtil.isNotEmpty(matchCom.getInnerCompany())) {
                    //内部抬头相匹配，则划分到对应公司
                    if (acntname.equals(matchCom.getInnerCompany().trim())) {
                        matchComName = matchCom.getCompanyName();
                        //提前结束循环
                        break;
                    }
                    //内部抬头不匹配，则跳过
                } else {
                    //内部抬头为空,直接确认公司
                    matchComName = matchCom.getCompanyName();
                }

            }
        }
        //没有匹配到结果返回“”
        return matchComName;
    }


    private String sqlBuildWZ(String sqlBuildParam, String startTime,
                              String endTime) {
        return "select explain,DIRFLAG,rid,ACTDATE,amount,OpAcntName,BANKNAME,ACNTNAME from NSTCSA.VW_BP_RECORD where "
                + sqlBuildParam + " AND ACTDATE between (to_date('"
                + startTime + "','yyyy-mm-dd'))" + " and (to_date('" + endTime
                + "','yyyy-mm-dd'))"
                + " and (explain not in('资金上存','资金上收','用款','资金下拨','上收') or explain is null) and (OpAcntName!='红豆集团财务有限公司' or OpAcntName is null) order by ACTDATE desc";
    }

    private String sqlBuild(String sqlBuildParam) {
        return "SELECT * FROM NSTCSA.VW_CNTBUSSSHEET where " + sqlBuildParam +
                " AND ACTDATE between TO_DATE(?, 'yyyy-mm-dd') and " +
                "TO_DATE(?,'yyyy-mm-dd') AND (explain not in('资金上存','资金上收'," +
                "'用款','资金下拨','上收') or explain is null) and extno is null order by ACTDATE desc";
    }


    //拼接查询语句1
    private String sqlBuildParam1() {
        List<HdNzDict> hdNzDictList = hdNzDictBiz.selectListAllByTenant(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
        StringBuffer sb = new StringBuffer("(JNO IN(");
        sqlBuildNzDict(sb, hdNzDictList);
        sb.append(" and DNO IN(");
        sqlBuildNzDict(sb, hdNzDictList);
        sb.append(")");
        return sb.toString();
    }

    private String sqlBuildParam2() {
        List<HdNzDict> hdNzDictList = hdNzDictBiz.selectListAllByTenant(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
        StringBuffer sb = new StringBuffer("(JNO IN(");
        sqlBuildNzDict(sb, hdNzDictList);
        sb.append(" and DNO NOT IN(");
        sqlBuildNzDict(sb, hdNzDictList);
        sb.append(")");
        return sb.toString();
    }

    private String sqlBuildParam3() {
        List<HdNzDict> hdNzDictList = hdNzDictBiz.selectListAllByTenant(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
        StringBuffer sb = new StringBuffer("(JNO NOT IN(");
        sqlBuildNzDict(sb, hdNzDictList);
        sb.append(" and DNO IN(");
        sqlBuildNzDict(sb, hdNzDictList);
        sb.append(")");
        return sb.toString();
    }

    private String sqlBuildWZParam() {
        List<HdNzDict> hdNzDictList = hdNzDictBiz.selectListAllByTenant(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
        StringBuffer sb = new StringBuffer("(ACNTNO in(");
        sqlBuildWzDict(sb, hdNzDictList);
        sb.append(")");
        return sb.toString();
    }


    //拼接查询语句内转子类
    private StringBuffer sqlBuildNzDict(StringBuffer sb, List<HdNzDict> hdNzDictList) {
        if (hdNzDictList != null) {
            for (HdNzDict hdNzDict : hdNzDictList) {
                sb.append("'" + hdNzDict.getNzid() + "',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        return sb;
    }

    //拼接查询语句外转子类
    private StringBuffer sqlBuildWzDict(StringBuffer sb, List<HdNzDict> hdNzDictList) {
        if (hdNzDictList != null) {
            for (HdNzDict hdNzDict : hdNzDictList) {
                sb.append(hdNzDict.getWbzh() + ",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        return sb;
    }

    //判断是否含有逗号，若有去第一个逗号前的内容
    private String getCompanyExplain(String explain) {
        int a = explain.indexOf(",");
        int b = explain.indexOf("，");
        if (a > 0 && b > 0) {
            if (a > b) {
                return explain.substring(0, b);
            } else {
                return explain.substring(0, a);
            }
        }
        if (a > 0 && b == -1) {
            return explain.substring(0, a);
        }
        if (a == -1 && b > 0) {
            return explain.substring(0, b);
        }
        return explain;
    }

    //外转封装
    public HdBankAccount getHdBankEntity(ResultSet rs, String comId) throws SQLException {
        HdBankAccount hdBank = new HdBankAccount();
        hdBank.setAccountDate(rs.getDate(4));
        hdBank.setSynAccountDate(rs.getDate(4));
        hdBank.setCompanyName(comId);
        hdBank.setRid(rs.getString(3));
        hdBank.setRemark(rs.getString(1));
        hdBank.setSubjects(rs.getString(6));
        //6.25
        hdBank.setAccountType("0");
        String dirflag = rs.getString(2);
        // 数值为支出
        if ("1".equals(dirflag)) {
            hdBank.setIncome(new BigDecimal("0.00"));
            hdBank.setPay(new BigDecimal(rs.getString(5) == null ? "0.00" : rs.getString(5)));
        }
        // 数值为收入
        if ("2".equals(dirflag)) {
            hdBank.setPay(new BigDecimal("0.00"));
            hdBank.setIncome(new BigDecimal(rs.getString(5) == null ? "0.00" : rs.getString(5)));
        }
        return hdBank;
    }

    //获取支出或者收入公司名
    private String getCompanyName(String explain, String flag) {
        //判断是否含有”付“字
        if (explain.indexOf("付") > 0) {
            if ("income".equals(flag)) {
                //收入
                explain = explain.substring(explain.indexOf("付") + 1);
            } else {
                //支出
                explain = explain.substring(0, explain.indexOf("付"));
            }
        }
        return explain;
    }

    //从九恒星拉取数据进行封装
    private HdBankAccount saveAccount(
            Date date, String explain, BigDecimal income, BigDecimal pay,
            String companyName, String sheetId, String subjects,String accountType
    ) {
        HdBankAccount entity = new HdBankAccount();
        entity.setAccountDate(date);
        entity.setSynAccountDate(date);
        entity.setRemark(explain);
        entity.setIncome(income);
        entity.setPay(pay);
        entity.setCompanyName(companyName);
        entity.setSubjects(subjects);
        entity.setSheetid(sheetId);
        entity.setAccountType(accountType);
        return entity;
    }

    //从九恒星拉取数据进行封装
    private HdBankPending savePend(
            Date date, String explain, BigDecimal income, BigDecimal pay,
            String companyName, String sheetId, String nzId,
            String mySubjects, String subjects,String accountType
    ) {
        HdBankPending entity = new HdBankPending();
        entity.setAccountDate(date);
        entity.setSynAccountDate(date);
        entity.setRemark(explain);
        entity.setIncome(income);
        entity.setPay(pay);
        entity.setMysubjects(mySubjects);
        entity.setSubjects(subjects);
        //HdNzDict hdNzDict = hdNzDictBiz.selectById(nzId);
        //内转账号根据nzid获取银行
        String hdNzName = hdNzDictBiz.getBankNameByNZId(nzId);
        String bankName = "";
        if (StringUtil.isNotEmpty(hdNzName)) {
            bankName = hdNzName;
        }
        entity.setBankname(bankName);
        entity.setCompanyName(companyName);
        entity.setSheetid(sheetId);
        entity.setAccountType(accountType);
        return entity;
    }


    public List<Statistics> statics(String startTime, String endTime, String tenantId,String userId) {
        //收入-支出+期初
        List<Statistics> statisticsList = this.mapper.selectStatics(startTime,
                endTime, tenantId,userId);
        if (statisticsList != null && statisticsList.size() > 0) {
            BigDecimal endBalance = BigDecimal.ZERO;
            for (Statistics statistics : statisticsList) {
                BigDecimal beginBalance = statistics.getBeginBalance();
                BigDecimal income = statistics.getCurrentIncome();
                BigDecimal pay = statistics.getCurrentPay();
                endBalance = income.subtract(pay).add(beginBalance);
                statistics.setEndBalance(endBalance);
            }
        }
        return statisticsList;
    }

    public Integer getSaveCount(String startTime, String endTime) {
        return this.mapper.getSaveCount(startTime, endTime, BaseContextHandler.getTenantID());
    }

    public static void main(String[] args) {
        System.out.println(UUIDUtils.generateUuid());
    }
}