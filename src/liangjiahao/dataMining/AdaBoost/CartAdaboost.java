package liangjiahao.dataMining.AdaBoost;

import liangjiahao.dataMining.DataStructure.PairsSet;
import liangjiahao.dataMining.DataStructure.RowAndCol;
import liangjiahao.dataMining.Utils.ReadForm;
import liangjiahao.dataMining.Utils.UnPurified;
import liangjiahao.dataMining.Utils.getSubSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**Cart算法
 * Created by A on 2017/7/19.
 */
public class CartAdaboost {
    private int attrNum;
    private int dataNum;
    private String[] attrName;
    private String[][] data;
    private File file;
    private String resultName;
    public TreeNode tree;
    private float right;
    private float fail;
    private ArrayList<Integer> continal;
    private HashMap<String,ArrayList<String>> hashMap;
    class Continal{
        Continal(String type,double value,int rowIndex){
            this.type = type;
            this.value = value;
            this.rowIndex = rowIndex;
        }
        String type;
        double value;
        int rowIndex;
    }
    class MyComparator implements  Comparator{
        @Override
        public int compare(Object o1, Object o2) {
            if(((Continal)o1).value>((Continal)o2).value)
                return 1;
            else return -1;
        }
    }
    public CartAdaboost(String filePath, int...continal){
        this.file = new File(filePath);
        int [] arr = continal;
        this.continal = new ArrayList<Integer>();
        for(int n:continal)
            this.continal.add(n);
        hashMap = new HashMap<>();
    }

    public static void main(String[] args) {
        CartAdaboost cart = new CartAdaboost("/media/logic_hacker/software/c4.5 (复件).txt",5);
        cart.init();
        for(int i =1;i<cart.dataNum;i++)
            cart.decide(cart.data[i]);
        cart.report();
    }


    public void decide(String[]str){
        System.out.println(getDes(tree, str));
        if(getDes(tree,str).equals(str [attrNum-1]))
            right++;
        else fail++;
    }


    public void report(){  //显示现在的表现
        System.out.println("正确 "+(int)right+" 个，错误 "+(int)fail+" 个,正确率为："+right/(right+fail));
    }

    private String getDes(TreeNode tr,String[]str){   //递归调用使用决策树
        if(tr.isCon)
            return Double.valueOf(str[getIndex(tr.Divide)])>tr.bandary?getDes(tr.son.get("s"),str):getDes(tr.son.get("l"),str);
        if(tr.isLeaf) return tr.type;
        else if(tr.son.get(str[getIndex(tr.Divide)])==null) return tr.type;
        else return getDes(tr.son.get(str[getIndex(tr.Divide)]),str);
    }

    private boolean stopCondiction(TreeNode tn){
        return (tn.gini==0||tn.rac.rowLeft()<2);
    }  //更改生长终止条件

    public  void init(){         //
        data = ReadForm.readFile(file);
        attrName = data[0];//第一行为属性的名字
        attrNum = data[0].length;
        dataNum = data.length;
        this.readAttr();  //读取data中的信息并构建数据表
        assert dataNum>1:"没有数据！";
        RowAndCol rowAndCol = new RowAndCol(data);
        TreeNode root=new TreeNode(rowAndCol);
        this.tree = root;
        root.grow(root);
    }

    /**
     * @param parent 当前节点
     * @return Key为测试条件，Value为子节点的HashMap
     */
    private HashMap<String,TreeNode> findBestSplit(TreeNode parent){
        double min = 1;
        int index=1;
        Result rs =null;
        PairsSet up =bestClass(parent.rac,data[0][1],parent);
        String nameSelected=data[0][1];
        for(int i=1;i<attrNum-1;i++){
            if(continal.contains(i)){
                rs =continalBestClass(parent.rac,data[0][i]);
                if(rs.gini<min){
                    min=rs.gini;
                    nameSelected = data[0][i];
                    index = i;
                }

            }
            PairsSet tmp=bestClass(parent.rac,data[0][i],parent);
            if(tmp==null)   //这个元素无法继续划分
                continue;
            if(tmp.gini<min){
                min=tmp.gini;
                nameSelected = data[0][i];
                up = tmp;
                index = i;
            }
        }
        //parent.rac.delCol(index);
        HashMap<String, TreeNode> NameToNode = new HashMap<>();
        parent.Divide = nameSelected;
        if(up==null)
            return null;
        if(continal.contains(getIndex(nameSelected))){
            TreeNode treeNode1 = new TreeNode (parent,nameSelected,rs.smaller,rs.smallergini,rs.bandary);
            TreeNode treeNode2 = new TreeNode (parent,nameSelected,rs.larger,rs.largergini,rs.bandary);
            NameToNode.put("s",treeNode1);
            NameToNode.put("l",treeNode2);
            return NameToNode;
        }
        TreeNode treeNode1 = new TreeNode (parent,nameSelected,up.getItems(),up.itemsgini);
        TreeNode treeNode2 = new TreeNode (parent,nameSelected,up.getMirror(),up.mirrorgini);
        ArrayList<String> status=hashMap.get(nameSelected);
        for(int i=0,n=up.getItems().length;i<n;i++)
            if(up.getItems()[i])
                NameToNode.put(status.get(i),treeNode1);
        for(int i=0,n=up.getItems().length;i<n;i++)
            if(up.getMirror()[i])
                NameToNode.put(status.get(i),treeNode2);
        System.out.println();
        return NameToNode;
    }

