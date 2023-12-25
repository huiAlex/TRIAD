package experiment.preprocess.easyclinic;


import util.FileIOUtil;

import java.util.Arrays;
import java.util.List;

public class ExtractEasyClinicMatric {
    private static String uc2CcPath = "dataset/easyclinic/originals/TracedMatrices/UC_CC.txt";
    private static String uc2IdSPath = "dataset/easyclinic/originals/TracedMatrices/UC_ID.txt";
    private static String id2CcSPath = "dataset/easyclinic/originals/TracedMatrices/ID_CC.txt";

    public static void main(String[] args) {
        FileIOUtil.initDirectory("dataset/easyclinic/trace_matrices");

        String uc2Cc = extract(uc2CcPath);
        FileIOUtil.writeFile(uc2Cc, "dataset/easyclinic/trace_matrices/uc-cc.txt");

        String uc2Id = extract(uc2IdSPath);
        FileIOUtil.writeFile(uc2Id, "dataset/easyclinic/trace_matrices/uc-id.txt");

        String id2Cc = extract(id2CcSPath);
        FileIOUtil.writeFile(id2Cc, "dataset/easyclinic/trace_matrices/id-cc.txt");
    }

    private static String extract(String matricPath) {
        StringBuilder sb = new StringBuilder();
        List<String> lines = FileIOUtil.readFileByLine(matricPath);
        lines.stream().forEach(line -> {

            String[] ary = line.split(":");
            String source = ary[0].replace(".txt", "");
            String[] targets = ary[1].split(" ");
            Arrays.stream(targets).forEach(target -> {
                if (!target.equals(""))
                    sb.append(source + " " + target.replace(".txt", "") + "\n");
            });
        });
        return sb.toString();
    }

}
