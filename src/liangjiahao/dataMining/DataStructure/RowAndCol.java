package liangjiahao.dataMining.DataStructure;

import java.util.ArrayList;

/**
 * 用来配合二维数组进行运算，可以节省空间
 * Created by A on 2017/7/19.
 */
public class RowAndCol<T>{
    private boolean[] row;
    private boolean[] col;
    private T[][] data;
    public RowAndCol(RowAndCol<T> rac){
        this.data = rac.data;
        int rowL = rac.row.length;
        int colL = rac.col.length;
        this.row = new boolean[rowL];
        this.col = new boolean[colL];
        for(int i=0;i<rowL;i++)
            row[i] = rac.row[i];
        for(int j=0;j<colL;j++)
            col[j] = rac.col[j];
    }

    public int colLeft(){
        int count =0;
        for(int i=0,size=col.length;i<size;i++)
            if(col[i])
                count++;
        return count;
    }

    public RowAndCol(T[][] data){
        int row = data.length;
        int col = data[0].length;
        this.data = data;
        this.row = new boolean[row];
        this.col = new boolean[col];
        for(int i=0;i<row;i++)
            this.row[i] = true;
        for(int j=0;j<col;j++)
            this.col[j] = true;
    }

    public RowAndCol(T[][] data,int randomColNum){
        int row = data.length;
        int col = data[0].length;
        this.data = data;
        this.row = new boolean[row];
        this.col = new boolean[col];
        for(int i=0;i<row;i++)
            this.row[i] = true;
        for(int j=0;j<col;j++)
            this.col[j] = true;
        for(int i=0,n=data[0].length-1-randomColNum;i<3;i++){
            int index = (int)(Math.random()*(data[0].length-2));
            if(this.col[index+1])
                this.col[index+1]=false;
            else i++;
        }
    }

    public void delRow(int i){
        row[i]=false;
    }

    public void delCol(int i){
        col[i]=false;
    }

    public boolean rowContains(int index){
        return row[index];
    }

    public boolean colContains(int index){
        return col[index];
    }

    public boolean[] colWithout(int k){
        boolean[]newRow = new boolean[this.row.length];
        for(int i =0;i<this.row.length;i++)
            newRow[i] = this.row[i];
        newRow[k] = false;
        return newRow;
    }

    public int getRowLength(){
        return row.length;
    }

    public int getColLength(){
        return col.length;
    }

    public void print(){
        for(int i=0,il=this.getRowLength();i<il;i++){
            if(this.rowContains(i)){
                for(int j=0,jl=this.getColLength();j<jl;j++)
                    if(this.colContains(j))
                        System.out.print(data[i][j]+"\t");
                System.out.println();
            }
        }
    }

    public int rowLeft(){
        int count =0;
        for(int i=0,size=row.length;i<size;i++)
            if(row[i])
                count++;
        return count;
    }
}
