# DataMock
a library of system data hooker
## 说明
零侵入的系统数据hook库，使用超级简单，支持模拟经纬度、Wifi、基站。
## 使用方式

```
allprojects {
    repositories {
        jcenter()
    }
}
-------

debugImplementation 'com.yizunda:datamock:1.0.0'
```

## 便捷使用

![入口](screenshots/1.png)  

点击齿轮图标进入  

![示例](/screenshots/2.png)  

打开经纬度开关，输入经纬度即可，经纬度以,隔开。
## 代码中使用
### 模拟经纬度
```
        DataMock.enableMockCoordinate(true)
        DataMock.mockCoordinate("113.212445,22.457344")
```
### 模拟wifi信号
```
        DataMock.enableMockWifi(true)
        //传入需要模拟的wifi列表
        DataMock.mockScanResultList = mutableListOf<ScanResult>()
```
### 模拟基站信号
```
        DataMock.enableMockCellInfo(true)
        //传入需要模拟的基站信号列表
        DataMock.mockCellInfoList = mutableListOf<CellInfo>()
```
