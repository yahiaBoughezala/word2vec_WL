#!/usr/bin/env python
# -*- coding: utf-8 -*-
from gensim.models import word2vec
import numpy as np
from scipy import linalg


# 对每个句子的所有词向量取均值，来生成一个句子的vector
def build_sentence_vector(text, size, w2v_model):
    vec = np.zeros(size).reshape((1, size))  # 初始化 1 行 size 列的 为0 的数组
    count = 0.  # 单词的总数，即对应的词向量的个数
    for word in text:
        try:
            vec += w2v_model[word].reshape((1,
                                            size))  # 1.wiki_w2v[word] 获取每个词的向量  2.  wiki_w2v[word].reshape((1, size)) 词向量的构成1 行 size 列的数组 3. 然后数组中的元素叠加
            count += 1.
        except KeyError:
            continue
    if count != 0:
        vec /= count  # vec 中的每个元素都除以count
    return vec

def get_news_w2v():
    model = word2vec.Word2Vec.load(u"./news_w2v_model/news_w2v_dir.model")
    return model
def compute_text_cosin(text1, text2, w2vModel):
    size = 200
    text1_Vector = build_sentence_vector(text1.split(), size, w2vModel)
    text2_Vector = build_sentence_vector(text2.split(), size, w2vModel)
    text1_vectorList = np.ndarray.tolist(text1_Vector[0])
    text2_vectorList = np.ndarray.tolist(text2_Vector[0])
    consin = np.dot(text1_vectorList, text2_vectorList) / (linalg.norm(text1_vectorList) * linalg.norm(text2_vectorList))
    return consin


