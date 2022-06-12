package hku.picshare.controller;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Controller
public class ImageFileController {
    private final ResourceLoader resourceLoader;
    @Value("${file.images.path}")
    private String path;

    @Autowired
    public ImageFileController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping(value = "imageTest")
    @ResponseBody()
    public ResponseObject test() {
        ResponseObject entity = new ResponseObject();
        entity.result = "success";
        entity.failReason = "";
        return entity;
    }

    @RequestMapping(value = "image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> showPhotos(String fileName) {

        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + path + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping("imageUpload")
    @ResponseBody()
    public ResponseObject upload(@RequestParam("fileName") MultipartFile file, Map<String, Object> map){
        // 上传成功或者失败的提示
        ResponseObject ret = null;
        if (upload(file, path, file.getOriginalFilename())){
            // 上传成功，给出页面提示
            ret = getResult("success");
            ret.urlSuffix = "image?fileName=" + file.getOriginalFilename();
        }else {
            ret = getResult("fail");
        }
        return ret;
    }

    public static boolean upload(MultipartFile file, String path, String fileName) {

        // 生成新的文件名
        //String realPath = path + "/" + FileNameUtils.getFileName(fileName);

        //使用原文件名
        String realPath = path + "/" + fileName;

        File dest = new File(realPath);

        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            //保存文件
            file.transferTo(dest);
            return true;
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ResponseObject getResult(String result) {
        ResponseObject object = new ResponseObject();
        object.result = result;
        return object;
    }

    @Data
    @ToString
    public static class ResponseObject {
        String result;
        String failReason;
        String urlSuffix;
    }
}
