package liangjiahao.dataMining.RandomForest;

import liangjiahao.dataMining.Utils.ReadForm;
import liangjiahao.dataMining.Utils.UnPurified;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class NewRandomForest {
    private int attrNum;
    private String[] attrName;
    private ArrayList<String> result = new ArrayList<>();
    private File file;
    public ArrayList<TreeNode> trees;
    int treeNumber = 6;
    private float right;
    private float fail;

    public static void main(String[] args) {
        NewRandomForest newCart = new NewRandomForest("/media/logic_hacker/software/DataSet/abalone 1.data");
        newCart.init(2);
        String [] [] arr = ReadForm.readFile(newCart.file);
        arr = Arrays.copyOfRange(arr,1,4177);
        for(int i =0;i<arr.length;i++)
            if(!newCart.decide(arr[i],8))
                System.out.println(i);;

        newCart.report();
    }


    public NewRandomForest(String filePath) {
        this.file = new File(filePath);
    }

    public boolean decide(String[]str,int result){
        /**
        if(getDes(tree,str).equals(str [result])) {
            right++;
            return true;
        }else {
            System.out.println(getDes(tree, str));
            System.out.println(Arrays.toString(str));
            fail++;
            return false;
        }*/
        int[]vote = new int[this.result.size()];
        for(TreeNode tr:trees)
            vote[this.result.indexOf(getDes(tr, str))]++;
        int max=0,index=0;
        for(int i=0;i<this.result.size();i++)
            if(vote[i]>max){
                index=i;
                max=vote[i];
            }
        System.out.println(this.result.get(index));
        if(this.result.get(index).equals(str [attrNum-1])){
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
        String [][]data = Arrays.copyOfRange(ReadForm.arr,0,4177);
        //String [] [] data = ReadForm.arr;
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
        for(String str:resultList)
            if(!result.contains(str))
                result.add(str);
        this.trees = new ArrayList<>();
        for(int i=0;i<this.treeNumber;i++){
            TreeNode root=new TreeNode(bagging.bagging(aad,num,resultList),bagging.result);
            this.trees.add(root);
            root.grow();
            root.getType();
        }
    }

    class TreeNode {
        //private double Entropy;
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
            return (this.data.size()<2||gini <0.2);
            //return this.gini == 0.0;
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
                for(int i=0;i<newArr.size()-2;i++){          //get the mininum of classing this character
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
