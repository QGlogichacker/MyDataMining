package liangjiahao.dataMining.AdaBoost;

import liangjiahao.dataMining.Utils.ReadForm;
import liangjiahao.dataMining.Utils.UnPurified;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class NewAdaboost {
    private Random random= new Random();
    private int attrNum;
    private String[] attrName;
    private ArrayList<String> result = new ArrayList<>();
    private File file;
    public ArrayList<TreeNode> trees= new ArrayList<>();
    private float right;
    private float fail;
    public ArrayList<ArrayList<Double>> MData;
    public ArrayList<String> MRes;
    public double [] dataW;

    public static void main(String[] args) {
        NewAdaboost newCart = new NewAdaboost("/media/logic_hacker/software/DataSet/abalone 1.data");
        newCart.init(10);
        String [] [] arr = ReadForm.readFile(newCart.file);
        arr = Arrays.copyOfRange(arr,1001,4177);

        for(int i =0;i<arr.length;i++)
            if(!newCart.decide(arr[i],8))
                System.out.println(i);;

        newCart.report();

    }


    public NewAdaboost(String filePath) {
        this.file = new File(filePath);
    }

    public boolean decide(String[]str,int result){
        double[]vote = new double[this.result.size()];
        for(TreeNode tr:trees)
            vote[this.result.indexOf(getDes(tr, str))]+=tr.getWeight();
        double max=0;
        int index=0;
        //search for the most voted class
        for(int i=0;i<this.result.size();i++)
            if(vote[i]>max){
                index=i;
                max=vote[i];
            }
        System.out.println(this.result.get(index));
        if(this.result.get(index).equals(str [result])){
            right++;
            return true;
        }
        else {
            fail++;
            return false;
        }
    }



    int getIndex(String s){
        for(int i=0;i<attrNum;i++)
            if(attrName[i].equals(s))
                return i;
        return -1;
    }

    public void report(){  //显示现在的表现
        System.out.println("正确 "+(int)right+" 个，错误 "+(int)fail+" 个,正确率为："+right/(right+fail));
    }

    private String getDes(TreeNode tr,String[]str){   //递归调用使用决策树
        if(tr.isLeaf) return tr.type;
        //TODO:to delete the first columum
        else return Double.valueOf(str[getIndex(tr.divide)+1])<tr.bandary?getDes(tr.son[0],str):getDes(tr.son[1],str);
    }

    private String getDes(TreeNode tr,ArrayList<Double>arr){   //递归调用使用决策树
        if(tr.isLeaf) return tr.type;
        else return arr.get(getIndex(tr.divide))<tr.bandary?getDes(tr.son[0],arr):getDes(tr.son[1],arr);
    }

    public static void print(TreeNode root){
        if(root == null)
            return ;
        Dprint(root,1);
    }

    private static void  Dprint(TreeNode root,int n){
        for(int i=0;i<n;i++)
            System.out.print("-");
        System.out.println(root.divide);
        if(root.son!=null)
            for(TreeNode s:root.son)
                Dprint(s,n+1);
    }

    public void init(int num){
        ReadForm.readFile(file);
        String [][]data = Arrays.copyOfRange(ReadForm.arr,0,3000);
        //String [] [] data = ReadForm.arr;
        //transform the data form string to double
        attrName = data[0];
        attrNum = attrName.length;
        ArrayList<String> resultList = new ArrayList<>();
        ArrayList<ArrayList<Double>> aad = new ArrayList<>();
        for(int i=1,n=data.length;i<n;i++){ //for every row data
            ArrayList<Double> ad = new ArrayList<>();
            //TODO:COL SHOULDNT BE 1
            for(int col=1;col<data[0].length-1;col++)
                ad.add(Double.valueOf(data[i][col]));
            resultList.add(data[i][data[0].length-1]);
            aad.add(ad);
        }
        //aad is the double data,resultList is the String result

        //construct the data form
        this.MData = aad;
        this.MRes = resultList;
        dataW = new double[resultList.size()];
        for(String str:resultList)
            if(!result.contains(str))
                result.add(str);

        adaboost(num);
        //adaboost




    }

    class TraningSet{
        ArrayList<ArrayList<Double>> arrayList;
        ArrayList<String> resultList;
        TraningSet(ArrayList<ArrayList<Double>> arrayList,ArrayList<String> resultList,double[]dataW){
            ArrayList<ArrayList<Double>> newArr = new ArrayList<>();
            ArrayList<String> newArr2 = new ArrayList<>();
            assert dataW.length==arrayList.size();
            double min = 1;
            for(int i=0;i<dataW.length;i++)
                if(dataW[i]<min)
                    min = dataW[i];
            int[]number = new int[dataW.length];
            for(int i=0;i<dataW.length;i++)
                number[i] = (int)Math.rint(dataW[i]/min);
            for(int i=0;i<dataW.length;i++)
                for(int num=0;num<number[i];num++){
                    if(random.nextInt()%2==0)
                        continue;
                    newArr.add(arrayList.get(i));
                    newArr2.add(resultList.get(i));
                }
            for(int i=0;i<dataW.length;i++)
                for(int num=0;num<number[i];num++){
                    if(random.nextInt()%2==1)
                        continue;
                    newArr.add(arrayList.get(i));
                    newArr2.add(resultList.get(i));
                }
            this.arrayList = newArr;
            this.resultList = newArr2;
        }
        TreeNode newTree(){
            TreeNode root=new TreeNode(arrayList,resultList);
            root.grow();
            root.getType();
            return root;
        }
    }


    void adaboost(int k){
        int datanum =MData.size();
        this.dataW = new double[datanum];
        for(int i=0;i<datanum;i++)
            dataW[i]=1/(double)datanum; //standard the weight
        for(int i=0;i<k;i++){ //for k round
            TraningSet ts = new TraningSet(MData,MRes,dataW);
            TreeNode root = ts.newTree();
            boolean[]fail = errorOfTree(root,dataW);

            //delete the tree with too high error or lowest
            if(root.error>0.5||root.error==0)
                continue;

            this.trees.add(root);

            //decrease the weight of data who is rightly classed
            for(int right=0;right<datanum;right++)
                if(!fail[i])
                    dataW[i] = root.error/(1-root.error);

            //standard the weight
            double sum = 0;
            for(double b:dataW)
                sum+=b;
            for(double b:dataW)
                b/=sum;
        }
    }

    boolean[] errorOfTree(TreeNode root,double[]dataW){
        double sum =0;
        boolean[] fail = new boolean[dataW.length];
        ArrayList<String> res = new ArrayList<>();
        for(int i=0;i<MData.size();i++)
            res.add(getDes(root,MData.get(i)));
        for(int i=0;i<MData.size();i++)
            if(!res.get(i).equals(MRes.get(i))) {//result un match
                fail[i]=true;
                sum+=dataW[i];
            }
        root.error=sum;
        return fail;

    }

    class TreeNode {
        //private double Entropy;
        private double error;
        private double gini = 100;
        private TreeNode []son;
        private String divide;
        private String type;
        public boolean isLeaf;
        public double bandary;
        public int righttimes;
        public int falsetimes;
        public int count[];
        public ArrayList<ArrayList<Double>> data;
        private ArrayList<String> resultList;

        double getWeight(){
            return Math.log(1-this.error)-Math.log(this.error);
        }

        @Override
        public String toString() {
            String str = "";
            if(!isLeaf)
                str = "\n"+son[0].toString()+" "+son[1].toString();
            return "Node{" +
                    "gini=" + gini +
                    ", divide='" + divide + ':'+ bandary+
                    ", type='" + type + +'}'+str;
        }

        boolean stopCondiction(){
            return this.gini == 0.0;
        }


        public TreeNode(ArrayList<ArrayList<Double>> data, ArrayList<String> resultList) {
            this.data = data;
            this.resultList = resultList;
        }

        public TreeNode(double gini) {
            this.gini = gini;
            this.data = new ArrayList<>();
            this.resultList = new ArrayList<>();
        }

        public void getType(){  //init type,right times,false times
            int count[] = new int[result.size()];
            for(int i=0;i<resultList.size();i++) // for every data in this dataset
                count[result.indexOf(resultList.get(i))]++; //get the number of count
            int max=count[0];
            int index = 0;
            for(int i=0;i<count.length;i++)
                if(count[i]>max){
                    index = i;
                    max = count[i];
                }
            type = result.get(index);
            this.count = count;
            righttimes = count[index]; //get the righttimes
            for(int i=0;i<count.length;i++)
                if(i!=index)
                    falsetimes+=count[i];  //get the falsetimes
        }

        private  void getSon(int name, double bandary,double[]gini){
            TreeNode[] son = new TreeNode[2];
            son[0] = new TreeNode(gini[0]);
            son[1] = new TreeNode(gini[1]);
            this.divide = attrName[name];
            this.bandary = bandary;  //get bandary
            for(int i=0,n=this.data.size();i<n;i++)
                if(data.get(i).get(name)<bandary){
                    son[0].data.add(data.get(i));
                    son[0].resultList.add(this.resultList.get(i));
                }
                else {
                    son[1].data.add(data.get(i));
                    son[1].resultList.add(this.resultList.get(i));
                }
            son[0].getType();
            son[1].getType();
            this.son = son; //getson
        }

        public void grow(){
            System.out.println("GROWING");
            if(stopCondiction()){
                this.isLeaf = true;
                this.getType();
            } else{
                this.bestClass();
                son[0].grow();
                son[1].grow();
            }
        }

        /*
         */
        private void bestClass(){
            double [] resultArr = new double[5];  //
            //  arr[1-2] is the gini,arr[3] is the bandary,arr[4] is the col
            double minmin = data.size();
            for(int j=0,n=data.get(0).size();j<n;j++) { //for every characher
                ArrayList<Double> darr = new ArrayList<>();
                for(int i=0,l=data.size();i<l;i++)
                    if(!darr.contains(data.get(i).get(j)))
                        darr.add(data.get(i).get(j));
                Collections.sort(darr);
                //TreeSet<Double> ts = new TreeSet<>(darr);
                //ArrayList<Double> newArr = new ArrayList<>(ts);
                ArrayList<Double> newArr = darr;
                double bandary=0;
                double [] tmpres = null;
                double min = data.size();
                for(int i=0;i<newArr.size()-1;i++){          //get the mininum of classing this character
                    double tmp = (newArr.get(i)+newArr.get(i+1))/2;
                    double[] gini=getGini(tmp,j);
                    if(gini[2]<min){
                        min =gini[2];
                        bandary = tmp;
                        tmpres = gini;
                    }
                }
                if(min<minmin){
                    minmin = min;
                    resultArr = tmpres;
                    resultArr[4] = j;
                    resultArr[3] = bandary;
                }
            }
            getSon((int)resultArr[4],resultArr[3],resultArr);
        }

        private  double[] getGini(double bandary,int col){
            int numcount[][] = new int [2][result.size()];
            int count1 = 0;
            int count2 = 0;
            for(int i=0;i<data.size();i++) // for every data in dataset
                if(data.get(i).get(col)<bandary){
                    numcount[0][result.indexOf(resultList.get(i))]++;
                    count1++;
                }else{
                    numcount[1][result.indexOf(resultList.get(i))]++;
                    count2++;
                }
            double result[] = new double[5];
            result[0] = UnPurified.getUnpurified(UnPurified.GINI,numcount[0]);
            result[1] = UnPurified.getUnpurified(UnPurified.GINI,numcount[1]);
            result[2] = count1*result[0]+count2*result[1];
            return result;
        }
    }
}
