**简要描述：**

- 删除文件

**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**

 ```
{
    "action":    "deleteFile",
    "data":    "/data/user/0/com.itgowo.tool.android_debugdata_webtool/databases"
}

 ```

|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |执行动作|
|data |否  |string | 要获取列表的根目录，不传则返回app根目录，    |

 **返回示例**

 ```
{
    "code": 200,
    "msg": "success"
}

 ```


 **返回参数说明**

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|code |int   |返回结果状态 200表示成功，不是200则提示msg信息  |
|msg |String   |返回状态文本，code不是200则提示msg信息  |



 **备注**

- 更多返回错误代码请看首页的错误代码描述


