**简要描述：**

- 获取共享参数所有数据

**请求URL：**
- ` / `

**请求方式：**
- POST

**参数：**


 {
     &quot;action&quot; : &quot;getDataFromSpFile&quot;,
     &quot;spFileName&quot; : &quot;appinfo&quot;
 }

 ```



 ```


|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
|action |是  |string |执行动作   |
|spFileName |是  |string | 待查共享参数文件名称    |


 **返回示例**


 ```
 {
    &quot;code&quot;: 200,
    &quot;editable&quot;: true,
    &quot;msg&quot;: &quot;success&quot;,
    &quot;tableData&quot;: {
        &quot;tableColumns&quot;: [
            {
                &quot;primary&quot;: true,
                &quot;title&quot;: &quot;Key&quot;
            },
            {
                &quot;primary&quot;: false,
                &quot;title&quot;: &quot;Value&quot;
            },
            {
                &quot;primary&quot;: false,
                &quot;title&quot;: &quot;DataType&quot;
            }
        ],
        &quot;tableDatas&quot;: [
            [
                &quot;Int&quot;,
                1234,
                &quot;integer&quot;
            ],
            [
                &quot;ggg&quot;,
                &quot;teddddst&quot;,
                &quot;text&quot;
            ],
            [
                &quot;aaaasdfsafee3aa&quot;,
                &quot;tadsfsadfest&quot;,
                &quot;text&quot;
            ],
            [
                &quot;Float&quot;,
                1.5,
                &quot;float&quot;
            ],
            [
                &quot;Booblean&quot;,
                true,
                &quot;boolean&quot;
            ],
            [
                &quot;aaaaa&quot;,
                &quot;test&quot;,
                &quot;text&quot;
            ],
            [
                &quot;Long&quot;,
                1232131231,
                &quot;long&quot;
            ],
            [
                &quot;adfs&quot;,
                &quot;1505489963276&quot;,
                &quot;text&quot;
            ],
            [
                &quot;String&quot;,
                &quot;tadsfsadfest&quot;,
                &quot;text&quot;
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


