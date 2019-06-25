package com.hodo.jjaccount.mapper;

import com.github.wxiaoqi.security.common.mapper.CommonMapper;
import com.hodo.jjaccount.entity.HdCompany;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:28:43
 * @email 463540703@qq.com
 */
public interface HdCompanyMapper extends CommonMapper<HdCompany> {
    String getCompanyNameById(@Param("companyId") String companyId, @Param("tenantId") String tenantId);

    List<HdCompany> getAllCompanyNames(@Param("tenantId") String tenantId,@Param("userId")
                                       String userId);

    HdCompany getCompanyByName(@Param("companyName") String companyName, @Param("tenantId") String tenantId,
                               @Param("userId") String userId);

    List<HdCompany> getAllCompanyEx(@Param("company") String company, @Param("tenantId") String tenantId,
                                    @Param("userId")String userId);
}
