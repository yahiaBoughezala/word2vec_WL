package com.caishi.bigdata.news.word2vec.trainer;

import com.caishi.bigdata.common.word2vec.Word2VecUtils;
import org.apache.spark.mllib.feature.Word2VecModel;
import org.junit.Before;
import org.junit.Test;
import scala.Tuple2;

/**
 * 对com.caishi.bigdata.news.word2vec.trainer.NewsWord2VecTrainning 训练模型的使用说明
 * 1.模型预测过程
 * 1.1 spark-shell 命令执行,然后进行预测
 * spark-shell --master spark://10.4.1.4:7077 --executor-memory 4096m --driver-memory 4096m --executor-cores 1 --conf spark.serializer=org.apache.spark.serializer.KryoSerializer --conf spark.akka.frameSize=1024  --conf spark.kryoserializer.buffer.max=1024
 * <p>
 * scala> word2vecModel.findSynonyms("马云", 10)
 * res0: Array[(String, Double)] = Array((阿里巴巴,728873.4226757289), (马化腾,694204.3332021954), (阿里,625143.7909097292), (王健林,573490.7893402464), (大佬,560984.1972743534), (刘强东,557761.766262701), (蚂蚁,553260.4625862375), (互联网电商,543804.421894635), (首富,533350.5565971779), (互联网金融,531674.903952446))
 * <p>
 * scala> word2vecModel.findSynonyms("习近平", 10)
 * res1: Array[(String, Double)] = Array((总书记,1258720.5354656614), (重要讲话,1098006.0058208788), (贯彻,1080162.2470029), (同志,1018663.599337964), (党中央,1000798.678402671), (十八届,969787.8144741518), (俞正声,945262.791588238), (领导人,936930.0179137179), (胡锦涛,931676.3839544035), (会见,930401.4662349261))
 * <p>
 * scala> word2vecModel.findSynonyms("投资", 10)
 * res2: Array[(String, Double)] = Array((资本,791356.5030891554), (投资者,773778.486430909), (资产,743971.5181558601), (基金,737923.7578778417), (投资人,729224.5600335201), (私募,720477.3620165566), (资金,720093.0260627058), (理财,703080.1458541512), (融资,701049.4169395124), (金融,696246.1809648738))
 * <p>
 * scala> word2vecModel.findSynonyms("黄飞鸿", 10)
 * res4: Array[(String, Double)] = Array((警察故事,2746432.0988298818), (武侠片,2734469.1022346993), (方世玉,2730899.2624470615), (功夫片,2702469.5384743907), (武术指导,2690362.6627661265), (武打,2680496.695340626), (龙门客栈,2666306.568155436), (吴宇森,2609858.341092637), (武打片,2584257.845021547), (赌圣,2569117.955642752))
 * 1.2 window下运行进行预测，需要设置heap内存
 * -Xms4096m -Xmx4096m
 * Created by fuli.shen on 2017/6/30.
 */
public class NewsWord2VecTrainningTest {

    // word2vec 模型存储路径
    public static final String w2vModelPath = "hdfs://10.4.1.1:9000/news/W2V/News_W2V_Model/";

    // w2v 模型实例对象
    Word2VecModel w2vModel = null;

    @Before
    public void init() {
        long s1 = System.currentTimeMillis();
        w2vModel = Word2VecUtils.loadW2VModel(w2vModelPath);
        System.out.println("Load News word2Vec Model successfully,the time is" + (System.currentTimeMillis()-s1)/1000 + "s");
    }

