package hku.droneflight.controller;

import hku.droneflight.entity.ResultNum;
import hku.droneflight.entity.Video;
import hku.droneflight.service.VideoService;
import hku.droneflight.util.Result;
import hku.droneflight.util.ResultNumRsp;
import hku.droneflight.util.StringReq;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;

import static hku.droneflight.controller.LiveController.stoppedStreamMap;

@Controller
public class FileController {
    private final ResourceLoader resourceLoader;
    @Autowired
    private VideoService videoService;

    @Value("${file.video.path}")
    private String videoBasePath;

    @Value("${file.result.path}")
    private String resultBasePath;

    @Value("${file.image.path}")
    private String imageBasePath;

    /**
     * 计算帧率和每秒平均个数
     * @param stringReq //文件名
     * @return ResultNumRsp //ResultNum列表
     * @throws IOException
     * @throws EncoderException
     */
    @RequestMapping(value = "/getResultNum")
    @ResponseBody
    public ResultNumRsp getResultNum(@RequestBody StringReq stringReq) throws IOException, EncoderException {
        File file = resourceLoader.getResource("file:" + videoBasePath + stringReq.string +".mp4").getFile();
        File result = resourceLoader.getResource("file:" + resultBasePath + stringReq.string +".txt").getFile();

        try (Scanner sc = new Scanner(new FileReader(result))) {
            ArrayList<String> fileString = new ArrayList<>();
            int cnt = 0;

            sc.useDelimiter("},");  //分隔符
            while (sc.hasNext()) {   //按分隔符读取字符串
                String str = sc.next();
                fileString.add(str);
                cnt++;
            }
            MultimediaObject m = new MultimediaObject(file);

            int seconds = (int)m.getInfo().getDuration() / 1000;    //计算秒数
//            System.out.println(seconds);

            int fps = cnt / seconds;    //计算平均帧率
//            System.out.println(fps);

            cnt = 0;
            ResultNum resultNum = new ResultNum();
            List<ResultNum> resultNumList = new ArrayList<>();
            for (String str : fileString){
                for (String s : str.split(",")){
                    String type = s.split("\"")[1];
                    String sNum = s.split(": ")[1];
                    if (sNum.contains("}]")){
                        sNum = sNum.split("}")[0];
                    }
                    int num = Integer.parseInt(sNum);

                    resultNum.add(type, num);
                }
                if (cnt >= fps){
                    resultNum.divide(fps);
                    resultNumList.add(resultNum);
                    System.out.println(resultNum.toString());
                    resultNum = new ResultNum();
                    cnt = 0;
                }
                cnt++;
            }
            return new ResultNumRsp(Result.SUCCESS, resultNumList);
        }catch (Exception exception){
            System.out.println(exception.toString());
            return new ResultNumRsp(Result.SUCCESS, exception.toString());
        }
    }



    @Autowired
    public FileController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @RequestMapping(value = "image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> showPhotos(String fileName) {
        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + imageBasePath + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "video", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> showVideo(String fileName) {
        try {
            HttpHeaders headers=new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename="+fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resourceLoader.getResource("file:" + videoBasePath + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @RequestMapping(value = "result", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> showResult(String fileName) {
        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + resultBasePath + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @RequestMapping("imageUpload")
    @ResponseBody()
    public ResponseObject upload(@RequestParam("fileName") MultipartFile file, Map<String, Object> map){
        // 上传成功或者失败的提示
        ResponseObject ret = null;
        if (upload(file, imageBasePath, file.getOriginalFilename())){
            // 上传成功，给出页面提示
            ret = getResult("success");
            ret.urlSuffix = "image?fileName=" + file.getOriginalFilename();
        }else {
            ret = getResult("fail");
        }
        return ret;
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
            Integer vid = stoppedStreamMap.get(streamUrl).VideoId;
            Video video = videoService.getById(vid);
            if (video == null) {
                ret = getResult("fail");
                ret.failReason = "user not save video";
                return ret;
            }
            video.setUrl(ret.urlSuffix);
            videoService.updateById(video);
            stoppedStreamMap.remove(streamUrl);
        }else {
            ret = getResult("fail");
        }
        return ret;
    }

    @RequestMapping("/resultUpload")
    @ResponseBody()
    public ResponseObject resultUpload(@RequestParam("streamUrl") String streamUrl, @RequestParam("fileName") MultipartFile file, Map<String, Object> map){
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