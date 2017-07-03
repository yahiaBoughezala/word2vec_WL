# coding=utf-8

from gensim.models import Doc2Vec

model = Doc2Vec.load('./imdb.d2v')

length = len(model.docvecs)
print length

docvec = model.docvecs[99]
docvec1 = model.docvecs['TEST_NEG_99']
sims = model.docvecs.most_similar(99)
sims2 = model.docvecs.most_similar('TEST_NEG_99')

# print docvec
# print docvec1

#默认最相似的10个文本 最相似的99号文本 与标签TEST_NEG_99文本
#自动相关的文本
print sims
print sims2