    @Test
    public void testfindSynonyms() {
        System.out.println("-------------------------------1.1.【投资】  关键词的同义词-----------------------------------");
        Tuple2<String, Object>[] w2vModelSynonyms = w2vModel.findSynonyms("投资", 10);
        for (Tuple2<String, Object> tuple2 : w2vModelSynonyms) {
            String word = tuple2._1();
            Double cosineSimilarity = (Double) tuple2._2();
            System.out.println(word + "\t" + cosineSimilarity);
        }
        System.out.println("-------------------------------1.2.【黄飞鸿】  关键词的同义词-----------------------------------");
        Tuple2<String, Object>[] w2vModelSynonyms_2 = w2vModel.findSynonyms("黄飞鸿", 10);
        for (Tuple2<String, Object> tuple2 : w2vModelSynonyms_2) {
            String word = tuple2._1();
            Double cosineSimilarity = (Double) tuple2._2();
            System.out.println(word + "\t" + cosineSimilarity);
        }
        System.out.println("-------------------------------1.3.【习近平】  关键词的同义词-----------------------------------");
        Tuple2<String, Object>[] w2vModelSynonyms_3 = w2vModel.findSynonyms("习近平", 10);
        for (Tuple2<String, Object> tuple2 : w2vModelSynonyms_3) {
            String word = tuple2._1();
            Double cosineSimilarity = (Double) tuple2._2();
            System.out.println(word + "\t" + cosineSimilarity);
        }
        System.out.println("-------------------------------1.4.【马云】  关键词的同义词-----------------------------------");
        Tuple2<String, Object>[] w2vModelSynonyms_4 = w2vModel.findSynonyms("马云", 10);
        for (Tuple2<String, Object> tuple2 : w2vModelSynonyms_4) {
            String word = tuple2._1();
            Double cosineSimilarity = (Double) tuple2._2();
            System.out.println(word + "\t" + cosineSimilarity);
        }
        System.out.println("-------------------------------1.5.【生活】  关键词的同义词-----------------------------------");
        Tuple2<String, Object>[] w2vModelSynonyms_5 = w2vModel.findSynonyms("生活", 10);
        for (Tuple2<String, Object> tuple2 : w2vModelSynonyms_5) {
            String word = tuple2._1();
            Double cosineSimilarity = (Double) tuple2._2();
            System.out.println(word + "\t" + cosineSimilarity);
        }
        System.out.println("-------------------------------1.6.【家居】  关键词的同义词-----------------------------------");
        Tuple2<String, Object>[] w2vModelSynonyms_6 = w2vModel.findSynonyms("家居", 10);
        for (Tuple2<String, Object> tuple2 : w2vModelSynonyms_6) {
            String word = tuple2._1();
            Double cosineSimilarity = (Double) tuple2._2();
            System.out.println(word + "\t" + cosineSimilarity);
        }
    }
    @Test
    public void testComputeTextConsineScoreByWord2Vec() {
        System.out.println("-------------------------------句子的向量表示-----------------------------------");
        String[] words_1 = "体育 北京 男篮 热身赛 磨合 战术 年轻 新秀 崭露头角 热身赛 中 北京队 小将 张才仁 锻炼 北京首钢队 天津 海峡 杯 比赛 中 大胜 台 啤 队 胜 负 战绩 结束 热身赛 旅 昨晚 比赛 中 后卫 方硕 手感 火热 全队 中锋 张松涛 马布里 面对 防守 马布里 持球 欲 突破 孙悦 上篮 孙悦 突破 马布里 突破 防守 方硕 突破 防守 马布里 撇嘴 莫里斯 遭遇 严防 莫里斯 出阵 张才仁 方硕 上篮 朱彦西 突破 吉喆 打暗号 张松涛 强攻 首钢队 热身赛 中 队伍 磨合 新 战术 李根 队 中 球员 顶替 队员 默契 配合 比赛 建立 首钢队 热身赛 中 锻炼 小将 张才仁 青年队 上调 队员 热身赛 中 打球 尚可 外援 莫里斯 说 打球 整体 打法 首钢队 队长 孙悦 热身赛 新 赛季 孙悦 恢复 寻找 比赛 孙悦 说 场上 稍微 身体 比赛 比赛 中 首钢队 不敌 天津 战全胜 锁定 冠军".split(" ");
        int vectorSize = 200;// 词
        double[] wordsVector_1 = Word2VecUtils.buildWordsVector(words_1, vectorSize, w2vModel);
        printWordsVector(wordsVector_1);//0.026998909888789058,-0.01186487590894103,0.024897634619264863,0.05851895920932293,-0.012189864530228078
        String[] words_2 = "体育 领队 调侃 技师 调车 时 维泰尔 安静 叫喊 阿里 巴贝内 说 莱科宁 新浪 体育讯 莱科宁 新加坡 站 季军 本赛季 登上 领奖台 队友 维泰尔 拿出 新加坡 统治 级 胜利 排位 赛后 莱科宁 赛车 不太好 度过 困难 正 赛中莱 科宁 工程师 报告 赛车 驾驶 法拉利 领队 阿德里巴 贝 澄清 莱科宁 车队 调侃 称 冰人 爱 抱怨 阿里 巴贝内 说 抱怨 技师 调校 赛车 塞巴 安静 叫喊 谈到 车手 阿里 巴贝内 说 蒙扎 强 塞巴 强 塞巴 赛道 强大 车手 领奖台".split(" ");
        double[] wordsVector_2 = Word2VecUtils.buildWordsVector(words_2, vectorSize, w2vModel);
        printWordsVector(wordsVector_2);//0.004943567619193345,0.010045223403722048,0.03035004214325454,0.02609390113502741,0.017606549547053874

        System.out.println("-------------------------------1.1.(cosineScore1_2)距离的余弦距离比较-----------------------------------");
        double cosineScore1_2 = Word2VecUtils.computeCosineScore(wordsVector_1, wordsVector_2);
        System.out.println(cosineScore1_2);
        System.out.println("-------------------------------1.2.(cosineScore3_4)距离的余弦距离比较-----------------------------------");
        String sentence3 = "菜谱 搞定 补钙 辅 食 妈妈 问 芹菜 宝宝 吃 包 饺子 炒 豆 干 买到 豆 干 临时 豆腐 补钙 宝宝 辅 食 值得注意 芹菜 焯 水 去掉 草酸 豆腐 食 影响 钙 吸收 补钙 翻 苦心 白费 道 辅 食 制作方法 太 蔬菜 成丁状 炒锅 炒 锅 成功率 高 快手 辅 食 必备 菠菜 苋菜 竹笋 苦瓜 茭白 草酸 含量 高 蔬菜 制作 焯 食 材 胡萝卜 芹菜 豆腐 米饭 调料 盐 鼓励 周岁 宝宝 吃 盐 辅 食 酌情 加盐 烹制 炒 月龄 月 难易 程度 文中 食 材 过敏 宝宝 芹菜 焯 切成 小段 芹菜 中 草酸 影响 钙 吸收 焯 去掉 草酸 宝宝 挑选 芹菜 叶 菜茎 嫩 胡萝卜 切成 小碎丁 豆腐 切成 块儿 食 材 芹菜 胡萝卜 豆腐 焖 米饭 锅 中 放入 底 油 先下 入 胡萝卜 芹菜 翻炒 加水 没过 蔬菜 稍微 炖 汤汁收 干 胡萝卜 芹菜 炒熟 加水 炖 会儿 豆腐 米饭 豆腐 炒熟 锅 加盐 宝 妈 宝宝 口味 自行 调整 月 宝宝 辅 食 鼓励 宝宝 辅 食 加盐 养成 清淡 口味 宝宝 豆腐 少 放 炒 铲 碎 米饭 盛 宝宝 吃 ";
        String sentence4 = "美食推荐 食 色 饮品 樱桃 蜜 饮 樱桃 季 吃 樱桃 樱桃 酸 倒 牙 换 法子 吃 喝 樱桃 打成 浓浆 加点 蜂蜜 改善 酸味 嚼 咽 进 肚子 里 樱桃 搅打 中会 氧化 黯然失色 确实 失色 原 粒 鲜艳 不似 褐 变 枯黄 黯淡 味道 用料 樱桃 凉开水 蜂蜜 适量 樱桃 洗净 蒂 核 料理 机 杯中 加水 盖 盖子 放入 机座 蔬果 键 打浆 结束 取下 料理 杯 杯中 蜂蜜 表面 樱桃 点缀 酸酸甜甜 ";
        double cosineScore3_4 = Word2VecUtils.computeCosineScore( Word2VecUtils.buildWordsVector(sentence3.split(" "), vectorSize, w2vModel), Word2VecUtils.buildWordsVector(sentence4.split(" "), vectorSize, w2vModel));
        System.out.println(cosineScore3_4);
        System.out.println("-------------------------------1.3.(cosineScoreNewsIds)距离的余弦距离比较-----------------------------------");
        String sentence_8ffbe33ee74dfc15 = "精明 想 卡 罗拉双 擎 卖 秘密 心 试 驾 十几万出头 家用车 写 价位 妥协 忍耐 畅销车 写 品牌 独特 秘密 发觉 星球 销量 车型 丰田 卡罗拉 动力 版本 时 跃跃欲试 诚惶诚恐 生怕 写点 指责 懂 字 概括 完 车 精明 注 试 驾 车辆 卡罗拉 双 擎 旗舰版 全新 行驶 公里 重度 车辆 外观 还好 张大嘴 丰田 年 推出 动力 车型 卡罗拉 双擎 外观设计 相较 丰田 车型 温和 正统 世纪 年代 公爵 王 入 主官 车 行列 丰田 东方 审美 设计 高 美誉度 顶天 考 斯特 皇冠 踏 佳美 海狮 亲切 大方 典范 丰田 外观 进化 过激 进 中 走 完 世纪 旗舰 雷克萨斯 身先士卒 咧开 嘴角 夸张 嘴 令人吃惊 相较 卡罗拉 双擎 设计 风格 新潮 老气 稳重 青年人 怪异 代卡罗 拉 双 擎 重心 高 车窗 面积 浅色 车身 视觉 加重 感官 评判 见仁见智 因人而异 整车 顺应时代 日间 行 车灯 外观 装饰 辨识 度 高 低调 含蓄 徽标 证明 混 动 车型 内饰 颜 值 掩盖 不住 品质 感 想 购车 预算 十万出头 消费者 自主 品牌 车 眼花缭乱 宣传 进到 卡罗拉 双 擎 时光倒流 感 自主 品牌 用上 全 液晶 大屏幕 小车 中装 氛围 灯 卡罗拉 双擎 内饰 克制 台 旗舰 型号 感受 台车 数十年 演进 中 积累 人性 财富 按钮 分区 触手可及 盲 操作 品质 感 不扬 外表 掩盖 平凡 材质 拖累 旋钮 手感 按压 反馈 细节 老人 当年 日本 制造 迷恋 意料之外 卡罗拉 双擎 自动 大灯 开过 车 最快 北京 钻 桥洞 开和 关 动作 车载 导航 规划 能力 玩 电子设备 惊讶 导航 操作 相比 智能手机 中 导航 稍 显 落后 菜单 中 车 愣 没能 研究 手机 中 导航 吐 槽 为生 胡编 开车 点 赞 卡 罗拉双 擎 开关 语言 功能 窗户 除霜 点错 丰田 造 车 秘诀 应有 配置 档次 适宜 空间 超乎 品质 时尚 落后 双 擎 节能 车 历史 纪元 路 测 豪 车 如云 车库 里 思考 半天 翻 特斯拉 牌子 车 开创 汽车 节能 环保 新纪元 车 万 下次 超 跑 没法 写 丰田 套 混 动系统 技术 回溯 年 年 丰田 量产 车 燃气轮机 电动机 概念车 性质 探索 混 动系统 雏形 年 丰田 混 动系统 伴随 普锐斯 丰田 首个 量产 动力车 年 丰田 混 动系统 技术 成熟度 提升 成本 普锐斯 发布会 丰田 混 动系统 成本 卡罗 拉双擎 套 油 电 混 动系统 顾名思义 升 排量 汽油机 电动机 起步 低速 阶段 电动机 介入 弥补 燃油 发动机 低速 时 效率 低 节油 高速 阶段 发动机 电池 充电 滑行 刹车 中 车辆 动能 回收 电池 能源 相较 品牌 混 动 丰田 套 混 动 方案 优势 外 接电源 全过程 中 车辆 加油 走 燃油 车 无异 可靠性 充足 验证 年 历代 中 故障 可靠性 说 全球 气候 路况 环境 验证 放下 电池组 整车 后备箱 凸起 影响 车主 路 测 打分 系统 美中不足 试 驾车 缘故 卡罗拉 双擎 接触 中 有待 提升 空间 方向盘 方向盘 自由 余量 重 手感 方向盘 挡 丰田 官方 卡罗拉 双擎 充满 科技 感 挡 卖点 驾驶 中 确实 灵敏 设计 尺寸 胳膊 短腿 肠炎 老师 够 不快 给油 起步 动力系统 套 混 动系统 实在 太安静 缓 速 阶段 电 驱动 无声 平顺 性 惊人 感受 发动机 启 停 声音 震动 感 品牌 自动 启 停 功能 低 宝马 系 自动 启 停 震颤 拖拉机 节油 效果 日程 缘故 试 驾 北京市 拥堵 大脚 油门 折腾 经历 驾驶 著称 胡编 折腾 里程 最长 平均 油耗 升 驾驶 控制 升 压力 说 打分 系统 动力系统 车辆 行驶 性能 开 入门级 家用车 机会 滴滴 用车 开玩笑 说 跑 滴滴 来用 操作 轻盈 拥挤 城市 环境 轻便 踏板 司机 疲劳度 车辆 操作性 借车 中 北京 年 遇 暴雨 预警 恐怖 上演 大雨 中 速度 匝道 转向 好戏 湿 滑 压 限速 肆意 超车 不安 初段 板油 发动机 车身 冲出去 车辆 稍 显 迟缓 有趣 油门 踩 深 发动机 迸发出 悦耳 啸叫 仪表盘 指针 变红 开 跑车 开玩笑 说 声音 丰田 录制 精明 放心 精明 词 贬义 内饰 动力 感受 惊讶 丰田 车 度 拿捏 超越 东西 品质 奢侈 坚守 价格 档次 角度 讲 丰田 家 精明 车厂 卡罗卡 双擎 城市 享受 新能源 车上 牌 优惠待遇 享受 优待 价位 足以 配 汽油车 直面 竞争 角度 讲 卡罗拉 双擎 精明 车型 讲 卡罗拉 双擎 消费者 优秀 动力 平顺 性 经济性 牺牲 配置 外在 喧嚣 少 动力 驰骋 咆哮 角度 讲 精明 购车者 丰田 卡罗 拉 纵横 江湖 秘密";
        String sentence_c9eae364e80cb267 = "新一代 丰田 凯美瑞 工厂 下线 新车 丰田 全球化 打造 全新 外观 内饰 设计 全球 车型 新一代 凯美瑞 前代 区分 北美版 国际版 车型 未来 国产 新一代 凯美瑞 该车 设计 国产 版本 今年年底 上市 外观 新一代 凯美瑞 采用 激进 设计 风格 前 格栅 采用 飞 翼状 设计 前 保险杠 氛围 造型 尾标 车型 红色 倾向 运动 风格 雷克萨斯 纺锤 状 前 脸 尾标 动力 车型 银色 主打 豪华 采用 横幅 式 前 保险杠 造型 尾灯 采用 光源 向下 延伸 格栅 设计 雷克萨斯 跑车 神韵 新车 增添 运动 气息 运动 版本 车尾 鸭 翼形 小型 扰流板 采用 双边 排气 布局 夸张 扩散器 新车 内饰 大胆 采用 不规则 曲线 设计 中控 面板 集成 不规则 区域 方向盘 采用 传统 式 设计 细节 处 调整 车内 全景 天窗 电子 手刹 选装 红色 运动 座椅 动力 美版 发动机 自然 吸气 发动机 匹配 速 自动 变速箱 动力系统 动力 ● 国产 车型 信息 同步 美版 外观";
        double cosineScoreNewsIds = Word2VecUtils.computeCosineScore( Word2VecUtils.buildWordsVector(sentence_8ffbe33ee74dfc15.split(" "), vectorSize, w2vModel), Word2VecUtils.buildWordsVector(sentence_c9eae364e80cb267.split(" "), vectorSize, w2vModel));
        System.out.println(cosineScoreNewsIds);
        System.out.println("-------------------------------1.4.(cosineScoreNewsIds)距离的余弦距离比较-----------------------------------");
        String textrank_tags_8ffbe33ee74dfc15 = "卡罗拉 双擎 丰田 车辆 品牌 品质 北京 车型 发动机 动力";
        String textrank_tags_c9eae364e80cb267 = "车型 运动 设计 外观 动力 版本 采用 丰田 美瑞谍 曾刚";
        double cosineScoreNewsIds_textrank = Word2VecUtils.computeCosineScore( Word2VecUtils.buildWordsVector(textrank_tags_8ffbe33ee74dfc15.split(" "), vectorSize, w2vModel), Word2VecUtils.buildWordsVector(textrank_tags_c9eae364e80cb267.split(" "), vectorSize, w2vModel));
        System.out.println(cosineScoreNewsIds_textrank);
        System.out.println("-------------------------------1.5.(cosineScoreNewsIds)距离的余弦距离比较-----------------------------------");
        double cosineScoreNewsIds_word1_s4 = Word2VecUtils.computeCosineScore( Word2VecUtils.buildWordsVector(words_1, vectorSize, w2vModel), Word2VecUtils.buildWordsVector(sentence3.split(" "), vectorSize, w2vModel));
        System.out.println(cosineScoreNewsIds_word1_s4);
    }


