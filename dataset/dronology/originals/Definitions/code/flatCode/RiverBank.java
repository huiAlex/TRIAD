package edu.nd.dronology.services.extensions.areamapping.internal;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.Vector;


public class RiverBank {
	private List<Point2D.Double> riverBank;
	
	public RiverBank() {
		riverBank = new Vector<Point2D.Double>();
	}
	
	public void add(Point2D.Double bankNode) {
		riverBank.add(bankNode);
	}
	
	public void add(int entry, Point2D.Double bankNode) {
		riverBank.add(entry,bankNode);
	}
	
	public void reverse() {
		Collections.reverse(riverBank);
	}
	
	public List<Point2D.Double> get(){
		return Collections.unmodifiableList(riverBank);
	}
	
	public Point2D.Double get(int entry){
		return riverBank.get(entry);
	}
	
	public int size() {
		return riverBank.size();
	}
}
