#!/usr/bin/env python
# -*- coding: utf-8 -*-

s1='a'
print s1

s2='中国'#  通过文件头部引入  -*- coding: utf-8 -*- 来声明
print s2

s3=u'美国'
print s3 #保持源码文件的utf-8不变，而是在’哈’前面加个u字


s4=unicode('日本',"UTF-8")#将字符串封装为一个unicode
print s4 #保持源码文件的utf-8不变，而是在’哈’前面加个u字

#decode是将普通字符串按照参数中的编码格式进行解析，然后生成对应的unicode对象
s4_1 = '日本'.decode("utf-8")
print s4_1,type(s4_1)


s5=unicode("美国", 'utf-8').encode('utf-8')
print s5