package experiment.preprocess.dronology;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import experiment.preprocess.dronology.entity.Code;
import experiment.preprocess.dronology.entity.DroneArtifact;
import util.FileIOUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReadJsonData {

    public static List<DroneArtifact> readDroneArtifactFromJSON (String jsonFilePath) throws IOException{
        List<DroneArtifact> droneArtifactsList = new ArrayList<>();

        String jsonText = FileIOUtil.readFile(jsonFilePath);
        JSONObject jsonObject = JSON.parseObject(jsonText);
        JSONArray jsonArray = jsonObject.getJSONArray("entries");
        for (int i = 0; i < jsonArray.size(); i++) {
            DroneArtifact droneArtifact = new DroneArtifact();

            JSONObject issueJsonObject = jsonArray.getJSONObject(i);
            String issueId = issueJsonObject.getString("issueid");
            JSONObject attributes = issueJsonObject.getJSONObject("attributes");
            String issueType = attributes.getString("issuetype");
            String status = attributes.getString("status");
            String summary = attributes.getString("summary");
            String description = attributes.getString("description");
            JSONArray codeArray = issueJsonObject.getJSONArray("code");
            String codeStatus = null;
            String codeName = null;
            String timestamp = null;
            Set<Code> codeSet = new HashSet<>();
            for (int j = 0; j < codeArray.size(); j++) {
                JSONObject codeObject = codeArray.getJSONObject(j);
                codeStatus = codeObject.getString("status");
                codeName = codeObject.getString("filename");
                timestamp = codeObject.getString("timestamp");
                Code code = new Code();
                code.setCodeName(codeName);
                code.setStatus(codeStatus);
                code.setTimestamp(timestamp);
                codeSet.add(code);
            }

            JSONObject childrenObject = issueJsonObject.getJSONObject("children");
            Set<String> set = new HashSet<>();
            childrenObject.keySet().stream().forEach(type -> {

                if (type != null) {
                    childrenObject.getJSONArray(type).stream().forEach(s -> {
                        set.add(s.toString());
                    });
                }
                droneArtifact.setChildrenType(type);
            });

            droneArtifact.setChildrenSet(set);
            droneArtifact.setIssueId(issueId);
            droneArtifact.setIssueType(issueType);
            droneArtifact.setStatus(status);
            droneArtifact.setSummary(summary);
            droneArtifact.setDescription(description);
            droneArtifact.setCodeSet(codeSet);

            droneArtifactsList.add(droneArtifact);
        }

        return droneArtifactsList;
    }

}
