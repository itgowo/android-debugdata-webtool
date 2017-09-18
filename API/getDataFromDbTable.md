**简要描述：**

- 获取表内所有数据

**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**

 ```
 {
     action : getDataFromDbTable,
     database:appinfo.db,
     tableName : historycache,
     pageIndex:1,
     pageSize:10
 }

 ```


|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |执行动作   |
|database |是  |string | 待查数据库名称    |
|tableName     |是  |string | 待查表名称    |
|pageIndex     |否  |int |页面索引，从1开始，默认为null，获取全部数据 |
|pageSize     |否  |int | 数据多少，默认null，获取全部数据    |

 **返回示例**

```
   {
    code: 200,
    editable: true,
    msg: success,
    tableData: {
        tableColumns: [
            {
                dataType: integer,
                notNull: false,
                primary: true,
                title: id
            },
            {
                dataType: text,
                notNull: false,
                primary: false,
                title: key
            },
            {
                dataType: text,
                notNull: false,
                primary: false,
                title: value
            },
            {
                dataType: long,
                notNull: false,
                primary: false,
                title: lasttime
            },
            {
                dataType: text,
                notNull: false,
                primary: false,
                title: bak
            },
            {
                dataType: text,
                notNull: false,
                primary: false,
                title: flag
            }
        ],
        tableDatas: [
            [
                16,
                1503476474752,
                bbbbbbbb,
                1503476474752,
                ,
                
            ],
            [
                17,
                1503893038974,
                aaaaaaaaaaaaaaaa,
                1503893038978,
                ,
                
            ],
            [
                18,
                1503893038983,
                bbbbbbbb,
                1503893038983,
                ,
                
            ],
            [
                19,
                1503897959943,
                aaaaaaaaaaaaaaaa,
                1503897959946,
                ,
                
            ],
            [
                20,
                1503897959948,
                bbbbbbbb,
                1503897959948,
                ,
                
            ]
        ],
        dataCount: 20
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
|dataCount |int   |数据库表中的数据总数  |

 **备注**

- 更多返回错误代码请看首页的错误代码描述


