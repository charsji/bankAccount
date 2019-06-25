package com.hodo.jjaccount.feign;


import com.github.wxiaoqi.security.admin.client.entity.Depart;
import com.github.wxiaoqi.security.admin.client.entity.User;
import com.github.wxiaoqi.security.auth.client.config.FeignApplyConfiguration;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//feign调用服务
@FeignClient(value = "ace-admin", configuration = FeignApplyConfiguration.class)
public interface IUserFeign {
    /**
     * 获取所有菜单和权限按钮
     */
    @RequestMapping(value = "/user/dataDepart", method = RequestMethod.GET)
    List<String> getUserDataDepartIds(@RequestParam("userId") String userId);

    /**
     * 获取部门信息
     *
     * @param id
     * @return java.util.Map<java.lang.String                               ,                               java.lang.String>
     * @author zb
     * @date 2018/8/23
     */
    @RequestMapping(value = "/depart/getDepartById", method = RequestMethod.POST)
    Depart getDepart(@RequestParam("id") String id);

    /**
     * 通过部门名称获取部门
     *
     * @return
     */
    @RequestMapping(value = "/depart/getByDepartName", method = RequestMethod.POST)
    Depart getByDepartName(@RequestParam("name") String name);

    /**
     * 获取当前部门的父类（不包括当前部门）
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/depart/getParentDepart", method = RequestMethod.POST)
    List<String> getParentDepart(@RequestParam("id") String id);

    /**
     * 获取当前部门的子类（不包含当前部门）
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/depart/getChildDepart", method = RequestMethod.POST)
    List<String> getChildDepart(@RequestParam("id") String id);

    /**
     * 保存当前部门的父类与子类实体类（不包含当前部门）
     *
     * @param companyid
     * @return
     */
    @RequestMapping(value = "/depart/saveParentAndChildDepart", method = RequestMethod.POST)
    String saveParentAndChildDepart(@RequestParam("companyid") String companyid);

    /**
     * 获取全部部门信息
     *
     * @return java.util.Map<java.lang.String                               ,                               java.lang.String>
     * @author zb
     * @date 2018/8/23
     */
    @RequestMapping(value = "/depart/getAllDepart", method = RequestMethod.POST)
    List<Depart> getAllDepart();

    /**
     * 添加User用户到系统用户表
     *
     * @param entity
     * @return com.github.wxiaoqi.security.common.msg.ObjectRestResponse<com.github.wxiaoqi.security.admin.client.entity.User>
     * @author zb
     * @date 2018/7/30
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    ObjectRestResponse<User> add(@RequestBody User entity);

    /**
     * 根据用户名获取系统用户
     *
     * @param username
     * @return com.github.wxiaoqi.security.admin.client.entity.User
     * @author zb
     * @date 2018/7/30
     */
    @RequestMapping(value = "/user/getUserByUsername", method = RequestMethod.GET)
    User getByUsername(@RequestParam("username") String username);

    /**
     * 根据id获取系统用户
     *
     * @return com.github.wxiaoqi.security.admin.client.entity.User
     * @author zb
     * @date 2018/7/30
     */
    @RequestMapping(value = "/user/getUserById", method = RequestMethod.GET)
    User getById(@RequestParam("id") String id);

    /**
     * 更新系统用户
     *
     * @param user
     * @return void
     * @author zb
     * @date 2018/7/30
     */
    @RequestMapping(value = "/user/updateUser", method = RequestMethod.POST)
    void updateUser(@RequestBody User user);

    /**
     * 根据用户名停用系统用户
     *
     * @param username
     */
    @RequestMapping(value = "/user/stopUserbyUsername", method = RequestMethod.POST)
    void stopUserbyUsername(@RequestParam("username") String username);

    @RequestMapping(value = "/user/", method = RequestMethod.POST)
    List<Depart> getDepartList(List<String> departStr);

    /**
     * 获取当前部门的下一级子类
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/depart/getNextChildDepart", method = RequestMethod.POST)
    List<String> getNextChildDepart(@RequestParam("id") String id);

    /**
     * 获取当前部门的下一级子类
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/depart/getNextChildDepartEntity", method = RequestMethod.POST)
    List<Depart> getNextChildDepartEntity(@RequestParam("id") String id);

//    /**
//     * 获取部门管理树
//     */
//    @RequestMapping(value = "/depart/tree", method = RequestMethod.GET)
//    List<DepartTree> getDepartTrees(@RequestParam("code") String code);


    @RequestMapping(value = "depart/updateDepartUser", method = RequestMethod.POST)
    void updateDepartUser(@RequestParam("userid") String userid, @RequestParam("oldDepartid") String oldDepartid,
                          @RequestParam("departid") String departid);
}




