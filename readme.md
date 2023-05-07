##### linux
start.sh
```shell
nohup java -jar /home/xie/work-space/servers/xie-cloud-node/cloud-node-application.jar > /dev/null 2>&1 &
```

stop.sh
```shell
ps -ef | grep cloud-node-application.jar | grep -v grep | awk '{print $2}' | xargs kill -9
```

/etc/systemd/system/xie-cloud-node.service
```
[Unit]
Description=xie cloud node service

[Service]
Type=forking
ExecStart=/bin/bash -c "/home/xie/work-space/servers/xie-cloud-node/start.sh"

[Install]
WantedBy=multi-user.target
Alias=xie-cloud-node
```
services
```shell
# 重载配置
systemctl daemon-reload
# 启动服务
systemctl start xie-cloud-node.service
# 停止服务
systemctl stop xie-cloud-node.service
# 查看服务状态
systemctl status xie-cloud-node.service
# 设置开机启动
systemctl enable xie-cloud-node.service
# 取消开机启动
systemctl disable app.service
    

```

##### windows
```bat
# 创建服务
sc create xie-cloud-node.service binpath= G:\xie-cloud-node\start.cmd type= own start= auto displayname= xie-cloud-node.service
# 启动服务
net start xie-cloud-node.service
# 停止服务
net stop xie-cloud-node.service
# 删除服务
sc delete "xie-cloud-node.service"
```
失败  使用 nssm
```shell
nssm install xie-cloud-node G:\xie-cloud-node\start.cmd
```

失败  使用 winsw
