package com.hodo.jjaccount.mapper;

import com.github.wxiaoqi.security.common.mapper.CommonMapper;
import com.hodo.jjaccount.entity.HdBankPending;
import com.hodo.jjaccount.entity.HdZbbankPending;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-05-08 10:28:39
 * @email 463540703@qq.com
 */
public interface HdZbbankPendingMapper extends CommonMapper<HdZbbankPending> {
    List<HdZbbankPending> getZbBankPendingList(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                               @Param("page") String page, @Param("limit") String limit,
                                               @Param("tenantId") String tenantId,
                                               @Param("params") Map<String, Object> params);

    List<HdZbbankPending> getAllZbBankPendingList(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                  @Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    public void addList(List<HdZbbankPending> hdBankPendings);
}
