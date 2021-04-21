package com.ask.home.videostream.service;

import com.ask.home.videostream.enumeration.VideoParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
public class VideoStreamService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${video.file.path}")
    private String filePath;

    @Value("${video.file.name}")
    private String fileName;

    public ResponseEntity<byte[]> prepareContent(String fileType, String range) {
        System.out.println("PATH: " + filePath);
        System.out.println("NAME: " + fileName);

        long rangeStart = 0;
        long rangeEnd;
        byte[] data;
        Long fileSize;
        String fullFileName = this.fileName + "." + fileType;
        try {
            fileSize = getFileSize(fullFileName);
            if (range == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .header(VideoParameter.CONTENT_TYPE.getValue(), VideoParameter.VIDEO_CONTENT.getValue() + fileType)
                        .header(VideoParameter.CONTENT_LENGTH.getValue(), String.valueOf(fileSize))
                        .body(readByteRange(fullFileName, rangeStart, fileSize - 1)); // Read the object and convert it as bytes
            }
            String[] ranges = range.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize - 1;
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }
            data = readByteRange(fullFileName, rangeStart, rangeEnd);
        } catch (IOException e) {
            logger.error("Exception while reading the file {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(VideoParameter.CONTENT_TYPE.getValue(), VideoParameter.VIDEO_CONTENT.getValue() + fileType)
                .header(VideoParameter.ACCEPT_RANGES.getValue(), VideoParameter.BYTES.getValue())
                .header(VideoParameter.CONTENT_LENGTH.getValue(), contentLength)
                .header(VideoParameter.CONTENT_RANGE.getValue(), VideoParameter.BYTES.getValue() + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .body(data);


    }

    public byte[] readByteRange(String filename, long start, long end) throws IOException {
        Path path = Paths.get(Objects.requireNonNull(getFilePath()), filename);
        try (InputStream inputStream = (Files.newInputStream(path));
             ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[Integer.parseInt(VideoParameter.BYTE_RANGE.getValue())];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                bufferedOutputStream.write(data, 0, nRead);
            }
            bufferedOutputStream.flush();
            byte[] result = new byte[(int) (end - start) + 1];
            System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
            return result;
        }
    }

    private String getFilePath() {
        URL url;
        try {
            url = new FileSystemResource(this.filePath).getURL();
            return new File(url.getFile()).getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getFileSize(String fileName) {
        return Optional.ofNullable(fileName)
                .map(file -> Paths.get(Objects.requireNonNull(getFilePath()), file))
                .map(this::sizeFromFile)
                .orElse(0L);
    }

    private Long sizeFromFile(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ioException) {
            logger.error("Error while getting the file size", ioException);
        }
        return 0L;
    }
}