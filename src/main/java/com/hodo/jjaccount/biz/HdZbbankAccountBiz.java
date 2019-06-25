package com.hodo.jjaccount.biz;

import com.github.ag.core.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.MyBeanUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.hodo.jjaccount.entity.*;
import com.hodo.jjaccount.mapper.HdZbbankAccountMapper;
import com.hodo.jjaccount.util.DbUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.wxiaoqi.security.common.biz.BusinessBiz;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author Mr.AG
 * @version 2019-04-26 15:09:43
 * @email 463540703@qq.com
 */
@Service
public class HdZbbankAccountBiz extends BusinessBiz<HdZbbankAccountMapper, HdZbbankAccount> {

    @Autowired
    private HdCompanyBiz hdCompanyBiz;
    @Autowired
    private HdNzDictBiz hdNzDictBiz;
    @Autowired
    private HdMatchCompanyBiz hdMatchCompanyBiz;

    public List<HdZbbankAccount> getBankAccountList(String companyName, String startTime,
                                                    String endTime, String page, String limit, String tenantId, Map<String, Object> params) {

        return this.mapper.getBankAccountList(companyName, startTime, endTime, page, limit, tenantId, params);
    }

    public List<HdZbbankAccount> getAllBankAccount(String companyName, String startTime,
                                                   String endTime, String tenantId, Map<String, Object> params) {
        return this.mapper.getAllBankAccount(companyName, startTime, endTime, tenantId, params);
    }

    public List<MonInAndPay> geteveryMonthPayAndIncome(String tenantId, String Year) {
        return this.mapper.geteveryMonthPayAndIncome(tenantId, Year);
    }


