//package com.hodo.jjaccount.rest;
//
//import com.github.wxiaoqi.security.auth.client.annotation.CheckClientToken;
//import com.github.wxiaoqi.security.auth.client.annotation.CheckUserToken;
//import com.github.wxiaoqi.security.auth.client.annotation.IgnoreClientToken;
//import com.github.wxiaoqi.security.auth.client.annotation.IgnoreUserToken;
//import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
//import com.github.wxiaoqi.security.common.rest.BaseController;
//import com.github.wxiaoqi.security.common.util.DateUtils;
//import com.github.wxiaoqi.security.common.util.ExcelUtil;
//import com.github.wxiaoqi.security.common.util.MyBeanUtils;
//import com.github.wxiaoqi.security.common.util.StringUtil;
//import com.hodo.jjaccount.biz.HdBankAccountBiz;
//import com.hodo.jjaccount.biz.HdCompanyBiz;
//import com.hodo.jjaccount.common.TableResultResponse;
//import com.hodo.jjaccount.entity.HdBankAccount;
//import com.hodo.jjaccount.entity.HdBankPending;
//import com.hodo.jjaccount.entity.Statistics;
//import com.hodo.jjaccount.util.jjUtil;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.apache.commons.collections.map.HashedMap;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("hdBankAccount")
//@CheckClientToken
//@CheckUserToken
//public class HdStaticticsController extends BaseController<HdBankAccountBiz,HdBankAccount,String> {
//    @Autowired
//    private HdCompanyBiz hdCompanyBiz;
//
//    @RequestMapping(params = "getStatisticsList",method = RequestMethod.GET)
//    @ResponseBody
//    public TableResultResponse<Statistics> getStatisticsList(String dateStart,
//                                                             String dateEnd){
//
//
//        return new TableResultResponse<>(total,)
//    }
//
//
//
//
//}
