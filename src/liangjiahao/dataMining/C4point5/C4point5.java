package liangjiahao.dataMining.C4point5;

import liangjiahao.dataMining.DataStructure.RowAndCol;
import liangjiahao.dataMining.Utils.ReadForm;
import liangjiahao.dataMining.Utils.UnPurified;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**c4.5算法
 * Created by A on 2017/7/17.
 */
public class C4point5 {
    private int attrNum;
    private int dataNum;
    private String[] attrName;
    private String[][] data;
    private File file;
    private String resultName;
    public TreeNode tree;
    private float right;
    private float fail;
    private HashMap<String,ArrayList<String>> hashMap;
    public C4point5(String filePath){
        this.file = new File(filePath);
        hashMap = new HashMap<>();
    }



    public static void main(String[] args) {
        C4point5 id3 = new C4point5("/media/logic_hacker/software/DataSet/adult1.test");
        id3.init();
        String[][]test = Arrays.copyOfRange(ReadForm.arr,5000,16000);
        for(int i =1;i<test.length;i++)
            id3.decide(test[i]);
        id3.report();
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
        if(tr.isLeaf) return tr.type;
        TreeNode son=tr.son.get(str[getIndex(tr.Divide)]);
        if(son!=null)return getDes(son,str);
        else return tr.type;
    }

    private boolean stopCondiction(TreeNode tn){
        return (tn.rac.rowLeft()<4||tn.Entropy==0);
    }  //更改生长终止条件

    public  void init(){         //
        ReadForm.readFile(file);
        data = Arrays.copyOfRange(ReadForm.arr,0,11000);
        attrName = data[0];//第一行为属性的名字
        attrNum = data[0].length;
        dataNum = data.length;
        this.readAttr();  //读取data中的信息并构建数据表
        assert dataNum>1:"没有数据！";
        TreeNode root=new TreeNode(new RowAndCol(data));
        this.tree = root;
        root.grow(root);
    }

    /**
     *
     * @param parent 当前节点
     * @return Key为测试条件，Value为子节点的HashMap
     */
    private HashMap<String,TreeNode> findBestSplit(TreeNode parent){
        double max = 0;
        int index=1;
        String nameSelected=data[0][1];
        for(int i=1;i<attrNum-1;i++)
            if(parent.rac.colContains(i)){
                double up=DUP_classWith(parent.rac,data[0][i],parent);
                if(up>max){
                    max=up;
                    nameSelected = data[0][i];
                    index = i;
                }
            }
        parent.rac.delCol(index);
        HashMap<String, TreeNode> NameToNode = new HashMap<>();
        for(String str: hashMap.get(nameSelected)){  //对于被选中的元素
            TreeNode treeNode = new TreeNode (parent,nameSelected,str);
            NameToNode.put(str,treeNode);
        }
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

    private double DUP_classWith(RowAndCol rac,String name,TreeNode parent){
        int index=getIndex(name);
        ArrayList classarr=new ArrayList(hashMap.get(name));
        ArrayList resarr = new ArrayList(hashMap.get(resultName));
        int [][] count = new int[classarr.size()][resarr.size()];
        for(int i=1,il=rac.getRowLength();i<il;i++){  //对第i行的数据处理
            if(rac.rowContains(i))
                for(int cla=0;cla<classarr.size();cla++)
                    if(classarr.get(cla).equals(data[i][index]))  //对属性name的第cla个状态进行处理
                        for(int res=0;res<resarr.size();res++)
                            if(resarr.get(res).equals(data[i][attrNum-1]))  //对属性name的cla个状态的各个结果进行处理
                                count[cla][res]++;
        }
        return DeltaEntropyratio(parent.Entropy,count);
    }

    class TreeNode{
        private double Entropy;
        private RowAndCol rac;
        private HashMap<String,TreeNode> son;
        private String type;
        public boolean isLeaf;
        public String Divide;


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
            Entropy = UnPurified.getUnpurified(UnPurified.ENTROPY,count);
        }



        /**
         * 新建一个结点
         * @param parent 父节点
         * @param name 按...分裂
         * @param Status 分裂出属性的名字
         */


        public TreeNode(TreeNode parent,String name,String Status){
            int index=getIndex(name);
            this.rac = new RowAndCol(parent.rac);
            this.rac.delCol(index);
            for(int i=1;i<dataNum;i++)
                if(this.rac.rowContains(i))
                    if(!data[i][index].equals(Status))
                        this.rac.delRow(i);
            //print(this.rac);
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
            Entropy = UnPurified.getUnpurified(UnPurified.ENTROPY,count);
            parent.Divide = name;
        }

        public void grow(TreeNode root){
            if(stopCondiction(root))
                this.isLeaf=true;
            else {
                son = findBestSplit(this);
                for(TreeNode treeNode:son.values())
                    treeNode.grow(treeNode);
            }
        }
    }

    private double DeltaEntropyratio(double Iparent,int[][]arr){
        int count =0;
        double sum = 0.0;
        double ratio = 0.0;
        int whole = arr.length;
        for(int i =0;i<arr.length;i++){ //对属性的状态i进行处理
            double tmp= UnPurified.getUnpurified(UnPurified.ENTROPY,arr[i]); //求出状态i的信息不纯度
            int number = 0;
            for(int j =0;j<arr[0].length;j++){ //对属性状态i的对应结果进行换算
                count+=arr[i][j];     //求记录个数和
                number+=arr[i][j];    //求状态i的结果总数
            }
            ratio+=(number/whole);
            sum+=(tmp*number);
        }
        return (Iparent-sum/count)/ratio; //信息增益lv
    }
}

