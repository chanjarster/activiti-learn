activiti-learn
==============

本项目包含了一系列工作流引擎[Activiti](http://www.activiti.org/)的样例代码。

所有代码都以JUnit4单元测试，请结合本项目的[Wiki](https://github.com/chanjarster/activiti-learn/wiki)来使用本项目。

## 如何运行

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
