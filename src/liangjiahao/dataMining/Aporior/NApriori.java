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
    ArrayList<ArrayList<Myset>> llm = new ArrayList<>(); //频集
    ArrayList<Myset> dao = new ArrayList<>(); //数据库
//TODO
    public void joinAndCut(int k,int stand){
        if(k==1){
            HashSet<String> myhs = new HashSet<>();
            for(Myset ms:dao)
                for(String s:ms.hs)
                    myhs.add(s);
            ArrayList<Myset> tmp = new ArrayList<>();
            for(String s:myhs)
                if(!tmp.contains(s))
                    tmp.add(new Myset(s));
            llm.add(cut(tmp,stand));
        }else{
            ArrayList<Myset> setOfLast = llm.get(k-2);
            ArrayList<Myset> tmp = new ArrayList<>();
            //connect k-1
            for(int i=0,n=setOfLast.size();i<n;i++)
                for(int j=0;j<n&j<i;j++)
                    if(setOfLast.get(i).connectable(setOfLast.get(j),k)){
                        Myset ms = Myset.connect(setOfLast.get(i),setOfLast.get(j));
                        //is all of its subSet in the k-1 set?
                        if(!tmp.contains(ms))
                            if(subIs(ms,k))
                                tmp.add(ms);
                    }

            //cut the tmp array with absolute support
            llm.add(cut(tmp,stand));
        }
    }

    void printRule(){
        for(int i=0;i<llm.size()-1;i++)
            for(int j=0;j<i;j++)
                for(Myset ms1:llm.get(i))
                    for(Myset ms2:llm.get(j)){
                        Myset ms3 = div(ms1,ms2); //ms1-ms2
                        if(ms3!=null)
                            System.out.println(ms2+"--->"+ms3+"    reputation:"+ ((float) ms1.count)/ ((float) ms2.count));
                    }
    }

    void printData(){
        System.out.println("database:");
        for(Myset ms:dao)
            System.out.println(ms);
    }

    void printRea(){
        for(int i=0;i<llm.size()-1;i++)
            System.out.println("Level "+i+" ReapetSet:\n"+llm.get(i));
    }

    Myset div(Myset m1,Myset m2){
        if(!m1.hs.containsAll(m2.hs))
            return null;
        Myset ms = new Myset();
        for(String s:m1.hs)
            if(!m2.hs.contains(s))
                ms.add(s);
        return ms;
    }

    public static void main(String[] args) {
        /*Myset ms1 = new Myset("A","B","C");
        Myset ms2 = new Myset("A","D","E");
        Myset ms3 = new Myset("A","B","D");
        System.out.println(ms1.connectable(ms2,3)+" And "+ms1.connectable(ms3,3));*/
        NApriori nApriori = new NApriori();

        nApriori.dao.add(new Myset("a","b","c","d","e","f","g"));
        nApriori.dao.add(new Myset("d","e","f"));
        nApriori.dao.add(new Myset("g","a","m","e"));
        nApriori.dao.add(new Myset("s","d","t","1","k","g"));
        nApriori.dao.add(new Myset("l","i","b","a","r","y"));

        nApriori.joinAndCut(1,2);
        int k=1;//round number
        nApriori.printData();
        while(!nApriori.llm.get(k-1).isEmpty()){
            k++;
            nApriori.joinAndCut(k,2);
            nApriori.printRea();
        }
        nApriori.printRule();




    }

    public  boolean subIs(Myset ms,int k){
        ArrayList ar = new ArrayList(ms.hs);
        ArrayList<Myset> a = new ArrayList<>();
        for(int i=0;i<k-1;i++){
            ArrayList<String> clone = new ArrayList<>(ar);
            clone.remove(i);
            a.add(new Myset(MyUtils.toArray(clone,String.class)));
        }
        return llm.get(k-2).containsAll(a);
    }

    public ArrayList<Myset> cut(ArrayList<Myset> vms,int n){
        for(Myset ms :vms )
            for(Myset mms:dao)
                if(mms.containsAll(ms))
                    ms.count++;

        ArrayList<Myset> newArr = new ArrayList<>();
        for(int i =0;i<vms.size();i++)
            if(vms.get(i).count>=n)
                newArr.add(vms.get(i));
        return newArr;
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
        return hs.toString();
        //+" "+count+" times";
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
        return tmp.size()==size;
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
