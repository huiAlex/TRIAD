package experiment.preprocess.parsecode.c;

import experiment.preprocess.util.TextPreprocess;
import experiment.project.LibEST;
import experiment.project.Project;
import util.FileIOUtil;

import java.util.List;
import java.util.Map;

public class ParseCCode {

    public static ParseCCodeXml parseCodeXml;

    private static String processPrefix;
    private static String unprocessPrefix;

    private static String projectPath;
    private static String commentDir;
    private static String codeType;


    private static String methodNameDir;
    private static String paramNameDir;
    private static String paramTypeDir;
    private static String invokeMethodDir;
    private static String codeFileNameDir;
    private static String fieldNameDir;
    private static String codeDir;

    public ParseCCode(Project project, String codeType) {
        this.parseCodeXml = new ParseCCodeXml(project, codeType);
        this.projectPath = project.getProjectPath();
        this.codeType = codeType;
        this.unprocessPrefix = projectPath + "/unprocessed/" + codeType + "/";
        this.processPrefix = projectPath + "/processed/" + codeType + "/";
        this.codeFileNameDir = "code_file_name";
        this.fieldNameDir = "field_name";
        this.commentDir = "comment";
        this.methodNameDir = "method_name";
        this.paramNameDir = "param/param_name";
        this.paramTypeDir = "param/param_type";
        this.invokeMethodDir = "invoke_method";
        this.codeDir = "code";
    }

    public void run() {
        parseCodeXml.run();
        initDir();
        processCodeElement(parseCodeXml.getCodeFileNameMap(), codeFileNameDir);
        processCodeElement(parseCodeXml.getCodeMethodMap(), methodNameDir);
        processCodeElement(parseCodeXml.getCodeInvokeMethodMap(), invokeMethodDir);
        processCodeElement(parseCodeXml.getCodeParamNameMap(), paramNameDir);
        processCodeElement(parseCodeXml.getCodeParamTypeMap(), paramTypeDir);
        processCodeElement(parseCodeXml.getCodeFieldMap(), fieldNameDir);
        processComment();
    }


    private static void processCodeElement(Map<String, List<String>> codeElementMap, String codeElementDir) {
        for (String codeName : codeElementMap.keySet()) {
            StringBuilder unprocessedSb = new StringBuilder();
            StringBuilder processedSb = new StringBuilder();
            List<String> elementList = codeElementMap.get(codeName);
            if (elementList == null || elementList.size() == 0) continue;
            for (String codeElement : elementList) {
                unprocessedSb.append(codeElement + "\n");
                TextPreprocess preprocess = new TextPreprocess(codeElement);
                processedSb.append(preprocess.doCFileProcess() + "\n");
            }
            FileIOUtil.writeFile(unprocessedSb.toString(), unprocessPrefix + codeElementDir + "/" + codeName + ".txt");
            FileIOUtil.writeFile(processedSb.toString(), processPrefix + codeElementDir + "/" + codeName + ".txt");
            System.out.println(unprocessPrefix + codeElementDir + "/" + codeName + ".txt");

            FileIOUtil.continueWriteFile(unprocessedSb.toString(), unprocessPrefix + codeDir + "/" + codeName + ".txt");
            FileIOUtil.continueWriteFile(processedSb.toString(), processPrefix + codeDir + "/" + codeName + ".txt");
        }
    }

    private static void processComment() {
        Map<String, List<String>> codeCommentMap = parseCodeXml.getCodeCommentMap();
        for (String codeName : codeCommentMap.keySet()) {
            StringBuilder unprocessedSb = new StringBuilder();
            StringBuilder processedSb = new StringBuilder();
            List<String> commentList = codeCommentMap.get(codeName);
            if (commentList == null || commentList.size() == 0) continue;
            for (String comment : commentList) {
                comment = comment.replaceAll("\n", " ")
                        .replaceAll("/\\*", "")
                        .replaceAll("/\\*\\*", "")
                        .replaceAll("\\*", "")
                        .replaceAll("@[a-zA-z]*", "");
                unprocessedSb.append(comment + "\n");
                TextPreprocess preprocess = new TextPreprocess(comment);
                processedSb.append(preprocess.doCFileProcess() + "\n");
            }
            FileIOUtil.writeFile(unprocessedSb.toString(), unprocessPrefix + commentDir + "/" + codeName + ".txt");
            FileIOUtil.writeFile(processedSb.toString(), processPrefix + commentDir + "/" + codeName + ".txt");

            FileIOUtil.continueWriteFile(unprocessedSb.toString(), unprocessPrefix + codeDir + "/" + codeName + ".txt");
            FileIOUtil.continueWriteFile(processedSb.toString(), processPrefix + codeDir + "/" + codeName + ".txt");
        }
    }

    private static void initDir() {
        FileIOUtil.initDirectory(unprocessPrefix + codeFileNameDir);
        FileIOUtil.initDirectory(unprocessPrefix + methodNameDir);
        FileIOUtil.initDirectory(unprocessPrefix + invokeMethodDir);
        FileIOUtil.initDirectory(unprocessPrefix + paramNameDir);
        FileIOUtil.initDirectory(unprocessPrefix + paramTypeDir);
        FileIOUtil.initDirectory(unprocessPrefix + fieldNameDir);

        FileIOUtil.initDirectory(processPrefix + codeFileNameDir);
        FileIOUtil.initDirectory(processPrefix + methodNameDir);
        FileIOUtil.initDirectory(processPrefix + invokeMethodDir);
        FileIOUtil.initDirectory(processPrefix + fieldNameDir);
        FileIOUtil.initDirectory(processPrefix + paramNameDir);
        FileIOUtil.initDirectory(processPrefix + paramTypeDir);

        FileIOUtil.initDirectory(unprocessPrefix + commentDir);
        FileIOUtil.initDirectory(processPrefix + commentDir);

        FileIOUtil.initDirectory(unprocessPrefix + codeDir);
        FileIOUtil.initDirectory(processPrefix + codeDir);
    }


}
