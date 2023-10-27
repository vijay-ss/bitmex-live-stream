package com.BitmexStreamPublisher;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Utils {

    public static void getCredentials() throws IOException {
        try {
            readConfig();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Local import failed.");
            System.out.println("Attempting GCP import...");
            try {
                String pubSubTopic =AccessGcpSecret.accessSecretVersion(
                        System.getenv("PROJECT_ID"), "topicId", "latest"
                );
                System.setProperty("topicId", pubSubTopic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void getFiles() throws IOException {
        File f = new File("."); // current directory
        File[] files = f.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.print("directory: ");
            } else {
                System.out.print("     file: ");
            }
            System.out.println(file.getCanonicalPath());
        }
    }

    public static void readConfig() throws Exception {

        String filePath = "./config";

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.filter(line -> line.contains("="))
                    .forEach(line -> {
                        String[] keyValuePair = line.split("=", 2);
                        String key = keyValuePair[0];
                        String value = keyValuePair[1];

                        System.setProperty(key, value);
                    });
            System.out.println("Loaded properties from " + filePath);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
