package com.hodo.jjaccount.mapper;


import com.github.wxiaoqi.security.common.mapper.CommonMapper;
import com.hodo.jjaccount.entity.HdBankRecord;
import com.hodo.jjaccount.entity.HdCompany;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:29:07
 * @email 463540703@qq.com
 */
public interface HdBankRecordMapper extends CommonMapper<HdBankRecord> {
    List<HdBankRecord> getHdBankRecordList(@Param("companyName") String companyName, @Param("startTime") String startTime,
                                           @Param("endTime") String endTime, @Param("page") String page, @Param("limit") String limit,
                                           @Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    List<HdBankRecord> getAllBankRecord(@Param("companyName") String companyName, @Param("startTime") String startTime,
                                        @Param("endTime") String endTime,
                                        @Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    void addList(List<HdBankRecord> hdBankRecords);
}
