package com.xiechanglei.code.cloud.node.videos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/api/video")
public class VideoApiController {
    private static final int DATA_BLOCK_SIZE = 1024 * 1024 * 100;
    @Autowired
    private VideoService videoService;



    /**
     * 获取所有的资源目录节点
     *
     * @return
     */
    @RequestMapping("/listShareFolder")
    @ResponseBody
    public List<FileInfo> listShareFolder() {
        return videoService.getAllFolders();
    }

    @RequestMapping("/listFolderFiles")
    @ResponseBody
    public List<FileInfo> listFolderFiles(String path) {
        return videoService.getFolderFiles(path);
    }
    @RequestMapping("/info")
    @ResponseBody
    public VideoInfo getVideoInfo(String path) {
        return videoService.getVideoInfo(path);
    }


    @RequestMapping("thumb")
    public void thumb(String path, @RequestHeader(value = "If-Modified-Since", required = false) String since, HttpServletResponse response) throws Exception {
        if (since != null) {
            response.setStatus(304);
        } else {
            File file = videoService.getThumbFile(path);
            if (!file.exists()) {
                response.setStatus(404);
            } else {
                response.setHeader("last-modified", new Date().toString());
                response.setHeader("Content-Type", "image/jpeg");
                response.setHeader("Content-Length", String.valueOf(file.length()));
                try(InputStream in = new FileInputStream(file)) {
                    StreamUtils.copy(in, response.getOutputStream());
                }
            }
        }
    }
    @RequestMapping("/stream")
    public void getVideo(String path,
                         @RequestHeader(required = false, value = "Range") String range, HttpServletResponse response) {
        try {
            FileInfo info = videoService.getFileInfo(path);
            String formatFileName = info.getName().toLowerCase();
            if (formatFileName.endsWith(".mp4")) {
                response.setHeader("Content-Type", "video/mp4");
            } else if (formatFileName.endsWith(".mkv")) {
                response.setHeader("Content-Type", "video/x-matroska");
            }
            response.setHeader("Content-Length", String.valueOf(info.getSize()));
            if (range != null) {
                response.setStatus(206);

                long start = Long.parseLong(range.substring(range.indexOf("=") + 1, range.indexOf("-")));
                long end = Math.min(start + DATA_BLOCK_SIZE - 1, info.getSize() - 1);

                response.setHeader("Accept-Ranges", "bytes");
                response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + info.getSize());

                videoService.writeVideo(path, response.getOutputStream(), start, end + 1);
            } else {
                response.setStatus(200);
//                response.setHeader("Accept-Ranges", "bytes");
                response.setHeader("Content-Range", "bytes " + 0 + "-" + 1 + "/" + info.getSize());

                videoService.writeVideo(path, response.getOutputStream(), 0, 1 + 1);
            }
        } catch (Exception e) {
            response.setStatus(404);
        }
    }

    @RequestMapping("/play")
    public void playVideo(String path,
                         @RequestHeader(required = false, value = "Range") String range, HttpServletResponse response) {
        try {
            FileInfo info = videoService.getFileInfo(path);
            String formatFileName = info.getName().toLowerCase();
            if (formatFileName.endsWith(".mp4")) {
                response.setHeader("Content-Type", "video/mp4");
            } else if (formatFileName.endsWith(".mkv")) {
                response.setHeader("Content-Type", "video/x-matroska");
            }
            if (range != null) {
                response.setStatus(206);
                long start = Long.parseLong(range.substring(range.indexOf("=") + 1, range.indexOf("-")));
                long end = Math.min(start + DATA_BLOCK_SIZE - 1, info.getSize() - 1);
                response.setHeader("Accept-Ranges", "bytes");
                response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + info.getSize());
                response.setHeader("Content-Length", String.valueOf(end - start + 1));

                videoService.writeVideo(path, response.getOutputStream(), start, end + 1);
            } else {
                response.setStatus(200);
                response.setHeader("Content-Length", String.valueOf(info.getSize()));
                videoService.writeVideo(path, response.getOutputStream(), 0, -1);
            }
        } catch (Exception e) {
            response.setStatus(404);
        }
    }
}
