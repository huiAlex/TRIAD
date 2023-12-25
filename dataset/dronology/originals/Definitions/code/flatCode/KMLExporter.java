package edu.nd.dronology.misc.kml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import edu.nd.dronology.core.util.FormatUtil;
import edu.nd.dronology.gstation.connector.messages.UAVMessageFactory;
import edu.nd.dronology.gstation.connector.messages.UAVStateMessage;
import edu.nd.dronology.util.FileUtil;

public class KMLExporter {

	private static final String UAVID = "IRIS2";
	private String name = "test.kml";
	private String datapath = "d:\\kmlexport";
	private String rootTemplate;
	private String pointTemplate;

	public static void main(String[] args) {

		new KMLExporter().createKMLFile("d:\\log.log");

	}

	public void createKMLFile(String logfile) {

		rootTemplate = FileUtil.readFile("./template/track.txt");
		pointTemplate = FileUtil.readFile("./template/trackpoint.txt");

		// System.out.println(pointTemplate);

		List<UAVStateMessage> messages = readMessages(logfile);

		String kmlString = createKMLBody(messages);

		writeToFile(kmlString);
	}

	private List<UAVStateMessage> readMessages(String logfile) {
		List<UAVStateMessage> messageList = new ArrayList<>();
		try {

			List<String> lines = Files.readAllLines(Paths.get(logfile));

			lines.forEach(l -> messageList.add(parseLine(l)));
			return messageList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messageList;
	}

	private UAVStateMessage parseLine(String l) {
		int start = l.indexOf("{");
		String msgText = l.substring(start);

		UAVStateMessage msg;
		try {
			msg = (UAVStateMessage) UAVMessageFactory.create(msgText);
			return msg;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private void writeToFile(String kmlString) {
		File testexists = new File(datapath + "/" + name + ".kml");
		Writer fwriter;

		if (!testexists.exists()) {
			try {

				fwriter = new FileWriter(datapath + "/" + name + ".kml");
				fwriter.write(kmlString);
				fwriter.flush();
				fwriter.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		else {

			// schleifenvariable
			String filecontent = "";

			ArrayList<String> newoutput = new ArrayList<String>();
			;

			try {
				BufferedReader in = new BufferedReader(new FileReader(testexists));
				while ((filecontent = in.readLine()) != null)

					newoutput.add(filecontent);

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// newoutput.add(2, kmlelement);

			String rewrite = "";
			for (String s : newoutput) {
				rewrite += s;
			}

			try {
				fwriter = new FileWriter(datapath + "/" + name + ".kml");
				fwriter.write(rewrite);
				fwriter.flush();
				fwriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private String createKMLBody(List<UAVStateMessage> messages) {

		String tqFile = new String(rootTemplate);

		StringBuilder cordBuilder = new StringBuilder();
		for (UAVStateMessage m : messages) {
			String res = createKMLElement(m);
			if (res != null) {
				cordBuilder.append(res);
			}
		}

		tqFile = tqFile.replace("%points", cordBuilder.toString());

		// StringBuilder sb = new StringBuilder();
		// content.forEach(str -> {
		// sb.append(str);
		// sb.append("\n");
		// });
		return tqFile;

	}

	private String createKMLElement(UAVStateMessage m) {

		// DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		// df.setTimeZone(TimeZone.getTimeZone("Zulu"));
		String ts = FormatUtil.formatTimestamp(m.getTimestamp(), "yyyy-MM-dd'T'HH:mm:ss'Z'");

		if (!m.getUavid().equals(UAVID)) {
			return null;
		}

		String tp = new String(pointTemplate);

		tp = tp.replace("%lat", Double.toString(m.getLocation().getLatitude()));
		tp = tp.replace("%lon", Double.toString(m.getLocation().getLongitude()));
		tp = tp.replace("%alt", Double.toString(m.getLocation().getAltitude()));
		tp = tp.replace("%time", ts);

		return tp + "\n";
	}
}
