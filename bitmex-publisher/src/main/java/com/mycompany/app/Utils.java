package com.mycompany.app;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Utils {

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

    public static void getCreds() {
        String filePath = "./config";
        Path file = Paths.get(filePath);

        if(Files.exists(file)) {
            System.out.println(filePath + " exists");
            try {
                readConfig();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            System.out.println("Local file import failed.");
            System.out.println("Attempting GCP import...");
            try {
                String pubSubTopic = AccessGcpSecret.accessSecretVersion(
                        System.getenv("PROJECT_ID"), "topicId", "latest"
                );
                System.setProperty("topicId", pubSubTopic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void readConfig() throws Exception {

        String filePath = "./config";
        Path file = Paths.get(filePath);

        if(Files.exists(file)) {

            try (Stream<String> lines = Files.lines(file)) {
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
}
