package com.marcuschu.webcodegenerate.service;

public interface ScreenshotService {

    /**
     * 生成网页截图
     * @param webUrl 网页链接
     * @return String
     */
    String generateAndUploadScreenshot(String webUrl);

}