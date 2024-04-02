package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.index.impl.DocumentBuilder;
import hust.cs.javacourse.search.index.impl.Index;
import hust.cs.javacourse.search.index.impl.IndexBuilder;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.FileUtil;

import java.io.File;

/**
 * 测试索引构建
 */
public class TestBuildIndex {
    /**
     *  索引构建程序入口
     * @param args : 命令行参数
     */
    public static void main(String[] args){
        AbstractDocumentBuilder documentBuilder=new DocumentBuilder();
        AbstractIndexBuilder indexBuilder=new IndexBuilder(documentBuilder);
        String rootDir=Config.DOC_DIR;
        System.out.println("Start build index...");
        AbstractIndex index= indexBuilder.buildIndex(rootDir);
        index.optimize();
//        System.out.println(index);

        // 测试保存索引序列化到文件
        index.save(new File(Config.INDEX_DIR+"index.dat"));

        // 测试保存索引字符串到文件
        FileUtil.write(index.toString(),Config.INDEX_DIR+"index.txt");

        // 测试从文件读取
        AbstractIndex index2=new Index();
        index2.load(new File(Config.INDEX_DIR+"index.dat"));
        System.out.println(index2);
    }
}
