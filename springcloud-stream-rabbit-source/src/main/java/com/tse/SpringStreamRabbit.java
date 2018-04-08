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

import java.nio.file.*;
import java.util.List;

@SpringBootApplication
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
