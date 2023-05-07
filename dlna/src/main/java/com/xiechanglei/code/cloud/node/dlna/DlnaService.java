package com.xiechanglei.code.cloud.node.dlna;

import org.cybergarage.upnp.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class DlnaService extends ControlPoint {

    @PostConstruct
    public void init() {
        this.start();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 60);
                    this.updateDeviceList();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public List<DlnaDevice> getDlanDeviceList() throws InterruptedException {
        synchronized (DlnaService.class) {
           if (this.getDeviceList().size() == 0) {
                this.updateDeviceList();
            }
            DeviceList deviceList = this.getDeviceList();
            List<DlnaDevice> dlnaDevices = new ArrayList<>();
            for (int i = 0; i < deviceList.size(); i++) {
                Device device = deviceList.getDevice(i);
                String location = device.getLocation();
                //目前只支持 dmr 设备与dlna设备
                if (location.endsWith("dmr") || location.endsWith("description.xml")) {
                    DlnaDevice dlnaDevice = new DlnaDevice(device.getFriendlyName(), device.getUDN());
                    dlnaDevices.add(dlnaDevice);
                }
            }
            return dlnaDevices;
        }
    }

    public void updateDeviceList() throws InterruptedException {
        synchronized (DlnaService.class) {
            DeviceList devices = this.getDeviceList();
            for (int i = 0; i < devices.size(); i++) {
                this.removeDevice(devices.getDevice(i));
            }
            this.search();
            Thread.sleep(3000);
        }
    }

    public void playVideo(String path, String deviceId) throws Exception {
        Device device = null;
        DeviceList devices = this.getDeviceList();
        for (int i = 0; i < devices.size(); i++) {
            Device d = devices.getDevice(i);
            if (d.getUDN().equals(deviceId)) {
                device = d;
                break;
            }
        }
        if (device == null) {
            throw new Exception("device is not exist");
        }

        String location = device.getLocation();
        if (location.endsWith("dmr")) { //
//            sendDlnaCommand(device, path,true);
        } else if (location.endsWith("description.xml")) {
            sendDlnaCommand(device, path, false);
        }
    }

    /**
     * 发送dlna 播放视频
     * @param device
     * @param path
     * @param autoplay 是否自动播放
     * @throws Exception
     */
    private void sendDlnaCommand(Device device, String path, boolean autoplay) throws Exception {
        Action action = device.getAction("SetAVTransportURI");
        if (action != null) {
            action.setArgumentValue("InstanceID", "0");
            action.setArgumentValue("CurrentURI", path);//播放地址
            action.setArgumentValue("CurrentURIMetaData", "");
            boolean result = action.postControlAction();
            if (!result) {
                throw new Exception("unsupported video format or url protocol");
            }
            if (autoplay) {
                action = device.getAction("Play");
                action.setArgumentValue("InstanceID", "0");
                action.setArgumentValue("Speed", "1");
                action.postControlAction();
            }
        }
    }


//    public static void main(String[] args) throws Exception {
//        DlnaService dlnaService = new DlnaService();
//        dlnaService.init();
//        List<DlnaDevice> dlanDeviceList = dlnaService.getDlanDeviceList();
//        for (DlnaDevice device : dlanDeviceList) {
//            dlnaService.playVideo("http://192.168.3.105:19000/api/video/stream?path=RTpcdGVtcF9zc1wyN1xndW9jaGFuMjA0OC5jb20tOC5tcDQ=", device.udn);
//        }
//    }

}
