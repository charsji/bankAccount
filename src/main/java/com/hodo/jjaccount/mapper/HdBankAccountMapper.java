package com.hodo.jjaccount.mapper;

import com.github.wxiaoqi.security.common.mapper.CommonMapper;
import com.hodo.jjaccount.entity.HdBankAccount;
import com.hodo.jjaccount.entity.IncomeAndPay;
import com.hodo.jjaccount.entity.Statistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:29:23
 * @email 463540703@qq.com
 */
public interface HdBankAccountMapper extends CommonMapper<HdBankAccount> {
    //    List<HdBankAccount> getBankAccountList(@Param("companyName") String companyName,
//                                           @Param("remark") String remark, @Param("startTime") String startTime,
//                                           @Param("endTime") String endTime, @Param("page") String page, @Param("limit") String limit,
//                                           @Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);
    List<HdBankAccount> getBankAccountListBySql(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                @Param("page") String page, @Param("limit") String limit,
                                                @Param("params") Map<String, Object> params, @Param("companyName") String companyName,
                                                @Param("tenantId") String tenantId);

    List<HdBankAccount> getAllBankAccountBySql(@Param("startTime") String startTime,
                                               @Param("endTime") String endTime,
                                               @Param("params") Map<String, Object> params,
                                               @Param("companyName") String companyName, @Param("tenantId") String tenantId);

    String selBalanceBysql(@Param("companyName") String companyName, @Param("tenantId") String tenantId);

    IncomeAndPay selIncomeAndPayBysql(@Param("companyName") String companyName, @Param("date")
            String date, @Param("tenantId") String tenantId);

    IncomeAndPay selIncomeAndPayOnNoBysql(@Param("companyName") String companyName, @Param("tenantId") String tenantId, @Param("date")
            String date, @Param("num") String no);

    List<String> searchRemark(@Param("tenantId") String tenantId,@Param("userId") String userId);

    Integer selectMaxNo(@Param("companyName") String companyName, @Param("tenantId") String tenantId);

    Integer selectMaxNoFreeTime(@Param("companyName") String companyName, @Param("tenantId") String tenantId,
                                @Param("accountDate") String accountDate);

    List<String> getSheetIdsInBankAccount(@Param("tenantId") String tenantId,@Param("userId") String userId);

    List<String> getSheetIdsInBankPend(@Param("tenantId") String tenantId,@Param("userId") String userId);

    List<String> getRIdsInBankAccount(@Param("tenantId") String tenantId,@Param("userId") String userId);

    List<String> getRIdsInBankPend(@Param("tenantId") String tenantId,@Param("userId") String userId);

    List<Statistics> selectStatics(@Param("startTime") String startTime,
                                   @Param("endTime") String endTime,
                                   @Param("tenantId") String tenantId,
                                   @Param("userId") String userId);

    void addList(List<HdBankAccount> hdBankAccounts);

    public Integer getSaveCount(@Param("startTime") String startTime,
                                @Param("endTime") String endTime,
                                @Param("tenantId") String tenantId);

}
