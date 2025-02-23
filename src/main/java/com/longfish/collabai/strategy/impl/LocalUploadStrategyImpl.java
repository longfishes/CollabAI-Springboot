package com.longfish.collabai.strategy.impl;

import com.longfish.collabai.properties.LocalProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service("localUploadStrategyImpl")
public class LocalUploadStrategyImpl extends AbstractUploadStrategyImpl {

    @Autowired
    private LocalProperties localProperties;

    @Override
    public Boolean exists(String filePath) {
        return new File(localProperties.getPath() + filePath).exists();
    }

    @Override
    public void upload(String path, String fileName, InputStream inputStream) throws IOException {
        Path uploadFilePath = Paths.get(localProperties.getPath() + path + fileName);
        Path parentDir = uploadFilePath.getParent();
        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        try (OutputStream outputStream = new FileOutputStream(uploadFilePath.toFile())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    @Override
    public String getFileAccessUrl(String filePath) {
        return localProperties.getUrl() + filePath;
    }
}
