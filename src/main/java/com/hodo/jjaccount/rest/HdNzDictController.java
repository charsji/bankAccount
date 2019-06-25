package com.hodo.jjaccount.rest;

import com.github.ag.core.context.BaseContextHandler;
import com.github.wxiaoqi.security.auth.client.annotation.IgnoreClientToken;
import com.github.wxiaoqi.security.auth.client.annotation.IgnoreUserToken;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;

import com.github.wxiaoqi.security.common.util.ExcelUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.UUIDUtils;
import com.hodo.jjaccount.biz.HdBankAccountBiz;
import com.hodo.jjaccount.biz.HdNzDictBiz;
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

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("hdNzDict")
@CheckClientToken
@CheckUserToken
public class HdNzDictController extends BaseController<HdNzDictBiz, HdNzDict, String> {
    @Autowired
    private HdBankAccountBiz hdBankAccountBiz;

    //重寫插入，判斷是否内转外转重复
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation("新增单个对象")
    public ObjectRestResponse<HdNzDict> add(@RequestBody HdNzDict entity) {
        try {
            baseBiz.insertSelective(entity);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            return new ObjectRestResponse<>(500, "有外部账号或内部账号重复插入");
        }
        return new ObjectRestResponse<HdNzDict>().data(entity);
    }

    /**
     * 批量删除银行账户
     *
     * @return
     */
    @ApiOperation("批量删除公司名匹配表")
    @RequestMapping(value = "/doBatchDel", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<String> doBatchDel(String ids) {
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
            message = "银行账户删除失败";
            return new ObjectRestResponse<>(500, message);
        }
        message = "您成功删除" + count + "条";
        return new ObjectRestResponse<>(200, message);
    }

    /**
     * 重写查询加入银行名称模糊查询
     */
//    @ApiOperation("分页获取数据2")
//    @RequestMapping(value = "/page", method = RequestMethod.GET)
//    @ResponseBody
//    public TableResultResponse<HdNzDict> list(@RequestParam Map<String,Object> params){
//
//    }

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
                          String bank) throws ParseException {
        List<HdNzDict> allNzDictEx = baseBiz.getAllNzDictEx(bank, BaseContextHandler.getTenantID(),BaseContextHandler.getUserID());
        ExcelUtil excelUtil = new ExcelUtil<HdNzDict>();
        try {
            excelUtil.print("银行账户管理列表", HdNzDict.class, "银行账户管理列表", "导出人:" + userName, "银行账户管理列表", allNzDictEx, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //添加录入人条件6.24
    @ApiOperation("导入")
    @RequestMapping(value = "/importXls", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse<HdNzDict> importXls(
            @RequestParam(value = "file", required = true) MultipartFile file
    ) {

        //内转账号，外部账号，银行名称必填
        List<HdNzDict> hdNzDicts;
        ExcelUtil excelUtil = new ExcelUtil<HdNzDict>();
        try {
            hdNzDicts = excelUtil.importXls(file, HdNzDict.class);
            int count = 0;
            if (hdNzDicts != null && hdNzDicts.size() > 0) {
                for (HdNzDict hdNzDict : hdNzDicts) {
                    count++;
                    if (StringUtil.isEmpty(hdNzDict.getNzid())) {
                        return new ObjectRestResponse<>(1004, "第" + count + "条数据内转账号为空");
                    }
                    if (StringUtil.isEmpty(hdNzDict.getWbzh())) {
                        return new ObjectRestResponse<>(1004, "第" + count + "条数据外转账号为空");
                    }
                    if (StringUtil.isEmpty(hdNzDict.getBank())) {
                        return new ObjectRestResponse<>(1004, "第" + count + "条数据银行名称为空");
                    }
                    //6.25
                    if(StringUtil.isEmpty(hdNzDict.getBankType())){

                    }

                }
            }
            baseBiz.batchSave(hdNzDicts);
        } catch (DuplicateKeyException e) {
            return new ObjectRestResponse<>(500, "有外部账号或内部账号重复插入");
        } catch (Exception e) {
            return new ObjectRestResponse<>(500, "导入失败");
        }


        return new ObjectRestResponse<>(200, "导入成功");
    }


}