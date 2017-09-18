**简要描述：**

- 获取数据库列表

**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**

 ```
 {
     &quot;action&quot; : &quot;getDbList&quot;
 }

 ```

|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |执行动作|


 **返回示例**

 ```
 {
     &quot;code&quot; : 200,
     &quot;msg&quot; : &quot;success&quot;,
     &quot;dbList&quot; : [
          {
            &quot;fileName&quot;: &quot;appinfo.db&quot;,
            &quot;path&quot;: &quot;/data/user/0/com.itgowo.tool.android_debugdata_webtool/databases/appinfo.db&quot;
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

 **备注**

- 更多返回错误代码请看首页的错误代码描述


