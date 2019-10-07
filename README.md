# Android_example_View08
Android services 后台下载 并且支持断点续传的练习 

## 实现后台下载指定的url 文件 
  <br/>step:
  <br/>1.添加本地依赖库，OkHttp;(我们需要对指定的Url进行下载)；
  <br/>2.创建 接口类，对下载的各种状态进行监听和回调! SUCCESS FAILED CALLED UPDATE_PROGRESS
  <br/>2.编写 DownLoadAsyncTask()类，继承AsyncTask() 去下载，使用异步下载不需要我们去处理线程，不需要我们去管异步方面，比较方便；
  <br/>3.重写其中的几个方法以及构造，构造获取到外部监听回调，可以获取数据，接着在doInBackGround里面去做下载的耗时操作; 
       <br/> -->新建文件,使用 SD卡作为文件下载路径->接着使用sd卡作为文件存储 路径之后 在去下载这个文件 ，同时 RANGE 代表的是断点续传，传入的是 文件路径 和 文件大小！
       <br/>接着 在onProgressUpdate 去做更新操作 每次进度条发生变化，我们就去回调一下！
  <br/>4.接着为了保障AsyncTask一直在后台运行，我们需要编写一个Service把这个放到后台中去！
  <br/>5.在Service中新建 几个方法，其中包括 对每个请求状态的 处理，接着绑定主活动 判断下载任务，接着设置diaLog的请求状态等等！
  <br/>6.接着新建Activity 去写几个按钮并且传入Url地址，这里我传一个歌曲 -> 5652%2F098c%2F78f2%2Fe944028f6b3821da471aeae0e46766b5.mp3 
  <br/>7.点击下载 去开启后台 异步任务执行下载，接着回调，接着 不小心关闭了，可以在RonDomAccessFile 断点续传!点击取消就是 销毁这个文件，并且取消掉文件！
 
## home 服务的step:
   <br/>1.create listener 之后回调5个方法之后创建新的监听器！
   <br/>2.在每一个监听器里面创建！
   <br/>3.接着 去实现 一个binder 与Activity 通信的方法 ，但是 需要实现的是开始下载，暂停下载和结束下载三种方法！ 但是需要注意的是 在监听里面还需要结束掉 前台 services 