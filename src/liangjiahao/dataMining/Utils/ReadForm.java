package liangjiahao.dataMining.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by A on 2017/7/19.
 */
public class ReadForm {
    public static String[][] readFile(File file){
        String str = null;
        ArrayList<String[]> twoDia= new ArrayList<>();
        String[] tmp = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while((str = br.readLine())!=null){
                tmp = str.split(" " );
                twoDia.add(tmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String [] []data = new String[twoDia.size()][];
        twoDia.toArray(data);
        return data;
    }
}
