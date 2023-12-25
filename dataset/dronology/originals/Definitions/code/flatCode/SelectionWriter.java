package edu.nd.dronology.services.extensions.areamapping.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.nd.dronology.core.util.FormatUtil;
import edu.nd.dronology.services.core.areamapping.ExportAllocationInformation;
import edu.nd.dronology.services.core.areamapping.ExportDrone;
import edu.nd.dronology.services.core.areamapping.MetricsStatistics;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.persistence.internal.FlightRouteXStreamPersistor;
import edu.nd.dronology.services.extensions.areamapping.selection.RouteSelectionResult;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class SelectionWriter {

	private static final ILogger LOGGER = LoggerProvider.getLogger(SelectionWriter.class);

	private static final String WRITE_LOCATION = "/home/michael/mapping/";

	public static final transient Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(DateFormat.LONG).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).setVersion(1.0)
			.serializeSpecialFloatingPointValues().serializeSpecialFloatingPointValues().create();

	private static final String SEPARATOR = ",";
	private FileOutputStream dest;
	private ZipOutputStream out;

	public void writeRouteSelection(RouteSelectionResult selection) {

		long runid = System.currentTimeMillis();
		String format = FormatUtil.formatTimestamp(runid, FormatUtil.FORMAT_FILE);
		if (Files.notExists(Paths.get(WRITE_LOCATION))) {
			new File(WRITE_LOCATION).mkdirs();
		}

		writeStatFile(WRITE_LOCATION + "" + format + ".csv", selection);
		writeJSONFile(WRITE_LOCATION + "" + format + ".txt", selection);

		createNewZipFile(WRITE_LOCATION + "" + format + ".zip");

		zipRoutes(selection);

	}

	private void writeJSONFile(String filename, RouteSelectionResult selection) {

		List<ExportAllocationInformation> allocations = selection.getEportAllocationInformation();
		List<WriteAllocation> writeAllocations = new ArrayList<>();
		for (ExportAllocationInformation info : allocations) {
			WriteAllocation alloc = new WriteAllocation();
			for (ExportDrone dro : info.getDroneAllocations()) {
				WriteData wd = new WriteData(dro.getUAVId());
				dro.getDroneRouteAssignment().forEach(route -> {
					wd.addRoute(route.getName());
				});
				alloc.addAssignment(wd);
			}
			writeAllocations.add(alloc);
		}

		String jsonString = GSON.toJson(writeAllocations);
		try {
			FileUtils.writeStringToFile(new File(filename), jsonString);
		} catch (IOException e) {
			LOGGER.error(e);
		}

	}

	private void zipRoutes(RouteSelectionResult selection) {
		HashMap<String, IFlightRoute> routes = new HashMap<>();

		for (ExportAllocationInformation alloc : selection.getEportAllocationInformation()) {
			for (ExportDrone a : alloc.getDroneAllocations()) {
				for (IFlightRoute r : a.getDroneRouteAssignment()) {
					routes.put(r.getName(), r);
				}
			}
		}

		LOGGER.info("UNIQUE ROUTES:" + routes.size());
		routes.forEach((name, route) -> {
			zipRoute(route);
		});
		try {
			out.flush();
			out.finish();
			out.close();
		} catch (IOException e) {
			LOGGER.error(e);
		}

	}

	FlightRouteXStreamPersistor provider = new FlightRouteXStreamPersistor();

	private void zipRoute(IFlightRoute route) {
		try {
			String filename = route.getName().replace(" ", "_");

			LOGGER.info("new zip entry:" + filename + ".froute");
			ZipEntry entry = new ZipEntry(filename + ".froute");
			out.putNextEntry(entry);
			provider.save(route, out, false);
			out.closeEntry();

		} catch (PersistenceException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean createNewZipFile(final String location) {
		dest = null;
		out = null;

		try {
			dest = new FileOutputStream(location);
			out = new ZipOutputStream(new BufferedOutputStream(dest));
			out.setMethod(ZipOutputStream.DEFLATED);
			return true;
		} catch (IOException e) {
			LOGGER.error("Error adding file", e);
		} finally {

			// if (out != null) {
			// try {
			// out.close();
			// } catch (IOException e) {
			// LOGGER.error(e);
			// }
			// }
			// if (dest != null) {
			// try {
			// dest.close();
			// } catch (IOException e) {
			// LOGGER.error(e);
			// }
			// }
		}
		return false;
	}

	private void writeStatFile(String filename, RouteSelectionResult selection) {
		List<String> lines = new ArrayList<>();

		StringBuilder sb = new StringBuilder();

		sb.append("time");
		sb.append(SEPARATOR);
		sb.append("overall");
		sb.append(SEPARATOR);
		sb.append("battery");
		sb.append(SEPARATOR);
		sb.append("collision");
		sb.append(SEPARATOR);
		sb.append("coverage-score");
		sb.append(SEPARATOR);
		sb.append("taskequality");
		sb.append(SEPARATOR);
		sb.append("allocation-priority");
		sb.append(SEPARATOR);
		sb.append("allocation-score");
		sb.append(SEPARATOR);
		sb.append("total-distance");
		sb.append(SEPARATOR);
		sb.append("USDS-ratio");
		sb.append(SEPARATOR);
		sb.append("drone-distances");

		lines.add(sb.toString());

		for (ExportAllocationInformation allocation : selection.getEportAllocationInformation()) {

			MetricsStatistics stat = allocation.getMetricStatistics();
			double overall = stat.getAllocationScore();
			boolean battery = stat.getBatteryFailed();
			int collision = stat.getCollisions();
			double dsus = stat.getDownstreamToUpstreamRatio();
			double cover = stat.getAllocationCoverage();
			double equality = stat.getEqualityOfTasks();

			double allocationPrority = stat.getAllocationPriorityCoverage();
			double allocationScope = stat.getAllocationScore();
			double totalDistance = stat.getTotalDistance();
			List<Double> distances = stat.getDroneDistances();

			sb = new StringBuilder();

			double time = selection.getSelectionTime();
			sb.append(time);
			sb.append(SEPARATOR);
			sb.append(overall);
			sb.append(SEPARATOR);
			sb.append(battery);
			sb.append(SEPARATOR);
			sb.append(collision);
			sb.append(SEPARATOR);
			sb.append(cover);
			sb.append(SEPARATOR);
			sb.append(equality);
			sb.append(SEPARATOR);
			sb.append(allocationPrority);
			sb.append(SEPARATOR);
			sb.append(allocationScope);
			sb.append(SEPARATOR);
			sb.append(totalDistance);
			sb.append(SEPARATOR);
			sb.append(dsus);
			sb.append(SEPARATOR);
			sb.append(distances.stream().map(i -> i.toString()).collect(Collectors.joining(";")));
			lines.add(sb.toString());

		}

		try {
			Files.write(Paths.get(filename), lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			LOGGER.error(e);
		}

	}

	public class WriteData implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final String uavId;
		private List<String> routes = new ArrayList<>();

		public WriteData(String uavId) {
			this.uavId = uavId;
		}

		public void addRoute(String routeid) {
			routes.add(routeid);
		}

	}

	public class WriteAllocation implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private List<WriteData> assignments = new ArrayList<>();

		public void addAssignment(WriteData ass) {
			assignments.add(ass);
		}
	}

}
