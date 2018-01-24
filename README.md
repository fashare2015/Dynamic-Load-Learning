# 1 前言
插件化算是比较复杂的一个话题。刚一接触的时候，我是一脸懵逼的，网上看了很多博客，一直是似懂非懂，不得其要领。期间也尝试看了`Small`，也是知其然不知其所以然。

就此搁置一段时间，直到真正拿出勇气，尝试自己实现插件化，成功加载了四大组件之一`Activity`。这才明白它的背后究竟做了什么，以及为什么这么做。

希望借着这篇文章，谈谈自己的理解。也希望通过我的小 Demo，能帮大家更轻松的理解诸如`Small`、`VirtualApk`、`Atlas`之类的大型框架。如有纰漏，请留言指出。

# 2 效果预览
`主apk[com.fashare.app.MainActivity]`唤起`sd卡`上的`插件apk[com.fashare.testapk.PluginActivity]` :

![preview](https://fashare2015.github.io/2018/01/24/dynamic-load-learning-load-activity/preview.gif)

# 3 原理与实现
详见我的博客：
[插件化理解与实现 —— 加载 Activity「类加载篇」](https://fashare2015.github.io/2018/01/24/dynamic-load-learning-load-activity/)

# 4 感谢
[《Android插件化技术——原理篇》—— 腾讯Bugly](https://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=2653579547&idx=1&sn=9f782f6c91c20fd0b17a6c3762b6e06a&chksm=84b3bb1cb3c4320ad660e3a4a274aa2e433bf0401389f38be337d01d2ba604714303e169d48a&mpshare=1&scene=23&srcid=0111lAPa4UGPssFMoc05pgLP#rd)

[Android 插件化原理解析——插件加载机制 —— weishu](http://weishu.me/2016/04/05/understand-plugin-framework-classloader/)

[8个类搞定插件化——Activity —— 开源实验室](https://kymjs.com/code/2016/05/15/01/)

[插件化框架 Small](https://github.com/wequick/Small)

[插件化框架 VirtualAPK —— 滴滴](https://github.com/didi/VirtualAPK)
