package com.caishi.bigdata.categorize.textseg.common.util;

import com.caishi.bigdata.categorize.textseg.common.domain.CustomTerm;
import com.caishi.bigdata.categorize.textseg.common.keyword.texrank.TextRankKeyword;
import com.caishi.bigdata.categorize.textseg.common.keyword.KeyWordComputer;
import com.caishi.bigdata.categorize.textseg.common.keyword.Keyword;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;

/**
 * ansj 中文分词
 * <p>
 * Created by fuli.shen on 2016/11/23.
 */
public class AnsjWord implements Serializable {

    private final static Logger logger = LoggerFactory.getLogger(AnsjWord.class);

    /**
     * 记录分词后文章的单词列表
     */
    private List<String> words = new ArrayList<>();
    /**
     * 记录分词后每个单词/词性关系列表/offe
     */
    private List<CustomTerm> customTerms = new ArrayList<>();
    /**
     * 记录每个单词出现的次数
     */
    private Map<String, Integer> wordsMap = new HashMap<>();

    /**
     * 默认构造方法
     */
    public AnsjWord() {
    }

    /**
     * 带有一个参数的构造方法
     *
     * @param content
     */
    public AnsjWord(String content) {
        this.getWord(content);
    }

    static {
        // 默认从根目录下加载
//        MyStaticValue.userLibrary = Object.class.getResource("/library/default.dic").getFile();
//        MyStaticValue.ambiguityLibrary = Object.class.getResource("/library/ambiguity.dic").getFile();
    }

    /**
     * 通过ansj中文分词对文本内容进行分析
     *
     * @param content
     */
    public void getWord(String content) {
        try {
            String text = HtmlRegexpUtil.filterHtmlTag(content, "!--");
            List<Term> terms = ToAnalysis.parse(text);
            for (Term term : terms) {
                String word = term.getName().replaceAll("[\\pP‘’“”]", "");// 关键词
                if (Pattern.matches("\\s+", word) || Pattern.matches("\\w+", word)) {
                    continue;
                }
                if (Pattern.matches("^[0-9].*", word)) {
                    continue;
                }
                if (HtmlRegexpUtil.isControlChar(word)) {
                    continue;
                }
                String nature = term.getNatureStr();//关键词词性
                if (StringUtils.isNotBlank(word) && !InitWords.stopWords.contains(word)) {
                    words.add(word);
                    customTerms.add(new CustomTerm(word, nature, term.getOffe(), term.termNatures(), term.item()));
                    if (wordsMap.containsKey(word)) {
                        wordsMap.put(word, wordsMap.get(word) + 1);
                    } else {
                        wordsMap.put(word, 1);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(" to analysis content exp,{}-{}", content, e);
        }
    }

    public List<String> getWords() {
        return words;
    }

    public List<CustomTerm> getCustomTerms() {
        return customTerms;
    }

    public Map<String, Integer> getWordsMap() {
        return wordsMap;
    }

    public static List<String> getOriginWords(String title, String content) {
        List<String> wordsList = new ArrayList<>();

        Map<String, Float> textRankMap = new TextRankKeyword().getKeywordList(title, content);
        if (null != textRankMap && !textRankMap.isEmpty()) {
            Set<String> words = textRankMap.keySet();
            for (String word : words) {
                wordsList.add(word);
            }
        }
        if (wordsList.isEmpty()) {
            KeyWordComputer keyWord = new KeyWordComputer(-1);
            List<Keyword> keywords = keyWord.computeArticleTfidf(title, content);
            if (null != keywords && !keywords.isEmpty()) {
                for (Keyword keyword : keywords) {
                    if (!wordsList.contains(keyword.getName())) {
                        wordsList.add(keyword.getName());
                    }
                }
            }
        }
        return wordsList;
    }

    public static Map<String, Integer> getWords(String title, String content) {
        KeyWordComputer keyWord = new KeyWordComputer(-1);
        List<Keyword> keywords = keyWord.computeArticleTfidf(title, content);
        Map<String, Integer> wordsMap = new HashMap<>();
        for (Keyword keyword : keywords) {
            if (keyword.getFreq() >= 1) {
                String name = keyword.getName();
                int freq = keyword.getFreq();
                wordsMap.put(name, freq);
            }
        }
        int size = wordsMap.size();
        Map<String, Float> textRankMap = new TextRankKeyword(Math.min(size, 20)).getKeywordList(title, content);
        //System.out.println("textRankMap:" + textRankMap);
        Map<String, Integer> textRankWordsmap = new HashMap<>();
        if (null != textRankMap) {
            for (String word : textRankMap.keySet()) {
                Integer count = wordsMap.get(word);
                if (null != count) {
                    textRankWordsmap.put(word, count);
                }
            }
        }
        return textRankWordsmap;
    }
    public static Map<String, Integer> getWord2VectorWords(String title, String content) {
        KeyWordComputer keyWord = new KeyWordComputer(-1);
        List<Keyword> keywords = keyWord.computeArticleTfidf(title, content);

        return null;
    }
    public static Map<String, Integer> getWordsByTfidf(String title, String content) {
        KeyWordComputer keyWord = new KeyWordComputer(20);
        List<Keyword> keywords = keyWord.computeArticleTfidf(title, content);
        Map<String, Integer> wordsMap = new HashMap<>();
        for (Keyword keyword : keywords) {
            if (keyword.getFreq() >= 1) {
                String name = keyword.getName();
                int freq = keyword.getFreq();
                wordsMap.put(name, freq);
            }
        }
        return wordsMap;
    }
    public static void main(String[] args) {
        System.out.println(getOriginWords("", "游戏联盟a"));//[联盟, 游戏]
        System.out.println(getOriginWords("", "猫咪狗狗宠物控"));//[宠物]
        System.out.println(getOriginWords("", "动漫趣闻"));//[趣闻, 动漫]
        System.out.println(getOriginWords("", "摄影之友"));//[摄影]
        System.out.println(getOriginWords("", "快科技"));//[科技]
        System.out.println(getOriginWords("", "美食天下"));//[美食]
        System.out.println(getOriginWords("", "韩剧资料馆"));//[韩剧]
        System.out.println(getOriginWords("", "珠宝秘籍"));//[珠宝]
        System.out.println(getOriginWords("", "橘子娱乐")); //[橘子]
    }
}
