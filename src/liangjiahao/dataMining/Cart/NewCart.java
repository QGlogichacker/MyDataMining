package liangjiahao.dataMining.Cart;

import liangjiahao.dataMining.Utils.ReadForm;
import liangjiahao.dataMining.Utils.UnPurified;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

public class NewCart {
    private int attrNum;
    private String[] attrName;
    private ArrayList<String> result = new ArrayList<>();
    private File file;
    public TreeNode tree;
    private float right;
    private float fail;

    public static void main(String[] args) {
        NewCart newCart = new NewCart("/media/logic_hacker/software/DataSet/abalone 1.data");
        newCart.init();
        print(newCart.tree);
        String [] [] arr = ReadForm.readFile(newCart.file);
        for(int i =0;i<arr.length;i++){
            newCart.decide(arr[i],7);
        }
        newCart.report();
    }


    public NewCart(String filePath) {
        this.file = new File(filePath);
    }

    public void decide(String[]str,int result){
        System.out.println(getDes(tree, str));
        if(getDes(tree,str).equals(str [result]))
            right++;
        else fail++;
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
        else return Double.valueOf(str[getIndex(tr.divide)])>tr.bandary?getDes(tr.son[1],str):getDes(tr.son[0],str);
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

    public void init(){
        ReadForm.readFile(file);
        String [][]data = Arrays.copyOfRange(ReadForm.arr,0,10);
        attrName = new String[data[0].length-1];
        attrNum = attrName.length;
        ArrayList<String> resultList = new ArrayList<>();
        ArrayList<ArrayList<Double>> aad = new ArrayList<>();
        for(int i=1,n=data.length;i<n;i++){ //for every row data
            ArrayList<Double> ad = new ArrayList<>();
                for(int col=1;col<data[0].length-1;col++)
                    ad.add(Double.valueOf(data[i][col]));
            resultList.add(data[i][data[0].length-1]);
            aad.add(ad);
        }
        for(String str:resultList)
            if(!result.contains(str))
                result.add(str);
        TreeNode root=new TreeNode(aad,resultList);
        this.tree = root;
        root.grow();
    }

    class TreeNode {
        //private double Entropy;
        private double gini = 100;
        private TreeNode []son;
        private String divide;
        private String type;
        public String resultName;
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

            //return (this.data.size()<2||gini <0.4);
            return this.gini == 0;
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
            if(stopCondiction())
                this.isLeaf = true;
            else{
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
            double minmin = 100;
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
                double min = data.size();
                for(int i=0;i<newArr.size()-2;i++){          //get the mininum of classing this character
                    double tmp = (newArr.get(i)+newArr.get(i+1))/2;
                    double[] gini=getGini(tmp,j);
                    if(gini[2]<min){
                        min =gini[2];
                        bandary = tmp;
                        resultArr = gini;
                    }
                }
                if(min<minmin){
                    minmin = min;
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
            for(int i=1;i<data.size();i++) // for every data in dataset
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
