**简要描述：**

- 获取数据库表列表 

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

 **备注**

- 更多返回错误代码请看首页的错误代码描述


