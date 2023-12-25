package edu.nd.dronology.ui.vaadin.activeflights;

import edu.nd.dronology.core.vehicle.IUAVProxy;

/**
 * Wrapper class for UAV data.,<br>
 * Provides uav status information (such as location, speed, etc.) in a properly formatted, ui friendly format.
 * 
 * @author Michael Vierhauser
 *
 */
public class UAVStatusWrapper {
	public static final String STATUS_GREEN = "green";

	private final String batteryLife;
	private final String speed;

	private final String healthColor;
	private final String status;
	private final String name;

	private String latitude;
	private String longitude;
	private String altitude;

	public UAVStatusWrapper(double batteryLife, double speed, double lat, double lon, double alt, String healthColor,
			String status, String name) {
		this.batteryLife = String.format("%.2f", batteryLife);
		this.speed = String.format("%.2f", speed);

		this.latitude = String.format("%.5f", lat);
		this.longitude = String.format("%.5f", lon);
		this.altitude = String.format("%.2f", alt);

		this.healthColor = healthColor;
		this.status = status;
		this.name = name;
	}

	public UAVStatusWrapper(IUAVProxy e) {
		this(e.getBatteryLevel(), e.getVelocity(), e.getLatitude(), e.getLongitude(), e.getAltitude(), STATUS_GREEN,
				e.getStatus(), e.getID());
	}

	public String getSpeed() {
		return speed;
	}

	public String getBatteryLife() {
		return batteryLife;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getAltitude() {
		return altitude;
	}

	public String getHealthColor() {
		return healthColor;
	}

	public String getStatus() {
		return status;
	}

	public String getName() {
		return name;
	}
}