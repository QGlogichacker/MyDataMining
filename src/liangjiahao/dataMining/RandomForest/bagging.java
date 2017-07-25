package liangjiahao.dataMining.RandomForest;

import liangjiahao.dataMining.Utils.MyUtils;

import java.util.ArrayList;
import java.util.Random;

public class bagging {
    public static Random random = new Random();
    public static ArrayList<String>result;
    public static ArrayList<ArrayList<Double>>  bagging(ArrayList<ArrayList<Double>> data,int num,ArrayList<String>aresult) {
        ArrayList<ArrayList<Double>> newData = new ArrayList<>();
        int rowL = data.size();
        int colL = data.get(0).size();
        boolean[]row = new boolean[rowL];
        boolean[]col = new boolean[colL];
        for(int i=0;i<rowL;i++)
            row[i] = true;
        for(int j=0;j<colL;j++)
            col[j] = true;
        for(int i=0,n=colL-1-num;i<3;i++){
            int index = (int)(Math.random()*(colL-2));
            if(col[index+1])
                col[index+1]=false;
            else i++;
        }
        result= new ArrayList<>();
        for(int i=0;i<rowL;i++)
            if(row[i]){
                ArrayList<Double> arr = new ArrayList<>();
                result.add(aresult.get(i));
                for(int j=0;j<colL;j++)
                    if(col[j])
                        arr.add(data.get(i).get(j));
                newData.add(arr);
            }
        return newData;
    }
}
