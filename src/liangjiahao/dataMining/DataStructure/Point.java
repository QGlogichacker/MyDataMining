package liangjiahao.dataMining.DataStructure;

import java.util.Arrays;

import static java.lang.Float.NaN;

/**
 * Created by A on 2017/7/14.
 */
public class Point {
    public double [] d;
    public int diamen;
    public Point(double...d){
        diamen = d.length;
        this.d = d;
    }


    @Override
    public String toString() {
        return "Point{" +
                "d=" + Arrays.toString(d) +
                '}';
    }

    public Point(int diamen){
        this.diamen = diamen;
        double [] d = new double [diamen];
        for(int i =0;i<diamen;i++)
            d[i] = Math.random()*100;
        this.d = d;
    }

    public static double dis(Point v1, Point v2) throws DiamenException {
        double sum = 0;
        if(v1.diamen!=v2.diamen)
            throw new DiamenException();
        for(int i=0;i<v1.diamen;i++){
            sum += (v1.d[i]-v2.d[i])*(v1.d[i]-v2.d[i]);
        }
        return Math.sqrt(sum);
    }

    public double getDis(Point v){
        try {
            return dis(this,v);
        } catch (DiamenException e) {
            e.printStackTrace();
            System.out.println("维数不正确");
        }
        return NaN;
    }


    public static double cos(Point v1, Point v2) throws DiamenException {
        double sum = 0;
        if(v1.diamen!=v2.diamen)
            throw new DiamenException();
        for(int i=0;i<v1.diamen;i++){
            sum += (v1.d[i]-v2.d[i])*(v1.d[i]-v2.d[i]);
        }
        return Math.sqrt(sum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point Point = (Point) o;
        return Arrays.equals(d, Point.d);
    }

    public double [] getPoint(){
        return this.d;
    }
}

