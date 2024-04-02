package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;

public class TermTuple extends AbstractTermTuple {
    /**
     * 判断二个三元组内容是否相同
     * @param obj ：要比较的另外一个三元组
     * @return 如果内容相等（三个属性内容都相等）返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj){
        if(this==obj) {
            // 引用相同，返回true
            return true;
        }
        if(obj==null || getClass()!=obj.getClass()){
            // 比较对象为空或类型不等，返回false
            return false;
        }
        // 比较对象类型相同，比较二者的值
        TermTuple tuple=(TermTuple)obj;
        return term.equals(tuple.term) && freq==tuple.freq && curPos==tuple.curPos;
    }

    /**
     * 获得三元组的字符串表示
     * @return ： 三元组的字符串表示
     */
    @Override
    public String toString(){
        return "TermTuple [term="+term+
                ", freq="+freq+
                ", curPos="+curPos+"]";
    }
}
