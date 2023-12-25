package edu.nd.dronology.services.extensions.areamapping.internal;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class ImageWaypoints {
	
	private List<ImageWaypoint> imageWaypoints;
	
	
	public ImageWaypoints() {
		imageWaypoints = new Vector<ImageWaypoint>();
	}
	
	public void add(ImageWaypoint bankNode) {
		imageWaypoints.add(bankNode);
	}
	
	public void reverse() {
		Collections.reverse(imageWaypoints);
	}
	
	public List<ImageWaypoint> get(){
		return Collections.unmodifiableList(imageWaypoints);
	}
	
	public ImageWaypoint get(int entry){
		return imageWaypoints.get(entry);
	}
	
	public void set(int index, ImageWaypoint entry) {
		imageWaypoints.set(index, entry);
	}
	
	public int size() {
		return imageWaypoints.size();
	}


}
