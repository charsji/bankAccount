package com.hodo.jjaccount.mapper;

import com.github.wxiaoqi.security.common.mapper.CommonMapper;
import com.hodo.jjaccount.entity.HdBankAccount;
import com.hodo.jjaccount.entity.HdZbbankAccount;
import com.hodo.jjaccount.entity.IncomeAndPay;
import com.hodo.jjaccount.entity.MonInAndPay;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-04-26 15:09:43
 * @email 463540703@qq.com
 */
public interface HdZbbankAccountMapper extends CommonMapper<HdZbbankAccount> {

    //根据摘要、公司名称、起止日期等参数拉取银行账户列表
    List<HdZbbankAccount> getBankAccountList(@Param("companyName") String companyName, @Param("startTime") String startTime, @Param("endTime") String endTime,
                                             @Param("page") String page, @Param("limit") String limit, @Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    //查询最大凭证编号
    Integer selectMaxNo(@Param("companyName") String companyName, @Param("tenantId") String tenantId);

    List<HdZbbankAccount> getAllBankAccount(@Param("companyName") String companyName, @Param("startTime") String startTime,
                                            @Param("endTime") String endTime, @Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    String selBalanceBysql(@Param("companyName") String companyName, @Param("tenantId") String tenantId);

    IncomeAndPay selIncomeAndPayBysql(@Param("companyName") String companyName, @Param("tenantId") String tenantId, @Param("date") String date);

    IncomeAndPay selIncomeAndPayOnNoBysql(@Param("companyName") String companyName, @Param("tenantId") String tenantId, @Param("date") String date, @Param("num") String no);

    List<String> getSheetIdsInZBBankAccount(@Param("tenantId") String tenantId);

    List<String> getRIdsInZBBankAccount(@Param("tenantId") String tenantId);

    List<String> getRIdsInBankPend(@Param("tenantId") String tenantId);

    List<String> getSheetIdsInBankPend(@Param("tenantId") String tenantId);

    void addList(List<HdZbbankAccount> hdBankAccounts);

    Integer getSaveCount(String tenantId);

    List<MonInAndPay> geteveryMonthPayAndIncome(@Param("tenantId") String tenantId, @Param("Year") String Year);


}
