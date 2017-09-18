**简要描述：**

- 获取表内所有数据

**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**

 ```
 {
     &quot;action&quot; : &quot;getDataFromDbTable&quot;,
     &quot;database&quot;:&quot;appinfo.db&quot;,
     &quot;tableName&quot; : &quot;historycache&quot;,
     &quot;pageIndex&quot;:1,
     &quot;pageSize&quot;:10
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
    &quot;code&quot;: 200,
    &quot;editable&quot;: true,
    &quot;msg&quot;: &quot;success&quot;,
    &quot;tableData&quot;: {
        &quot;tableColumns&quot;: [
            {
                &quot;dataType&quot;: &quot;integer&quot;,
                &quot;notNull&quot;: false,
                &quot;primary&quot;: true,
                &quot;title&quot;: &quot;id&quot;
            },
            {
                &quot;dataType&quot;: &quot;text&quot;,
                &quot;notNull&quot;: false,
                &quot;primary&quot;: false,
                &quot;title&quot;: &quot;key&quot;
            },
            {
                &quot;dataType&quot;: &quot;text&quot;,
                &quot;notNull&quot;: false,
                &quot;primary&quot;: false,
                &quot;title&quot;: &quot;value&quot;
            },
            {
                &quot;dataType&quot;: &quot;long&quot;,
                &quot;notNull&quot;: false,
                &quot;primary&quot;: false,
                &quot;title&quot;: &quot;lasttime&quot;
            },
            {
                &quot;dataType&quot;: &quot;text&quot;,
                &quot;notNull&quot;: false,
                &quot;primary&quot;: false,
                &quot;title&quot;: &quot;bak&quot;
            },
            {
                &quot;dataType&quot;: &quot;text&quot;,
                &quot;notNull&quot;: false,
                &quot;primary&quot;: false,
                &quot;title&quot;: &quot;flag&quot;
            }
        ],
        &quot;tableDatas&quot;: [
            [
                16,
                &quot;1503476474752&quot;,
                &quot;bbbbbbbb&quot;,
                1503476474752,
                &quot;&quot;,
                &quot;&quot;
            ],
            [
                17,
                &quot;1503893038974&quot;,
                &quot;aaaaaaaaaaaaaaaa&quot;,
                1503893038978,
                &quot;&quot;,
                &quot;&quot;
            ],
            [
                18,
                &quot;1503893038983&quot;,
                &quot;bbbbbbbb&quot;,
                1503893038983,
                &quot;&quot;,
                &quot;&quot;
            ],
            [
                19,
                &quot;1503897959943&quot;,
                &quot;aaaaaaaaaaaaaaaa&quot;,
                1503897959946,
                &quot;&quot;,
                &quot;&quot;
            ],
            [
                20,
                &quot;1503897959948&quot;,
                &quot;bbbbbbbb&quot;,
                1503897959948,
                &quot;&quot;,
                &quot;&quot;
            ]
        ],
        &quot;dataCount&quot;: 20
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


