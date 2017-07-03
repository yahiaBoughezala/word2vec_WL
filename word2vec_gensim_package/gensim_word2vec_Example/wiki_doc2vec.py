#!/usr/bin/env python
# -*- coding: utf-8 -*-
from gensim.models import Doc2Vec


def documents(args):
    documents=['我 爱 北京 天安门','我 爱 北京 长城']
    return documents

model = Doc2Vec(documents, size=100, window=5, min_count=1, workers=4)
model.vector_size

