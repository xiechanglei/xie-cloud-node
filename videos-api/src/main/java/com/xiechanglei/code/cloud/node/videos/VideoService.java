package com.xiechanglei.code.cloud.node.videos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService {

    @Value("${video.path}")
    private String videoPaths;

    @Value("${video.thumb.path}")
    private String thumbPath;
    private final List<File> folders = new ArrayList<>();

    @PostConstruct
    public void init() {
        for (String path : videoPaths.split(";")) {
            File file = new File(path);
            if (file.exists()) {
                folders.add(file);
            }
        }
        File file = new File(thumbPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //empty thumbPath
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }

    }

    /**
     * 获取视频流
     */
    public void writeVideo(String path, OutputStream outputStream, long start, long end) throws IOException {
        File videoFile = new File(FileInfo.decodePath(path));
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(videoFile, "r")) {
            if (end == -1) {
                end = videoFile.length();
            }
            randomAccessFile.seek(start);
            byte[] bytes = new byte[1024 * 1024];
            int len;
            long needLen = end - start;
            while ((len = randomAccessFile.read(bytes)) != -1) {
                if (needLen < len) {
                    outputStream.write(bytes, 0, (int) needLen);
                    outputStream.flush();
                    break;
                } else {
                    outputStream.write(bytes, 0, len);
                    outputStream.flush();
                    needLen -= len;
                }
            }
        }
    }

    /**
     * 获取所有共享的文件夹
     */
    public List<FileInfo> getAllFolders() {
        List<FileInfo> result = new ArrayList<>();
        for (File folder : folders) {
            result.add(new FileInfo(folder));
        }
        return result;
    }

    public List<FileInfo> getFolderFiles(String path) {
        List<FileInfo> result = new ArrayList<>();
        File file = new File(FileInfo.decodePath(path));
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (file.isDirectory()) {
                        result.add(new FileInfo(f));
                    } else if (VideoUtil.isVideoFile(f.getName())) {
                        result.add(new FileInfo(f));
                    }
                }
            }
        }
        return result;
    }

    public FileInfo getFileInfo(String path) {
        return new FileInfo(new File(FileInfo.decodePath(path)));
    }

    public VideoInfo getVideoInfo(String path){
        return VideoUtil.resolveVideoInfo(FileInfo.decodePath(path));
    }

    public File getThumbFile(String path) throws Exception {
        String videoPath = FileInfo.decodePath(path);
        String thumbName = thumbPath  + Md5Util.md5(videoPath) + ".jpg" ;
        File file = new File( thumbName);
        if (!file.exists()) {
            VideoUtil.createThumb(FileInfo.decodePath(path), thumbName);
        }
        return file;
    }



}