    private void readAttr(){
        for(int j =1;j<attrNum;j++){
            ArrayList<String> attrValue = new ArrayList<>(); //存储各个属性的各种值
            for(int i=1;i<data.length;i++)
                if(!attrValue.contains(data[i][j]))  //若未出现则加到属性对应的列表中
                    attrValue.add(data[i][j]);

            hashMap.put(data[0][j],attrValue);    //最后将对应的attr名和值加到hashmap中来
        }
        resultName = data[0][attrNum-1];
    }

    private int getIndex(String name){
        int index=0;
        for(int j = 1;j<attrNum;j++)
            if(attrName[j].equals(name))
                index=j;                               //获取对应name的下标
        assert index!=0:"参数名错误！";
        return index;
    }

    private PairsSet bestClass(RowAndCol rac,String name,TreeNode parent){
        int index=getIndex(name);
        ArrayList tmp =hashMap.get(name);
        ArrayList resarr = new ArrayList(hashMap.get(resultName));
        ArrayList classarr=new ArrayList();
        for(int i=1;i<dataNum;i++)
            if(rac.rowContains(i)&&tmp.contains(data[i][index])&&!classarr.contains(data[i][index]))
                classarr.add(data[i][index]);
        int [][] count = new int[classarr.size()][resarr.size()];
        for(int i=1,il=rac.getRowLength();i<il;i++)  //对第i行的数据处理
            if(rac.rowContains(i))
                for(int cla=0;cla<classarr.size();cla++)
                    if(classarr.get(cla).equals(data[i][index]))  //对属性name的第cla个状态进行处理
                        for(int res=0;res<resarr.size();res++)
                            if(resarr.get(res).equals(data[i][attrNum-1]))  //对属性name的cla个状态的各个结果进行处理
                                count[cla][res]++;
        if(classarr.size()<=1)
            return null;
        ArrayList<PairsSet> arrs = getSubSet.getPair(classarr.size());
        double gini = 1;
        PairsSet selected = null;
        for(PairsSet pairset:arrs)
            if(pairset.getGini(count)<gini){
                gini = pairset.gini;
                selected = pairset;
            }
        return selected;
    }

    class Result{
        double bandary;
        double gini;
        boolean[]smaller;
        boolean[]larger;
        double smallergini;
        double largergini;
        Result(double bandary,double gini,boolean[]smaller,boolean[]larger, double smallergini, double largergini){
            this.bandary = bandary;
            this.gini = gini;
            this.smaller = smaller;
            this.larger = larger;
            this.smallergini = smallergini;
            this.largergini=largergini;
        }
    }

    Result continalBestClass(RowAndCol rac,String name){
        int index=getIndex(name);
        ArrayList tmp =hashMap.get(name);
        ArrayList resarr = new ArrayList(hashMap.get(resultName));
        double smallergini=0;
        double largergini=0;
        int mark=0;
        ArrayList<Continal> continals = new ArrayList();
        for(int i=1;i<dataNum;i++)
            if(rac.rowContains(i))
                continals.add(new Continal(data[i][attrNum-1],Double.valueOf(data[i][index]),i));
        double min = 1.0*continals.size();
        continals.sort(new MyComparator());
        int countFloat[][]=new int[2][resarr.size()];
        for(int i=1,n=continals.size();i<n;i++){
            if(!continals.get(i-1).type.equals(continals.get(i))){  //Improve to avoid aduadant calculate
                Continal fact1 = continals.get(i-1);
                Continal fact2 = continals.get(i);
                double fact = (fact1.value+fact2.value)/2.0;
                for(int w=0;w<n;w++){
                    if(w<=i) countFloat[0][resarr.indexOf(continals.get(w).type)]++;
                    else countFloat[1][resarr.indexOf(continals.get(w).type)]++;
                }
                double d1=UnPurified.getUnpurified(UnPurified.GINI,countFloat[0]);
                double d2=UnPurified.getUnpurified(UnPurified.GINI,countFloat[1]);
                double doubleGini = d1*(i+1)+d2*(n-i-1);
                if(doubleGini<min){
                    min = doubleGini;
                    mark = i;
                    smallergini =d1;
                    largergini = d2;
                }
            }

        }
        boolean[] smaller = new boolean[dataNum];
        boolean[] larger = new boolean[dataNum];
        for(int i=0,n=continals.size();i<n;i++){
            if(i<mark) smaller[continals.get(i).rowIndex]=true;
            else larger[continals.get(i).rowIndex]=true;
        }
        return new Result((continals.get(mark-1).value+continals.get(mark).value)/2,min,smaller,larger,smallergini,largergini);
    }

