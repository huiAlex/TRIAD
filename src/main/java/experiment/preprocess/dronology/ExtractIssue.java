package experiment.preprocess.dronology;

import experiment.preprocess.dronology.entity.Code;
import experiment.preprocess.dronology.entity.DroneArtifact;
import experiment.project.Dronology;
import util.FileIOUtil;
import util.SetMapUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ExtractIssue {
    private static Map<String, List<String>> dd2ManualCodeMap = new HashMap<>();
    private static Map<String, List<String>> dd2GitCodeMap = new HashMap<>();
    private static Map<String, List<String>> dd2taskMap = new HashMap<>();
    private static Map<String, List<String>> req2codeMap = new HashMap<>();
    private static Map<String, List<String>> req2ddMap = new HashMap<>();
    private static Map<String, List<String>> req2taskMap = new HashMap<>();
    private static Map<String, List<String>> task2codeMap = new HashMap<>();

    private static Map<String, String> reqMap = new HashMap<>();
    private static Map<String, String> ddMap = new HashMap<>();
    private static Map<String, String> taskMap = new HashMap<>();


    private static Set<String> closedReqSet = new HashSet<>();
    private static Set<String> closedDDSet = new HashSet<>();
    private static Set<String> closedTaskSet = new HashSet<>();
    private static Set<String> clsSet = new HashSet<>();
    private static Set<String> srcSet = new HashSet<>();

    private static Dronology dronology = new Dronology();


    public static void main(String[] args) throws IOException {
        initDirectory();
        getSrcSet();
        extractInitData();
        printInfo("init");
        filterData();
        printInfo("new");
        outputData();
    }

    private static void printInfo(String prefix) {
        System.out.println(prefix + "-req: " + reqMap.size());
        System.out.println(prefix + "-dd: " + ddMap.size());
        System.out.println(prefix + "-task: " + taskMap.size());
    }

    private static void outputData() {
        outputArtifact(reqMap, dronology.getUnprocessedLevel1ArtifactPath());
        outputArtifact(ddMap, dronology.getUnprocessedLevel2ArtifactPath());
        outputTraceMatrix(req2codeMap, dronology.getLevel1ToLevel3MatricPath());
        outputTraceMatrix(req2ddMap, dronology.getLevel1ToLevel2MatricPath());
        outputCodeTraceWithSuffix(dd2ManualCodeMap, dd2GitCodeMap, dronology.getLevel2ToLevel3MatricPath());
        outputClsFile(clsSet);
    }

    private static void outputClsFile(Set<String> clsSet) {
        String srcPath = "dataset/dronology/unprocessed/code/src";
        File src = new File(srcPath);
        for (File cls : src.listFiles()) {
            String clsName = cls.getName().split("\\.")[0];
            if (clsSet.contains(clsName)) {
                FileIOUtil.writeFile(FileIOUtil.readFile(cls.getPath()), dronology.getUnprocessedLevel3ArtifactPath() + "/" + clsName + ".java");
            }
        }
    }

    private static void outputArtifact(Map<String, String> map, String outputPath) {
        for (String name : map.keySet()) {
            FileIOUtil.writeFile(map.get(name), outputPath + "/" + name + ".txt");
        }
    }

    private static void outputTraceMatrix(Map<String, List<String>> traceMap, String outputPath) {
        traceMap = SetMapUtil.sortMapListByKey(traceMap);
        StringBuilder sb = new StringBuilder();
        for (String source : traceMap.keySet()) {
            for (String target : traceMap.get(source)) {
                sb.append(source + " " + target + "\n");
            }
        }
        FileIOUtil.writeFile(sb.toString(), outputPath);
    }

    private static void outputCodeTraceWithSuffix(Map<String, List<String>> manualTraceMap,
                                                  Map<String, List<String>> gitTraceMap,
                                                  String outputPath) {
        ddMap = SetMapUtil.sortMapByKey(ddMap);
        StringBuilder sb = new StringBuilder();
        for (String dd : ddMap.keySet()) {
            if (manualTraceMap.containsKey(dd)) {
                for (String cls : manualTraceMap.get(dd)) {
                    sb.append(dd + " " + cls + " 1\n");
                }
            }
            if (gitTraceMap.containsKey(dd)) {
                for (String cls : gitTraceMap.get(dd)) {
                    sb.append(dd + " " + cls + " 0\n");
                }
            }
        }
        FileIOUtil.writeFile(sb.toString(), outputPath);
    }

    private static void filterData() {
        filterTask();
        filterDD();
        filterReq();
    }

    private static void filterTask() {
        Iterator<String> taskIt = taskMap.keySet().iterator();
        while (taskIt.hasNext()) {
            String task = taskIt.next();
            Iterator<String> it = task2codeMap.get(task).iterator();
            while (it.hasNext()) {
                String cls = it.next();
                if (!srcSet.contains(cls))
                    it.remove();
            }
            if (task2codeMap.get(task).size() == 0) {

                taskIt.remove();
                task2codeMap.remove(task);
            } else {
                clsSet.addAll(task2codeMap.get(task));
            }
        }
    }

    private static void filterDD() {

        Iterator<String> ddIt = ddMap.keySet().iterator();
        while (ddIt.hasNext()) {
            boolean noTask = false;
            boolean noManualCode = false;
            boolean noGitCode = false;
            String dd = ddIt.next();

            if (dd2taskMap.get(dd) != null) {
                Iterator<String> it = dd2taskMap.get(dd).iterator();
                while (it.hasNext()) {
                    String task = it.next();
                    if (!taskMap.keySet().contains(task))
                        it.remove();
                }
                if (dd2taskMap.get(dd).size() == 0)
                    noTask = true;
            } else {
                noTask = true;
            }

            if (dd2ManualCodeMap.get(dd) != null) {
                Iterator<String> it = dd2ManualCodeMap.get(dd).iterator();
                while (it.hasNext()) {
                    String cls = it.next();
                    if (!srcSet.contains(cls))
                        it.remove();
                }
                if (dd2ManualCodeMap.get(dd).size() == 0)
                    noManualCode = true;
            } else {
                noManualCode = true;
            }

            if (dd2GitCodeMap.get(dd) != null) {
                Iterator<String> it = dd2GitCodeMap.get(dd).iterator();
                while (it.hasNext()) {
                    String cls = it.next();
                    if (!srcSet.contains(cls))
                        it.remove();
                }
                if (dd2GitCodeMap.get(dd).size() == 0)
                    noGitCode = true;
            } else {
                noGitCode = true;
            }

            if (noTask && noGitCode && noManualCode) {
                System.out.println(dd + ":" + noTask + " " + noManualCode + " " + noGitCode);
                ddIt.remove();
                dd2taskMap.remove(dd);
                dd2ManualCodeMap.remove(dd);
                dd2GitCodeMap.remove(dd);
            } else {
                clsSet.addAll(dd2ManualCodeMap.get(dd));
                clsSet.addAll(dd2GitCodeMap.get(dd));
            }
        }
    }

    private static void filterReq() {
        Iterator<String> reqIt = reqMap.keySet().iterator();
        while (reqIt.hasNext()) {
            String req = reqIt.next();
            Iterator<String> it = req2ddMap.get(req).iterator();
            while (it.hasNext()) {
                String dd = it.next();
                if (!ddMap.keySet().contains(dd))
                    it.remove();
            }
            if (req2ddMap.get(req).size() == 0) {
                reqIt.remove();
                req2ddMap.remove(req);
                req2codeMap.remove(req);
            } else {
                Set<String> codeSet = new HashSet<>();
                Set<String> taskSet = new HashSet<>();
                for (String dd : req2ddMap.get(req)) {
                    if (dd2GitCodeMap.containsKey(dd))
                        codeSet.addAll(dd2GitCodeMap.get(dd));
                    if (dd2ManualCodeMap.containsKey(dd))
                        codeSet.addAll(dd2ManualCodeMap.get(dd));
                    if (dd2taskMap.containsKey(dd))
                        taskSet.addAll(dd2taskMap.get(dd));
                }
                req2codeMap.put(req, new ArrayList<>(codeSet));
                req2taskMap.put(req, new ArrayList<>(taskSet));
            }
        }
    }


    private static void initDirectory() {
        FileIOUtil.initDirectory(dronology.getUnprocessedDdPath());
        FileIOUtil.initDirectory(dronology.getUnprocessedReqPath());
    }

    public static void getSrcSet() {
        String srcPath = "dataset/dronology/unprocessed/code/src";
        File src = new File(srcPath);
        for (File cls : src.listFiles()) {
            String clsName = cls.getName().split("\\.")[0];
            srcSet.add(clsName);
        }
    }


    public static void extractInitData() throws IOException {
        String jsonFilePath = "dataset/dronology/originals/Dronology.json";
        // read dronology artifact entity from json file
        List<DroneArtifact> droneArtifactList = ReadJsonData.readDroneArtifactFromJSON(jsonFilePath);
        for (int i = 0; i < droneArtifactList.size(); i++) {
            DroneArtifact droneArtifact = droneArtifactList.get(i);
            String issueType = droneArtifact.getIssueType();
            String issueId = droneArtifact.getIssueId();
            Set<String> childrenSet = null;
            Set<Code> codeSet = new HashSet<>();
            String summary, description = null;

            if (droneArtifact.getStatus().equals("Closed")) {
                if (issueType.equals("Requirement") || issueId.contains("RE-")) {// requirement
                    closedReqSet.add(issueId);
                    summary = droneArtifact.getSummary();
                    description = droneArtifact.getDescription();
                    String text = "[SUMMARY]\n" + summary + "\n[DESCRIPTION]\n" + description;
                    reqMap.put(issueId, text);
                    // traced Req/DD
                    req2ddMap.put(issueId, new ArrayList<>());
                    childrenSet = droneArtifact.getChildrenSet();
                    if (childrenSet != null) {
                        for (String child : childrenSet) {
                            if (child.contains("DD-")) {
                                req2ddMap.get(issueId).add(child);
                            }
                        }
                    }
                    req2ddMap = SetMapUtil.sortMapListByKey(req2ddMap);

                } else if (issueType.equals("Design Definition") || issueId.contains("DD-")) { // design definition
                    closedDDSet.add(issueId);
                    summary = droneArtifact.getSummary();
                    description = droneArtifact.getDescription();
                    String text = "[SUMMARY]\n" + summary + "\n[DESCRIPTION]\n" + description;
                    ddMap.put(issueId, text);

                    // traced DD/Task
                    dd2taskMap.put(issueId, new ArrayList<>());
                    childrenSet = droneArtifact.getChildrenSet();
                    if (childrenSet != null) {
                        for (String child : childrenSet) {
                            if (child.contains("ST")) {
                                dd2taskMap.get(issueId).add(child);
                            }
                        }
                    }
                    dd2taskMap = SetMapUtil.sortMapListByKey(dd2taskMap);

                    // traced codes
                    dd2GitCodeMap.put(issueId, new ArrayList<>());
                    dd2ManualCodeMap.put(issueId, new ArrayList<>());
                    codeSet = droneArtifact.getCodeSet();
                    if (codeSet != null) {
                        for (Code code : codeSet) {
                            String name = code.getCodeName();
                            if (name.endsWith(".py"))
                                continue;
                            name = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."));

                            String status = code.getStatus();
                            if (status.equals("#manual-tagged")) {
                                dd2ManualCodeMap.get(issueId).add(name);
                            } else if (status.equals("#github")) {
                                dd2GitCodeMap.get(issueId).add(name);
                            }
                        }
                        for (String cls : dd2ManualCodeMap.get(issueId)) { // 删除已人工标注
                            if (dd2GitCodeMap.get(issueId).contains(cls))
                                dd2GitCodeMap.get(issueId).remove(cls);
                        }
                    }
                    dd2GitCodeMap = SetMapUtil.sortMapListByKey(dd2GitCodeMap);
                    dd2ManualCodeMap = SetMapUtil.sortMapListByKey(dd2ManualCodeMap);

                } else if (issueType.equals("Sub-task") || issueId.contains("ST-")) {// task
                    closedTaskSet.add(issueId);
                    summary = droneArtifact.getSummary();
                    description = droneArtifact.getDescription();
                    String text = "[SUMMARY]\n" + summary + "\n[DESCRIPTION]\n" + description;
                    taskMap.put(issueId, text);

                    task2codeMap.put(issueId, new ArrayList<>());
                    codeSet = droneArtifact.getCodeSet();
                    if (codeSet != null) {
                        for (Code code : codeSet) {
                            String name = code.getCodeName();
                            name = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."));
                            if (name.endsWith(".py"))
                                continue;
                            task2codeMap.get(issueId).add(name);
                        }
                    }
                    task2codeMap = SetMapUtil.sortMapListByKey(task2codeMap);
                }
            } else {
                continue;
            }
        } // for
    }

}
