package experiment.preprocess.easyclinic;

import experiment.preprocess.util.StanfordNlpUtil;
import experiment.preprocess.util.TextPreprocess;
import experiment.project.EasyClinic;
import util.FileIOUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessEasyClinic {
    private static EasyClinic easyClinic = new EasyClinic();

    public static void main(String[] args) {
        initDirectory(easyClinic);
        splitProcessCCText(easyClinic.getUnprocessedLevel3ArtifactPath());
        splitProcessIDText(easyClinic.getUnprocessedLevel2ArtifactPath());
        splitProcessUCText(easyClinic.getUnprocessedLevel1ArtifactPath());
    }

    private static void initDirectory(EasyClinic easyClinic) {
        FileIOUtil.initDirectory(easyClinic.getProcessedLevel1ArtifactPath());
        FileIOUtil.initDirectory(easyClinic.getProcessedLevel2ArtifactPath());
        FileIOUtil.initDirectory(easyClinic.getProcessedLevel3ArtifactPath());
    }

    private static void splitProcessIDText(String idDirPath) {
        File idDirectory = new File(idDirPath);

        if (idDirectory.isDirectory()) {
            for (File f : idDirectory.listFiles()) {
                if (!f.getName().contains(".txt"))
                    continue;
                String text = FileIOUtil.readFile(f.getPath());
                TextPreprocess textPreprocess = new TextPreprocess(text);
                String processedText = textPreprocess.doNlpFileProcess();
                FileIOUtil.continueWriteFile(processedText, easyClinic.getProcessedLevel2ArtifactPath() + "/" + f.getName());
            }
        }
    }

    private static void splitProcessCCText(String ccDirPath) {
        File ccDirectory = new File(ccDirPath);

        if (ccDirectory.isDirectory()) {
            for (File f : ccDirectory.listFiles()) {
                if (!f.getName().contains(".txt"))
                    continue;
                List<String> ccTxtLineList = FileIOUtil.readFileByLine(f.getPath());
                Map<String, Integer> lineNumMap = getLineOfCC(ccTxtLineList);

                for (int i = 0; i < ccTxtLineList.size(); i++) {
                    String currentLine = ccTxtLineList.get(i);
                    if (i == lineNumMap.get("class")) {
                        String clsName = currentLine.split(":")[1];
                        splitProcessCode(clsName, f.getName(), easyClinic.getProcessedLevel3ArtifactPath());
                    } else if (i == lineNumMap.get("description")) {
                        String description = currentLine.split(":")[1];
                        splitProcessPart(description, f.getName(), easyClinic.getProcessedLevel3ArtifactPath());
                    } else if (i > lineNumMap.get("attribute") && i < lineNumMap.get("method")) {
                        String[] attributeAry = currentLine.split(";");
                        String attrName = attributeAry[0];
                        splitProcessCode(attrName, f.getName(), easyClinic.getProcessedLevel3ArtifactPath());
                        String attrDescription = attributeAry[2];
                        splitProcessPart(attrDescription, f.getName(), easyClinic.getProcessedLevel3ArtifactPath());
                    } else if (i > lineNumMap.get("method")) {
                        String[] methodAry = currentLine.split(";");
                        String methodName = methodAry[0];
                        splitProcessCode(methodName, f.getName(), easyClinic.getProcessedLevel3ArtifactPath());
                        String methodDescription = methodAry[2];
                        splitProcessPart(methodDescription, f.getName(), easyClinic.getProcessedLevel3ArtifactPath());
                    }
                }
            }
        }
    }

    private static void splitProcessUCText(String ucDirPath) {
        File ucDirectory = new File(ucDirPath);

        if (ucDirectory.isDirectory()) {
            for (File f : ucDirectory.listFiles()) {
                if (!f.getName().contains(".txt"))
                    continue;
                List<String> ucTxtLineList = FileIOUtil.readFileByLine(f.getPath());
                Map<String, Integer> lineNumMap = getLineOfUC(ucTxtLineList);

                for (int i = 0; i < ucTxtLineList.size(); i++) {
                    String currentLine = ucTxtLineList.get(i);
                    if (i > lineNumMap.get("title") && i < lineNumMap.get("description")) {
                        splitProcessPart(currentLine, f.getName(), easyClinic.getProcessedLevel1ArtifactPath());
                    } else if (i > lineNumMap.get("description") && i < lineNumMap.get("precon")) {
                        splitProcessPart(currentLine, f.getName(), easyClinic.getProcessedLevel1ArtifactPath());
                    } else if (i > lineNumMap.get("precon") && i < lineNumMap.get("postcon")) {
                        splitProcessPart(currentLine, f.getName(), easyClinic.getProcessedLevel1ArtifactPath());
                    } else if (i > lineNumMap.get("postcon") && i < lineNumMap.get("subflow")) {
                        splitProcessPart(currentLine, f.getName(), easyClinic.getProcessedLevel1ArtifactPath());
                    } else if (i > lineNumMap.get("subflow") && i < lineNumMap.get("alterflow")) {
                        splitProcessPart(currentLine, f.getName(), easyClinic.getProcessedLevel1ArtifactPath());
                    } else if (i > lineNumMap.get("alterflow")) {
                        splitProcessPart(currentLine, f.getName(), easyClinic.getProcessedLevel1ArtifactPath());
                    }
                }
            }
        }
    }

    private static Map<String, Integer> getLineOfUC(List<String> ucTxtLineList) {
        Map<String, Integer> lineNumMap = new HashMap();
        for (int i = 0; i < ucTxtLineList.size(); i++) {
            String line = ucTxtLineList.get(i);
            if (line.contains("Use case:")) {
                lineNumMap.put("title", i);
            } else if (line.contains("Description:")) {
                lineNumMap.put("description", i);
            } else if (line.contains("Preconditions:")) {
                lineNumMap.put("precon", i);
            } else if (line.contains("Postconditions:")) {
                lineNumMap.put("postcon", i);
            } else if (line.contains("Sub-flows:")) {
                lineNumMap.put("subflow", i);
            } else if (line.contains("Alternative flows:")) {
                lineNumMap.put("alterflow", i);
                return lineNumMap;
            }
        }
        return null;
    }

    private static Map<String, Integer> getLineOfCC(List<String> ccTxtLineList) {
        Map<String, Integer> lineNumMap = new HashMap();
        for (int i = 0; i < ccTxtLineList.size(); i++) {
            String line = ccTxtLineList.get(i);
            if (line.contains("Class:")) {
                lineNumMap.put("class", i);
            } else if (line.contains("Date:")) {
                lineNumMap.put("date", i);
            } else if (line.contains("Version:")) {
                lineNumMap.put("version", i);
            } else if (line.contains("Description:")) {
                lineNumMap.put("description", i);
            } else if (line.contains("Attributes:")) {
                lineNumMap.put("attribute", i);
            } else if (line.contains("Methods:")) {
                lineNumMap.put("method", i);
                return lineNumMap;
            }
        }
        return null;
    }

    private static void splitProcessPart(String currentLine, String fileName, String processedTextDir) {
        if (currentLine.equals(""))
            return;
        TextPreprocess textPreprocess = new TextPreprocess(currentLine);
        String processedText = textPreprocess.doNlpFileProcess();
        FileIOUtil.continueWriteFile(processedText, processedTextDir + "/" + fileName);
    }

    private static void splitProcessCode(String currentLine, String fileName, String processedTextDir) {
        if (currentLine.equals(""))
            return;
        TextPreprocess textPreprocess = new TextPreprocess(currentLine);
        String processedText = textPreprocess.doJavaFileProcess();
        FileIOUtil.continueWriteFile(processedText, processedTextDir + "/" + fileName);
    }
}
