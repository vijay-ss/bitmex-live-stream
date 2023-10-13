package com.BitmexStreamPublisher;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Utils {

    public static void readConfig() {

        String filePath = "./config";

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.filter(line -> line.contains("="))
                    .forEach(line -> {
                        String[] keyValuePair = line.split("=", 2);
                        String key = keyValuePair[0];
                        String value = keyValuePair[1];

                        System.setProperty(key, value);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
