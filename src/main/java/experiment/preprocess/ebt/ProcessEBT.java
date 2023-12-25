package experiment.preprocess.ebt;

import experiment.project.EBT;
import util.FileIOUtil;

import java.io.File;
import java.util.List;

public class ProcessEBT {
    private static EBT ebt = new EBT();
    private static String reqPath = "dataset/ebt/originals/comet/req.txt";
    private static String tcPath = "dataset/ebt/originals/comet/testcase.txt";
    private static String codePath = "dataset/ebt/originals/comet/code.txt";

    public static void main(String[] args) {
        initDirectory(ebt);
        readCometTxt("RQ", reqPath, ebt.getProcessedLevel1ArtifactPath());
        readCometTxt("TC", tcPath, ebt.getProcessedLevel2ArtifactPath());
        readCometCodeTxt(codePath, ebt.getProcessedLevel3ArtifactPath());
    }

    private static void initDirectory(EBT ebt) {
        FileIOUtil.initDirectory(ebt.getProcessedLevel1ArtifactPath());
        FileIOUtil.initDirectory(ebt.getProcessedLevel2ArtifactPath());
        FileIOUtil.initDirectory(ebt.getProcessedLevel3ArtifactPath());
    }

    private static void readCometCodeTxt(String cometPath, String outputDir) {
        List<String> cometList = FileIOUtil.readFileByLine(cometPath);
        for (int i = 0; i < cometList.size(); i++) {
            String[] ary = cometList.get(i).split("\\.java: ");
            String id = ary[0];
            String content = ary[1].replaceAll(",", "").replaceAll("'", "").replaceAll("\\[", "").replaceAll("]", "");
            FileIOUtil.writeFile(content, outputDir + File.separator + id + ".txt");
        }
    }

    private static void readCometTxt(String prefix, String cometPath, String outputDir) {
        List<String> cometList = FileIOUtil.readFileByLine(cometPath);
        for (int i = 0; i < cometList.size(); i++) {
            String[] ary = cometList.get(i).split(": \\[");
            String id = ary[0].replace(prefix, "");
            String content = ary[1].replaceAll(",", "").replaceAll("'", "").replaceAll("\\[", "").replaceAll("]", "");
            FileIOUtil.writeFile(content, outputDir + File.separator + id + ".txt");
        }
    }
}
