package liangjiahao.dataMining.Utils;

/**
 * Created by A on 2017/7/19.
 */
public class UnPurified{
    //计算Classification error不纯度
    /*
    请输出各个类出现的个数，不需要按顺序
     */
    public static final int CLASSFICATION_ERROR = 1;
    public static final int ENTROPY = 2;
    public static final int GINI = 3;

    public static double getUnpurified(int method,int...times){
        switch(method){
            case 1:return Classification_error(times);
            case 2:return Entropy(times);
            case 3:return Gini(times);
            default:assert false:"错误的int值！";break;
        }
        return 0.0;
    }

    /**
     * 计算Classfication_error
     * @param times 各个东西的结果
     * @return classfication error
     */
    private static double Classification_error(int...times){

        double d = MyUtils.getMax(times);
        double csum = 0;
        for(int time:times)
            csum+=time;
        return 1.0-d/csum;
    }


    //计算Entropy不纯度
    /*
    请输出各个类出现的个数，不需要按顺序
     */
    private static double Entropy(int...times){
        double sum = 0;
        double csum = 0;
        for(int time:times)
            csum+=time;
        if(csum==0)
            return 0;
        for(int i =0,size=times.length;i<size;i++){
            double p = times[i]/csum;
            if(p==0)
                continue;
            double v = p * Math.log(p) / Math.log(2.0);
            sum+=v;
        }
        return -sum;
    }

    //计算Gini不纯度
    /*
    请输出各个类出现的个数，不需要按顺序
     */
    private static double Gini(int...times){
        double sum = 0;
        double csum = 0;
        for(int time:times)
            csum+=time;
        for(int i =0,size=times.length;i<size;i++){
            double p = times[i]/csum;
            sum+=p*p;
        }
        return 1.0-sum;
    }
}
