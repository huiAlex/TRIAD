package experiment.preprocess.ebt;

import util.FileIOUtil;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtractCodeElement {
    private static String originMethodNamePath = "dataset/ebt/unprocessed/origin/code/method_name";
    private static String originInvokeMethodNamePath = "dataset/ebt/unprocessed/origin/code/invoke_method";
    private static String invokeMethodNamePath = "dataset/ebt/unprocessed/code/invoke_method";
    private static Set<String> allMethodSet = new HashSet<>();

    public static void main(String[] args) {
        FileIOUtil.initDirectory(invokeMethodNamePath);
        readAllMethod();
        filterInvokeMethod();
    }

    private static void filterInvokeMethod(){
        File dir = new File(originInvokeMethodNamePath);
        for(File f:dir.listFiles()) {
            List<String> list =  FileIOUtil.readFile2List(f);
            StringBuilder sb = new StringBuilder();
            for(String invoke:list) {
                if(allMethodSet.contains(invoke))
                    sb.append(invoke+"\n");
            }
            FileIOUtil.writeFile(sb.toString(),invokeMethodNamePath+"/"+f.getName());
        }
    }

    private static void readAllMethod (){
        File dir = new File(originMethodNamePath);
        for(File f:dir.listFiles()) {
            List<String> list =  FileIOUtil.readFile2List(f);
            for(String method:list) {
                allMethodSet.add(method);
            }
        }
    }

}
