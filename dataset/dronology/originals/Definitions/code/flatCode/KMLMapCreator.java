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
import java.util.ArrayList;
import java.util.List;

import edu.nd.dronology.gstation.connector.messages.UAVMessageFactory;
import edu.nd.dronology.gstation.connector.messages.UAVStateMessage;

public class KMLMapCreator {

	private static final String UAVID = "IRIS2";
	private String name = "result";
	private String datapath = "d:\\kmlexport";

	public static void main(String[] args) {

		new KMLMapCreator().createKMLFile("d:\\log.log");

	}

	public void createKMLFile(String logfile) {

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

	private final String kmlstart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n";

	// private final String kmlelement = "\t<Placemark>\n" + "\t<name>WP%num</name>\n" + "\t<description>" + name
	// + "</description>\n" + "\t<Point>\n" + "\t\t<coordinates>%lon,%lat,%alt</coordinates>\n" + ""
	// + "\t<gx:TimeSpan>\n"
	// + "\t<begin>%time</begin>\n"
	// + "\t</gx:TimeSpan>\n"
	// + "\t</Point>\n"
	// + "\t</Placemark>\n";
	//

	private final String kmlelement = "\t<Placemark>\n" + "\t<name>WP%num</name>\n" + "\t<description> %desc"
			+ "</description>\n" + "\t<LineString>\n" + "<extrude>1</extrude>" + "<tessellate>1</tessellate>"
			+ "<altitudeMode>absolute</altitudeMode>" + "\t\t<coordinates>%coords</coordinates>\n" + "</LineString>"
			+ "\t</Placemark>\n";

	private String createKMLBody(List<UAVStateMessage> messages) {

		String kmlend = "</kml>";

		ArrayList<String> content = new ArrayList<String>();
		content.add(kmlstart);

		StringBuilder cordBuilder = new StringBuilder();
		for (UAVStateMessage m : messages) {
			String res = createKMLElement(m);
			if (res != null) {
				cordBuilder.append(res);
			}
		}

		String kmElement = kmlelement.replace("%coords", cordBuilder.toString());
		kmElement = kmElement.replace("%desc", UAVID);
		content.add(kmElement);
		content.add(kmlend);

		StringBuilder sb = new StringBuilder();
		content.forEach(str -> {
			sb.append(str);
			sb.append("\n");
		});
		return sb.toString();

	}

	private String createKMLElement(UAVStateMessage m) {

		if (!m.getUavid().equals(UAVID)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(m.getLocation().getLongitude());
		sb.append(",");
		sb.append(m.getLocation().getLatitude());
		sb.append(",");
		sb.append(m.getLocation().getAltitude());
		sb.append(",");

		return sb.toString();
	}
}
