package com.hodo.jjaccount.mapper;

import com.github.wxiaoqi.security.common.mapper.CommonMapper;
import com.hodo.jjaccount.entity.HdMatchCompany;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:28:43
 * @email 463540703@qq.com
 */
public interface HdMatchCompanyMapper extends CommonMapper<HdMatchCompany> {
    List<HdMatchCompany> getHdMatchCompanyList(@Param("companyName") String companyName, @Param("page") String page,
                                               @Param("limit") String limit,
                                               @Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    List<HdMatchCompany> getAllMatchCompany(@Param("companyName") String companyName,
                                            @Param("tenantId") String tenantId, @Param("params") Map<String, Object> params);

    List<HdMatchCompany> getMatchCompanyByTerm(@Param("customerName") String customerName,
                                               @Param("tenantId") String tenantId,@Param("userId") String userId);
}
