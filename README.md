# android_debugdata_webtool
android debug database SharedPreference 查看并修改手机数据库和共享参数的工具，优化了amitshekhar工程的好多细节功能和代码

http://itgowo.com

##被踩肩膀的那个巨人 Github：https://github.com/amitshekhariitbhu/Android-Debug-Database
大家可以去英文版看看

![ image](https://github.com/hnsugar/android-debugdata-webtool/blob/master/img1.png)

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




因为web功底薄弱，请大家耐心等待
