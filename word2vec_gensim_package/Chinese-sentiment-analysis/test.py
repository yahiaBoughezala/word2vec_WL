# -*- coding: utf-8 -*-
import numpy as np
import jieba
import sys
reload(sys)
sys.setdefaultencoding('utf8')

#对每个句子的所有词向量取均值，来生成一个句子的vector
def build_sentence_vector(text, size,imdb_w2v):
    vec = np.zeros(size).reshape((1, size))
    count = 0.
    for word in text:
        try:
            vec += imdb_w2v[word].reshape((1, size))
            count += 1.
        except KeyError:
            continue
    if count != 0:
        vec /= count
    return vec
if __name__=="__main__":
    string = '电池充完了电连手机都打不开.简直烂的要命.真是金玉其外,败絮其中!连5号电池都不如'
    words = jieba.lcut(string)
    print words#[u'\u7535\u6c60', u'\u5145\u5b8c', u'\u4e86', u'\u7535\u8fde', u'\u624b\u673a', u'\u90fd', u'\u6253\u4e0d\u5f00', u'.', u'\u7b80\u76f4', u'\u70c2', u'\u7684', u'\u8981\u547d', u'.', u'\u771f\u662f', u'\u91d1\u7389\u5176\u5916', u',', u'\u8d25\u7d6e\u5176\u4e2d', u'!', u'\u8fde', u'5', u'\u53f7', u'\u7535\u6c60', u'\u90fd', u'\u4e0d\u5982']
    for word in words:
        print  word,#电池 充完 了 电连 手机 都 打不开 . 简直 烂 的 要命 . 真是 金玉其外 , 败絮其中 ! 连 5 号 电池 都 不如

