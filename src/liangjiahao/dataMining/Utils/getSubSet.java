package liangjiahao.dataMining.Utils;

/**
 * 利用位运算进行集合的求子集
 * Created by A on 2017/7/19.
 */
import liangjiahao.dataMining.DataStructure.PairsSet;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class getSubSet {
    public static<T> ArrayList<ArrayList<T>> getSubset(ArrayList<T> L) {
        ArrayList<ArrayList<T>> result = new ArrayList<ArrayList<T>>();
        if(L==null||L.size()==0||L.size()==1){
            return null;
        }
        int pow=2;
        for(int i=1,size=L.size();i<size;i++)
            pow *= 2;
        for (int i = 1,size=pow-1; i <size ; i++) {  //一共2的n次方种可能性
            ArrayList<T> subSet = new ArrayList<T>();
            int index = i;               //遍历到哪一位
            for (int j = 0; j < L.size(); j++) {  // 通过位运算、位移动进行计算
                if ((index & 1) == 1)    // 最后一位若为1，加入到集合中
                    subSet.add(L.get(j));
                index >>= 1;// 索引右移一位
            }
            result.add(subSet); // 把子集存储起来
        }
        return result;
    }

    //生成0到n的所有子集可能性
    public static ArrayList<ArrayList<Integer>> getProper(int n){
        ArrayList<Integer> arr = new ArrayList<>();
        for(int i=0;i<n;i++)
            arr.add(i);
        return getSubset(arr);
    }

    public static<T> ArrayList<ArrayList<T>> getSubset(ArrayList<T> L,boolean[] remain) {
        ArrayList<ArrayList<T>> result = new ArrayList<ArrayList<T>>();
        if(L==null||L.size()==0||L.size()==1){
            return null;
        }
        int pow=2;
        for(int i=1,size=L.size();i<size;i++)
            pow *= 2;
        for (int i = 1,size=pow-1; i <size ; i++) {  //一共2的n次方种可能性
            ArrayList<T> subSet = new ArrayList<T>();
            int index = i;               //遍历到哪一位
            for (int j = 0; j < L.size(); j++) {  // 通过位运算、位移动进行计算
                if ((index & 1) == 1)    // 最后一位若为1，加入到集合中
                    subSet.add(L.get(j));
                index >>= 1;// 索引右移一位
            }
            result.add(subSet); // 把子集存储起来
        }
        return result;
    }


    public static ArrayList<boolean[]> getSubsetBoolean(boolean[]origin) {
        int length=0; //剩下表的大小
        for(boolean b:origin)
            if(b)
                length++;
        if(length==0|| length==1)
            return null;
        ArrayList<boolean[]> arrb = new ArrayList<>();
        int pow=2;
        for(int i=1;i<length;i++)
            pow *= 2;
        /**
        for (int i = 1; i <pow-1 ; i++) {  //一共2的n次方种可能性
            boolean[] bool = new boolean[length];
            int index = i;               //遍历到哪一位
            for (int j = 0; j < length; j++) {
                // 通过位运算、位移动进行计算
                if ((index & 1) == 1)    // 最后一位若为1，加入到集合中
                    bool[j]=true;
                index >>= 1;// 索引右移一位
            }
            arrb.add(bool);
        }
         */
        for (int i = 1; i <(pow-1) ; i++) {  //一共2的n次方种可能性
            int oi=0;
            boolean[] bool = Arrays.copyOf(origin,origin.length);
            int index = i;               //遍历二进制的i到哪一位
            for (int j = 0; j < length; j++) {
                // 通过位运算、位移动进行计算
                while((!origin[oi])&&oi<origin.length-1)
                    oi++;
                if ((index & 1) != 1)    // 最后一位若为1，加入到集合中
                    bool[oi]=false;
                index >>= 1;// 索引右移一位
                oi++;
            }
            arrb.add(bool);
        }
        return arrb;
    }


    public static ArrayList<boolean[]> getSubsetBoolean(int length) {
        if(length==0|| length==1)
            return null;
        ArrayList<boolean[]> arrb = new ArrayList<>();
        int pow=2;
        for(int i=1;i<length;i++)
            pow *= 2;
         for (int i = 1; i <pow-1 ; i++) {  //一共2的n次方种可能性
         boolean[] bool = new boolean[length];
         int index = i;               //遍历到哪一位
         for (int j = 0; j < length; j++) {
         // 通过位运算、位移动进行计算
         if ((index & 1) == 1)    // 最后一位若为1，加入到集合中
         bool[j]=true;
         index >>= 1;// 索引右移一位
         }
         arrb.add(bool);
         }

        return arrb;
    }

    public static void main(String[] args) {
        boolean[] origin = {true,false,true,true};
        ArrayList<PairsSet> arr = getPair(3);
        for(PairsSet ps:arr)
            System.out.println(ps);
    }


    //可用作后面森林生成
    public static<T> HashSet<PairsSet> getPair(boolean[]origin) {
        int length = origin.length;
        if(length==0|| length==1)
            return null;
        HashSet<PairsSet> arrb = new HashSet<>();
        ArrayList<boolean[]> arr =getSubsetBoolean(origin);
        for(boolean[]barr:arr){
            boolean has= false;
            PairsSet newps = new PairsSet(origin,barr);
            for(PairsSet ps:arrb)
                if(ps.logicEquals(newps))
                    has = true;
            if(!has)
                arrb.add(newps);
        }
        return arrb;
    }

    //生成n对补集
    public static<T> ArrayList<PairsSet> getPair(int length) {
        ArrayList<PairsSet> arrb = new ArrayList<>();
        ArrayList<boolean[]> arr =getSubsetBoolean(length);
        for(boolean[]barr:arr){
            PairsSet ps =new PairsSet(barr);
            if(!arrb.contains(ps))
                arrb.add(ps);
        }
        return arrb;
    }
}
