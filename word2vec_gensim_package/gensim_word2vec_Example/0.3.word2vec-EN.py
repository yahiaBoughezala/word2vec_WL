# coding=utf-8
# 引入 word2vec
from gensim.models import word2vec
import multiprocessing


# 引入数据集
raw_sentences = ["the quick brown fox jumps over the lazy dogs",
                 "yoyoyo you go home now to sleep"]
# 切分词汇
sentences= [s.encode('utf-8').split() for s in raw_sentences]
print sentences
# 构建模型
model = word2vec.Word2Vec(sentences,size=200, window=5, min_count=1, workers=multiprocessing.cpu_count())

# 进行相关性比较
print  model.most_similar(['dogs'],topn=5)
print  model.similarity('dogs','you')
print  model.most_similar(positive=['lazy', 'sleep'], negative=['fox'], topn=1)