    /**
     * . 采用tag计算   cmsgqmpc6oyvr  480000（生活）  的相关新闻 为3efb5e7bdefdfd7d和2a7a4ff3feaf3f3f，明显不正确
     {"vFeedTag":{"0.0.1":[{"短信":0.9729118},{"信息":0.98907214},{"主将":0.9526567},{"口号":1.0279036},{"方式":1.0126568},{"幼儿园":1.0667655},{"经典":1.0201882},{"旧情":1.0580716},{"马上拉":1.4473567},{"时期":1.0711678}]}}
     【唔哩来撩】收到前任短信“我还喜欢你”时应该怎么回？


     短信 信息 主将 口号 方式 幼儿园 经典 旧情 马上拉 时期

     1.1 3efb5e7bdefdfd7d   480300（家居）
     这些经典的家具问题！你不应该错过！
     {"vFeedTag" : {"0.0.1" :[{"家具":0.99448675},{"经典":0.99448675}]}}

     家具 经典

     1.2 2a7a4ff3feaf3f3f   480300（家居）
     {"vFeedTag" : {"0.0.1" :[{"北欧":1.6906272},{"经典":0.9944866}]}}
     北欧 经典



     分析问题存在的原因： tags的关键词比较少导致分配错误。

     1.3 解决方案，通过ansj进行分词（stopwords过滤和个别词的简单过滤）---> 然后通过Word2vec 的词向量计算。注意： 第一个关键词是  类别信息

     生活 唔 撩 前任 短信 时 回 能比 分手 人心 塞 分手 前任 纠缠 说 纠缠 太过分 明明 分手 发来 短信 机智 唔 前任 发来 说 圈 主将 话题 选 获 赞 唔 送 红包 先 拒绝 派 发 信息 信不信 怼 死 打着 口号 经典 拒 怼 收藏 好哟 分手 闹 开心 杀人 心 旧情 复燃 美好 幼儿园 感天动地 新 另一半 哈哈哈 抱歉 太 理 分手时 当机立断 马上拉 黑 根本就是 凄凉 经验 评论 区
     家居 经典 家具 经典 家具
     家居 复古 经典 北欧 复古风 复古 经典 北欧 复古风
     */

