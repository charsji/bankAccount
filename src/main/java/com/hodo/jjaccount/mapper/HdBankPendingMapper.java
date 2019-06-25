package com.hodo.jjaccount.mapper;


import com.github.wxiaoqi.security.common.mapper.CommonMapper;
import com.hodo.jjaccount.entity.HdBankAccount;
import com.hodo.jjaccount.entity.HdBankPending;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:29:23
 * @email 463540703@qq.com
 */
public interface HdBankPendingMapper extends CommonMapper<HdBankPending> {
    List<HdBankPending> getBankPendingList(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                           @Param("page") String page, @Param("limit") String limit, @Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    List<HdBankPending> getAllBankPending(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                          @Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    public void addList(List<HdBankPending> hdBankPendings);

}
