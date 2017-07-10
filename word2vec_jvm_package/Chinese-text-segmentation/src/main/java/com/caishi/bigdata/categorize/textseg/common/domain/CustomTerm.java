package com.caishi.bigdata.categorize.textseg.common.domain;

import org.ansj.domain.AnsjItem;
import org.ansj.domain.TermNatures;

import java.io.Serializable;

/**
 * 自定义Term
 * Created by fuli.shen on 2016/7/19.
 */
public class CustomTerm implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 关键词
     */
    public String word;
    /**
     * 关键词词性（ 详细可以参考：https://github.com/NLPchina/ansj_seg/wiki/%E8%AF%8D%E6%80%A7%E6%A0%87%E6%B3%A8%E8%A7%84%E8%8C%83）
     */
    public String nature;

    /**
     * Term 中的offe
     */
    private int offe;
    private TermNatures termNatures;
    private AnsjItem item;

    public void setWord(String word) {
        this.word = word;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public void setOffe(int offe) {
        this.offe = offe;
    }

    public TermNatures getTermNatures() {
        return termNatures;
    }

    public void setTermNatures(TermNatures termNatures) {
        this.termNatures = termNatures;
    }

    public AnsjItem getItem() {
        return item;
    }

    public void setItem(AnsjItem item) {
        this.item = item;
    }

    public TermNatures termNatures(){
        return termNatures;
    }
    public CustomTerm(String word, String nature, int offe) {
        this.word = word;
        this.nature = nature;
        this.offe = offe;
    }

    public CustomTerm(String word, String nature, int offe, TermNatures termNatures, AnsjItem item) {
        this.word = word;
        this.nature = nature;
        this.offe = offe;
        this.termNatures = termNatures;
        this.item = item;
    }

    public int getOffe() {
        return offe;
    }
    public String getWord() {
        return word;
    }
    public String getNature() {
        return nature;
    }

    @Override
    public String toString() {
        return "CustomTerm{" +
                "word='" + word + '\'' +
                ", nature='" + nature + '\'' +
                ", offe=" + offe +
                '}';
    }
    public AnsjItem item() {
        return this.item;
    }
}