    @Test
    public void testComputeTextConsineScoreByWordVec_02(){

        int vectorSize = 200;
        String words="生活 唔 撩 前任 短信 时 回 能比 分手 人心 塞 分手 前任 纠缠 说 纠缠 太过分 明明 分手 发来 短信 机智 唔 前任 发来 说 圈 主将 话题 选 获 赞 唔 送 红包 先 拒绝 派 发 信息 信不信 怼 死 打着 口号 经典 拒 怼 收藏 好哟 分手 闹 开心 杀人 心 旧情 复燃 美好 幼儿园 感天动地 新 另一半 哈哈哈 抱歉 太 理 分手时 当机立断 马上拉 黑 根本就是 凄凉 经验 评论 区 ";

        String word1="家居 经典 家具 经典 家具 ";
        String word2="家居 复古 经典 北欧 复古风 复古 经典 北欧 复古风 ";

        double cosineScore1_words_word1 = Word2VecUtils.computeCosineScore(Word2VecUtils.buildWordsVector(words.split(" "), vectorSize, w2vModel), Word2VecUtils.buildWordsVector(word1.split(" "), vectorSize, w2vModel));
        System.out.println("cosineScore1_words_word1:" + cosineScore1_words_word1);//cosineScore1_words_word1:0.03991371393261079
        double cosineScore1_words_word2 = Word2VecUtils.computeCosineScore(Word2VecUtils.buildWordsVector(words.split(" "), vectorSize, w2vModel), Word2VecUtils.buildWordsVector(word2.split(" "), vectorSize, w2vModel));
        System.out.println("cosineScore1_words_word2:" + cosineScore1_words_word2);//cosineScore1_words_word2:0.1022375129223416
    }

