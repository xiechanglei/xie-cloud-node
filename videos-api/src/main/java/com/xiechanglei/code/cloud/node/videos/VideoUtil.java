package com.xiechanglei.code.cloud.node.videos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class VideoUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(VideoUtil.class);

    public static final String[] VIDEO_SUFFIX = new String[]{".mp4", ".avi", ".mkv", ".rmvb", ".flv", ".wmv", ".mov", ".3gp", ".mpg", ".mpeg", ".m4v", ".f4v", ".rm", ".asf", ".dat", ".vob", ".m2ts", ".mts", ".ts", ".tp", ".trp", ".mxf", ".nsv"};

    public static boolean isVideoFile(String fileName) {
        fileName = fileName.toLowerCase();
        for (String suffix : VIDEO_SUFFIX) {
            if (fileName.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取视频第一帧的画面
     */
    public static void createThumb(String videoPath, String framePath) throws Exception {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            String[] command = new String[]{"F:\\program_sdk\\ffmpeg\\bin\\ffmpeg", "-i", videoPath, "-y", "-f", "image2", "-ss", "1", "-t", "0.001", framePath};
            CommandUtil.executeCommand(command);
        }else{
            String[] command = new String[]{"F:\\program_sdk\\ffmpeg\\bin\\ffmpeg", "-i", videoPath, "-y", "-f", "image2", "-ss", "1", "-t", "0.001", framePath};
            CommandUtil.executeCommand(command);
        }

    }

    public static VideoInfo resolveVideoInfo(String path) {
        //linux
        String[] command;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            command = new String[]{"cmd", "/c", "F:\\program_sdk\\ffmpeg\\bin\\ffprobe -v quiet -print_format json -show_format -show_streams \"" + path + "\""};
        } else {
            command = new String[]{"sh","-c","F:\\program_sdk\\ffmpeg\\bin\\ffprobe -v quiet -print_format json -show_format -show_streams \"" + path + "\""};
        }
        try {
            String result = CommandUtil.executeCommand(command);
            VideoInfo videoInfo = new VideoInfo(new File(path));
            JsonNode jsonNode = mapper.readTree(result);
            JsonNode streams = jsonNode.get("streams");
            for (JsonNode stream : streams) {
                String codecType = stream.get("codec_type").asText();
                if ("video".equals(codecType)) {
                    String codecName = stream.get("codec_name").asText();
                    if ("png".equalsIgnoreCase(codecName)) {
                        continue;
                    }
                    videoInfo.videoCodec = codecName;
                    videoInfo.width = stream.get("width").asInt();
                    videoInfo.height = stream.get("height").asInt();
                    videoInfo.coded_width = stream.get("coded_width").asInt();
                    videoInfo.coded_height = stream.get("coded_height").asInt();

                    JsonNode nbFrames = stream.get("nb_frames");
                    if (nbFrames != null) {
                        videoInfo.frames = nbFrames.asInt();
                    }
                    JsonNode tags = stream.get("tags");
                    if (tags != null) {
                        JsonNode rotate = tags.get("NUMBER_OF_FRAMES");
                        if (rotate != null) {
                            videoInfo.frames = rotate.asInt();
                        }
                    }
                } else if ("audio".equals(codecType)) {
                    videoInfo.audioCodec = stream.get("codec_name").asText();
                    videoInfo.audioChannels = stream.get("channels").asInt();
                    videoInfo.audioSampleRate = stream.get("sample_rate").asInt();
                }
            }
            JsonNode format = jsonNode.get("format");
            videoInfo.duration = format.get("duration").asDouble();
            return videoInfo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

