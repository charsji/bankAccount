package com.hodo.jjaccount.feign;


import com.github.wxiaoqi.security.auth.client.config.FeignApplyConfiguration;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

//feign调用服务
@FeignClient(value = "ace-file-upload", configuration = FeignApplyConfiguration.class)
public interface FileUploadFeign {

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    ObjectRestResponse<String> uploadFile(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "path") String path);

    @RequestMapping(value = "/uploadByBytes", method = RequestMethod.POST)
    ObjectRestResponse<String> uploadByBytes(@RequestParam(value = "file") File file, @RequestParam(value = "filePath") String filePath);

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    ObjectRestResponse<String> deleteFile(@RequestParam(value = "path") String path);
}




