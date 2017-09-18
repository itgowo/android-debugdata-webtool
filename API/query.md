**简要描述：**

- 执行SQL数据

**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**

 ```
{
    &quot;action&quot;:    &quot;query&quot;,
    &quot;database&quot;:    &quot;appinfo.db&quot;,
    &quot;data&quot;:    &quot;select * from historycache where key like '%Home%'&quot;
}

 ```


|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |执行动作   |
|database |是  |string | 待操作数据库名称    |
|data     |是  |string | SQL语句    |

 **返回示例**

```
{
    &quot;code&quot;: 200,
    &quot;editable&quot;: true,
    &quot;msg&quot;: &quot;success&quot;,
    &quot;tableData&quot;: {
        &quot;dataCount&quot;: 12,
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
                1,
                &quot;GetHomeInfo&quot;,
                &quot;llljk&quot;,
                1505489911016,
                &quot;&quot;,
                &quot;&quot;
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
|dataCount |int   |数据库表中的数据总数  |


 **备注**

- 更多返回错误代码请看首页的错误代码描述


