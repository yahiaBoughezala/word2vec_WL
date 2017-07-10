package com.caishi.bigdata.categorize.textseg.common.main;

import com.alibaba.fastjson.JSON;
import com.caishi.bigdata.categorize.textseg.common.domain.CategoryInfo;
import com.caishi.bigdata.categorize.textseg.common.domain.CustomTerm;
import com.caishi.bigdata.categorize.textseg.common.util.AnsjWord;
import com.caishi.bigdata.categorize.textseg.common.util.CategoryInfoUtils;
import com.caishi.bigdata.categorize.textseg.common.util.InitWords;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 提取满足Word2Vector的样本数据
 * Created by fuli.shen on 2017/6/27.
 */
public class Word2VectorTextSegment {
    static String srcPath = "data/newsContent.json";
    static String dstPath = "data/word2vector_sampling.text";
    public static final Logger logger = LoggerFactory.getLogger(Word2VectorTextSegment.class);

    public static void main(String[] args) {
        if (args.length != 2) {
            logger.warn("Usage: java -jar Word2VectorTextSegment-1.0.jar com.caishi.textseg.common.main.Word2VectorTextSegment <srcPath><dstPath> ");
            System.exit(1);
        }
        srcPath = args[0];
        dstPath = args[1];
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String line = null;
        try {
            Map<Integer, String> categorysMap = CategoryInfoUtils.categorysMap;

            reader = new BufferedReader(new FileReader(srcPath));
            writer = new BufferedWriter(new FileWriter(dstPath));

            while ((line = reader.readLine()) != null) {
                StringBuffer wordsBuffer = new StringBuffer();
                CategoryInfo categoryInfo = JSON.parseObject(line, CategoryInfo.class);
                // title 和 content 通过ansj 分词后的关键词
                String title = categoryInfo.getTitle();
                String content = null;
                if (!StringUtils.isBlank(title)) {
                    content = title + " " + categoryInfo.getContent();
                }
                AnsjWord ansjWord = new AnsjWord(content);//停止词一级过滤
                List<CustomTerm> customTerms = ansjWord.getCustomTerms();
                for (CustomTerm customTerm : customTerms) {
                    String word = customTerm.getWord();
                    if (InitWords.stopWordsExtentions.contains(word)
                            || InitWords.DIGIT.contains(String.valueOf(word.charAt(0)))) {//停止词二级过滤，方便扩展
                        continue;
                    }
                    wordsBuffer.append(word + " ");
                }
                wordsBuffer.append("\n");
                writer.write(wordsBuffer.toString());
            }
        } catch (Exception e) {
            logger.error("parse exception ,data:{},err:{} ", line, e);
        } finally {
            close(reader, writer);
        }
    }

    private static void close(BufferedReader reader, BufferedWriter writer) {
        if (null != writer) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != reader) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
