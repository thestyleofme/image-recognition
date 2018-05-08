package com.tse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@SpringBootApplication
@Controller
@EnableBinding(Source.class)
public class SpringStreamRabbit {

    private static Logger logger = Logger.getLogger(SpringStreamRabbit.class);

    @Value("${imagesPath}")
    private String imagesPath;

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller = @Poller(fixedDelay = "1000"))
    public MessageSource<String> createMessage() {
//        return () -> new GenericMessage<>(new SimpleDateFormat().format(new Date()));
        return () -> new GenericMessage<>(getImagePath());
    }

    @RequestMapping("/getImage")
    @ResponseBody
    public Boolean getImage(String imageBase64, String imageName) {
        return decodeBase64ToImage(imageBase64, imageName);
    }

    /**
     * 将Base64位编码的图片进行解码，并保存到指定目录
     *
     * @param base64 base64编码的图片信息
     * @return
     */
    public Boolean decodeBase64ToImage(String base64, String imgName) {
        String imagePath = imagesPath + "/" + imgName;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            FileOutputStream write = new FileOutputStream(new File(imagePath));
            byte[] decoderBytes = decoder.decodeBuffer(base64);
            write.write(decoderBytes);
            write.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getImagePath() {
//        return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(new Date());
        String imagePath = null;
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Paths.get(imagesPath).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
//            WatchKey watchKey = watchService.poll(60L, TimeUnit.SECONDS);
            WatchKey watchKey = watchService.take();
            List<WatchEvent<?>> events = watchKey.pollEvents();
            for (WatchEvent event : events) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                WatchEvent<Path> ev = event;
                Path temp = ev.context();
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    String imageName = temp.getFileName().toString();
                    logger.info("add image:" + imageName);
                    imagePath = imagesPath + "/" + imageName;
                    break;
                }
                if (!watchKey.reset()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringStreamRabbit.class, args);
    }
}