    class TreeNode{
        //private double Entropy;
        private double gini;
        private RowAndCol rac;
        private HashMap<String,TreeNode> son;
        private String type;
        public String Divide;
        public boolean isLeaf;
        public double bandary;
        public boolean isCon;


        public TreeNode(RowAndCol rac){
            this.rac = rac;
            ArrayList resarr = new ArrayList(hashMap.get(resultName));
            int count[] = new int[resarr.size()];
            for(int i=1;i<dataNum;i++)
                if(this.rac.rowContains(i))
                    for(int res=0;res<resarr.size();res++)
                        if(resarr.get(res).equals(data[i][attrNum-1]))
                            count[res]++;
            int max=count[0];
            String mark = (String) resarr.get(0);
            for(int i=1;i<resarr.size();i++)
                if(count[i]>max){
                    max = count[i];
                    mark = (String) resarr.get(i);
                }
            type = mark;
            this.gini = 100;
            //Entropy = UnPurified.getUnpurfied(UnPurified.ENTROPY,count);
        }



        /**
         * 新建一个结点
         * @param parent 父节点
         * @param name 按...分裂
         * @param barr 分裂出属性的名字对应的布尔值数组

         */
        public TreeNode(TreeNode parent,String name,boolean[] barr,double gini){
            ArrayList<String> namelist = new ArrayList(hashMap.get(name));
            ArrayList<String> resarr = new ArrayList<>(hashMap.get(resultName));
            List<String> strings = new ArrayList<>();
            for(int i=0;i<barr.length;i++)
                if(barr[i])
                    strings.add(namelist.get(i));
            int index=getIndex(name);
            this.rac = new RowAndCol(parent.rac);  //复制训练集
            for(int i=1;i<dataNum;i++)
                if(this.rac.rowContains(i))
                    if(!strings.contains(data[i][index]))
                        this.rac.delRow(i);   //删除不在状态列表里的所有行
            this.rac.print();

            //统计这个结点该打什么type
            int count[] = new int[resarr.size()];
            for(int i=1;i<dataNum;i++)
                if(this.rac.rowContains(i))
                    for(int res=0;res<resarr.size();res++)
                        if(resarr.get(res).equals(data[i][attrNum-1]))
                            count[res]++;
            int max=count[0];
            String mark = resarr.get(0);
            for(int i=1;i<resarr.size();i++)
                if(count[i]>max){
                    max = count[i];
                    mark = resarr.get(i);
                }
            type = mark;
            //parent.Divide = name;
            this.gini = gini;
        }

        public TreeNode(TreeNode parent,String name,boolean[] barr,double gini,double bandary){
            ArrayList<String> resarr = new ArrayList<>(hashMap.get(resultName));
            int index=getIndex(name);
            this.rac = new RowAndCol(parent.rac);  //复制训练集
            for(int i=0;i<barr.length;i++)
                if(!barr[i])
                    this.rac.delRow(i);
            this.rac.print();

            //统计这个结点该打什么type
            int count[] = new int[resarr.size()];
            for(int i=1;i<dataNum;i++)
                if(this.rac.rowContains(i))
                    for(int res=0;res<resarr.size();res++)
                        if(resarr.get(res).equals(data[i][attrNum-1]))
                            count[res]++;
            int max=count[0];
            String mark = resarr.get(0);
            for(int i=1;i<resarr.size();i++)
                if(count[i]>max){
                    max = count[i];
                    mark = resarr.get(i);
                }
            type = mark;
            //parent.Divide = name;
            this.gini = gini;
            parent.bandary =bandary;
            parent.isCon =true;
        }

        public void grow(TreeNode root){
            if(stopCondiction(root))
                this.isLeaf=true;
            else {
                son = findBestSplit(this);
                if(son==null){
                    this.isLeaf=true;
                    return;
                }
                for(TreeNode treeNode:son.values())
                    treeNode.grow(treeNode);
            }
        }
    }

    public static double GINI(int[][]arr){
        int count=0;
        double sum = 0.0;
        for(int i =0;i<arr.length;i++){ //对属性的状态i进行处理
            double tmp= UnPurified.getUnpurified(UnPurified.GINI,arr[i]); //求出状态i的信息不纯度
            int number = 0;
            for(int j =0;j<arr[0].length;j++){ //对属性状态i的对应结果进行换算
                count+=arr[i][j];     //求记录个数和
                number+=arr[i][j];    //求状态i的结果总数
            }
            sum+=(tmp*number);
        }
        return sum/count; //GINI信息增益
    }
}

