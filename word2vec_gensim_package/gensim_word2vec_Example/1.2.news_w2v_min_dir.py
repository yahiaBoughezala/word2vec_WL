#!/usr/bin/env python
# -*- coding: utf-8 -*-
'''
对于大量的输入语料集或者需要整合磁盘上多个文件夹下的数据，我们可以以迭代器的方式而不是一次性将全部内容读取到内存中来节省 RAM 空间
'''
import os

import multiprocessing

import time
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

sentences = MultiGetSentences(u'./data/news_w2v_sampling_min') # a memory-friendly iterator
print "===========================训练的模型==========================="
model = word2vec.Word2Vec(sentences, size=200, window=5, min_count=1, workers=multiprocessing.cpu_count())
endTime=time.time()
print "save news_word2vec_min custompy successfully",(endTime-time.time())

print "---------------------1.1 进行相关性比较-----------------------------------"
# 进行相关性比较
similar = model.most_similar(['体育'], topn=4)
for word in similar:
    wordName =word[0]
    score = word[1]
    print wordName +":" + str(score)

print "---------------------1.2 进行相似度比较-----------------------------------"
print  model.similarity('体育','北京')



'''
输出结果：

===========================训练的模型===========================
---------------------1.1 进行相关性比较-----------------------------------
毛:0.992698788643
国际组:0.986666917801
知名:0.984381377697
级:0.983260035515
---------------------1.2 进行相似度比较-----------------------------------
0.932634991806
'''