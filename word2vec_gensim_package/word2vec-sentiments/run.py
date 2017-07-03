# coding=utf-8
# gensim modules
from gensim import utils
from gensim.models.doc2vec import LabeledSentence
from gensim.models import Doc2Vec

# numpy
import numpy

# shuffle
from random import shuffle

# logging
import logging
import os.path
import sys

program = os.path.basename(sys.argv[0])
logger = logging.getLogger(program)
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s')
logging.root.setLevel(level=logging.INFO)
logger.info("running %s" % ' '.join(sys.argv))

#每篇文章给唯一的标签
class LabeledLineSentence(object):
    def __init__(self, sources):
        self.sources = sources
        flipped = {}

        # make sure that keys are unique
        #sources的数据形式 {'test-neg.txt': 'TEST_NEG', 'test-pos.txt': 'TEST_POS'}
        for key, value in sources.items():
            if value not in flipped:
                flipped[value] = [key]
            else:
                raise Exception('Non-unique prefix encountered')

    def __iter__(self):
        #{'test-neg.txt':'TEST_NEG', 'test-pos.txt':'TEST_POS'}
        #多个列表数据
        for source, prefix in self.sources.items():
            with utils.smart_open(source) as fin:
                #遍历每行文本
                for item_no, line in enumerate(fin):
                    #每行文本被分割 加上其编号
                    yield LabeledSentence(utils.to_unicode(line).split(), [prefix + '_%s' % item_no])

    #分词后的语料转为最终doc2vec输入
    def to_array(self):
        self.sentences = []
        #{'test-neg.txt':'TEST_NEG', 'test-pos.txt':'TEST_POS'}
        for source, prefix in self.sources.items():
            with utils.smart_open(source) as fin:
                #打开每行文件
                for item_no, line in enumerate(fin):
                    #语料为列表[] 形式
                    self.sentences.append(LabeledSentence(utils.to_unicode(line).split(), [prefix + '_%s' % item_no]))
        #返回最终的语料
        return self.sentences

    #打乱语料
    def sentences_perm(self):
        shuffle(self.sentences)
        return self.sentences

#每篇文章都有一个编号
sources = {'test-neg.txt':'TEST_NEG', 'test-pos.txt':'TEST_POS', 'train-neg.txt':'TRAIN_NEG', 'train-pos.txt':'TRAIN_POS', 'train-unsup.txt':'TRAIN_UNS'}

#数据被转换为语料
sentences = LabeledLineSentence(sources)

#生成向量维度100
model = Doc2Vec(min_count=1, window=10, size=100, sample=1e-4, negative=5, workers=7)

#转为数组输入
model.build_vocab(sentences.to_array())

# for epoch in range(10):
#     logger.info('Epoch %d' % epoch)
#     #循环迭代进行模型训练
#     model.train(sentences.sentences_perm(),total_examples=model.corpus_count,epochs=model.iter)

#简化模型训练
model.train(sentences.sentences_perm(),total_examples=model.corpus_count,epochs=10)

model.save('./imdb.d2v')
