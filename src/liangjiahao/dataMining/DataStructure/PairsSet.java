package liangjiahao.dataMining.DataStructure;

import liangjiahao.dataMining.Cart.*;
import liangjiahao.dataMining.Utils.UnPurified;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 一对互为补集的对象
 * Created by A on 2017/7/19.
 */
public class PairsSet<T> {
    private boolean[] items;
    private boolean[] mirror;
    private boolean[] origin;
    private HashSet<boolean[]> iden;
    public double itemsgini;
    public double mirrorgini;
    public double gini;

    public boolean[] getItems() {
        return items;
    }

    public boolean[] getMirror() {
        return mirror;
    }

    public PairsSet(boolean []items){
        iden = new HashSet<>();
        this.items = items;
        this.mirror=Arrays.copyOf(items,items.length);
        for(int i=0,n=items.length;i<n;i++)
            mirror[i] = !items[i];
        iden.add(this.items);
        iden.add(mirror);
    }

    /**
     *
     * @param origin 原本的表
     * @param items  子集表
     */
    public PairsSet(boolean []origin,boolean []items){
        this.origin = origin;
        iden = new HashSet<>();
        this.items = items;
        mirror = Arrays.copyOf(origin,origin.length);
        for(int i=0;i<origin.length;i++)
            mirror[i]=((!items[i])&mirror[i]);  //计算补集
        iden.add(this.items);
        iden.add(mirror);
    }

    //返回这对互补集的基尼值
    public double getGini(int count[][]){
        assert count.length==this.origin.length:"长度匹配失败";
        int resnum = count[0].length;
        int[][]trans = new int[2][resnum];
        for(int i=0;i<items.length;i++)
            if(items[i])   //对于每行
                for(int j=0;j<resnum;j++)
                    trans[0][j]+= count[i][j];
        for(int i=0;i<mirror.length;i++)
            if(mirror[i])   //对于每行
                for(int j=0;j<resnum;j++)
                    trans[1][j]+= count[i][j];
        this.itemsgini = UnPurified.getUnpurified(UnPurified.GINI,trans[0]);
        this.mirrorgini = UnPurified.getUnpurified(UnPurified.GINI,trans[1]);
        int num1=0,num2 =0;
        for(boolean b:items)
            if(b)
                num1++;
        for(boolean b:mirror)
            if(b)
                num2++;
        return gini=(itemsgini*num1+mirrorgini*num2)/(num1+num2);
    }

    @Override
    public String toString() {
        return "PairsSet{" +
                "items=" + Arrays.toString(items) +
                ", mirror=" + Arrays.toString(mirror) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(Arrays.equals(this.items,((PairsSet) obj).mirror))
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcs = new HashCodeBuilder();
        return hcs.append(iden).hashCode();
    }

    public boolean logicEquals(PairsSet ps){
        if(equals(ps)) //正向相等
            return true;
        if(!origin.equals(ps.origin))
            return false;
        for(int i=0;i<origin.length;i++)
            if(origin[i])
                if(ps.mirror[i]!=this.items[i])//位逻辑异
                    return false;
        return true;
    }
}
