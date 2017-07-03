#!/usr/bin/env python
# -*- coding: utf-8 -*-
'''
对于大量的输入语料集或者需要整合磁盘上多个文件夹下的数据，我们可以以迭代器的方式而不是一次性将全部内容读取到内存中来节省 RAM 空间
'''
import os

import multiprocessing
from gensim.models import word2vec
import sys
reload(sys)
sys.setdefaultencoding('utf8')

class MultiGetSentences(object):
    def __init__(self, dirname):
        self.dirname = dirname

    def __iter__(self):
        for fname in os.listdir(self.dirname):
            for line in open(os.path.join(self.dirname, fname)):
                yield line.split()

sentences = MultiGetSentences(u'./data/wiki_w2v_sampling') # a memory-friendly iterator

model = word2vec.Word2Vec(sentences, size=5, window=5, min_count=1, workers=multiprocessing.cpu_count())
print model#Word2Vec(vocab=5, size=200, alpha=0.025)
print "\n===========================1. 进行相关性比较==========================="
# 进行相关性比较
word = '爱'
similar = model.most_similar([word], topn=4)
for word_tuple in similar:
    wordName =word_tuple[0]
    cosine_distance = word_tuple[1]
    print wordName +":" + str(cosine_distance),#天安门:0.112462349236 北京:0.0837550461292 长城:-0.00229895114899 我:-0.0121749490499
print "\n===========================2. 直接获取某个单词的向量表示，直接以下标方式访问即可==========================="
#我 爱 北京 天安门

print model['爱']  # raw NumPy vector of a word
print model['天安门']  # raw NumPy vector of a word
print model['长城']  # raw NumPy vector of a word

print "\n===========================3. 直接获取每行文本的向量表示==========================="
for sentence_list in sentences:
    for word in sentence_list:
        print word,

keyed_vectors = model.wv
print keyed_vectors.vocab
