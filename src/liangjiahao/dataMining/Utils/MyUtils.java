package liangjiahao.dataMining.Utils;

import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by A on 2017/7/15.
 */
public class MyUtils {
    private MyUtils(){
        assert true :"不应该出现实例";
    }

    /**
     * 请实现Serializable接口，并加上序列化ID
     * 对所有对象进行了深度克隆
     * @param obj 被克隆的东西
     * @param <T> 实现了Serializable接口的Obj
     * @return 不用类型转换
     */
    public static <T extends Serializable>T clone(T obj){
        //拷贝对象
        T clonedObj = null;
        try{

            //读取对象字节数据
            ByteArrayOutputStream baos = new ByteArrayOutputStreamEx();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            //分配内存空间，写入原始对象并生成新对象
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            clonedObj = (T)ois.readObject();
            ois.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        return clonedObj;
    }



    /**
     * @param datas 原来的数组
     * @param newLen 新的数组的长度
     * @param <T> 泛型
     * @return 新的数组
     */
    public static<T> T[]expandCapacity(T[]datas,int newLen){
        newLen = newLen<0?0:newLen;
        return Arrays.copyOf(datas,newLen);
    }

    public static <T extends Comparable>T getMax(T[]datas){
        if(datas == null)
            return null;
        T max=datas[0];
        for(T t:datas){
            max = max.compareTo(t)>0?max:t;
        }
        return max;
    }

    public static int getMax(int[]datas){
        if(datas == null)
            return 0;
        int max=datas[0];
        for(int t:datas){
            max = max>t?max:t;
        }
        return max;
    }

    public static <T extends Comparable>T getMin(T[]datas){
        if(datas == null)
            return null;
        T min=datas[0];
        for(T t:datas){
            min = min.compareTo(t)<0?min:t;
        }
        return min;
    }


    /**
     * 求平均值
     * @param list List\<Integer\>
     * @return 平均值
     */
    public static double average(List<Integer> list){
        if(list==null)
            return 0;
        int sum = 0;
        int size = list.size();
        if(list instanceof RandomAccess)
            for(int i=0;i<size;i++)
                sum+=list.get(i);
        else for(int i:list)
            sum+=i;
        return sum/size;
    }

    /**
     * subArray上锁
     * @param list 子列表
     * @param <T>  所有
     * @return  上锁的子列表
     */
    public static <T> List<T> sublistLocked (List<T> list){
        if(list==null)
            return null;
        list = Collections.unmodifiableList(list);
        return list;
    }

    /**
     *
     * @param t 泛型
     * @param <T> 泛型
     * @return 泛型ArrayList列表
     */
    public static <T> List<T> asList(T...t){
        List<T>list = new ArrayList<>();
        Collections.addAll(list,t);
        return list;
    }

    /**
     * List<T></>转换为泛型数组T[]
     * @param list 原来的list
     * @param tClass Object.getClass
     * @param <T> ? extends Object
     * @return T[]
     */
    public static <T> T[]toArray(List<T>list,Class<T> tClass){
        if(list==null||tClass==null)
            return null;
        T[]t=(T[]) Array.newInstance(tClass,list.size());
        for(int i =0,n=list.size();i<n;i++)
            t[i] = list.get(i);
        return t;
    }





}
