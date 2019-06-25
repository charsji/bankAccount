package com.hodo.jjaccount.rest;

import com.github.ag.core.context.BaseContextHandler;
import com.github.wxiaoqi.security.auth.client.annotation.IgnoreClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.IgnoreUserToken;
import com.github.wxiaoqi.security.common.audit.AceAudit;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;

import com.github.wxiaoqi.security.common.util.ExcelUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.hodo.jjaccount.biz.HdBankAccountBiz;
import com.hodo.jjaccount.biz.HdCompanyBiz;
import com.hodo.jjaccount.entity.HdBankPending;
import com.hodo.jjaccount.entity.HdCompany;
import com.hodo.jjaccount.entity.HdMatchCompany;
import com.hodo.jjaccount.entity.HdNzDict;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import com.github.wxiaoqi.security.auth.client.annotation.CheckClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.CheckUserToken;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("hdCompany")
@CheckClientToken
@CheckUserToken
@AceAudit
public class HdCompanyController extends BaseController<HdCompanyBiz, HdCompany, String> {
    @Autowired
    private HdBankAccountBiz hdBankAccountBiz;

    //page展示//添加录入人条件6.24

    //获取所有公司名称
    //添加录入人条件6.24
    @ApiOperation("获取所有公司名称")
    @RequestMapping(value = "/getAllCompanyNames", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse<HdCompany> getAllCompanyNames() {
        return new ObjectRestResponse<>().data(baseBiz.getAllCompanyNames());
    }

    /**
     * 批量删除公司操作记录
     *
     * @return
     */
    @ApiOperation("批量删除银行操作记录")
    @RequestMapping(value = "/doBatchDel", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<HdCompany> doBatchDel(String ids) {
        if (StringUtil.isEmpty(ids)) {
            return new ObjectRestResponse<>(1003, "参数传递失败");
        }

        String message = null;
        int count = 0;
        try {
            for (String id : ids.split(",")) {
                baseBiz.deleteById(id);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "公司删除失败";
            return new ObjectRestResponse<>(500, message);
        }
        message = "您成功删除" + count + "条";
        return new ObjectRestResponse<>(200, message);
    }

    //重写公司录入,公司名不可重复
    //重寫插入，判斷是否内转外转重复
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("新增单个对象")
    public ObjectRestResponse<HdCompany> add(@RequestBody HdCompany entity) {
        try {
            baseBiz.insertSelective(entity);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return new ObjectRestResponse<>(500, "不可插入同名公司");
        }
        return new ObjectRestResponse<HdCompany>().data(entity);
    }


    /**
     * 导出
     */
    //添加录入人条件6.24
    @ApiOperation("导出")
    @RequestMapping(value = "/exportXls", method = RequestMethod.GET)
    @ResponseBody
//    @IgnoreUserToken
//    @IgnoreClientToken
    public void exportXls(String userName,
                          String company) throws ParseException {
        List<HdCompany> allCompanyEx = baseBiz.getAllCompanyEx(company, BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
        ExcelUtil excelUtil = new ExcelUtil<HdCompany>();
        try {
            excelUtil.print("银行公司管理列表", HdCompany.class, "银行公司管理列表", "导出人:" + userName, "公司管理列表", allCompanyEx, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //添加录入人条件6.24
    @ApiOperation("导入")
    @RequestMapping(value = "/importXls", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<HdCompany> importXls(
            @RequestParam(value = "file", required = true) MultipartFile file
    ) {
        List<HdCompany> companies;
        ExcelUtil excelUtil = new ExcelUtil<HdCompany>();

        try {
            companies = excelUtil.importXls(file, HdCompany.class);
            if (companies != null && companies.size() > 0) {
                baseBiz.batchSave(companies);
            }
        } catch (DuplicateKeyException e) {
            return new ObjectRestResponse<>(500, "导入重名公司");
        } catch (SQLException e) {
            return new ObjectRestResponse<>(500, "数据不完整");
        } catch (Exception e) {
            e.printStackTrace();
            return new ObjectRestResponse<>(500, "导入失败");
        }

        //检查是否有同名，是否无名称
        return new ObjectRestResponse<>(200, "导入成功");
    }


}