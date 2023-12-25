package experiment.preprocess.warc;

import experiment.preprocess.util.TextPreprocess;
import experiment.project.WARC;
import util.FileIOUtil;

import java.io.File;

public class ProcessWarc {

    public static void main(String[] args) {
        WARC warc = new WARC();
        initDirectory(warc);
        processArtifact(warc.getUnprocessedLevel1ArtifactPath(), warc.getProcessedLevel1ArtifactPath());
        processArtifact(warc.getUnprocessedLevel2ArtifactPath(), warc.getProcessedLevel2ArtifactPath());
        processArtifact(warc.getUnprocessedLevel3ArtifactPath(), warc.getProcessedLevel3ArtifactPath());
    }

    private static void initDirectory(WARC warc) {
        FileIOUtil.initDirectory(warc.getProcessedLevel1ArtifactPath());
        FileIOUtil.initDirectory(warc.getProcessedLevel2ArtifactPath());
        FileIOUtil.initDirectory(warc.getProcessedLevel3ArtifactPath());
    }

    private static void processArtifact (String unprocessedDirPath, String processedDirPath) {
        File unprocessedDir = new File(unprocessedDirPath);
        for (File f : unprocessedDir.listFiles()) {
            if (!f.getName().contains(".txt"))
                continue;
            String fileName = f.getName().split(".txt")[0];
            String text = FileIOUtil.readFile(f.getPath());
            TextPreprocess textPreprocess = new TextPreprocess(text);
            String processedText = textPreprocess.doNlpFileProcess();
            FileIOUtil.writeFile(processedText, processedDirPath +File.separator +fileName +".txt");
        }
    }

}
