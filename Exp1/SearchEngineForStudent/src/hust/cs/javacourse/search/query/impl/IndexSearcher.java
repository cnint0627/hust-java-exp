package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Posting;
import hust.cs.javacourse.search.index.impl.PostingList;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexSearcher extends AbstractIndexSearcher {
    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile){
        index.load(new File(indexFile));
    }

    /**
     * 根据单个检索词进行搜索
     * @param queryTerm ：检索词
     * @param sorter ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter){
        AbstractPostingList postingList=index.search(queryTerm);
        List<AbstractHit> hits = new ArrayList<>();
        if(postingList!=null) {
            // 如果搜到了该单词
            for (int i = 0; i < postingList.size(); i++) {
                AbstractPosting posting = postingList.get(i);
                Map<AbstractTerm, AbstractPosting> termPostingMapping = new HashMap<>();
                termPostingMapping.put(queryTerm, posting);
                int docId = posting.getDocId();
                String docPath = index.getDocName(docId);
                AbstractHit hit = new Hit(docId, docPath, termPostingMapping);
                hits.add(hit);
            }
            sorter.sort(hits);
        }
        return hits.toArray(new AbstractHit[0]);
    }

    /**
     *
     * 根据二个检索词进行搜索
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter ：    排序器
     * @param combine ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
        AbstractPostingList postingList1 = index.search(queryTerm1);
        AbstractPostingList postingList2 = index.search(queryTerm2);
        List<AbstractHit> hits = new ArrayList<>();
        // 单词1和单词2共有的posting
        AbstractPostingList sharedPostingList=new PostingList();
        if(postingList1!=null) {
            // 如果搜到了单词1
            for (int i = 0; i < postingList1.size(); i++) {
                AbstractPosting posting1 = postingList1.get(i);
                int docId = posting1.getDocId();
                String docPath = index.getDocName(docId);
                int docIndex=postingList2.indexOf(docId);
                if (postingList2 != null && docIndex != -1) {
                    // 如果搜到了单词2，寻找单词1和单词2共有的posting
                    AbstractPosting posting2 = postingList2.get(docIndex);
                    Map<AbstractTerm, AbstractPosting> termPostingMapping = new HashMap<>();
                    termPostingMapping.put(queryTerm1, posting1);
                    termPostingMapping.put(queryTerm2, posting2);
                    AbstractHit hit = new Hit(docId, docPath, termPostingMapping);
                    hits.add(hit);
                    sharedPostingList.add(posting1);
                    sharedPostingList.add(posting2);
                }
            }
            if (combine == LogicalCombination.OR) {
                for (int i = 0; i < postingList1.size(); i++) {
                    AbstractPosting posting = postingList1.get(i);
                    if (!sharedPostingList.contains(posting)) {
                        int docId = posting.getDocId();
                        String docPath = index.getDocName(docId);
                        Map<AbstractTerm, AbstractPosting> termPostingMapping = new HashMap<>();
                        termPostingMapping.put(queryTerm1, posting);
                        AbstractHit hit = new Hit(docId, docPath, termPostingMapping);
                        hits.add(hit);
                    }
                }
            }
        }
        if(postingList2!=null && combine == LogicalCombination.OR){
            for (int i = 0; i < postingList2.size(); i++) {
                AbstractPosting posting = postingList2.get(i);
                if (!sharedPostingList.contains(posting)) {
                    int docId = posting.getDocId();
                    String docPath = index.getDocName(docId);
                    Map<AbstractTerm, AbstractPosting> termPostingMapping = new HashMap<>();
                    termPostingMapping.put(queryTerm2, posting);
                    AbstractHit hit = new Hit(docId, docPath, termPostingMapping);
                    hits.add(hit);
                }
            }
        }
        sorter.sort(hits);
        return hits.toArray(new AbstractHit[0]);
    }
}
