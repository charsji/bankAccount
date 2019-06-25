package com.hodo.jjaccount.feign;


import com.github.wxiaoqi.security.auth.client.config.FeignApplyConfiguration;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//feign调用服务
@FeignClient(value = "ace-sender", configuration = FeignApplyConfiguration.class)
public interface SendFeign {

    @RequestMapping(value = "/sendHodo/sendRTX", method = RequestMethod.POST)
    ObjectRestResponse<String> sendRTX(@RequestParam(value = "sender") String sender, @RequestParam(value = "receiver") String receiver, @RequestParam(value = "content") String content);

    @RequestMapping(value = "/sendHodo/sendSms", method = RequestMethod.POST)
    ObjectRestResponse<String> sendSms(@RequestParam(value = "phone") String phone, @RequestParam(value = "type") String type, @RequestParam(value = "timeout") String timeout);

    @RequestMapping(value = "/sendAli/sendSms", method = RequestMethod.POST)
    ObjectRestResponse<String> sendSms(@RequestParam(value = "accessKeyId") String accessKeyId, @RequestParam(value = "accessKeySecret") String accessKeySecret,
                                       @RequestParam(value = "contact") String contact, @RequestParam(value = "msgType") String msgType,
                                       @RequestParam(value = "signName") String signName);

    @RequestMapping(value = "/sendHodo/checkCode", method = RequestMethod.POST)
    ObjectRestResponse<String> checkCode(@RequestParam(value = "phone") String phone, @RequestParam(value = "code") String code, @RequestParam(value = "timeout") String timeout);


    @RequestMapping(value = "/sendHodo/sendSmsTwo", method = RequestMethod.POST)
    ObjectRestResponse<String> sendSmsTwo(@RequestParam(value = "phone") String phone, @RequestParam(value = "type") String type, @RequestParam(value = "timeout") String timeout);

    @RequestMapping(value = "/sendHodo/checkCodeTwo", method = RequestMethod.POST)
    ObjectRestResponse<String> checkCodeTwo(@RequestParam(value = "phone") String phone, @RequestParam(value = "code") String code, @RequestParam(value = "timeout") String timeout);
}




