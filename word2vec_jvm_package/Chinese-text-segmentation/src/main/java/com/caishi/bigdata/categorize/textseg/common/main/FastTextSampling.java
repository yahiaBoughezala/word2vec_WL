package com.caishi.bigdata.categorize.textseg.common.main;

import com.alibaba.fastjson.JSON;
import com.caishi.bigdata.categorize.textseg.common.domain.CategoryInfo;
import com.caishi.bigdata.categorize.textseg.common.domain.CustomTerm;
import com.caishi.bigdata.categorize.textseg.common.util.AnsjWord;
import com.caishi.bigdata.categorize.textseg.common.util.InitWords;
import com.caishi.bigdata.categorize.textseg.common.util.StringLength;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FastText样本的提取
 * <p>
 * 数据格式：
 * __label__9 , suyugüzel bala , suyugüzel is a village in the district of bala ankara province turkey .
 * 相关字段说明
 * __label__9 ： 标签
 * suyugüzel bala：title 分词结果，空格分割
 * suyugüzel is a village in the district of bala ankara province turkey . ：content 分词结果，空格分割
 * Created by fuli.shen on 2017/7/11.
 */
public class FastTextSampling {
    //  data/newsContent.json   data/fasttext_sampling.text
    static String srcPath = "data/newsContent.json";
    static String dstPath = "data/fasttext_sampling.text";
    public static final Logger logger = LoggerFactory.getLogger(Word2VectorTextSegment.class);

    public static void main(String[] args) {
        if (args.length != 2) {
            logger.warn("Usage: java -jar Word2VectorTextSegment-1.0.jar com.caishi.textseg.common.main.FastTextSampling <srcPath><dstPath> ");
            System.exit(1);
        }
        srcPath = args[0];
        dstPath = args[1];
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(srcPath));
            writer = new BufferedWriter(new FileWriter(dstPath));
            while ((line = reader.readLine()) != null) {

                StringBuffer wordsBuffer = new StringBuffer();
                CategoryInfo categoryInfo = JSON.parseObject(line, CategoryInfo.class);

                // label 标签
                String categoryIds = categoryInfo.getCategoryIds();
                categoryIds = categoryIds.replace("[", "").replace("]", "");
                if (!StringUtils.isBlank(categoryIds)) {
                    String[] categoryIdsSplits = categoryIds.split(",");
                    for (String categoryId : categoryIdsSplits) {
                        Integer currentCategoryId = Integer.valueOf(categoryId.trim());
                        Integer label = category2LabelMapping.get(currentCategoryId / 10000 * 10000);

                        if (label != null) {
                            wordsBuffer.append(String.format("__label__%d", label));
                            wordsBuffer.append(",");
                            // title 分词
                            getTextSegmentsLine(wordsBuffer, categoryInfo.getTitle());
                            wordsBuffer.append(",");
                            // content 分词
                            getTextSegmentsLine(wordsBuffer, categoryInfo.getContent());
                            wordsBuffer.append("\n");
                            writer.write(wordsBuffer.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("parse exception ,data:{},err:{} ", line, e);
        } finally {
            close(reader, writer);
        }
    }

    private static void getTextSegmentsLine(StringBuffer wordsBuffer, String content) {
        if (StringUtils.isBlank(content)) {
            wordsBuffer.append("");
            return;
        }
        AnsjWord ansjWord = new AnsjWord(content);//停止词一级过滤
        List<CustomTerm> customTerms = ansjWord.getCustomTerms();
        for (CustomTerm customTerm : customTerms) {
            String word = customTerm.getWord();
            if (StringLength.length(word) >= 2) {
                if (InitWords.stopWordsExtentions.contains(word)
                        || InitWords.DIGIT.contains(String.valueOf(word.charAt(0)))) {//停止词二级过滤，方便扩展
                    continue;
                }
                wordsBuffer.append(word + " ");
            }
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

    public static Map<Integer, Integer> category2LabelMapping = new HashMap<>();

    static {
        category2LabelMapping.put(10000, 1);
        category2LabelMapping.put(30000, 2);
        category2LabelMapping.put(50000, 3);
        category2LabelMapping.put(60000, 4);
        category2LabelMapping.put(90000, 5);
        category2LabelMapping.put(100000, 6);
        category2LabelMapping.put(110000, 7);
        category2LabelMapping.put(120000, 8);
        category2LabelMapping.put(130000, 9);
        category2LabelMapping.put(140000, 10);
        category2LabelMapping.put(150000, 11);
        category2LabelMapping.put(180000, 12);
        category2LabelMapping.put(190000, 13);
        category2LabelMapping.put(200000, 14);
        category2LabelMapping.put(210000, 15);
        category2LabelMapping.put(220000, 16);
        category2LabelMapping.put(230000, 17);
        category2LabelMapping.put(240000, 18);
        category2LabelMapping.put(250000, 19);
        category2LabelMapping.put(260000, 20);
        category2LabelMapping.put(280000, 21);
        category2LabelMapping.put(290000, 22);
        category2LabelMapping.put(300000, 23);
        category2LabelMapping.put(320000, 24);
        category2LabelMapping.put(340000, 25);
        category2LabelMapping.put(350000, 26);
        category2LabelMapping.put(360000, 27);
        category2LabelMapping.put(370000, 28);
        category2LabelMapping.put(380000, 29);
        category2LabelMapping.put(390000, 30);
        category2LabelMapping.put(400000, 31);
        category2LabelMapping.put(410000, 32);
        category2LabelMapping.put(420000, 33);
        category2LabelMapping.put(430000, 34);
        category2LabelMapping.put(450000, 35);
        category2LabelMapping.put(470000, 36);
        category2LabelMapping.put(480000, 37);
        category2LabelMapping.put(490000, 38);
        category2LabelMapping.put(500000, 39);
        category2LabelMapping.put(510000, 40);
    }

}
