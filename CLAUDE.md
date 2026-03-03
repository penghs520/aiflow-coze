# Claude 工作记录

## Android 端启动方式

### 方式一：使用模拟器
1. 启动 Android 模拟器：
```bash
export ANDROID_SDK_ROOT=/Users/penghongsi/Library/Android/sdk
export ANDROID_HOME=/Users/penghongsi/Library/Android/sdk
/Users/penghongsi/Library/Android/sdk/emulator/emulator -avd Medium_Phone_API_36.1 &
```

2. 启动应用：
```bash
cd mobile
pnpm start
```
然后按 `a` 键在模拟器上运行。

### 方式二：使用真机
1. 通过 USB 连接 Android 手机
2. 在手机上启用开发者选项和 USB 调试  （关于手机-版本信息-连续点击版本号）
3. 验证设备连接：
```bash
adb devices
```

4. 启动应用：
```bash
cd mobile
pnpm start
```
然后按 `a` 键在真机上运行。


                                                                                                                                                                   
### 方式三：使用 Expo Go                                                                                                                                              
1. 在手机上安装 Expo Go 应用                                                                                                                                        
2. 启动开发服务器：                                                                                                                                                 
```bash                                                                                                                                                        
cd mobile                                                                                                                                                      
pnpm start                                                                                                                                                     
```                                                                                                                                                            
3. 用 Expo Go 扫描终端中的二维码即可运行   