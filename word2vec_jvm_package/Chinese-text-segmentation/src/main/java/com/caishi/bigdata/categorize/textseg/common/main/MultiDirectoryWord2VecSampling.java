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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 多目录方式对文本分词
 * Created by fuli.shen on 2017/6/27.
 */
public class MultiDirectoryWord2VecSampling {
    public static final Logger LOGGER = LoggerFactory.getLogger(MultiDirectoryWord2VecSampling.class);
    // data/20170628/     news/w2v/sampling/20170628/
    static String srcPath = "data/20170719/";
    static String dstPath = "news/w2v/sampling/20170719/";
    static List<File> filelist = new LinkedList<>();
    public static void main(String[] args) {
        if (args.length != 2) {
            LOGGER.warn("Usage: java -jar multiDirectoryWord2VecSampling-1.0.jar com.caishi.textseg.common.main.MultiDirectoryWord2VecSampling <srcPath><dstPath> ");
            System.exit(1);
        }
        srcPath = args[0];
        dstPath = args[1];
        getFileList(srcPath);
        for (File file : filelist) {
            String fileAbsolutePath = file.getAbsolutePath();
            String subCategoryName = file.getParentFile().getName();
            String mainCategoryName = file.getParentFile().getParentFile().getName();
            SubMainChineseTokenizer(file, fileAbsolutePath, subCategoryName, mainCategoryName);
        }
    }

    private static void SubMainChineseTokenizer(File file, String fileAbsolutePath, String subCategoryName, String mainCategoryName) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(fileAbsolutePath));
            String targetFileDirectoryPath = dstPath + mainCategoryName + "/" + subCategoryName + "/";
            File targetFileDirectory = new File(targetFileDirectoryPath);
            if (!targetFileDirectory.exists()) {
                targetFileDirectory.mkdirs();
            }
            String fileName = targetFileDirectoryPath + file.getName();
            LOGGER.info("Write Data FileName:" + fileName);
            writer = new BufferedWriter(new FileWriter(fileName));
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
            LOGGER.error("parse exception ,data:{},err:{} ", line, e);
        } finally {
            close(reader, writer);
        }
    }

    public static void getFileList(String strPath) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) {
                    getFileList(files[i].getAbsolutePath());
                } else if (fileName.startsWith("part-r")) {
                    String strFileName = files[i].getAbsolutePath();
                    filelist.add(new File(strFileName));
                }
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
}
