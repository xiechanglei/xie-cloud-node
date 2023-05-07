package com.xiechanglei.code.cloud.node.videos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoInfo extends  FileInfo{
    public int width;
    public int height;

    public int coded_width;
    public int coded_height;
    public String videoCodec;
    public double duration;
    public int frames;
    public String audioCodec;
    public int audioChannels;
    public int audioSampleRate;

    public VideoInfo(File file) {
        super(file);
    }
    //时长

}
