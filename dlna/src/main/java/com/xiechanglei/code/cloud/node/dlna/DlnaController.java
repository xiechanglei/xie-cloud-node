package com.xiechanglei.code.cloud.node.dlna;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dlna")
public class DlnaController {
    @Autowired
    private DlnaService dlnaService;


    @RequestMapping("/play")
    public String play(String url,String udn) throws Exception {
        dlnaService.playVideo( url,udn);
        return "success";
    }

    @RequestMapping("/list")
    public List<DlnaDevice> getDlnaDeviceList() throws Exception {
        return dlnaService.getDlanDeviceList();
    }
}
