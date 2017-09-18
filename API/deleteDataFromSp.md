**简要描述：**

- 删除共享参数文件中的数据，一次只能删除一条数据

**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**

 ```
	{
		action:    deleteDataFromSp,
		spFileName:appinfo,
		RowDataRequests:
		[
			{
				primary:    true,
				title:    id,
				value:    3,
				dataType:    integer
			}
		]
	}

 ```


|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |执行动作   |
|database |是  |string | 待操作数据库名称    |
|tableName     |是  |string | 待操作表名称    |
|RowDataRequests     |是  |array |需要添加的数据数组 |
|title     |是  |String | 列名称    |
|primary     |否  |boolean | 是否是主键    |
|value     |是  |String | 对应列要添加的数据    |
|dataType     |是  |String | 数据类型    |

 **返回示例**

```
{
    code: 200,
    msg: success
}
```

 **返回参数说明**

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|code |int   |返回结果状态 200表示成功，不是200则提示msg信息  |
|msg |String   |返回状态文本，code不是200则提示msg信息  |


 **备注**

- 更多返回错误代码请看首页的错误代码描述


