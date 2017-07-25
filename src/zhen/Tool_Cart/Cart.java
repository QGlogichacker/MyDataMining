package zhen.Tool_Cart;

import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;


/**
 * Created by lenovo on 2017/7/18.
 */
public class Cart {

    public static void main(String[] arg){
        Node node;
        ArrayList<ArrayList<Double>> creatDatas=new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> textDatas=new ArrayList<ArrayList<Double>>();
        ArrayList<String> attributes=new ArrayList<String>();
        TreeMap<Double,ArrayList<ArrayList<Double>>> label_datas;
        CreatDecisionTree_Cart creatDC=new CreatDecisionTree_Cart();
        ArrayList<Integer> counts=new ArrayList<Integer>();
        int count;
        String creatFile="/media/logic_hacker/software/DataSet/abalone 1 (另一个复件).data";
        String textFile="/media/logic_hacker/software/DataSet/abalone 1 (另一个复件).data";

        counts.add(0);
        counts.add(0);

        //读取数据构建决策树和进行数据的测试
        LoadDatas.loadDatas(creatDatas,attributes,creatFile);

        //生成决策树
        node= creatDC.creatDecisionTree((ArrayList<ArrayList<Double>>) creatDatas.clone(),attributes);

        System.out.println("----------已生成决策树-----------");

        //读取测试数据
        LoadDatas.loadDatas(textDatas,new ArrayList<String>(),textFile);

        //将数据分类
        label_datas= Classify_Cart.classifyDatas(node, (ArrayList<ArrayList<Double>>) textDatas.clone(),attributes,counts);

        count=counts.get(0)+counts.get(1);
        System.out.println("分类正确率为："+(double)counts.get(0)/(double)count);

        int i=0;
        System.out.println("分类结果为：");
        for(double key:label_datas.keySet()){
            System.out.print("标签为："+key);
            System.out.println("对应的数据个数为："+label_datas.get(key).size());
            i++;
        }
    }


    //先序遍历决策树，输出节点属性
    public static void printNode(Node node){
        if(node.getAttribute().equals("leafNode")){
            System.out.println(node.getAttribute());
            return;
        }


        System.out.println(node.getAttribute());
        printNode(node.getChildNodes().get(0));
        printNode(node.getChildNodes().get(1));
    }

}
