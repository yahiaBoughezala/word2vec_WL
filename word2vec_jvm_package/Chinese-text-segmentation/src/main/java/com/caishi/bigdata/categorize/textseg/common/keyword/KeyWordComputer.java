package com.caishi.bigdata.categorize.textseg.common.keyword;

import com.caishi.bigdata.categorize.textseg.common.util.InitWords;
import com.caishi.bigdata.categorize.textseg.common.domain.CustomTerm;
import com.caishi.bigdata.categorize.textseg.common.util.AnsjWord;
import org.nlpcn.commons.lang.util.StringUtil;

import java.util.*;

/**
 * TFIDF关键词抽取，通过选关键词在文档中的统计性质进行排序
 * created by fuli.shen at 2016/07/28
 */
public class KeyWordComputer {

    private int nKeyword = 5;

    public KeyWordComputer() {
    }

    /**
     * 返回关键词个数
     *
     * @param nKeyword
     */
    public KeyWordComputer(int nKeyword) {
        this.nKeyword = nKeyword;
    }

    /**
     * 获取关键词列表,结果按照分值排序好的列表
     *
     * @param content
     * @param titleLength
     * @return
     */
    public List<Keyword> computeArticleTfidf(String content, int titleLength) {
        Map<String, Keyword> tm = new HashMap<String, Keyword>();
        int cWord = 0;
        AnsjWord ansjWord = new AnsjWord(content);//停止词一级过滤
        List<CustomTerm> customTerms = ansjWord.getCustomTerms();
        for (CustomTerm customTerm : customTerms) {
            double weight = getWeight(customTerm, content.length(), titleLength);
            if (InitWords.stopWordsExtentions.contains(customTerm.getWord())
                    || InitWords.DIGIT.contains(String.valueOf(customTerm.getWord().charAt(0)))
                    || weight == 0) {//停止词二级过滤，方便扩展
                continue;
            }
            cWord++;
            Keyword keyword = tm.get(customTerm.getWord());
            if (keyword == null) {
                keyword = new Keyword(customTerm.getWord(), weight);
                tm.put(customTerm.getWord(), keyword);
            } else {
                keyword.updateWeight(weight);
            }
        }
        TreeSet<Keyword> treeSet = new TreeSet<Keyword>();
        for (Keyword keyword : tm.values()) {
            treeSet.add(keyword.calcScore(cWord));
        }
        ArrayList<Keyword> arrayList = new ArrayList<Keyword>(treeSet);

        if (-1 == this.nKeyword || treeSet.size() <= this.nKeyword) {
            return arrayList;
        } else {
            return arrayList.subList(0, this.nKeyword);
        }
    }

    /**
     * 根据标题和正文通过Tfidf算法获取关键词列表
     *
     * @param title   标题
     * @param content 正文
     * @return
     */
    public List<Keyword> computeArticleTfidf(String title, String content) {
        if (StringUtil.isBlank(title)) {
            title = "";
        }
        if (StringUtil.isBlank(content)) {
            content = "";
        }
        return computeArticleTfidf(title + "\t" + content, title.length());
    }

    /**
     * 获取关键词权重
     *
     * @param customTerm
     * @param length
     * @param titleLength
     * @return
     */
    private double getWeight(CustomTerm customTerm, int length, int titleLength) {
        if (customTerm.getWord().trim().length() < 2) {
            return 0;
        }
        String pos = customTerm.getNature();
        Double posScore = InitWords.POS_SCORE.get(pos);
        if (posScore == null) {
            posScore = 1.0;
        } else if (posScore == 0) {
            return 0;
        }
        if (titleLength > customTerm.getOffe()) {
            return 5 * posScore;
        }
        return (length - customTerm.getOffe()) * posScore / (double) length;
    }

    public static void main(String[] args) {
        String content = "程序员(英文Programmer)是从事程序开发、维护的专业人员。一般将程序员分为程序设计人员和程序编码人员，但两者的界限并不非常清楚，特别是在中国。软件从业人员分为初级程序员、高级程序员、系统分析员和项目经理四大类。";
        List<Keyword> keywords = new KeyWordComputer(100).computeArticleTfidf(null, content);
        System.out.println(keywords);

        Map<String, Integer> wordsMap = new HashMap<>();
        for (Keyword kw : keywords) {
            wordsMap.put(kw.getName(), kw.getFreq());
        }
        System.out.println(wordsMap);
    }
}
