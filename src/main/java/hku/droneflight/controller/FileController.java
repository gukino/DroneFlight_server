package hku.droneflight.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hku.droneflight.entity.Video;
import hku.droneflight.service.VideoService;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static hku.droneflight.controller.LiveController.streamVideoMap;


@Controller
public class FileController {
    private final ResourceLoader resourceLoader;
    @Autowired
    private VideoService videoService;

    @Value("${file.video.path}")
    private String videoBasePath;

    @Value("${file.result.path}")
    private String resultBasePath;


    @Autowired
    public FileController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @RequestMapping(value = "video", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> showVideo(String fileName) {
        try {
            return ResponseEntity.ok(resourceLoader.getResource(videoBasePath + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @RequestMapping(value = "result", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> showResult(String fileName) {
        try {
            return ResponseEntity.ok(resourceLoader.getResource(resultBasePath + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @RequestMapping("/videoUpload")
    @ResponseBody()
    public ResponseObject videoUpload(@RequestParam("streamUrl") String streamUrl, @RequestParam("fileName") MultipartFile file, Map<String, Object> map){
        // 上传成功或者失败的提示
        ResponseObject ret = null;
        if (upload(file, videoBasePath, file.getOriginalFilename())){
            // 上传成功，给出页面提示
            ret = getResult("success");
            ret.urlSuffix = "video?fileName=" + file.getOriginalFilename();
            Integer vid = streamVideoMap.get(streamUrl);
            LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(vid!=null,Video::getId,vid);
            Video video = videoService.getOne(queryWrapper);
            video.setUrl(ret.urlSuffix);
            videoService.updateById(video);

        }else {
            ret = getResult("fail");
        }
        return ret;
    }

    @RequestMapping("/resultUpload/")
    @ResponseBody()
    public ResponseObject resultUpload(@RequestParam("fileName") MultipartFile file, Map<String, Object> map){
        // 上传成功或者失败的提示
        ResponseObject ret = null;
        if (upload(file, resultBasePath, file.getOriginalFilename())){
            // 上传成功，给出页面提示
            ret = getResult("success");
            ret.urlSuffix = "result?fileName=" + file.getOriginalFilename();
        }else {
            ret = getResult("fail");
        }
        return ret;
    }

    public static boolean upload(MultipartFile file, String path, String fileName) {

        // 生成新的文件名
        //String realPath = path + "/" + FileNameUtils.getFileName(fileName);
        //使用原文件名
        String realPath = path + fileName;

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