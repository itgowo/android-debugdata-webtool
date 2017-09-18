**简要描述：**

- 获取文件列表

**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**

 ```
{
    action:    getFileList,
    data:    /data/user/0/com.itgowo.tool.android_debugdata_webtool/databases
}

 ```

|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |执行动作|
|data |否  |string | 要获取列表的根目录，不传则返回app根目录，    |

 **返回示例**

 ```
{
    code: 200,
    msg: success,
    fileList: [
        {
            dir: false,
            fileName: appinfo.db,
            fileSize: 20.00KB,
            fileTime: 2017-09-17 23:19:32,
            path: /data/user/0/com.itgowo.tool.android_debugdata_webtool/databases/appinfo.db,
            rootPath: /data/user/0/com.itgowo.tool.android_debugdata_webtool
        },
        {
            dir: false,
            fileName: appinfo.db-journal,
            fileSize: 12.52KB,
            fileTime: 2017-09-17 23:19:32,
            path: /data/user/0/com.itgowo.tool.android_debugdata_webtool/databases/appinfo.db-journal,
            rootPath: /data/user/0/com.itgowo.tool.android_debugdata_webtool
        }
    ]
}

 ```


 **返回参数说明**

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|code |int   |返回结果状态 200表示成功，不是200则提示msg信息  |
|msg |String   |返回状态文本，code不是200则提示msg信息  |
|fileList |array   |返回结果，文件对象数组  |
|dir |array   |返回结果，是否是文件夹  |
|fileName |String   |返回结果，文件名  |
|fileSize |String   |返回结果，文件大小  |
|fileTime |String   |返回结果，文件最后编辑时间  |
|path |String   |返回结果，文件路径  |
|rootPath |String   |返回结果，文件所在的上级目录地址，返回上级传这个参数，如果为空则是请求首页，及data为空  |


 **备注**

- 更多返回错误代码请看首页的错误代码描述


