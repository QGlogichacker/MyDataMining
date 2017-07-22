package liangjiahao.dataMining.kNN;


import liangjiahao.dataMining.DataStructure.Point;

import java.util.LinkedList;
import java.util.List;

/**KNN算法
 * Created by A on 2017/7/17.
 */
public class KNN{
    public LinkedList<DataDiastance> data;
    public LinkedList<DataDiastance> queue;
    int k;

    public KNN(List<Point> list, int k){
        data = new LinkedList<>();
        for(Point dt:list){
            DataDiastance dataDiastance = new DataDiastance(dt);
            data.add(dataDiastance);
        }
        this.k = k;
    }

    public LinkedList<DataDiastance> run(Point myvec){
        queue = new LinkedList<>();
        for(DataDiastance dd:data){
            dd.getDistance(myvec);
            OrderInsert(dd);
        }
        return queue;
    }

    private void OrderInsert(DataDiastance dd){
        for(int i=0,size=queue.size();i<size;i++)
            if(dd.compareTo(queue.get(i))>0){
                queue.add(i,dd);
                if(queue.size()==k+1)
                    queue.poll();
            }
    }

    class DataDiastance implements Comparable{
        double dis;
        Point data;

        public DataDiastance(Point v){
            data = v;
        }

        public void getDistance(Point v){
            dis = data.getDis(v);
        }
        public int compareTo(Object o) {
            if(o==null||!o.getClass().equals(this.getClass()))
                return -1;
            DataDiastance dd = (DataDiastance) o;
            if(dd.dis>this.dis)
                return -1;
            else return 1;
        }
    }
}