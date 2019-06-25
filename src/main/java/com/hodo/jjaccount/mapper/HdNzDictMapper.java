package com.hodo.jjaccount.mapper;


import com.github.wxiaoqi.security.common.mapper.CommonMapper;
import com.hodo.jjaccount.entity.HdNzDict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Mr.AG
 * @version 2019-04-09 16:28:42
 * @email 463540703@qq.com
 */
public interface HdNzDictMapper extends CommonMapper<HdNzDict> {
    List<HdNzDict> getAllNzDictEx(@Param("bankName") String bankName,
                                  @Param("tenantId") String tenantId,
                                    @Param("userId") String userId);

    List<HdNzDict> selectListAllByTenant(@Param("tenantId") String tenantId,@Param("userId") String userId);

    String getBankNameByNZId(@Param("nzId") String nzId,@Param("userId") String userId);
}
