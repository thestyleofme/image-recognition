package com.tse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableBinding(Processor.class)
public class SpringStreamRabbitProcessor {

    private static Logger logger = Logger.getLogger(SpringStreamRabbitProcessor.class);

    @Value("${modelDir}")
    private String modelDir;

    @Value("${pbName}")
    private String pbName;

    @Value("${labelsName}")
    private String labelsName;

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public Map<String, Object> transform(String imagePath) {
        if (imagePath != null) {
            Map<String, Object> resultMap = new HashMap<>();
            String[] imagePathArray = imagePath.split("/");
            byte[] graphDef = readAllBytesOrExit(Paths.get(modelDir, pbName));
            List<String> labels =
                    readAllLinesOrExit(Paths.get(modelDir, labelsName));
            byte[] imageBytes = readAllBytesOrExit(Paths.get(imagePath));

            try (Tensor<Float> image = (Tensor<Float>) Tensor.create(imageBytes)) {
                float[] labelProbabilities = executeInceptionGraph(graphDef, image);
                int[] maxThreeIndex = maxThreeIndex(labelProbabilities);
                logger.info(imagePathArray[imagePathArray.length - 1] + " is in the calculation......");
                logger.info(
                        String.format("BEST MATCH: %s (%.2f%% likely)",
                                labels.get(maxThreeIndex[0]),
                                labelProbabilities[maxThreeIndex[0]] * 100f));
                logger.info(
                        String.format("SECOND MATCH: %s (%.2f%% likely)",
                                labels.get(maxThreeIndex[1]),
                                labelProbabilities[maxThreeIndex[1]] * 100f));
                resultMap.put("imageName", imagePathArray[imagePathArray.length - 1]);
                resultMap.put("imagePath", imagePath);
                resultMap.put("best_lable", labels.get(maxThreeIndex[0]));
                resultMap.put("best_probability", labelProbabilities[maxThreeIndex[0]] * 100f);
                resultMap.put("second_lable", labels.get(maxThreeIndex[1]));
                resultMap.put("second_probability", labelProbabilities[maxThreeIndex[1]] * 100f);
                return resultMap;
            }
        }
        return null;
    }

    private static float[] executeInceptionGraph(byte[] graphDef, Tensor<Float> image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g);
                 Tensor<Float> result =
                         s.runner().feed("DecodeJpeg/contents", image).fetch("final_result").run().get(0).expect(Float.class)) {
                final long[] rshape = result.shape();
                if (result.numDimensions() != 2 || rshape[0] != 1) {
                    throw new RuntimeException(
                            String.format(
                                    "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                    Arrays.toString(rshape)));
                }
                int nlabels = (int) rshape[1];
                return result.copyTo(new float[1][nlabels])[0];
            }
        }
    }

    private static int[] maxThreeIndex(float[] probabilities) {
        int result[] = new int[2];
        int best = 0;
        int second = 0;
        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > probabilities[best]) {
                best = i;
            }
        }
        for (int i = 1; i < probabilities.length; ++i) {
            if (i == best) {
                continue;
            }
            if (probabilities[i] > probabilities[second]) {
                second = i;
            }
        }
        result[0] = best;
        result[1] = second;
        return result;
    }

    private static byte[] readAllBytesOrExit(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private static List<String> readAllLinesOrExit(Path path) {
        try {
            return Files.readAllLines(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(0);
        }
        return null;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringStreamRabbitProcessor.class, args);
    }

}
