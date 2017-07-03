#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
作者：fuli.shen
时间：2017年6月27日 
"""

from gensim.models import word2vec
import logging

# 主程序
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)
sentences = word2vec.Text8Corpus(u"E:\\work_document\\part-r-00000-news_w2v_sampling.text")  # 加载语料
model = word2vec.Word2Vec(sentences, size=200)  # 训练skip-gram模型; 默认window=5
model.save(u"./word2vec_model/news_word2vec.custompy")
print "save news_word2vec custompy successfully"
if __name__ == "__main__":
    pass
