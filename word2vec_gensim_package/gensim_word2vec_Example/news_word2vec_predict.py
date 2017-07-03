#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""

"""

from gensim.models import word2vec
import logging

# 主程序
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)
# 加载模型
model = word2vec.Word2Vec.load(u"./word2vec_model/news_word2vec.custompy")
# 计算某个词的相关词列表
y2 = model.most_similar("克洛普", topn=20)  # 20个最相关的
print u"和China最相关的词有：\n"
for item in y2:
    print item[0], item[1]
print "--------\n"
if __name__ == "__main__":
    pass
