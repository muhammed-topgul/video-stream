package com.ask.home.videostream.enumeration;

/*
 * created by Muhammed Topgul
 * on 21/04/2021
 * at 08:50
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VideoParameter {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    VIDEO_CONTENT("video/"),
    CONTENT_RANGE("Content-Range"),
    ACCEPT_RANGES("Accept-Ranges"),
    BYTES("bytes"),
    BYTE_RANGE("1024");

    private final String value;
}
