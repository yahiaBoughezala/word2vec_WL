package com.caishi.bigdata.categorize.textseg.common.keyword;

/**
 * 关键词对象
 * created by fuli.shen at 2016/07/28
 */
public class Keyword implements Comparable<Keyword> {
    /**
     * 关键词名称
     */
    private String name;
    /**
     * 根据权重计算关键词的得分
     */
    private double score;

    /**
     * 关键词出现频率（次数）
     */
    private int freq;


    /**
     * 根据频率计算idf
     */
    private double idf;

    public Keyword(String name, double score) {
        this.name = name;
        this.score = score;
        this.idf = 1;
        freq++;
    }

    public Keyword(String name, int docFreq, double weight) {
        this.name = name;
        this.idf = Math.log(10000 + 10000.0 / (docFreq + 1));
        this.score = idf * weight;
        freq++;
    }

    public void updateWeight(int weight) {
        this.score += weight * 1;
        freq++;
    }

    public void updateWeight(double weight) {
        this.score += weight * 1;
        freq++;
    }

    public Keyword calcScore(int nDocWords) {
        this.idf = Math.log(10000 + 10000.0 * this.freq / (nDocWords + 1));
        this.score *= this.idf;
        return this;
    }

    public int getFreq() {
        return freq;
    }

    /**
     * 按照得分进行排名
     * @param o
     * @return
     */
    @Override
    public int compareTo(Keyword o) {
        if (this.score < o.score) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Keyword) {
            Keyword k = (Keyword) obj;
            return k.name.equals(name);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name + "/" + score;// "="+score+":"+freq+":"+idf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }



}
