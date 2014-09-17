activiti-learn
==============

本项目包含了一系列工作流引擎[Activiti](http://www.activiti.org/)的样例代码。

包含以下几个部分：

1. ``GatewayTest.java``，gateway的例子
2. ``ErrorEventTest.java``，error event的例子
3. ``TimerEventTest.java``，timer event的例子
4. ``SignalEventTest.java``，signal event的例子
5. ``MessageEventTest.java``，message event的例子
6. ``SubprocessTest.java``，sub process的例子

所有代码都以JUnit4单元测试代码的形式编写。关于这些测试用例的详解解释可以看本项目的[Wiki](https://github.com/chanjarster/activiti-learn/wiki)。

运行前，需要安装[h2database](http://www.h2database.com/html/main.html)。

将h2database压缩包解压缩后，到其目录下执行：

```bash
chmod +x bin/h2.sh
```

然后执行下面命令运行h2：

```bash
bin/h2.sh
```

在打开的浏览器里，将``Saved Settings``选择为``Generic H2 (Server)``。在``JDBC URL``里填写``jdbc:h2:tcp://localhost/~/activiti-learn``。


然后可以运行以下命令执行所有单元测试代码：

```bash
mvn clean test
```

也可以在eclipse里选择单独的JUnit测试用例执行。
