**简要描述：**

- 更新数据库表内数据，一次只能更新一条数据

**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**

 ```
{
    &quot;action&quot;:    &quot;updateDataToSp&quot;,
    &quot;spFileName&quot;:    &quot;appinfo&quot;,
    &quot;RowDataRequests&quot;:
    [
        {
            &quot;primary&quot;:    true,
            &quot;title&quot;:    &quot;key&quot;,
            &quot;value&quot;:    &quot;ggg&quot;,
            &quot;dataType&quot;:    &quot;text&quot;
        },
        {
            &quot;primary&quot;:    false,
            &quot;title&quot;:    &quot;value&quot;,
            &quot;value&quot;:    &quot;啦啦啦&quot;,
            &quot;dataType&quot;:    &quot;text&quot;
        }
    ]
}

 ```


|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |执行动作   |
|fileName     |是  |string | 共享参数文件名称    |
|RowDataRequests     |是  |array |需要添加的数据数组 |
|title     |是  |String | 列名称    |
|primary     |否  |boolean | 是否是主键    |
|value     |是  |String | 对应列要添加的数据    |
|dataType     |是  |String | 数据类型    |

 **返回示例**

```
{
    &quot;code&quot;: 200,
    &quot;msg&quot;: &quot;success&quot;
}
```

 **返回参数说明**

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|code |int   |返回结果状态 200表示成功，不是200则提示msg信息  |
|msg |String   |返回状态文本，code不是200则提示msg信息  |


 **备注**

- 更多返回错误代码请看首页的错误代码描述


