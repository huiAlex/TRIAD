package experiment.preprocess.dronology;

import experiment.preprocess.parsecode.java.ParseJavaCode;
import experiment.preprocess.util.TextPreprocess;
import experiment.project.Dronology;
import util.FileIOUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessDronology {
    public static void main(String[] args) {
        // process code
        ParseJavaCode parseJavaCode = new ParseJavaCode("dronology");
        parseJavaCode.run();

        initDirectory();
        // process reqs
        splitProcessIssueText(Dronology.getUnprocessedReqPath(), Dronology.getProcessedReqPath(), Dronology.getProcessedReqSummaryPath(),
                Dronology.getProcessedReqDescriptionPath());
        // process design definitions
        splitProcessIssueText(Dronology.getUnprocessedDdPath(), Dronology.getProcessedDdPath(), Dronology.getProcessedDdSummaryPath(),
                Dronology.getProcessedDdDescriptionPath());
    }

    private static void initDirectory() {
        FileIOUtil.initDirectory(Dronology.getProcessedDdPath());
        FileIOUtil.initDirectory(Dronology.getProcessedDdSummaryPath());
        FileIOUtil.initDirectory(Dronology.getProcessedDdDescriptionPath());
        FileIOUtil.initDirectory(Dronology.getProcessedDdSummaryTpPath());
        FileIOUtil.initDirectory(Dronology.getProcessedDdDescriptionTpPath());

        FileIOUtil.initDirectory(Dronology.getProcessedReqPath());
        FileIOUtil.initDirectory(Dronology.getProcessedReqSummaryPath());
        FileIOUtil.initDirectory(Dronology.getProcessedReqDescriptionPath());
        FileIOUtil.initDirectory(Dronology.getProcessedReqSummaryTpPath());
        FileIOUtil.initDirectory(Dronology.getProcessedReqDescriptionTpPath());

    }

    private static void splitProcessIssueText(String unprocessedIssuePath, String processedIssuePath, String processedSummaryPath, String processedDescriptionPath) {
        File issueDirectory = new File(unprocessedIssuePath);
        for (File f : issueDirectory.listFiles()) {
            if (!f.getName().contains(".txt"))
                continue;
            List<String> issueTxtLineList = FileIOUtil.readFileByLine(f.getPath());
            Map<String, Integer> lineNumMap = getLineOfIssue(issueTxtLineList);
            for (int i = 0; i < issueTxtLineList.size(); i++) {
                String currentLine = issueTxtLineList.get(i);
                if (i > lineNumMap.get("summary") && i < lineNumMap.get("description")) {
                    processPart(currentLine, f.getName(), processedIssuePath, processedSummaryPath);
                } else if (i > lineNumMap.get("description")) {
                    processPart(currentLine, f.getName(), processedIssuePath, processedDescriptionPath);
                }
            }
        }
    }

    private static Map<String, Integer> getLineOfIssue(List<String> ucTxtLineList) {
        Map<String, Integer> lineNumMap = new HashMap();
        for (int i = 0; i < ucTxtLineList.size(); i++) {
            String line = ucTxtLineList.get(i);
            if (line.contains("[SUMMARY]")) {
                lineNumMap.put("summary", i);
            } else if (line.contains("[DESCRIPTION]")) {
                lineNumMap.put("description", i);
                return lineNumMap;
            }
        }
        return null;
    }

    private static void processPart(String currentLine, String fileName, String processedIssuePath, String processedPartPath) {
        if (currentLine.equals(""))
            return;
        TextPreprocess textPreprocess = new TextPreprocess(currentLine);
        String processedText = textPreprocess.doNlpFileProcess();
        FileIOUtil.continueWriteFile(processedText, processedIssuePath + "/" + fileName);
        FileIOUtil.continueWriteFile(processedText + "\n", processedPartPath + "/" + fileName);
    }

}
