package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.List;

public class TermTupleScanner extends AbstractTermTupleScanner {
    private StringSplitter stringSplitter=new StringSplitter();
    private List<String> wordList=new ArrayList<>();
    private int curPos=0;
    /**
     * 缺省构造函数
     */
    public TermTupleScanner(){

    }

    /**
     * 构造函数
     * @param input：指定输入流对象，应该关联到一个文本文件
     */
    public TermTupleScanner(BufferedReader input){
        super(input);
        // 配置字符串分词器
        stringSplitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
        String line;
        try {
            // 读取输入流，得到全部单词列表
            while ((line=input.readLine())!=null){
                wordList.addAll(stringSplitter.splitByRegex(line));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获得下一个三元组
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next(){
        if(curPos== wordList.size()){
            // 如果当前位置到了单词列表的末尾，返回null
            return null;
        }
        // 返回单词列表中当前位置的三元组
        AbstractTermTuple tuple=new TermTuple();
        if(Config.IGNORE_CASE) {
            // 如果忽略大小写，则把单词全部转为小写
            tuple.term = new Term(wordList.get(curPos).toLowerCase());
        }else{
            tuple.term = new Term(wordList.get(curPos));
        }
        tuple.curPos=curPos;
        curPos++;
        return tuple;
    }
}
