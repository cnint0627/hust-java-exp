package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;

import java.io.*;
import java.util.*;

public class Index extends AbstractIndex {
    /**
     * 返回索引的字符串表示
     * @return 索引的字符串表示
     */
    @Override
    public String toString(){
        StringBuffer stringBuffer=new StringBuffer("Index [\ndocIdToDocPathMapping=\n");
        for(int docId:docIdToDocPathMapping.keySet()){
            stringBuffer.append("docId="+docId+", docPath="+docIdToDocPathMapping.get(docId)+"\n");
        }
        stringBuffer.append(", termToPostingListMapping=\n");
        for(AbstractTerm term:getDictionary()){
            stringBuffer.append("term="+term.getContent()+", postingList="+termToPostingListMapping.get(term)+"\n");
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document){
        int docId=document.getDocId();
        String docPath=document.getDocPath();
        docIdToDocPathMapping.put(docId,docPath);
        for(AbstractTermTuple tuple:document.getTuples()) {
            AbstractTerm term = tuple.term;
            AbstractPostingList postingList = termToPostingListMapping.get(term);
            if (postingList == null) {
                // 如果索引未包含该单词
                postingList = new PostingList();
            }
            int docIndex = postingList.indexOf(docId);
            AbstractPosting posting;
            if (docIndex != -1) {
                // 如果单词的postingList已经包含该文档
                posting = postingList.get(docIndex);
            } else {
                // 如果单词的postingList未包含该文档
                posting = new Posting(docId, 0, new ArrayList<>());
                postingList.add(posting);
            }
            posting.setFreq(posting.getFreq() + 1);
            posting.getPositions().add(tuple.curPos);
            // 更新索引内部的HashMap
            termToPostingListMapping.put(term, postingList);
        }
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file){
        try {
            FileInputStream fileInputStream=new FileInputStream(file);
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
            readObject(objectInputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file){
        try{
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(fileOutputStream);
            writeObject(objectOutputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 返回指定单词的PostingList
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term){
        return termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary(){
        return termToPostingListMapping.keySet();
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize(){
        for(AbstractTerm term:getDictionary()){
            AbstractPostingList postingList=termToPostingListMapping.get(term);
            // 对每个单词的PostingList按docId从小到大排序
            postingList.sort();
            for(int i=0;i<postingList.size();i++){
                // 对每个Posting的positions从小到大排序
                postingList.get(i).sort();
            }
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId){
        return docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out){
        try {
            // 将成员依次序列化
            out.writeObject(docIdToDocPathMapping);
            out.writeObject(termToPostingListMapping);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in){
        try{
            // 将成员依次反序列化
            docIdToDocPathMapping=(Map<Integer, String>)in.readObject();
            termToPostingListMapping=(Map<AbstractTerm, AbstractPostingList>)in.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
