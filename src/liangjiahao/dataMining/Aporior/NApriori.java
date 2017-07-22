package liangjiahao.dataMining.Aporior;

import liangjiahao.dataMining.Utils.MyUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Aporior算法
 * Created by A on 2017/7/16.
 */
public class NApriori {
    List<List<Myset>> llm = new ArrayList<>(); //频集
    List<Myset> dao = new ArrayList<>(); //数据库
//TODO
    public ArrayList<Myset> join(ArrayList<Myset> itemSet, int k){
        return null;
    }

    public static void main(String[] args) {
        /*Myset ms1 = new Myset("A","B","C");
        Myset ms2 = new Myset("A","D","E");
        Myset ms3 = new Myset("A","B","D");
        System.out.println(ms1.connectable(ms2,3)+" And "+ms1.connectable(ms3,3));*/
        NApriori nApriori = new NApriori();
        Myset ms1 =new Myset("a");
        Myset ms2 =new Myset("b");
        Myset ms3 =new Myset("c");
        ArrayList ar = new ArrayList();
        ar.add(ms1);
        ar.add(ms2);
        ar.add(ms3);
        nApriori.llm.add(ar);
        Myset mysetTest = new Myset("a","b");
        nApriori.dao.add(new Myset("a","b","c","d"));
        System.out.println(nApriori.subIs(mysetTest,2));
        ArrayList<Myset> arr = new ArrayList<>();
        arr.add(mysetTest);
        nApriori.cut(arr,1);
        System.out.println(arr);

    }

    public  boolean subIs(Myset ms,int k){
        ArrayList ar = new ArrayList(ms.hs);
        ArrayList<Myset> a = new ArrayList<>();
        for(int i=0;i<k;i++){
            ArrayList<String> clone = new ArrayList<>(ar);
            clone.remove(i);
            a.add(new Myset(MyUtils.toArray(clone,String.class)));
        }
        return llm.get(k-2).containsAll(a);
    }

    public void cut(ArrayList<Myset> vms,int n){
        for(Myset ms :vms )
            for(Myset mms:dao)
                if(mms.containsAll(ms))
                    ms.count++;
        for(int i =0;i<vms.size();i++)
            if(vms.get(i).count<n){
                vms.remove(i);
                i--;
            }
    }

/**
    public  boolean subIs(Myset ms,int k){
        ArrayList ar = new ArrayList(ms.hs);
        for(int i=0;i<k;i++){
            ArrayList clone = new ArrayList(ar);
            clone.remove(i);
            if(!isInLevel(clone,k-1))return false;
        }
        return true;
    }


    boolean isInLevel(ArrayList clone,int k){
        for(Myset myset :llm.get(k-1))
            if(myset.hs.equals(new HashSet(clone)))
                return true;
        return false;
    }
 */
}

class Myset extends Object{
    int count = 0;
    HashSet<String> hs;
    Myset(HashSet hs){
        this.hs = hs;
    }

    @Override
    public String toString() {
        return hs.toString()+" "+count+" times";
    }

    public HashSet<String> getClonedHash(){
        return (HashSet<String>) hs.clone();
    }

    public boolean add(String s){
        return hs.add(s);
    }

    Myset(String...str){
        hs = new HashSet<>();
        for(String s:str){
            hs.add(s);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null||!this.getClass().equals(obj.getClass()))
            return false;
        return ((Myset)obj).hs.equals(this.hs);
    }

    public int size(){
        return hs.size();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        return hashCodeBuilder.append(hs).hashCode();
    }

    public boolean connectable(Myset ms,int size){
        HashSet<String> tmp = (HashSet) hs.clone();
        tmp.addAll(ms.hs);
        return tmp.size()==size+1;
    }

    public static Myset connect(Myset ms1,Myset ms2){
        Myset tmp = new Myset(ms1.getClonedHash());
        tmp.hs.addAll(ms2.hs);
        return tmp;
    }

    public boolean containsAll(Myset ms){
        return this.hs.containsAll(ms.hs);
    }

}
