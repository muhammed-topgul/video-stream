package com.ask.home.videostream.controller;

import com.ask.home.videostream.service.VideoStreamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/video")
public class VideoStreamController {

    private final VideoStreamService videoStreamService;

    public VideoStreamController(VideoStreamService videoStreamService) {
        this.videoStreamService = videoStreamService;
    }

    @GetMapping("/stream/{fileType}")
    public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable("fileType") String fileType) {
        return Mono.just(videoStreamService.prepareContent(fileType, httpRangeList));
    }
}
