package edu.nd.dronology.services.extensions.areamapping.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.nd.dronology.services.extensions.areamapping.internal.RiverBank;

public class RiverSubsegment {
	private List<RiverBank> riverSubsegment;
	
	public RiverSubsegment() {
		riverSubsegment = new ArrayList<>();
	}
	
	public RiverSubsegment(RiverBank bank1, RiverBank bank2) {
		riverSubsegment = new ArrayList<>();
		riverSubsegment.add(bank1);
		riverSubsegment.add(bank2);
	}
	
	public void add(RiverBank riverBank) {
		riverSubsegment.add(riverBank);
	}
	
	public List<RiverBank> get(){
		return Collections.unmodifiableList(riverSubsegment);
	}
	
	public RiverBank get(int entry) {
		return riverSubsegment.get(entry);
	}
	
	public void set(int index, RiverBank entry) {
		riverSubsegment.set(index, entry);
	}
	
	
}
