package com.caishi.bigdata.categorize.textseg.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 加载停止词语/词性列表
 * Created by fuli.shen on 2016/7/21.
 */
public class InitWords {
    private final static Logger logger = LoggerFactory.getLogger(InitWords.class);

    /**
     * 停止词定义一级过滤词
     */
    public static Set<String> stopWords = new HashSet<String>();


    /**
     * 停止词定义二级过滤词
     */
    public static Set<String> stopWordsExtentions = new HashSet<String>();

    /**
     * 语义特征（词性, 权重: 只有在该词性列表中的词才会在最终结果中保留）
     */
    public static final Map<String, Double> POS_SCORE = new HashMap<String, Double>();

    /**
     * 初始化DIGIT
     */
    public static final Set<String> DIGIT = new TreeSet<String>();

    static {
        stopWords = initStopWordsSet("stopwords/stop_words.txt");
        stopWordsExtentions = initStopWordsSet("stopwords/stop_words_extentions.txt");
        initPosScore();
        initDigit();
    }

    /**
     * 定义关键词词性列表
     *
     * @return
     */
    public static void initDigit() {
        DIGIT.add("0");
        DIGIT.add("1");
        DIGIT.add("2");
        DIGIT.add("3");
        DIGIT.add("4");
        DIGIT.add("5");
        DIGIT.add("6");
        DIGIT.add("7");
        DIGIT.add("8");
        DIGIT.add("9");
        DIGIT.add("零");
        DIGIT.add("一");
        DIGIT.add("二");
        DIGIT.add("两");
        DIGIT.add("三");
        DIGIT.add("四");
        DIGIT.add("五");
        DIGIT.add("六");
        DIGIT.add("七");
        DIGIT.add("八");
        DIGIT.add("九");
        DIGIT.add("这");
        DIGIT.add("那");
        DIGIT.add("几");
        DIGIT.add("第");
        DIGIT.add("背");
    }

    /**
     * 定义关键词词性列表（仅考虑名词和部分动词）
     *
     * @return
     */
    public static void initPosScore() {
        /** 1) 名词
         n 名词
         nr 人名: 赵薇, 郑钧
         nr1 汉语姓氏: 完颜
         nr2 汉语名字: 鳌拜
         nrj 日语人名: 小野伸二
         nrf 音译人名 外国人名: 罗马里奥
         ns 地名: 中国、北京、天安门
         nsf 音译(外国)地名: 美国、印度、澳大利亚
         nt 机构团体名: 中国版权保护中心, 黑龙江电视台, 外交学会
         nz 其它专名: 太阳花
         nl 名词性惯用语
         ng 名词性语素
         nw 新词

         // 自定义(default.dic)：
         na/nba 动物名称:  中华鲟
         nbc 生物名词: 褐藻门
         nf 食品食物食材: 无花果
         nhd 病症: 哮喘
         nhm 药物: 红霉素
         nis 机构、工厂、学校等概念名: 消防站, 经发局, 修船厂, 水电局
         nit 科室、系、部: 公共部, 财政系, 心血管科
         nmc 化学、化合物: 氨基磺酸, 二苯甲酮
         nnd 职位、职称、职责: 演职员, 规划者, 养路工, 魔法师, 放贷人, 赛车手, 曲艺家
         nnt 类nnd: 司令, 木工, 艺术家, 副官
         ntc 公司名: 中国华能集团, 百胜集团, 和记黄埔有限公司
         ntcb 银行名: 德意志银行, 中国银行
         ntcf 工厂名: 哈尔滨制药六厂, 江南造船厂
         ntch 酒店、饭店名: 翠宫饭店, 上海和平饭店
         nth  医院名: 青岛市人民医院, 北京中日友好医院
         nto 政府机关、办公司: 哈尔滨市委, 柳州铁路局, 上海海关, 江西省政府
         nts 小学、中学学校名: 濠江中学, 拉萨中学, 新港小学
         ntu 大学学校名: 清华大学
         */
        POS_SCORE.put("n", 1.0); // 基准
        POS_SCORE.put("en", 0.5);
        POS_SCORE.put("nr", 1.5);
        POS_SCORE.put("nr1", 0.7);
        POS_SCORE.put("nr2", 0.7);
        POS_SCORE.put("nrj", 1.0);
        POS_SCORE.put("nrf", 1.0);
        POS_SCORE.put("ns", 1.7);
        POS_SCORE.put("nsf", 1.6);
        POS_SCORE.put("nt", 1.5);
        POS_SCORE.put("nz", 1.0);
        POS_SCORE.put("nl", 0.8);
        POS_SCORE.put("ng", 0.8);
        POS_SCORE.put("nw", 0.8);
        POS_SCORE.put("na", 1.2);
        POS_SCORE.put("nba", 1.2);
        POS_SCORE.put("nbc", 0.5);
        POS_SCORE.put("nf", 0.7);
        POS_SCORE.put("nhd", 1.7);
        POS_SCORE.put("nhm", 0.9);
        POS_SCORE.put("nis", 1.5);
        POS_SCORE.put("nit", 0.6);
        POS_SCORE.put("nmc", 0.3);
        POS_SCORE.put("nnd", 1.2);
        POS_SCORE.put("nnt", 1.2);
        POS_SCORE.put("ntc", 1.6);
        POS_SCORE.put("ntcb", 1.5);
        POS_SCORE.put("ntcf", 1.5);
        POS_SCORE.put("ntch", 1.5);
        POS_SCORE.put("nth", 1.6);
        POS_SCORE.put("nto", 1.3);
        POS_SCORE.put("nts", 1.0);
        POS_SCORE.put("ntu", 1.8);
        /**
         *
         2) 动词
         v 动词
         vn 名动词
         vi 不及物动词（内动词）
         */
        POS_SCORE.put("v", 0.5);
        POS_SCORE.put("vn", 0.7);
        POS_SCORE.put("vi", 0.6);
    }

    public static Set<String> initStopWordsSet(String path) {
        Set<String> stopWords = new HashSet<String>();
        try {
            String encoding = "UTF-8";
            File file = new File(path);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stopWords.add(line);
                }
                read.close();
                logger.info("init user stopwords ok path is {}", path);
            } else {
                logger.warn("File {} Not Found", file);
            }
        } catch (Exception e) {
            logger.error("error reading stop words", e);
            e.printStackTrace();
        }
        return stopWords;
    }
}
