package experiment.preprocess.ebt;


import util.FileIOUtil;
import util.SetMapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtractEbtMatrices {
    private static String req2CcPath = "dataset/ebt/originals/AnswerReq2CC.txt";
    private static String req2TcPath = "dataset/ebt/originals/AnswerReq2Testcase.txt";
    private static Map<String, List<String>> reqCcMap = new HashMap<>();
    private static Map<String, List<String>> reqTcMap = new HashMap<>();

    public static void main(String[] args) {
        FileIOUtil.initDirectory("dataset/ebt/trace_matrices");

        String req2Cc = extract(req2CcPath, reqCcMap);
        FileIOUtil.writeFile(req2Cc, "dataset/ebt/trace_matrices/req-code.txt");

        String req2Tc = extract(req2TcPath, reqTcMap);
        FileIOUtil.writeFile(req2Tc, "dataset/ebt/trace_matrices/req-tc.txt");
    }

    private static String extract(String matricPath, Map<String, List<String>> map) {
        List<String> lines = FileIOUtil.readFileByLine(matricPath);
        lines.stream().forEach(line -> {

            String[] ary = line.split(" ");
            String source = ary[0];
            if (!map.containsKey(source))
                map.put(source, new ArrayList<>());
            for (int i = 1; i < ary.length; i++) {
                String target = ary[i];
                map.get(source).add(target);
            }
        });
        Map<String, List<String>> sortedMap = SetMapUtil.sortMapListByKey(map);
        StringBuilder sb = new StringBuilder();
        for (String source : sortedMap.keySet()) {
            sortedMap.get(source).stream().forEach(target -> {
                sb.append(source + " " + target + "\n");
            });
        }
        return sb.toString();
    }
}