if __name__ == "__main__":
    # 加载模型
    w2vModel = get_news_w2v()
    size = 200
    print "---------------------【ONLINE 环境数据】计算句子的相似度(word2vec tags)-----------------------------------"
    text1 = "金奖 体验 吉利 帝豪 门 下午 汽车 技术 研究 中心 北京 雁栖湖 生态 汽车 车型 吉利 帝豪 自动 旗舰型 广 汽 本田雅阁 混 动 锐 酷 版 金牌 生态 汽车 车型 参评 车型 各车 企 自愿 提出 市场 热度 高 车型 选送 参评 生态 性能 优于 行业 平均水平 数据分析 车型 指标 综合 油耗 尾气 排放 车内 噪声 指标 车 企 健康 节能 环保 生态 性能 需 历届 正 呈现 参评 车型 变少 分数 高 评选 雅阁 混 动 版 车型 成绩 不禁 疑问 参评 车型 车企 门槛 太 高 测试 分数 太 高 车辆 检测 标准 宽限 标准 过时 代表 生态 汽车 标杆 消费者 深思 不可否认 确实 生态 车型 要说 帝豪 同日 有幸 吉利 帝豪 生态 门品鉴 旅 体验 门 吉利 汽车 吉利 帝豪 生态 舱 打造 模拟 噪声 雾 霾 沙尘 环境 房间 体验 模拟 噪声 环境 模拟 北京 晚 高峰 国贸 桥 仪表 车外 环境 分贝 环境 呆 感到烦闷 难受 上班族 闭 紧 车门 体验 确实 帝豪 分贝 车外 喧嚣 隔离 车内 噪音 分贝 车内 震动 噪声 异 响 调节 车 噪声 模拟 唐山 雾 霾 天气 时 帝豪 配置 空气质量 管理系统 印象 模拟 唐山 空气 污染 空间 闷热 天气 雾 霾 尾气 污染源 狭小 体验 空间 难以承受 坐到 车内 帝豪 数秒 感知 自动 切换 循环 阻隔 污染空气 保证 车内 空气质量 瞬间 跳入 新手 车主 外 循环系统 用法 不太熟悉 主动 探测 空 环境 切换 循环 动作 确实 消费者 测试 模拟 沙尘 天气 帝豪 车内 空气 沙尘 天气 帝豪 采用 高效 精 滤 技术 活性炭 化学 滤料 强效 吸附 技术 等离子 杀菌 味 技术 技术 净化 车内 空气 电晕 放电 释放出 负氧离子 吸附 有害物质 净化 车内 空气 车内 数值 降至 门 体验 领略到 平均 月 销 达 自主 品牌 轿车 魅力 吉利 身为 车企 消费者 生态 健康 穿越 门 发现 帝豪 空气质量 控制系统 负离子 装置 净化 车内 环境 出众 能力 车内 噪声 综合 油耗 汽车 市场竞争 自主 品牌 车 企 费心 尽力 外观 尺寸 外观 科技 配置 下功夫 忽视 污染 吉利 前瞻性 生态 健康 相比 合资 品牌 价格 生态 环保 角度 吉利 走 自主 品牌 前列"  # "汽车 帝豪 生态 车型 汽车 噪声 车企 环境 北京 消费者 体验"
    text2 = "帝豪 雅阁 锐 混 动 缘何 获 白金 认证 汽车 技术 研究 中心 生态 汽车 车型 浙江 吉利 控股集团 有限公司 帝豪 自动 旗舰型 广 汽 本田 汽车 有限公司 雅阁 锐 混 动 锐 酷 版 吉利 帝豪 自动 旗舰型 广 汽 本田雅阁 锐 混 动 锐 酷 版 最高级别 白金 牌 认证 生态 汽车 官方 参评 车型 吉利 汽车 广 汽 本田 主推 热销 车型 帝豪 各项 指标 成绩 表现出色 环保 生态 净化 优良 品质 广 本雅阁 锐 混 动 纪录 节能 环保 性能 混 动 车型 优势 天津 华诚 认证 中心 总经理 管理中心 副 主任 郑元辉 广 汽 本田 颁发 认证 证书 生态 汽车 规程 简称 编制 生态 汽车 生态 设计 汽车 全 生命周期 汽车 健康 节能 环保 绩效 指标 综合性 生态 汽车 划分 认证 推崇 生态 设计 价值 生命周期 源头 竭力 环境污染 产品设计 开发阶段 系统 原材料 选用 制造 生产 各个环节 环境 影响 力求 全 生命周期 中 限度 资源 消耗 尽可能少 有毒 有害物质 原材料 污染物 排放 环境保护 国家 认监委 认证 监管部 处长 王昆 吉利 汽车 颁发 认证 证书 生态 汽车 提出 健康 节能 环保 汽车 生态 性能 车内 空气质量 车内 噪声 有害物质 综合 油耗 尾气 排放 指标 利用率 可回收 利用率 核算 报告 企业 温室 气体 排放 报告 生命周期 报告 加 分项 讲 十分相似 内容 汽车 碰撞 环境影响 指标 评定 健康 节能 环保 生态 评定 采用 量化 白金 金 银 铜牌 牌 消费者 直观 参评 车型 成绩 评定 标准 参评 车型 认证 证书 牌 货真价实 评定 公正 编制 生态 汽车 规程 时 摸底 车型 难以达到 认证 标准 生态 汽车 车型 参评 车型 各车 企 自愿 提出 市场 热度 高 车型 选送 参评 生态 性能 优于 行业 平均水平 中 汽 中心 副 主任 管理中心 主任 张建伟 相关 数据 车型 指标 综合 油耗 尾气 排放 车内 噪声 指标 品牌 改进 提升 空间 自愿 参评 品牌 企业 策略 前瞻性 强制性 提升 生态 性能 目光 长远 汽车品牌 中 吉利 奇瑞 江淮 着手 提升 生态 性能 正向 技术 开发 设计 研发 阶段 车辆 健康 节能 环保 生态 性能 消费市场 环境 负 责任 态度 品牌 长远 向上 必需 态度"  # "生态 汽车 车型 性能 指标 认证 帝豪 管理中心 中心 本田"
    cosin = compute_text_cosin(text1, text2, w2vModel)
    print "cosin", cosin  # hM_1_Cosin 0.834618498544->hM_1_Cosin 0.865184046861
