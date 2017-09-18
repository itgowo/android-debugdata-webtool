**简要描述：**

- 获取共享参数所有数据

**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**




 ```

 {
     action : getDataFromSpFile,
     spFileName : appinfo
 }

 ```


|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |执行动作   |
|spFileName |是  |string | 待查共享参数文件名称    |


 **返回示例**


 ```
 {
    code: 200,
    editable: true,
    msg: success,
    tableData: {
        tableColumns: [
            {
                primary: true,
                title: Key
            },
            {
                primary: false,
                title: Value
            },
            {
                primary: false,
                title: DataType
            }
        ],
        tableDatas: [
            [
                Int,
                1234,
                integer
            ],
            [
                ggg,
                teddddst,
                text
            ],
            [
                aaaasdfsafee3aa,
                tadsfsadfest,
                text
            ],
            [
                Float,
                1.5,
                float
            ],
            [
                Booblean,
                true,
                boolean
            ],
            [
                aaaaa,
                test,
                text
            ],
            [
                Long,
                1232131231,
                long
            ],
            [
                adfs,
                1505489963276,
                text
            ],
            [
                String,
                tadsfsadfest,
                text
            ]
        ]
    }
}

 ```


 **返回参数说明**

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|code |int   |返回结果状态 200表示成功，不是200则提示msg信息  |
|msg |String   |返回状态文本，code不是200则提示msg信息  |
|editable |boolean   |是否可以编辑  |
|tableData |对象   |返回的数据对象  |
|tableColumns |Array   |返回表结构信息  |
|dataType |String   |数据类型  |
|notNull |boolean   |是否可以为null  |
|primary |boolean   |是否是主键  |
|title |String   |列名称  |
|tableDatas |array   |对应的一条记录数据  |
|dataCount |int   |数据总数  |

 **备注**

- 更多返回错误代码请看首页的错误代码描述


