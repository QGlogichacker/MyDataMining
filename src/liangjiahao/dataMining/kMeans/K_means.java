package liangjiahao.dataMining.kMeans;

import liangjiahao.dataMining.DataStructure.DiamenException;
import liangjiahao.dataMining.DataStructure.Point;

import java.util.ArrayList;
import java.util.Arrays;

/**kmeans算法
 * Created by A on 2017/7/14.
 */

public class K_means {
    private int diamen = 3;
    private ArrayList<DataVec> data;  //存储数据点
    private ArrayList<MetVec> mets;  //存储聚类点
    private ArrayList<MetVec> tmp;  //暂存聚类点

    public ArrayList<double[]> getResult(){
        ArrayList<double[]> v = new ArrayList<>();
        for(MetVec mv : mets)
            v.add(mv.getPoint());
        return v;
    }

    public static void main(String[] args) throws DiamenException {
        K_means k = new K_means();
        ArrayList<double []> v =k.getResult();
        for(double[] d:v)
            System.out.println(Arrays.toString(d));
    }

    public K_means() {
        data= new ArrayList<>();
        mets=new ArrayList<>();
        tmp=new ArrayList<>();
        for(int i = 0;i<1000;i++)
            this.addData(new DataVec(3));  //随机创建100个数据点和2个聚类点
        this.addMets(new MetVec(3));
        this.addMets(new MetVec(3));
        this.addMets(new MetVec(3));
        this.addMets(new MetVec(3));
        this.addMets(new MetVec(3));
        System.out.println(this.data);   //打印他们的信息
        System.out.println(this.mets);
        for(DataVec d:this.data){
            d.findDis();                //所有点找到最近的聚类点并将自己加入set
        }
        for(MetVec m :this.mets){
            m.findMid();                //聚类点变动到自己set的平均值点
        }
        System.out.println(this.mets);
        while(!tmp.equals(mets)){
            cleanSet();   //第二次进行，先另存并清理set
            for(DataVec d:this.data){
                d.findDis();
            }
            for(MetVec m :this.mets){
                m.findMid();
            }
            System.out.println(this.mets);
        }
    }

    public K_means(ArrayList<double[]> arr1, ArrayList<double[]> arr2) {
        data= new ArrayList<>();
        mets=new ArrayList<>();
        tmp=new ArrayList<>();
        for(double[] d: arr1)
            data.add(new DataVec(d));
        for(double[] d2: arr2)
            mets.add(new MetVec(d2));
        diamen = arr1.get(0).length;
        System.out.println(this.data);   //打印他们的信息
        System.out.println(this.mets);
        for(DataVec d:this.data){
            d.findDis();                //所有点找到最近的聚类点并将自己加入set
        }
        for(MetVec m :this.mets){
            m.findMid();                //聚类点变动到自己set的平均值点
        }
        System.out.println(this.mets);
        while(!tmp.equals(mets)){
            cleanSet();   //第二次进行，先另存并清理set
            for(DataVec d:this.data){
                d.findDis();
            }
            for(MetVec m :this.mets){
                m.findMid();
            }
            System.out.println(this.mets);
        }
    }

    public void addData_s(DataVec v){  //加入数据点
        if(v.diamen!=this.diamen)
            try {
                throw new DiamenException();
            } catch (DiamenException e) {
                e.printStackTrace();
            }
        data.add(v);
    }

    public void addData(DataVec v){  //加入数据点
        data.add(v);
    }

    public void addMets_s(MetVec v){  //加入数据点
        if(v.diamen!=this.diamen)
            try {
                throw new DiamenException();
            } catch (DiamenException e) {
                e.printStackTrace();
            }
        mets.add(v);
    }

    public void addMets(MetVec v){  //加入数据点
        mets.add(v);
    }


    public void cleanSet(){
        tmp = mets;  //保存信息
        for(int i =0;i<mets.size();i++){  //清空信息
            mets.get(i).set = new ArrayList<DataVec>();
        }
    }

    class MetVec extends Point {
        public ArrayList<DataVec> set;

        public MetVec(double[] d){
            super(d);
            set = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "metVec(" + Arrays.toString(d)+
                    ')'+set;
        }

        public MetVec(int diamen){
            super(diamen);
            set = new ArrayList<>();
        }

        public void findMid(){
            double[] sum = new double[diamen];
            for(double d:sum){
                d = 0;
            }
            for(int sub =0;sub<diamen;sub++){   //对于分维度
                for(DataVec v : set)     //各个坐标求平均
                    sum[sub] += v.d[sub];
                sum[sub]/=set.size();
            }
            this.d = sum;  //坐标换算完毕
        }

    }

    class DataVec extends Point {
        public double distance;

        public DataVec(double[] d){
            super(d);
        }

        public DataVec(int diamen){
            super(diamen);
        }

        @Override
        public String toString() {
            return "Data(" + Arrays.toString(d) +
                    ')';
        }

        public double min(double ...a){
            double tmp = a[0];
            int mark = 0;
            for(int i=0;i<a.length;i++)
                if (a[i] < tmp)
                    tmp = a[i];
            return tmp;
        }

        public void findDis(){
            MetVec ma = mets.get(0);
            for(MetVec m:mets)
                try {
                    if(dis(m,this) < dis(ma,this))
                        ma = m;
                } catch (DiamenException e) {
                    e.printStackTrace();
                }
            ma.set.add(this);
        }
    }


}
