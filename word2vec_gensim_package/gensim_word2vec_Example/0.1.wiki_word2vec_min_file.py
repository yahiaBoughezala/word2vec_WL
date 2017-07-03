#!/usr/bin/env python
# -*- coding: utf-8 -*-
import multiprocessing

import jieba
from gensim.models import word2vec
import codecs
import  numpy as np

#对每个句子的所有词向量取均值，来生成一个句子的vector
from scipy import linalg


def build_sentence_vector(text, size,w2v_model):
    vec = np.zeros(size).reshape((1, size)) # 初始化 1 行 size 列的 为0 的数组
    count = 0. # 单词的总数，即对应的词向量的个数
    for word in text:
        try:
            vec += w2v_model[word].reshape((1, size))# 1.wiki_w2v[word] 获取每个词的向量  2.  wiki_w2v[word].reshape((1, size)) 词向量的构成1 行 size 列的数组 3. 然后数组中的元素叠加
            count += 1.
        except KeyError:
            continue
    if count != 0:
        vec /= count  # vec 中的每个元素都除以count
    return vec

if __name__=="__main__":
    # 引入数据集
    results=[]
    f=codecs.open(u'./data/zh.jian.wiki.seg-1.3gg.txt','r',encoding="utf8")
    for line in f:
        replace = line.replace("\n", "")
        results.append(replace.encode('utf-8').split())

    #构建模型
    size = 5# 每个词向量的长度
    model = word2vec.Word2Vec(results, size, window=5, min_count=1, workers=multiprocessing.cpu_count())
    print "---------------------1.1 进行相关性比较-----------------------------------"
    # 进行相关性比较
    similar = model.most_similar(['爱'], topn=4)
    for word in similar:
        wordName =word[0]
        score = word[1]
        print wordName +":" + str(score)

    print "---------------------1.2 进行相似度比较-----------------------------------"
    print  model.similarity('爱','天安门')
    print "---------------------1.3 获取句子的向量表示-----------------------------------"

    sentence_vector1 = build_sentence_vector('我 爱 北京 天安门'.split(), size, model)
    print sentence_vector1#[[ 0.05692063 -0.01361525  0.00759237 -0.00975635  0.02076727]]
    sentence_vector2 = build_sentence_vector('我 爱 北京 长城'.split(), size, model)
    print sentence_vector2#[[ 0.03147641 -0.05260395 -0.02651844 -0.02845464  0.01595987]]
    print np.ndarray.tolist(sentence_vector1[0])

    print "---------------------1.4 计算句子的相似度-----------------------------------"
    a=np.ndarray.tolist(sentence_vector1[0])#[0.05692063,-0.01361525 , 0.00759237, -0.00975635  ,0.02076727]
    b=np.ndarray.tolist(sentence_vector2[0])#[0.03147641, -0.05260395 ,-0.02651844, -0.02845464 , 0.01595987]
    cosV12 = np.dot(a, b) / (linalg.norm(a) * linalg.norm(b))
    print cosV12#0.619436566106
'''
输出结果： 

---------------------1.1 进行相关性比较-----------------------------------
天安门:0.0287922471762
我:-0.0657515078783
北京:-0.302871078253
长城:-0.488448292017
---------------------1.2 进行相似度比较-----------------------------------
0.0287922654371
---------------------1.3 获取句子的向量表示-----------------------------------
[[ 0.05692063 -0.01361525  0.00759237 -0.00975635  0.02076727]]
[[ 0.03147641 -0.05260395 -0.02651844 -0.02845464  0.01595987]]
---------------------1.4 计算句子的相似度-----------------------------------
0.619436566106


'''