    public List<HdZbbankAccount> calBalance(List<HdZbbankAccount> banks, String companyName) throws Exception {
        List<HdZbbankAccount> lists = new ArrayList<HdZbbankAccount>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < banks.size(); i++) {
            String date = sdf.format(banks.get(i).getAccountDate());
            // 拷贝实体类
            HdZbbankAccount bank = new HdZbbankAccount();
            MyBeanUtils.copyBeanNotNull2Bean(banks.get(i), bank);
            // 根据公司查结存金额
//            String balanceStr = this.mapper.selBalanceBysql(companyName,BaseContextHandler.getTenantID());
//            BigDecimal balance = BigDecimal.ZERO;
//            if (StringUtil.isNotEmpty(balanceStr)) {
//                balance = new BigDecimal(balanceStr);
//            }
            // 计算在此条日期前的总收入和总支出
            IncomeAndPay incomeAndPay = this.mapper.selIncomeAndPayBysql(companyName, BaseContextHandler.getTenantID(), date);
            BigDecimal income = BigDecimal.ZERO;
            BigDecimal pay = BigDecimal.ZERO;
            if (incomeAndPay != null) {
                income = new BigDecimal(incomeAndPay.getIncomes());
                pay = new BigDecimal(incomeAndPay.getPays());
                //System.out.println(incomeAndPay.getIncomes()+"期前"+incomeAndPay.getPays());
            }

            // 找到这个日期里到这条凭证编号之前的总收入和总支出
            IncomeAndPay incomeAndPayOnNo = this.mapper.selIncomeAndPayOnNoBysql(companyName, BaseContextHandler.getTenantID(), date, banks.get(i).getNo().toString());
            BigDecimal newincome = BigDecimal.ZERO;
            BigDecimal newpay = BigDecimal.ZERO;
            if (incomeAndPayOnNo != null) {
                newincome = new BigDecimal(incomeAndPayOnNo.getIncomes());
                newpay = new BigDecimal(incomeAndPayOnNo.getPays());
                //System.out.println(incomeAndPayOnNo.getIncomes()+"期中"+incomeAndPayOnNo.getPays());
            }

            // 计算结存金额
            BigDecimal Balance1 = (income.subtract(pay));
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


    public List<String> getSheetIdsInZBBankAccount() {
        return this.mapper.getSheetIdsInZBBankAccount(BaseContextHandler.getTenantID());
    }

    public List<String> getRIdsInZBBankAccount() {
        return this.mapper.getRIdsInZBBankAccount(BaseContextHandler.getTenantID());
    }

    public List<String> getSheetIdsInBankPend() {
        return this.mapper.getSheetIdsInBankPend(BaseContextHandler.getTenantID());
    }

    public List<String> getRIdsInBankPend() {
        return this.mapper.getRIdsInBankPend(BaseContextHandler.getTenantID());
    }

    public Integer synNZPay(List<HdZbbankAccount> accountList, List<HdZbbankPending> pendingList, List<String> sheetIdInBankAccount,
                            List<String> sheetIdInBankPend, String begin_date, String end_date) {
        int num = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rsn = null;
        try {
            conn = DbUtil.getConnection();
            String sqlNZpay = sqlBuild(sqlBuildParam2());
            ps = conn.prepareStatement(sqlNZpay);
            ps.setString(1, begin_date);
            ps.setString(2, end_date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (!sheetIdInBankAccount.contains(rs.getString(1)) &&
                        !sheetIdInBankPend.contains(rs.getString(1))) {
                    String explain = "";
                    if (StringUtil.isNotEmpty(rs.getString(5))) {
                        explain = rs.getString(5).trim();
                    }
                    //先判断explain是否存在，然后根据z做判断
                    if (StringUtil.isNotEmpty(explain)) {
                        HdCompany hdCompany = hdCompanyBiz.getCompanyByName(rs.getString(40));
                        if (explain.startsWith("z") || explain.startsWith("Z")) {
                            if (hdCompany != null) {
                                accountList.add(saveAccount(rs.getDate(4),
                                        explain, new BigDecimal("0"), new BigDecimal(rs.getString(6)),
                                        rs.getString(41), rs.getString(1), hdCompany.getId()));
                            } else {
                                //公司不存在
                                pendingList.add(savePend(rs.getDate(4), explain,
                                        new BigDecimal("0"),
                                        new BigDecimal(rs.getString(6)),
                                        rs.getString(40),
                                        rs.getString(1), rs.getString(10),
                                        rs.getString(41)));
                            }
                        } else {
                            //摘要不匹配
                            pendingList.add(savePend(rs.getDate(4), explain,
                                    new BigDecimal("0"),
                                    new BigDecimal(rs.getString(6)),
                                    rs.getString(40),
                                    rs.getString(1), rs.getString(10),
                                    rs.getString(41)));
                        }
                    } else {
                        //摘要不存在
                        pendingList.add(savePend(rs.getDate(4), explain
                                , new BigDecimal("0"), new BigDecimal(rs.getString(6)),
                                rs.getString(40),
                                rs.getString(1), rs.getString(10),
                                rs.getString(41)));
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


    public Integer synNZIncome(List<HdZbbankAccount> accountList, List<HdZbbankPending> pendingList, List<String> sheetIdInBankAccount,
                               List<String> sheetIdInBankPend, String begin_date, String end_date) {
        int num = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rsn = null;
        try {
            conn = DbUtil.getConnection();
            String sqlIncome = sqlBuild(sqlBuildParam3());
            ps = conn.prepareStatement(sqlIncome);
            ps.setString(1, begin_date);
            ps.setString(2, end_date);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (!sheetIdInBankAccount.contains(rs.getString(1)) &&
                        !sheetIdInBankPend.contains(rs.getString(1))) {
                    String explain = "";
                    if (StringUtil.isNotEmpty(rs.getString(5))) {
                        explain = rs.getString(5).trim();
                    }
                    //先判断explain是否存在，然后根据z做判断
                    if (StringUtil.isNotEmpty(explain)) {
                        if (explain.startsWith("z") || explain.startsWith("Z")) {
                            HdCompany hdCompany = hdCompanyBiz.getCompanyByName(rs.getString(41));
                            if (hdCompany != null) {
                                accountList.add(saveAccount(rs.getDate(4), explain + "," + rs.getString(40), new BigDecimal(rs.getString(6)),
                                        new BigDecimal("0"), rs.getString(40), rs.getString(1), hdCompany.getId()));
                            } else {
                                //进入待处理
                                pendingList.add(savePend(rs.getDate(4), explain,
                                        new BigDecimal(rs.getString(6)), new BigDecimal("0"),
                                        rs.getString(41),
                                        rs.getString(1), rs.getString(10),
                                        rs.getString(40)));
                            }
                        } else {
                            //进入待处理
                            pendingList.add(savePend(rs.getDate(4), explain,
                                    new BigDecimal(rs.getString(6)), new BigDecimal("0"),
                                    rs.getString(41),
                                    rs.getString(1), rs.getString(10),
                                    rs.getString(40)));
                        }
                    } else {
                        //进入待处理
                        pendingList.add(savePend(rs.getDate(4), explain,
                                new BigDecimal(rs.getString(6)), new BigDecimal("0"),
                                rs.getString(41),
                                rs.getString(1), rs.getString(10),
                                rs.getString(40)));
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


    public Integer synWZ(List<HdZbbankAccount> accountList, List<HdZbbankPending> pendingList, List<String> rIdInBankAccount,
                         List<String> sheetIdInBankPend, String begin_date, String end_date) {
        int num = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        HdZbbankAccount hdZbbankAccount = null;
        try {
            String sqlWZ = sqlBuildWZ(sqlBuildWZParam(), begin_date, end_date);
            conn = DbUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlWZ);
            while (rs.next()) {
                String info = "";
                String companyId = "";
                String explain = "";
                String acntName = "";
                String opAcntName = "";

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
                        !sheetIdInBankPend.contains(rs.getString(3))) {
                    num++;
                    //1、先按摘要匹配
                    info = explain;
                    //System.out.println("外转账单explain:" + explain+",money:" + rs.getString(5));
                    if (explain != null) {
                        if (rs.getString(7).equals("中国银行") && explain.contains(";")) {
                            String[] strs = explain.split(";");
                            if (strs.length > 1) {
                                explain = explain.split(";")[1];
                                if (explain != null && (explain.startsWith("z") || explain.startsWith("Z"))) {
                                    HdCompany hdCompany = hdCompanyBiz.getCompanyByName(acntName);
                                    if (hdCompany != null) {
                                        companyId = hdCompany.getId();
                                    }
                                    hdZbbankAccount = getHdBankEntity(rs, companyId);
                                    if (hdZbbankAccount != null) {
                                        accountList.add(hdZbbankAccount);
                                        continue;
                                    }
                                }
                            }
                        }
                        if (explain.startsWith("z") || explain.startsWith("Z")) {
                            HdCompany hdCompany = hdCompanyBiz.getCompanyByName(acntName);
                            if (hdCompany != null) {
                                companyId = hdCompany.getId();
                            }
                            hdZbbankAccount = getHdBankEntity(rs, companyId);
                            if (hdZbbankAccount != null) {
                                accountList.add(hdZbbankAccount);
                                continue;
                            }
                        }
                        System.out.println("标记1---------");
                        //匹配不成功进入待处理
                        // 进入待转数据
                        HdZbbankPending hdBankPending = new HdZbbankPending();
                        hdBankPending.setAccountDate(rs.getDate(4));
                        hdBankPending.setRid(rs.getString(3));
                        hdBankPending.setRemark(explain);
                        hdBankPending.setCompanyName(acntName);
                        String dirflag = rs.getString(2);
                        hdBankPending.setSubjects(opAcntName);
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

                    } else {
                        //匹配不成功进入待处理
                        // 进入待转数据
                        HdZbbankPending hdBankPending = new HdZbbankPending();
                        hdBankPending.setAccountDate(rs.getDate(4));
                        hdBankPending.setRid(rs.getString(3));
                        hdBankPending.setRemark(explain);
                        hdBankPending.setCompanyName(acntName);
                        String dirflag = rs.getString(2);
                        hdBankPending.setSubjects(opAcntName);
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
        System.out.println(pendingList.size() + "$$$");
        return num;
    }
//    // 判断是否匹配外部账户维护表
//    private String getMatchCompanyId(String opAcntName,String acntname) {
//        List<HdMatchCompany> matchComList = new ArrayList<HdMatchCompany>();
//        //String hql = "from HdMatchCompanyEntity where customerName = '"+opAcntName+"'";
//        String matchComName = "";
//        //根据外部单位查找内部匹配公司
//        matchComList = hdMatchCompanyBiz.getMatchCompanyByTerm(opAcntName);
//        if(matchComList!=null&&matchComList.size()>0){
//            //循环找到对应的内公司名称
//            for(HdMatchCompany matchCom : matchComList){
//                //内部抬头不为空
//                if(StringUtil.isNotEmpty(matchCom.getInnerCompany())){
//                    //内部抬头相匹配，则划分到对应公司
//                    if(acntname.equals(matchCom.getInnerCompany().trim())){
//                        matchComName = matchCom.getCompanyName();
//                        //提前结束循环
//                        break;
//                    }
//                    //内部抬头不匹配，则跳过
//                }else{
//                    //内部抬头为空,直接确认公司
//                    matchComName = matchCom.getCompanyName();
//                }
//
//            }
//        }
//        //没有匹配到结果返回“”
//        return matchComName;
//    }


//    //判断是否含有逗号，若有去第一个逗号前的内容
//    private String getCompanyExplain(String explain){
//        int a = explain.indexOf(",");
//        int b = explain.indexOf("，");
//        if(a>0&&b>0){
//            if(a>b){
//                return explain.substring(0,b);
//            }else{
//                return explain.substring(0,a);
//            }
//        }
//        if(a>0&&b==-1){
//            return explain.substring(0,a);
//        }
//        if(a==-1&&b>0){
//            return explain.substring(0,b);
//        }
//        return explain;
//    }


    //从九恒星拉取数据进行封装
    private HdZbbankAccount saveAccount(
            Date date, String explain, BigDecimal income, BigDecimal pay,
            String subject, String sheetId, String companyName) {
        HdZbbankAccount entity = new HdZbbankAccount();
        entity.setCompanyName(companyName);
        entity.setAccountDate(date);
        entity.setSynAccountDate(date);
        entity.setRemark(explain);
        entity.setIncome(income);
        entity.setPay(pay);
        entity.setSubjects(subject);
        entity.setSheetid(sheetId);
        entity.setTenantId(BaseContextHandler.getTenantID());
        return entity;
    }


    //外转封装
    public HdZbbankAccount getHdBankEntity(ResultSet rs, String comId) throws SQLException {
        HdZbbankAccount hdBank = new HdZbbankAccount();
        hdBank.setAccountDate(rs.getDate(4));
        hdBank.setSynAccountDate(rs.getDate(4));
        hdBank.setCompanyName(comId);
        hdBank.setTenantId(BaseContextHandler.getTenantID());
        hdBank.setRid(rs.getString(3));
        hdBank.setRemark(rs.getString(1));
        hdBank.setSubjects(rs.getString(6));
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


    /**
     * 获取hd_zbbankaccount表中最大编号用于比较插入
     */
    public synchronized Integer getNoMaxCurrentTime(HdZbbankAccount entity) {
        Integer maxNo = null;
        int No = 1;
        if (StringUtil.isNotEmpty(entity.getCompanyName())) {
            maxNo = this.mapper.selectMaxNo(entity.getCompanyName(), BaseContextHandler.getTenantID());
        }
        if (maxNo == null) {
            maxNo = 0;
        }
        if (maxNo != null) {
            No = maxNo + 1;
        }
        entity.setNo(No);
        return maxNo;
    }

    //
    //重新新增增加序号,在从当前时间获取最大值
    public void insertEntity(HdZbbankAccount entity) {
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


    //拼接查询语句
    private String sqlBuildParam2() {
        List<HdNzDict> hdNzDictList = hdNzDictBiz.selectListAllByTenant(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
        StringBuffer sb = new StringBuffer("(JNO IN(");
        sqlBuildNzDict(sb, hdNzDictList);
        sb.append(")");
        return sb.toString();
    }

    private String sqlBuildParam3() {
        List<HdNzDict> hdNzDictList = hdNzDictBiz.selectListAllByTenant(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
        StringBuffer sb = new StringBuffer("(DNO IN(");
        sqlBuildNzDict(sb, hdNzDictList);
        sb.append(")");
        return sb.toString();
    }

    private String sqlBuildWZParam() {
        List<HdNzDict> hdNzDictList = hdNzDictBiz.selectListAllByTenant(BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
        StringBuffer sb = new StringBuffer("(ACNTNO IN(");
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


    //封裝handBankAccountList
    public void handBankAccountList(List<HdZbbankAccount> accountList) {
//        Map<String,Map<Integer,Object>> mapResult = new HashedMap();
        //Map<Integer,Object> valueResult = new HashedMap();
        Integer m = null;
        for (HdZbbankAccount hdBankAccount : accountList) {
            try {
                System.out.println(BaseContextHandler.getTenantID() + "-------");
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
                if (hdBankAccount.getCompanyName() != null || !"".equals(hdBankAccount.getCompanyName())) {
                    System.out.println(hdBankAccount.getCompanyName());
                    //获取最大值
                    if (m == null) {
                        m = getNoMaxCurrentTime(hdBankAccount);
                        hdBankAccount.setNo(++m);
                    } else {
                        hdBankAccount.setNo(++m);
                    }

                    //valueResult.put(hdBankAccount.getNo(), hdBankAccount);
                } else {
                    System.out.println("companyName为空出错");
                    continue;
                }

            } catch (Exception e) {
                System.out.println("錯誤信息" + e.getMessage());
                e.printStackTrace();
                continue;
            }
        }
    }


    //处理年月合计
    public List<MonInAndPay> handleMIAP(List<MonInAndPay> miapList) {
        StringBuffer date = new StringBuffer();
        BigDecimal incomes;
        BigDecimal pays;
        BigDecimal balance;
        for (MonInAndPay miap : miapList) {
            date = new StringBuffer();
            date.append(miap.getYearMon());
            date.insert(4, "-");
            String datenew = date.toString();
            miap.setYearMon(datenew);
            incomes = new BigDecimal(miap.getIncomes());
            pays = new BigDecimal(miap.getPays());
            balance = incomes.subtract(pays);
            miap.setBalance(balance.toString());
            date.delete(0, date.length());
        }

        //添加本年合计数据
        BigDecimal finalIncomes = BigDecimal.ZERO;
        BigDecimal finalPays = BigDecimal.ZERO;
        BigDecimal finalBalance = BigDecimal.ZERO;
        for (MonInAndPay miap : miapList) {
            finalIncomes = finalIncomes.add(new BigDecimal(miap.getIncomes()));
            finalPays = finalPays.add(new BigDecimal(miap.getPays()));
            finalBalance = finalIncomes.subtract(finalPays);
        }
        MonInAndPay last = new MonInAndPay();
        last.setYearMon("本年合计");
        last.setIncomes(finalIncomes.toString());
        last.setPays(finalPays.toString());
        last.setBalance(finalBalance.toString());
        miapList.add(last);

        return miapList;
    }


    //处理年月合计日期
    public String handleMIAPDate(String Year) {
        StringBuffer sb = new StringBuffer();
        sb.append(Year);
        sb.append("-01-01");
        return sb.toString();
    }


    //批量增加
    public void batchSave(List<HdZbbankAccount> accountList) {
        while (accountList.size() > 1000) {
            List<HdZbbankAccount> hdBankRecordList = accountList.subList(0, 999);
            this.mapper.addList(hdBankRecordList);
            accountList.removeAll(hdBankRecordList);
        }
        this.mapper.addList(accountList);
    }


    public Integer getSaveCount() {
        return this.mapper.getSaveCount(BaseContextHandler.getTenantID());
    }

    //待处理封装
    private HdZbbankPending savePend(Date date, String explain, BigDecimal income,
                                     BigDecimal pay, String companyName, String sheetId,
                                     String mySubjects, String subjects) {
        HdZbbankPending entity = new HdZbbankPending();
        entity.setAccountDate(date);
        entity.setRemark(explain);
        entity.setIncome(income);
        entity.setPay(pay);
        entity.setSubjects(subjects);
        entity.setCompanyName(companyName);
        entity.setSheetid(sheetId);
        return entity;
    }

}