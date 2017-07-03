#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""

"""

from gensim.models import word2vec
import logging

# 主程序
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)
# 加载模型
model = word2vec.Word2Vec.load(u"./word2vec_model/text8.custompy")
# 计算两个词的相似度/相关程度
y1 = model.similarity("woman", "man")
print u"woman和man的相似度为：", y1 #0.689936745429
print "--------\n"

# 计算某个词的相关词列表
y2 = model.most_similar("good", topn=20)  # 20个最相关的
print u"和good最相关的词有：\n"
for item in y2:
    print item[0], item[1]
print "--------\n"

# 寻找对应关系
print ' "boy" is to "father" as "girl" is to ...? \n'
y3 = model.most_similar(['girl', 'father'], ['boy'], topn=3)
for item in y3:
    print item[0], item[1]
print "--------\n"

more_examples = ["he his she", "big bigger bad", "going went being"]
for example in more_examples:
    a, b, x = example.split()
    predicted = model.most_similar([x, b], [a])[0][0]
    print "'%s' is to '%s' as '%s' is to '%s'" % (a, b, x, predicted)
print "--------\n"

# 寻找不合群的词
y4 = model.doesnt_match("breakfast cereal dinner lunch".split())
print u"不合群的词：", y4
print "--------\n"

'''
程序的输出结果： 
woman和man的相似度为： 2017-06-27 15:14:16,776 : INFO : precomputing L2-norms of word weight vectors
0.689936745429
--------

和good最相关的词有：

bad 0.72913479805
poor 0.570361733437
luck 0.529860556126
safe 0.508648514748
reasonable 0.507859349251
easy 0.497260928154
wrong 0.492454111576
fun 0.491364955902
quick 0.488845974207
happy 0.487308382988
simple 0.484117388725
true 0.482919752598
practical 0.479188859463
pleasure 0.475019216537
sick 0.474985182285
you 0.474665880203
dangerous 0.470970988274
little 0.469758749008
clever 0.467076063156
everyone 0.46677416563
--------

 "boy" is to "father" as "girl" is to ...? 

mother 0.771958947182
grandmother 0.722119927406
wife 0.702465415001
--------

'he' is to 'his' as 'she' is to 'her'
'big' is to 'bigger' as 'bad' is to 'worse'
'going' is to 'went' as 'being' is to 'was'
--------

不合群的词： cereal
--------

2017-06-27 15:14:17,153 : INFO : saving Word2Vec object under text8.custompy, separately None
2017-06-27 15:14:
'''
if __name__ == "__main__":
    pass
