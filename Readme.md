# ContentProvider Demo 说明

## 参考

Providertest2 和 Providertest2_client 这两个工程代码参考于：[ContentObserver的使用完整详细示例 ](https://www.cnblogs.com/xiaoxiaoshen/p/5191186.html)

其他参考：

[Android：关于ContentProvider的知识都在这里了！](https://blog.csdn.net/carson_ho/article/details/76101093)，Carson-Ho 工程源码来源于此

同作者：[使用ContentProvider](https://blog.csdn.net/a992036795/article/details/51610936)，[ContentProvider原理分析](https://blog.csdn.net/a992036795/article/details/51612425)，Providertest1 工程源码参考于此


## 说明

主要看 Providertest2 和 Providertest2_client 这两个工程


Providertest2 是服务端角色，内部有 ContentProvider 的实现；

Providertest2_client 是客户端的角色，内部有访问 ContentProvider 的实现；

具体代码的注视都比较清楚，这里小结一下要点：
- ContentProvider 端
    - 明白 ContentProvider 的子类要重写的六个函数在 ContentProvider 框架角度下的意义
    - AndroidManifest.xml 注册 ContentProvider 时各个属性的意义，
        - 其他程序通过 uri 在此处找寻匹配 ContentProvider
    - URI 的语法结构和 UriMatcher 的使用
    - SQLiteOpenHelper 的使用，在 ContentProvider 内部维护数据集的方法就是通过 SQLiteOpenHelper 框架
    - 通知观察者 ContentObserver

- client 端
    - ContentResolver 和 ContentValues 相结合去访问 ContentProvider 提供的增删改查接口
    - URI 的使用
    - ContentObserver 的使用

## 设计思路

ContentProvider 作为 四大组件之一，其作用是封装好统一的接口提供给外部，让外部可以通过同意的接口访问某一种类型的数据集。

内部的数据存储的实现，跟外部隔离。

ContentProvider 其实是一个框架，其使用方法属于拓展类套路。
其派生类需要重写6个函数：
初始化函数 onCreate，绑定数据源；
增删改查四个函数，统一的数据操作接口；
类型判断函数 getType，判断某个uri的类型；
其中，增删改查四个函数是提供给外部的接口！

外部通过 ContentResolver 来访问某个 ContentProvider，方式：

```
getContext().getContentResolver().insert( uri，ContentValue )
```

Uri 会指定某个具体的 ContentProvider，通过 ContentProvider 程序的 AndroidManifest.xml 清单文件确定是否匹配，然后该 ContentProvider 的 insert 函数会被调用，在函数内部，借用 UriMatcher 类解析 uri 并执行相应的操作。

Uri 的匹配解析，有一套规则。

ContentObserver 可以监听某个 ContentProvider，属于观察者模式。