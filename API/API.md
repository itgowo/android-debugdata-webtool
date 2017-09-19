# Android-Debugdata-Webtool API文档

## 数据库
#### 1. [获取数据库列表](#getDbList)
#### 2. [获取数据库表列表](#getTableList)





[数据库](#db)

[数据库](#db)

[数据库](#db)


<span id="db"></span>
### ++<span id="getDbList">获取数据库列表</span>++


**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**

 ```
 {
     action : getDbList
 }

 ```

|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |执行动作|


 **返回示例**

 ```
 {
     code : 200,
     msg : success,
     dbList : [
          {
            fileName: appinfo.db,
            path: /data/user/0/com.itgowo.tool.android_debugdata_webtool/databases/appinfo.db
          }
     ]
 }

 ```

 **返回参数说明**

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|code |int   |返回结果状态 200表示成功，不是200则提示msg信息  |
|msg |String   |返回状态文本，code不是200则提示msg信息  |
|dbList |array   |返回结果，数据库文件对象数组  |
|fileName |String   |返回结果，文件名  |
|path |String   |返回结果，文件路径  |











 
### ++<span id="getTableList">获取数据库表列表</span>++


**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**


 ```
 {
     action : getTableList,
     database : appinfo.db
 }

 ```



|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |请求参数|
|database |是  |string | 请求参数    |


 **返回示例**

```

 {
     code : 200,
     msg : success,
     dbVersion : 1,
     tableList :[
          android_metadata,
          historycache,
          sqlite_sequence
     ]
 }

```

 **返回参数说明**

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|code |int   |返回结果状态 200表示成功，不是200则提示msg信息  |
|msg |String   |返回状态文本，code不是200则提示msg信息  |
|dbVersion |int   |数据库版本  |
|tableList |array   |数据表名字列表数组  |
