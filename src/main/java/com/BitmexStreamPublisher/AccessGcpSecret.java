package com.BitmexStreamPublisher;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import java.io.IOException;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

public class AccessGcpSecret {

    // Access the payload for the given secret version if one exists. The version
    // can be a version number as a string (e.g. "5") or an alias (e.g. "latest").
    public static String accessSecretVersion(String projectId, String secretId, String versionId)
            throws IOException {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, versionId);

            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);

            byte[] data = response.getPayload().getData().toByteArray();
            Checksum checksum = new CRC32C();
            checksum.update(data, 0, data.length);
            if (response.getPayload().getDataCrc32C() != checksum.getValue()) {
                System.out.println("Data corruption detected.");
                return null;
            }

            return response.getPayload().getData().toStringUtf8();
        }
    }
}
