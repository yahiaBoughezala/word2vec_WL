package com.caishi.bigdata.categorize.textseg.common.keyword.texrank;


import com.caishi.bigdata.categorize.textseg.common.domain.CustomTerm;
import com.caishi.bigdata.categorize.textseg.common.util.AnsjWord;
import com.caishi.bigdata.categorize.textseg.common.util.InitWords;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description：基于图的排序算法TextRank提取关键词
 * 其中：以TFIDF为代表的统计方法则仅仅考虑词的统计性质，TextRank为代表的图方法的优势在于考虑文档中词与词之间的语义关系
 * 但不足之处，TFIDF和TextRank等方法均没有考虑所抽取的关键词对文档主题的覆盖度问题，导致推荐的关键词往往集中在某一个大的主题中，而没有顾及文档的其
 * 他主题
 * Author：Created by fuli.shen
 * Date: 2016/7/29.
 * Reference: {https://github.com/hankcs/TextRank、https://github.com/hankcs/HanLP}
 */
public class TextRankKeyword {

    /**
     * 默认提取关键词个数
     */
    public int nKeyword = 10;

    /**
     * 阻尼系数
     */
    final static float d = 0.85f;
    /**
     * 最大迭代次数
     */
    final static int max_iter = 100;
    final static float min_diff = 0.001f;

    /**
     * 默认构造函数
     */
    public TextRankKeyword() {
    }

    /**
     * 抽取关键词个数
     *
     * @param nKeyword
     */
    public TextRankKeyword(int nKeyword) {
        this.nKeyword = nKeyword;
    }

    /**
     * 获取指定个数的分词列表
     *
     * @param content 在调用方法前根据实际情况对content进行内容过滤
     * @return
     */
    public Map<String, Float> getKeywordList(String content) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        AnsjWord ansjWord = new AnsjWord(content);
        List<CustomTerm> customTerms = ansjWord.getCustomTerms();
        return this.getWordsRank(customTerms, nKeyword);
    }


    /**
     * 获取指定个数的分词列表
     *
     * @param content
     * @param size
     * @return
     */
    public Map<String, Float> getKeywordList(String content, int size) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        if (size <= 0) {
            size = nKeyword;
        }
        AnsjWord ansjWord = new AnsjWord(content);
        List<CustomTerm> customTerms = ansjWord.getCustomTerms();
        return this.getWordsRank(customTerms, size);
    }

    public Map<String, Float> getKeywordList(String title, String content) {
        try {
            return this.getKeywordList(title, content, nKeyword);
        } catch (Exception exp) {
            System.out.println("Exception extracting text keywords");
        }
        return null;
    }

    public Map<String, Float> getKeywordList(String title, String content, int size) {
        if (StringUtils.isBlank(title) && StringUtils.isBlank(content)) {
            return null;
        }
        if (size <= 0) {
            size = nKeyword;
        }
        return getKeywordList(title + "\t" + content, size);
    }

    private CustomTerm getTerm(List<CustomTerm> parse, String keyword) {
        for (CustomTerm term : parse) {
            if (term.getWord().equals(keyword.trim())) {
                return term;
            }
        }
        return null;
    }

    private Map<String, Float> getWordsRank(List<CustomTerm> terms, int size) {
        Map<String, Float> map = getWordsRank(terms);
        Map<String, Float> result = new LinkedHashMap<String, Float>();

        // 过滤： 设置非n，v的词score为-1
        for (String key : map.keySet()) {
            CustomTerm keywordTerm = getTerm(terms, key);
            if (keywordTerm != null) {
                if (!InitWords.POS_SCORE.containsKey(keywordTerm.getNature())) {
                    map.put(key, -1.0f);
                } else if ((null == keywordTerm.item() || null == keywordTerm.item().termNatures) &&
                        // 非核心字典切分出来的词
                        keywordTerm.termNatures().allFreq > 1 && keywordTerm.termNatures().allFreq < 50) {
                    map.put(key, -1.0f);
                } else {
                    double textRankScore = map.get(key);
                    double posWeight = (Double) InitWords.POS_SCORE.get(keywordTerm.getNature());
                    map.put(key, Double.valueOf(posWeight * textRankScore).floatValue());
                }
            }
        }
        for (Map.Entry<String, Float> entry : new MaxHeap<Map.Entry<String, Float>>(size, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        }).addAll(map.entrySet()).toList()) {
            // limit: 只取 Rank Score 大于0.8 的结果返回
            if (entry.getValue() > 0.8f) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 使用已经分好的词来计算rank
     *
     * @param termList
     * @return
     */
    public Map<String, Float> getWordsRank(List<CustomTerm> termList) {
        List<String> wordList = new ArrayList<String>(termList.size());
        for (CustomTerm t : termList) {
            if (shouldInclude(t)) {
                wordList.add(t.getWord());
            }
        }
        Map<String, Set<String>> words = new TreeMap<String, Set<String>>();
        Queue<String> que = new LinkedList<String>();
        for (String w : wordList) {
            if (!words.containsKey(w)) {
                words.put(w, new TreeSet<>());
            }
            que.offer(w);
            if (que.size() > nKeyword) {
                que.poll();
            }
            for (String w1 : que) {
                for (String w2 : que) {
                    if (w1.equals(w2)) {
                        continue;
                    }
                    words.get(w1).add(w2);
                    words.get(w2).add(w1);
                }
            }
        }
        Map<String, Float> score = new HashMap<String, Float>();
        for (int i = 0; i < max_iter; ++i) {
            Map<String, Float> m = new HashMap<String, Float>();
            float max_diff = 0;
            for (Map.Entry<String, Set<String>> entry : words.entrySet()) {
                String key = entry.getKey();
                Set<String> value = entry.getValue();
                m.put(key, 1 - d);
                for (String element : value) {
                    int size = words.get(element).size();
                    if (key.equals(element) || size == 0) continue;
                    m.put(key, m.get(key) + d / size * (score.get(element) == null ? 0 : score.get(element)));
                }
                max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 0 : score.get(key))));
            }
            score = m;
            if (max_diff <= min_diff) break;
        }
        return score;
    }

    boolean shouldInclude(CustomTerm t) {

        String pos = t.nature;// 如果是英文，需要时全大写，且不能多于四个字母，如: NBA, CTO
        boolean result = true;
        if (t.getWord().length() < 2
                || InitWords.stopWordsExtentions.contains(t.getWord())
                || InitWords.DIGIT.contains(String.valueOf(t.getWord().charAt(0)))
                || "en".equalsIgnoreCase(pos) && (pos.length() > 4 || judgeContainsStr(pos))) {
            result = false;
        }
        return result;
    }

    /**
     * 是否有小写字母
     *
     * @param cardNum
     * @return
     */
    private boolean judgeContainsStr(String cardNum) {
        String regex = ".*[a-z]+.*";
        Matcher m = Pattern.compile(regex).matcher(cardNum);
        return m.matches();
    }
    public static void main(String[] args) {
        String content = "程序员(英文Programmer)是从事程序开发、维护的专业人员。一般将程序员分为程序设计人员和程序编码人员，但两者的界限并不非常清楚，特别是在中国。软件从业人员分为初级程序员、高级程序员、系统分析员和项目经理四大类。";
        Map<String, Float> keywordList = new TextRankKeyword(100).getKeywordList(content);
        System.out.println(keywordList);
        //{中国=1.9453669, 程序员=1.4820709, 人员=1.2813325, 程序=1.1482054, 软件=1.0818297, 编码=1.0798515, 界限=1.0090704, 专业=0.8472118}
    }
}
