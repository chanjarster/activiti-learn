activiti-learn
==============

本项目包含了一系列工作流引擎[Activiti](http://www.activiti.org/)的样例代码。

包含以下几个部分：

1. gateway（``GatewayTest.java``）
2. error event（``ErrorEventTest.java``）
3. timer event（``TimerEventTest.java``）
4. signal event（``SignalEventTest.java``）
5. message event（``MessageEventTest.java``）
6. sub process（``SubprocessTest.java``）

所有代码都以JUnit4单元测试代码的形式编写。

运行前，需要安装[h2database](http://www.h2database.com/html/main.html)。

将h2database压缩包解压缩后，到其目录下执行：

```bash
chmod +x bin/h2.sh
```

然后执行下面命令运行h2：

```bash
bin/h2.sh
```

在打开的浏览器，将``Saved Settings``选择为``Generic H2 (Server)``。在``JDBC URL``里填写``jdbc:h2:tcp://localhost/~/activiti-learn``。


然后可以运行以下命令执行所有单元测试代码：

```bash
mvn clean test
```

也可以在eclipse里选择单独的代码执行。
