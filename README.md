# android_debugdata_webtool
android debug database SharedPreference 查看并修改手机数据库和共享参数的工具，优化了amitshekhar工程的好多细节功能和代码

http://itgowo.com

##原版 Github：https://github.com/amitshekhariitbhu/Android-Debug-Database
大家可以去膜拜一下前辈

因为一次使用中发现国外前辈写的服务和接口无法满足我的使用，主要是用的GET请求方式，再编码后url过于长，超出范围就会数据丢失，所以我改成了POST请求的，并且重写了Server逻辑，从Socket拿到流再解析http报文，再到业务处理，同时增加了文件管理功能。


##优化与改进

###1.增加多线程处理请求

###2.手动解析http报文数据，增加HttpRequest类

###3.web端get请求改成post请求，满足大数据量需求

###4.服务端增加post处理能力，支持大数据量传输

###5.增加文件管理功能（接口已完成）

###6.更改布局和页面逻辑，数据库和共享参数分开，除数据库外不显示sql查询等模块（正在制作）

###7.增加跨域请求Options处理，解决跨域问题

###8.统一规范：返回值Response类，请求Request类，接口规范GET请求为资源请求，options请求为跨域说明，POST请求为数据交互

###9.POST请求统一用action表示意向操作


##API

###Database 数据库

[getDbList](/API/getDbList.md)

[getTableList](/API/getTableList.md)

[getDataFromDbTable](/API/getDataFromDbTable.md)

[addDataToDb](/API/addDataToDb.md)

[deleteDataFromDb](/API/deleteDataFromDb.md)

[updateDataToDb](/API/updateDataToDb.md)

[query](/API/query.md)


###SharedPreferences 共享参数

[getSpList](/API/getSpList.md)

[getDataFromSpFile](/API/getDataFromSpFile.md)

[addDataToSp](/API/addDataToSp.md)

[deleteDataFromSp](/API/deleteDataFromSp.md)

[updateDataToSp](/API/updateDataToSp.md)


###FileManager 文件管理

[getFileList](/API/getFileList.md)

[downloadFile](/API/downloadFile.md)



##图片示例

![ image](https://github.com/hnsugar/android-debugdata-webtool/blob/master/img1.png)

![ image](https://github.com/hnsugar/android-debugdata-webtool/blob/master/img2.png)

因为web功底薄弱，请大家耐心等待
