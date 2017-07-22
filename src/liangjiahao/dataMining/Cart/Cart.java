package liangjiahao.dataMining.Cart;

import liangjiahao.dataMining.DataStructure.*;
import liangjiahao.dataMining.Utils.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.*;


/**Cart算法
 * Created by A on 2017/7/19.
 */
public class Cart {
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
    class continal{
        continal(String type,double value){
            this.type = type;
            this.value = value;
        }
        String type;
        double value;
    }
    class MyComparator implements  Comparator{
        @Override
        public int compare(Object o1, Object o2) {
            if(((continal)o1).value>((continal)o2).value)
                return 10;
            else return -1;
        }
    }
    public Cart(String filePath){
        this.file = new File(filePath);
        hashMap = new HashMap<>();
    }

    public static void main(String[] args) {
        Cart cart = new Cart("/media/logic_hacker/software/c4.5.txt");
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
        TreeNode root=new TreeNode(new RowAndCol(data));
        this.tree = root;
        //TODO
        root.grow(root);
    }

    /**
     * @param parent 当前节点
     * @return Key为测试条件，Value为子节点的HashMap
     */
    private HashMap<String,TreeNode> findBestSplit(TreeNode parent){
        double min = 1;
        int index=1;
        PairsSet up =bestClass(parent.rac,data[0][1],parent);
        String nameSelected=data[0][1];
        for(int i=1;i<attrNum-1;i++){
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
        ArrayList classarr=new ArrayList();
        for(int i=1;i<dataNum;i++)
            if(rac.rowContains(i)&&tmp.contains(data[i][index])&&!classarr.contains(data[i][index]))
                classarr.add(data[i][index]);
        ArrayList resarr = new ArrayList(hashMap.get(resultName));
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

    class TreeNode{
        //private double Entropy;
        private double gini;
        private RowAndCol rac;
        private HashMap<String,TreeNode> son;
        private String type;
        public String Divide;
        public boolean isLeaf;


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

