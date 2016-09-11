-- ----------------------------
-- Table structure for `resources`
-- ----------------------------
DROP TABLE IF EXISTS `resources`;
CREATE TABLE `resources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `enName` varchar(20) DEFAULT NULL,
  `alias` varchar(20) DEFAULT NULL,
  `tags` varchar(30) DEFAULT NULL,
  `typos` varchar(20) DEFAULT NULL,
  `icon` varchar(200) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `hot` int(11) DEFAULT '0',
  `modifiDate` datetime DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `status` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=876875 DEFAULT CHARSET=utf8;


INSERT INTO `res`.`resources` (`id`, `type`, `name`, `enName`, `alias`, `tags`, `typos`, `icon`, `description`, `hot`, `modifiDate`, `createDate`, `status`) VALUES ('742181', '1', '我的世界移动版', 'Minecraft–Pocket Edi', 'MC,minecraft,我的世界手机版', '经典游戏,沙盒,像素风,电视游戏', '世界', 'http://img6.android.d.cn/android/new/game_image/68/4568/icon.png', '我们最新的免费更新包括活塞、丛林神庙、纹理包、Xbox Live 支持（包括成就！）以及一种全新服务：领域！领域是您在一个始终存在的世界中与好友跨平台同玩的最简单方法：随时随地都能游戏。马上到应用程序中试试30天免费试玩，并到以下地址了解更多信息：http://minecraft.net/realms。\n\n《我的世界 minecraft》这款游戏在PC上让开发者在一天之内获得了26万欧元（242万人民币）收入的沙盘游戏（独立游戏神作）——《我的世界 移动版minecraft》是一款非常独特的不是游戏的游戏。Minecraft被称为独立游戏神作《Minecraft》，不仅登录了Xbox360，还登录到了Android手机。\n我的世界minecraft说它不是游戏，是因为它并没有什么特定的玩法，也没有游戏情节，也没有游戏规则，说它是游戏，因为同样能够消磨你的时间，让你体验到无穷的乐趣。这款游戏还将支持基于本地无线网络的多人游戏。\n我的世界minecraft这款3D的第一人称沙盘游戏没有华丽的画面，更注重游戏性。玩家在游戏中做着“建设”与“破坏”两件事，通过像乐高一样的积木来组合与拼凑，轻而易举的就能制作出小木屋、城堡甚至城市，但是若再加上玩家的想像力，空中之城、地底都市都一样能够实现。\n有人说，在我的世界这款游戏中，你只需要从事两种操作：建设和破坏。你能够利用游戏中提供给你的各种基石来创造各种东西，发挥你的想象力，你能够建造房屋，构建城堡甚至是城市。\n您可以探索随机生成的世界，建造不可思议的事物，从最简单的住宅，到最宏伟的城堡。您可以在创意模式中享用无限资源，也可以到生存模式中挖掘整个世界，合成武器和护甲，抵御各种危险生物。\n\n您可以独自或与朋友一起，在移动设备或 Windows 10 上，尽情合成、制作和探索。\n\n【0.15.0 更新内容】\n- 领域登场！随时随地，在现在世界中与最多 10 位好友跨平台同玩。到应用程序中试试30天免费试玩吧！\n- 城市和塑料纹理包，可更改您世界的外观和感觉\n- Xbox Live 支持登录 iOS 和 Android，包括成就\n- Xbox Live 跨平台会话浏览器（加入好友的游戏）\n- 活塞 – 红石最终功能震憾推出！\n- 丛林神庙和僵尸村庄\n- 马、骑猪、羊肉、萝卜钓竿\n- 全新生物登场！剥皮者生物和流浪者生物\n- 推出全新游戏', '2002', '2016-08-25 11:04:37', '2011-08-17 23:44:36', '1');
INSERT INTO `res`.`resources` (`id`, `type`, `name`, `enName`, `alias`, `tags`, `typos`, `icon`, `description`, `hot`, `modifiDate`, `createDate`, `status`) VALUES ('746803', '1', '我的世界完整专业版', 'Minecraft Pro Full', '', '', NULL, 'http://img2.android.d.cn/android/new/game_image/90/9190/icon.png', 'Minecraft的增强版，有着的手工艺、暴徒、成就、宝石和更多内容，所有这些都不需要联网和权限。', '21', '2014-06-13 14:34:06', '2012-03-28 17:09:19', '1');
