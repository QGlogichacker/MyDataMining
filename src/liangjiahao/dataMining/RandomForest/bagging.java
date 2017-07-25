package liangjiahao.dataMining.RandomForest;

import liangjiahao.dataMining.Utils.MyUtils;

import java.util.ArrayList;
import java.util.Random;

public class bagging {
    public static Random random = new Random();
    public static ArrayList<String>result;
    public static ArrayList<ArrayList<Double>>  bagging(ArrayList<ArrayList<Double>> data,int num,ArrayList<String>aresult) {
        ArrayList<ArrayList<Double>> newData = new ArrayList<>();
        result = new ArrayList<>();
        int rowL = data.size();
        for(int i=0;i<rowL;i++)
            if(random.nextInt()%4==0){
                newData.add(data.get(i));
                result.add(aresult.get(i));
            }
        for(int i=0;i<rowL;i++)
            if(random.nextInt()%4==3){
                newData.add(data.get(i));
                result.add(aresult.get(i));
            }
        return newData;
    }
}