    private static void printWordsVector(double[] wordsVector_1) {
        StringBuffer wordsBuffer = new StringBuffer();
        int count = 0;
        int wordsVectorLength = wordsVector_1.length;
        for (int i = 0; i < wordsVectorLength; i++) {
            wordsBuffer.append(wordsVector_1[i]);
            count += 1;
            if (count != wordsVectorLength) {
                wordsBuffer.append(",");
            }
        }
        System.out.println(wordsBuffer);
    }


    @Test
    public void testWordLength(){
        String word = "北京";
        System.out.println(word.length());
    }
}
/**
 *
 Input Result:


 Load News word2Vec Model successfully,the time is 102s
 -------------------------------1.1.【投资】  关键词的同义词-----------------------------------
 资本	782408.327137235
 投资者	763129.5662595857
 投资人	743162.1265930445
 资产	721816.647049782
 基金	721212.5180297984
 资金	720170.0363401364
 私募	697714.710380789
 企业	695402.1979681276
 融资	694402.1856521369
 并购	674471.4168534336
 -------------------------------1.2.【黄飞鸿】  关键词的同义词-----------------------------------
 方世玉	3369854.656889707
 警察故事	3327364.619644495
 功夫片	3253980.0145932636
 醉拳	3215501.087689645
 武术指导	3190052.638941494
 武打	3157699.54503617
 武侠片	3132025.28115942
 吴宇森	3116860.397585014
 徐克	3104681.7448405162
 李连杰	3103499.356360355
 -------------------------------1.3.【习近平】  关键词的同义词-----------------------------------
 总书记	1309540.2466774343
 重要讲话	1130940.2874737347
 贯彻	1125907.7383304203
 党中央	1065820.3158950794
 十八届	977863.25266753
 同志	970612.3737561465
 指示精神	942475.6429439788
 认真学习	933368.575684405
 胡锦涛	930892.5400540156
 十八大	924159.746496946
 -------------------------------1.4.【马云】  关键词的同义词-----------------------------------
 阿里巴巴	696870.3815692059
 马化腾	639223.965736335
 蚂蚁	550694.8060594413
 阿里	548613.7021694303
 互联网金融	537924.9543409514
 王健林	536539.2778342582
 互联网电商	532789.7808522749
 刘强东	530575.153957411
 大佬	521040.3137675699
 企业家	515941.3004977615


 -------------------------------1.5.【生活】  关键词的同义词-----------------------------------
 晒晒	730169.7533099676
 🔴	664501.6362013684
 生活知识	652392.7731806132
 告诉您	647206.1718441544
 收好	641688.5409035311
 早上好	638074.9773014195
 搞笑图片	627519.8202931918
 别忘了	624571.5647161978
 奉送	624512.1776874246
 各位朋友	621907.2177790685
 -------------------------------1.6.【家居】  关键词的同义词-----------------------------------
 装修	527587.2166769102
 家具	519542.3736560367
 家装	502933.2450705217
 客厅	491030.70961996535
 卧室	465606.10803723364
 中式	436845.8563877454
 卫生间	433645.459904296
 厨房	431360.8504815447
 阳台	415597.9044952247
 北欧	415067.5671273492


 -------------------------------句子的向量表示-----------------------------------
 -61919.02356335001,125860.42579244911,13012.467213481938,7951.383247025516,-112252.17801435279,-32894.55441256182,47145.87354131576,132952.8378480684,22414.623600111096,-76402.24279505179,-88452.82702300746,-125821.27942006523,-4995.003785299598,-73388.43881253584,-23136.610405318235,-28393.665882355577,10278.882206628083,-116717.88151060331,-124639.99878601634,55987.85507258144,-61687.313359531785,-2082.0539836358585,26309.89189945886,-129554.64911336637,-25333.669686693665,-81752.96998638188,51678.35680592388,145313.58064815975,53102.710674320886,-87120.21357153097,36774.7632989446,173618.34061606872,31415.1074949492,-105131.29008161912,-48034.19186093392,-85540.55823481848,-44590.8819468087,-170859.94151376147,163075.02197489608,-90387.96201037486,74589.35503058477,-54132.82409164009,76806.42611554347,112722.95494037593,-52091.25050843965,-8081.71784287199,-39922.15414288722,-1120.2669596540818,-61093.65894842585,28775.9453780148,149773.98085623924,-48706.98075208751,101270.11822341778,41776.28233155417,-80937.55592164205,-82690.34295584302,-9425.000371040554,31694.74351459468,-133281.99952067804,-21993.48620829451,267756.02892254473,135082.42516081923,226262.1447731508,82092.01289801642,-112956.57039061381,43552.367083838224,-52837.47330593844,88999.20045177215,75393.23437580494,-30330.22407853713,-31123.89725907352,11170.451925575186,88104.2291254166,-68945.76884250467,82104.40126303576,27861.62943876774,77801.53546758529,5950.019113523151,-80774.80498203663,-165041.96771408222,-8574.29927608945,78419.24754403491,77276.97043518626,-86031.13850444829,-24398.394014131038,-33080.71948018205,68172.59251025839,86630.85262179593,25828.879457246272,-31613.81745994638,-18807.13552380483,-157956.67930673022,-73372.76743757616,99073.47827148438,73535.89810936604,-71184.60356966071,94320.33005628674,48983.49780385428,-119618.5483958393,43181.98776385106,105728.62742908722,-189111.83804069308,55929.21813180906,-87858.02563924527,-49803.31939249301,-62245.72912037701,50821.69988229734,35295.82998069273,-129468.62374233981,-27842.362896560528,-31379.55886252867,-82883.6991229976,-20868.984354281645,129564.04027872348,-24321.26572355218,92651.8457927179,-103980.36242115825,74522.97668205052,66808.32874770559,17285.56380581637,150685.95341897666,-138295.22206717674,102863.59019484214,-28015.451746389408,-39579.975315198986,-33236.1621698502,105022.70909860593,-64470.052512632596,4123.261119597549,117041.4182736458,-142723.2294529906,20860.276105688252,46710.70819371775,27822.293676183857,-32582.126666427754,-51190.868188805536,-90702.93920002508,-187100.41066748943,32572.624344851993,-98750.27493846089,119529.33497045675,139064.45084822067,-151923.24086026533,166157.15017112243,13542.807242017274,84648.68633641234,-28895.35283695886,179126.52938198825,46612.99596034059,-220723.9381590677,-114335.66830024369,-45339.257371255015,78487.78364325008,13183.506471966384,104555.70232090382,133125.73615397883,33743.4331956216,153208.3657584934,-92212.11475617294,-208328.52544886037,26711.536992904243,3877.832256072158,-62834.52205552968,-142207.35267569165,51120.658477503224,48237.29062309615,12528.498501278938,-24980.437834853426,-45240.014221751364,-81189.31490948878,-297889.15844950546,131736.2573510966,43540.338399904584,-10160.348247563074,1907.8091313073394,-124256.29239115583,-13451.009121676103,132610.33725031582,113393.12764970971,83706.73394579407,-33876.7070110916,40641.174366802254,12033.510100758403,-73292.02108624659,10846.062820994526,36028.9497115109,120508.95896197678,75914.83914366557,-10842.429201458572,-177965.13300512472,-24416.681349448107,-133459.471106293,-272919.52754085435,-97531.82594341313,-190571.15170596063,16734.42194443449,-15374.236849583616,96357.71205293149,-65013.62341116109,-9222.947550082425
 -24578.573292216315,51825.809852475024,-35315.877857646,-14648.444961297708,-45838.7657980997,13500.79191014024,-13435.142760229892,38385.82028648502,26781.662856555376,-60915.82898149334,-13331.870767562115,-92413.77364161758,2979.0473952996927,23693.86892450051,-14900.067269247087,6167.850869475818,-25875.019723360656,-113271.4991204934,-71439.13009993757,-27971.93633482886,-68121.23792004194,27648.02247694672,64412.717583328,-35720.23759885694,20243.242183497696,16397.204761942878,44267.35163935677,66144.66702020363,50960.78843814037,-30446.063908811477,56981.40569768187,35206.74368636335,24704.234365932276,-65861.85024924357,54895.80140961194,-99993.81808321593,37801.69883652984,-15835.71106557377,-21705.90610591701,-16232.473236584272,57532.248854840385,-14697.99884333376,67974.20863617443,6067.999943967726,-3953.8049116290986,9469.385552077996,4719.188076331967,-10397.049994797004,-56147.636834816854,-108485.08594400375,83585.14797323258,16530.598334640752,48683.39660944704,47539.331232790086,-36979.040517337984,19659.430948226178,33257.367757828506,-36045.95610371574,-85020.71667680584,52167.058155497565,94225.40751322762,21428.987096567624,57865.025798860144,47896.16744644915,-78453.87285476434,35408.943561991706,84213.30043555087,-52296.62525014408,-22809.396400326586,-23049.238226218302,-79769.1239553983,1911.1892445048347,55618.54218029585,-29418.33713018699,24519.952332543544,-79805.72366376783,-70459.82011638704,6182.4088595030735,29050.342201107836,-45566.6251520876,-17423.823664931002,40812.844536453,-823.9240322425717,57135.6332987801,38795.697217597335,-39480.704268658745,-56791.18303742956,25899.22081268811,-53085.11891049244,10059.068019179047,69642.06604003906,-80252.01413214012,-23613.841331106716,20168.633358814677,8678.988801549693,12203.212338306865,70161.39782714844,47233.44206062692,-93085.15365250384,-77359.5689597208,25594.49448082095,32717.932445088372,27360.98686843622,-21707.876801037397,-22338.97638239626,-44785.65034359791,29164.44057977395,17831.167953241067,-13809.064463130764,2559.5669745773566,7744.486220062756,-47683.569559066025,11447.380451139856,86803.9020628382,6295.14942206711,-38513.18785020171,-54182.14596244937,37184.04736328125,31954.45966876921,75633.75054431353,52423.880285044186,16180.283161601083,22607.258138062523,16949.098948994622,-20055.66361474209,-33583.38990758677,-17387.684888495773,45578.89237800973,-43549.92248335041,56794.61589075308,-39219.10093413806,64126.91206584993,53971.13255435131,84966.01878682121,-37141.26883134685,-50542.51167672579,-23547.19583279969,-924.987681404489,-17653.691552334145,51302.76689172964,13361.826468045594,132312.01520876025,-75551.0164494749,39560.61401767418,96535.96046322682,91561.38663470159,-78778.94450603548,41667.00928134606,-16493.641674291892,-156451.2972852363,-79090.18021380315,29597.909207703637,9575.985851850666,-84787.4230756916,-6008.348165543353,20844.36669921875,28687.733797792527,35887.77402984119,-9201.040013047515,-127267.22901791432,5908.770782470703,4503.820200435451,-33717.33145291688,-33747.465153928664,87246.93824442879,6445.070756755891,-18919.318953717342,-50573.45497806737,-18733.225882108094,-31655.604766345416,-34938.0197553791,43311.975633965165,-21444.029777151638,24948.3233182313,8239.406778304303,-38422.0680592021,-33416.12294681737,32455.34599529329,-1788.0565485719774,15146.5372699675,34458.927635317945,-40643.87408547323,68817.92557713242,-37297.357758568935,33400.51361384157,-38230.68013575819,56342.863593429815,39041.35800621158,116002.54111768378,-170502.36852427,51234.79855997054,-66699.04014412302,-69762.98997222401,-21764.637265064677,-45279.94426589716,50331.12860807825,35866.567462858606,47129.12808977971,-933.8249311603483,-49628.71080092133
 -------------------------------1.1.(cosineScore1_2)距离的余弦距离比较-----------------------------------
 0.5501444042332296
 -------------------------------1.2.(cosineScore3_4)距离的余弦距离比较-----------------------------------
 0.8125541790650117
 -------------------------------1.3.(cosineScoreNewsIds)距离的余弦距离比较-----------------------------------
 0.8422920801976059
 -------------------------------1.4.(cosineScoreNewsIds)距离的余弦距离比较-----------------------------------
 0.714023747919713
 -------------------------------1.5.(cosineScoreNewsIds)距离的余弦距离比较-----------------------------------
 -0.05536450268485793


 cosineScore1_words_word1:0.03991371393261079
 cosineScore1_words_word2:0.1022375129223416



 */
