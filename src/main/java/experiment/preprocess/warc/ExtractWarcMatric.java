package experiment.preprocess.warc;


import util.FileIOUtil;

import java.util.List;

public class ExtractWarcMatric {
    private static String FRS2SRSPath = "dataset/WARC/originals/FRStoSRS.txt";
    private static String NFR2SRSPath = "dataset/WARC/originals/NFRtoSRS.txt";

    public static void main(String[] args) {
        FileIOUtil.initDirectory("dataset/WARC/trace_matrices");

        String FRS2SRS = extractFRS2SRS(FRS2SRSPath);
        FileIOUtil.writeFile(FRS2SRS, "dataset/WARC/trace_matrices/FRS-SRS.txt");

        String NFR2SRS = extractNFR2SRS(NFR2SRSPath);
        FileIOUtil.writeFile(NFR2SRS, "dataset/WARC/trace_matrices/NFR-SRS.txt");
    }

    private static String extractFRS2SRS(String matricPath) {
        StringBuilder sb = new StringBuilder();
        List<String> lines = FileIOUtil.readFileByLine(matricPath);
        lines.stream().forEach(line -> {

            if (line.length() > 8) {
                String source = line.substring(0, 4);
                String targets = line.substring(9);
                if (targets.length() > 0) {
                    String[] arry = targets.split(" ");
                    for (int i = 0; i < arry.length; i++) {
                        String target = arry[i].split("\\.")[0];
                        sb.append(source + " " + target + "\n");
                    }
                }
            }
        });
        return sb.toString();
    }

    private static String extractNFR2SRS(String matricPath) {
        StringBuilder sb = new StringBuilder();
        List<String> lines = FileIOUtil.readFileByLine(matricPath);
        lines.stream().forEach(line -> {

            if (line.length() > 8) {
                String source = line.substring(0, 5);
                String targets = line.substring(9);
                if (targets.length() > 0) {
                    String[] arry = targets.split(" ");
                    for (int i = 0; i < arry.length; i++) {
                        String target = arry[i].split("\\.")[0].trim();
                        sb.append(source + " " + target + "\n");
                    }
                }
            }
        });
        return sb.toString();
    }
}
