package com.tse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Controller
@EnableBinding(Sink.class)
public class SpringStreamRabbitSink {

    private static Logger logger = Logger.getLogger(SpringStreamRabbitSink.class);

    private Map<String, Object> imageMap;

    @Value("${baseImage}")
    private String baseImagePath;

    @StreamListener(Sink.INPUT)
    public void processVote(Map<String, Object> map) {
        if (map != null) {
            imageMap = new HashMap<>(map);
            String imageName = String.valueOf(map.get("imageName"));
            String best_lable = String.valueOf(map.get("best_lable"));
            String best_probability = String.valueOf(map.get("best_probability"));
            String second_lable = String.valueOf(map.get("second_lable"));
            String second_probability = String.valueOf(map.get("second_probability"));
            logger.info("the image:" + imageName + " BEST MATCH " + best_lable + ":" + best_probability + "%");
            logger.info("the image:" + imageName + " SECOND MATCH " + second_lable + ":" + second_probability + "%");
        }
    }

    @GetMapping(value = "/index")
    public String index(Model model) {
        model.addAttribute("image", imageMap);
        StringBuffer sb = new StringBuffer();
        StringBuffer sbTemp = new StringBuffer();
        if (imageMap != null) {
            sb.append("<img style=\"margin: 0 auto;\" src=\"data:image/jpg;base64," + imageToBase64(String.valueOf(imageMap.get("imagePath"))) + "\"/></br>\n");
            sb.append("        <h3>the image : " + imageMap.get("imageName") + ": </h3>\n");
            sb.append("        <p style=\"font-size: medium;color: red;\">BEST MATCH : " + imageMap.get("best_lable") + " :</p>\n");
            sb.append("        <h4 style=\"font-size: large\">" + imageMap.get("best_probability") + "%</h4>\n");
            sb.append("        <p style=\"font-size: medium;color: red;\">SECOND MATCH :" + imageMap.get("second_lable") + "  :</p>\n");
            sb.append("        <h4 style=\"font-size: large\">" + imageMap.get("second_probability") + "%</h4>\n");
            sb.append("        </br>");
            model.addAttribute("myDiv", sb.toString());
        } else {
            sbTemp.append("<img style=\"margin: 0 auto;\" src=\"data:image/jpg;base64," + imageToBase64(baseImagePath) + "\"/></br>\n");
            model.addAttribute("myDiv", sbTemp.toString());
        }
        return "index";
    }

    public String imageToBase64(String imagePath) {
        String imageStr = null;
        if (imagePath != null) {
            try {
                InputStream in = new FileInputStream(imagePath);
                byte[] data = new byte[in.available()];
                in.read(data);
                imageStr = new BASE64Encoder().encode(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imageStr;
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringStreamRabbitSink.class, args);
    }
}
