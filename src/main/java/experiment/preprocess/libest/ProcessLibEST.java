package experiment.preprocess.libest;

import experiment.preprocess.parsecode.c.ParseCCode;
import experiment.project.LibEST;
import util.FileIOUtil;

import java.io.File;
import java.util.List;

public class ProcessLibEST {
    private static LibEST libEST = new LibEST();
    private static String reqPath = "dataset/libEST/originals/comet/req.txt";
    private static String tcPath = "dataset/libEST/originals/comet/test.txt";
    private static String codePath = "dataset/libEST/originals/comet/code.txt";

    public static void main(String[] args) {
        // parse code identifiers
        ParseCCode parseCCode = new ParseCCode(libEST, "code"); // from project code
        parseCCode.run();
        ParseCCode parseCCode2 = new ParseCCode(libEST, "test"); // from test code
        parseCCode2.run();

        initDirectory(libEST);
        readCometTxt(reqPath, libEST.getProcessedLevel1ArtifactPath());
        readCometTxt(tcPath, libEST.getProcessedLevel2ArtifactPath());
        readCometTxt(codePath, libEST.getProcessedLevel3ArtifactPath());
    }

    private static void initDirectory(LibEST libEST) {
        FileIOUtil.initDirectory(libEST.getProcessedLevel1ArtifactPath());
        FileIOUtil.initDirectory(libEST.getProcessedLevel2ArtifactPath());
        FileIOUtil.initDirectory(libEST.getProcessedLevel3ArtifactPath());
    }

    /**
     * use processed text from COMET
     * @param cometPath
     * @param outputDir
     */
    private static void readCometTxt(String cometPath, String outputDir) {
        List<String> cometList = FileIOUtil.readFileByLine(cometPath);
        for (String comet : cometList) {
            String[] ary = comet.split(":");
            String artifactName = ary[0].replace(".txt", "");
            String content = ary[1].replaceAll(",", "").replaceAll("'", "").replaceAll("\\[", "").replaceAll("]", "");
            FileIOUtil.writeFile(content, outputDir + File.separator + artifactName + ".txt");
        }

    }
}
