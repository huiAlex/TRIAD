package edu.nd.dronology.ui.vaadin.areamapping;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolygon;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LeafletMouseOutEvent;
import org.vaadin.addon.leaflet.LeafletMouseOutListener;
import org.vaadin.addon.leaflet.LeafletMouseOverEvent;
import org.vaadin.addon.leaflet.LeafletMouseOverListener;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.services.core.items.IAreaMapping;
import edu.nd.dronology.services.core.items.PriorityArea;
import edu.nd.dronology.services.core.items.PrioritySide;
import edu.nd.dronology.services.core.persistence.AreaMappingPersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.remote.IAreaMappingRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.areamapping.mapoperations.AMMapAddMarkerListener;
import edu.nd.dronology.ui.vaadin.areamapping.mapoperations.AMMarkerDragEndListener;
import edu.nd.dronology.ui.vaadin.areamapping.mapoperations.AMMarkerMouseOutListener;
import edu.nd.dronology.ui.vaadin.areamapping.mapoperations.AMMarkerMouseOverListener;
import edu.nd.dronology.ui.vaadin.areamapping.mapoperations.AMPolylineClickListener;
import edu.nd.dronology.ui.vaadin.areamapping.mapoperations.AMPriorityAreaMouseOverListener;
import edu.nd.dronology.ui.vaadin.areamapping.mapoperations.AMPriorityDragEndListener;
import edu.nd.dronology.ui.vaadin.areamapping.mapoperations.AMPriorityMouseOutListener;
import edu.nd.dronology.ui.vaadin.areamapping.mapoperations.AMPrioritySideMouseOverListener;
import edu.nd.dronology.ui.vaadin.areamapping.mapoperations.AMRightClickListener;
import edu.nd.dronology.ui.vaadin.areamapping.mapoperations.AMUpstreamClickListener;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMPriorityInfoWindow;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/**
 * Adapted from the MapMarkerUtilities class in the utils folder, this class has listeners that allow the user to interact with the map.
 * Also includes many functions for manipulating and updating a current mapping
 * @author Andrew Slavin
 *
 */

public class AMMapMarkerUtilities {	
	private LMap map;
	private AMMapComponent mapComponent;
	 
	private AMMapAddMarkerListener mapAddMarkerListener;
	private AMMarkerMouseOverListener markerMouseOverListener;
	private AMMarkerMouseOutListener markerMouseOutListener;
	private AMMarkerDragEndListener markerDragEndListener;
	private AMPolylineClickListener polylineClickListener;
	private AMRightClickListener rightClickListener;
	
	private AMPriorityInfoWindow priorityInfoWindow;
	
	private LinkedList<Point> polygonVertices = new LinkedList<>();
	private Vector<PriorityArea> allPriorityAreas = new Vector<>();
	private Vector<List<UIEdgePoint>> sides;
	private LPolyline mousedOverLine;
	private PriorityArea mousedOverArea;
	private PrioritySide mousedOverSide;
	private PriorityArea areaToAdd;
	private LinkedList<LMarker> newPolygonPins = new LinkedList<>();
	private LinkedList<LMarker> allPolygonPins = new LinkedList<>();
	private LinkedList<Point> allPolygonPinsCopy = new LinkedList<>();
	private LinkedList<LPolyline> newPolygonLines = new LinkedList<>();
	private LinkedList<LPolyline> newPrioritySideLines = new LinkedList<>();
	private LinkedList<LPolyline> allPriorityLines = new LinkedList<>(); // all red lines on the map
	private LinkedList<PrioritySide> allPrioritySides = new LinkedList<>();
	private LinkedList<LPolyline> removedPriorityLines = new LinkedList<>();
	private LinkedList<LPolyline> allPolylines = new LinkedList<>();
	private LinkedList<UIEdgePoint> allSideWaypoints = new LinkedList<>();
	
	private Boolean upstream; // true means that upstream is the direction the points are drawn; false is opposite
	private boolean sidesAreEditable = false;
	private boolean prioritiesAreEditable = false;
	
	public AMMapMarkerUtilities(AMMapComponent mapComponent) {
		this.mapComponent = mapComponent;
		this.map = mapComponent.getMap();

		// add listeners
		this.mapAddMarkerListener = new AMMapAddMarkerListener(this);
		this.markerMouseOverListener = new AMMarkerMouseOverListener(this);
		this.markerMouseOutListener = new AMMarkerMouseOutListener(this);
		this.markerDragEndListener = new AMMarkerDragEndListener(this);
		this.polylineClickListener = new AMPolylineClickListener(this);	
		this.rightClickListener = new AMRightClickListener(this);
		
		map.addClickListener(mapAddMarkerListener);
		// rightClickListener only knows that the map is being clicked, but uses info about which polyline is moused over
		map.addContextClickListener(rightClickListener);
	}
	
	public AMMapMarkerUtilities(LMap map) {
		this.map = map;
	}
	
	// adds a new pin at a specified point and at a certain index in the list of waypoints (index is relevant when adding a waypoint between two other waypoints)
	// -1 signals that a waypoint was added to the end
	public LMarker addNewPin(Point point, int index) { 
		if (getPins().size() == 0 && mapComponent.getEditSidesController().getSideA()) {
			Notification.show("Starting with Side A", Notification.Type.ERROR_MESSAGE);
			mapComponent.getEditSidesController().setSideA();
		}
		if (index > this.getPins().size())
			index = this.getPins().size();
		// Creates a waypoint at the given point, and assigns it a random id.
		UIEdgePoint p = new UIEdgePoint(point, false);
		// set side
		if (mapComponent.getEditSidesController().getSideA())
			p.setSide(1);
		else
			p.setSide(0);
		return addNewPin(p, index);
	}
	
	public LMarker addNewPin(UIEdgePoint p, int index) {
		if (index > this.getPins().size()) {
			index = this.getPins().size();
		}
		
		// Assign the order
		if (index == -1) {
			p.setOrder(this.getPins().size());
		} else {
			p.setOrder(index);
			List<LMarker> pins = this.getPins();
			for (int i = 0; i < pins.size(); i++) {
				UIEdgePoint pInMap = (UIEdgePoint)pins.get(i).getData();
				if (pInMap.getOrder() >= index) {
					pInMap.setOrder(pInMap.getOrder() + 1);
				}
			}
		}
		
		LMarker newPin = null;
		// if sideA is selected, draw it green; otherwise, draw it blue
		newPin = addPinForWayPoint(p);
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource greenIcon = new FileResource(new File(basepath+"/VAADIN/img/green-dot.png"));
		FileResource blueIcon = new FileResource(new File(basepath+"/VAADIN/img/blue-dot.png"));
		if (p.getSide() == 0) // side A is 0 is red
			newPin.setIcon(greenIcon);
		else if (p.getSide() == 1) // side B is 1 is blue
			newPin.setIcon(blueIcon);
		newPin.setIconSize(new Point(15, 15));
		newPin.setIconAnchor(new Point(7, 10));
		
		
		mapComponent.updateLinesAndGrid();	
		allSideWaypoints.add(p);
		return newPin;
	}
	
	// adds a vertex to a polygon as it's being drawn
	public void addPolygonVertex(Point point) {

		UIEdgePoint p = new UIEdgePoint(point, false);
		LMarker newPin = new LMarker(p.toPoint());
		newPin.setData((p));
		newPin.setId(p.getId());
		newPin.setHeight("2px");
		newPin.addDragEndListener(new AMPriorityDragEndListener(this, newPin));
		newPolygonPins.add(newPin);
		map.addComponent(newPin);
		setAllPolygonPins();
		polygonVertices.add(point);
		
		// add polyline between each additional point that is drawn
		if (polygonVertices.size() > 1) {
			LPolyline polyline = new LPolyline(point, polygonVertices.get(polygonVertices.size()-2));
			polyline.setId(UUID.randomUUID().toString());
			polyline.setColor("#ff5050");
			polyline.setDashArray("5 10");
			polyline.setHeight("2");
			polyline.setWeight(1);
			map.addComponent(polyline);
			newPolygonLines.add(polyline);
		}
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource redIcon = new FileResource(new File(basepath+"/VAADIN/img/red-dot.png"));
		newPin.setIcon(redIcon);
		newPin.setIconSize(new Point(10, 10));
		newPin.setIconAnchor(new Point(5, 7));
	}
	
	// adds a pin in a location designated by the wayPoints
	private LMarker addPinForWayPoint(UIEdgePoint wayPoint) {
		LMarker leafletMarker = new LMarker(wayPoint.toPoint());
		leafletMarker.setData(wayPoint);
		leafletMarker.setId(wayPoint.getId());
		
		if (markerMouseOverListener != null) leafletMarker.addMouseOverListener(markerMouseOverListener);
		if (markerMouseOutListener != null) leafletMarker.addMouseOutListener(markerMouseOutListener);
		if (markerDragEndListener != null) leafletMarker.addDragEndListener(markerDragEndListener);

		leafletMarker.addClickListener(new AMUpstreamClickListener(this));
		
		map.addComponent(leafletMarker);
		return leafletMarker;
	}
	
	// removes a pin when given its ID
	public void removePinById (String id) {
		LMarker p = this.getPinById(id);
		removePin(p);
	}
	
	// remove a pin from the map
	public void removePin (LMarker p) {
		if (p == null)
			return;
		
		UIEdgePoint w = (UIEdgePoint)p.getData();
		
		List<LMarker> pins = this.getPins();
		for (int i = 0; i < pins.size(); i++) {
			UIEdgePoint pInMap = (UIEdgePoint)pins.get(i).getData();
			if (pInMap.getOrder() >= w.getOrder())
				pInMap.setOrder(pInMap.getOrder() - 1);
		}
		map.removeComponent(p);
		
		// if pin was in a priority side, remove it from the list and redraw the priority sides
		for (PrioritySide side : allPrioritySides) {
			List<LlaCoordinate> tempCoords = new LinkedList<LlaCoordinate>();
			removedPriorityLines.clear();
			for (int i = 0; i < side.getCoordinates().size(); i ++) {
				if (new LlaCoordinate(p.getPoint().getLat(), p.getPoint().getLon(), 0).equals(side.getCoordinates().get(i))) {
					// remove lines from allPriorityLines
					for (LPolyline line : getAllPriorityLines()) {
						if (((double)Math.round(line.getPoints()[0].getLat() * 1000000d) / 1000000d == (double)Math.round(p.getPoint().getLat() * 1000000d) / 1000000d)
						&& ((double)Math.round(line.getPoints()[0].getLon() * 1000000d) / 1000000d == (double)Math.round(p.getPoint().getLon() * 1000000d) / 1000000d)
						|| (((double)Math.round(line.getPoints()[1].getLat() * 1000000d) / 1000000d == (double)Math.round(p.getPoint().getLat() * 1000000d) / 1000000d)
						&& ((double)Math.round(line.getPoints()[1].getLon() * 1000000d) / 1000000d == (double)Math.round(p.getPoint().getLon() * 1000000d) / 1000000d)))
						{
							removedPriorityLines.add(line);
							allPriorityLines.remove(line);
						}
					}
				}
				else {
					tempCoords.add(side.getCoordinates().get(i)); // if the deleted pin isn't in allPrioritySides, add it to the temp list
				}
			}
			side.setCoordinates(tempCoords);
		}
		mapComponent.updateLinesAndGrid();
	}
	
	//removes all of the pins from the map
	public void removeAllPins() {
		allSideWaypoints.clear();
		List<LMarker> pins = getPins();
		for (int i = pins.size() - 1; i >= 0; i--) {
			map.removeComponent(pins.get(i));
		}
	}
	
	/**
	 * 
	 * @param wayPoints
	 * @param fromActive
	 * 			should be true if drawLines is being called from the active flights UI. This
	 * 			determines if the first line segment should be green (which it shouldn't
	 * 			be in the flight routes UI). 
	 * @return list of polylines drawn on the map
	 */
	public Vector<List<LPolyline>> drawLinesForWayPoints(Vector<List<UIEdgePoint>> wayPoints, boolean fromActive) {
		// Draws polylines based on a list of waypoints, then outputs the newly formed arraylist of polylines.
		sides = wayPoints;
		Vector<List<LPolyline>> polylines = new Vector<>();
		
		
		for (int j = 0; j <= 1; j++) {
			polylines.add(j, new ArrayList<>());
			for (int i = 0; i < wayPoints.get(j).size() - 1; i++) {
				UIEdgePoint current = wayPoints.get(j).get(i);
				LPolyline polyline = new LPolyline(current.toPoint(), wayPoints.get(j).get(i + 1).toPoint());
				polyline.setId(UUID.randomUUID().toString());
				
				// if priority lines were recently deleted, update allPriorityLines accordingly
				if (removedPriorityLines.size() == 2) {
					if ((((double)Math.round(polyline.getPoints()[0].getLat() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(0).getPoints()[0].getLat() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[0].getLon() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(0).getPoints()[0].getLon() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[1].getLat() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(1).getPoints()[0].getLat() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[1].getLon() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(1).getPoints()[0].getLon() * 1000000d) / 1000000d))
					|| (((double)Math.round(polyline.getPoints()[0].getLat() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(0).getPoints()[0].getLat() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[0].getLon() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(0).getPoints()[0].getLon() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[1].getLat() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(1).getPoints()[1].getLat() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[1].getLon() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(1).getPoints()[1].getLon() * 1000000d) / 1000000d))
					|| (((double)Math.round(polyline.getPoints()[0].getLat() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(0).getPoints()[1].getLat() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[0].getLon() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(0).getPoints()[1].getLon() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[1].getLat() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(1).getPoints()[1].getLat() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[1].getLon() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(1).getPoints()[1].getLon() * 1000000d) / 1000000d))
					|| (((double)Math.round(polyline.getPoints()[0].getLat() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(0).getPoints()[1].getLat() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[0].getLon() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(0).getPoints()[1].getLon() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[1].getLat() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(1).getPoints()[0].getLat() * 1000000d) / 1000000d)
					&& ((double)Math.round(polyline.getPoints()[1].getLon() * 1000000d) / 1000000d == (double)Math.round(removedPriorityLines.get(1).getPoints()[0].getLon() * 1000000d) / 1000000d)))
					{
						allPriorityLines.add(polyline);
					}
				}
	
				// if it's a priority line, draw it red; otherwise, grey
				polyline.setColor("#444");

				for (LPolyline line : allPriorityLines) {
					if ((((double)Math.round(line.getPoints()[0].getLat() * 10000d) / 10000d == (double)Math.round(polyline.getPoints()[0].getLat() * 10000d) / 10000d)
					&& ((double)Math.round(line.getPoints()[0].getLon() * 10000d) / 10000d == (double)Math.round(polyline.getPoints()[0].getLon() * 10000d) / 10000d)
					&& ((double)Math.round(line.getPoints()[1].getLat() * 10000d) / 10000d == (double)Math.round(polyline.getPoints()[1].getLat() * 10000d) / 10000d)
					&& ((double)Math.round(line.getPoints()[1].getLon() * 10000d) / 10000d == (double)Math.round(polyline.getPoints()[1].getLon() * 10000d) / 10000d))
					|| (((double)Math.round(line.getPoints()[0].getLat() * 10000d) / 10000d == (double)Math.round(polyline.getPoints()[1].getLat() * 10000d) / 10000d)
					&& ((double)Math.round(line.getPoints()[0].getLon() * 10000d) / 10000d == (double)Math.round(polyline.getPoints()[1].getLon() * 10000d) / 10000d)
					&& ((double)Math.round(line.getPoints()[1].getLat() * 10000d) / 10000d == (double)Math.round(polyline.getPoints()[0].getLat() * 10000d) / 10000d)
					&& ((double)Math.round(line.getPoints()[1].getLon() * 10000d) / 10000d == (double)Math.round(polyline.getPoints()[0].getLon() * 10000d) / 10000d)))
					{
						// if a priority line endpoint was dragged, change that information in allPrioritySides
						if (markerDragEndListener.getDraggedPriorityLine()) {
							for (int p = 0; p < allPrioritySides.size(); p++) {
								for (int q = 0; q < allPrioritySides.get(p).getCoordinates().size(); q++) {
									if ((double)Math.round(allPrioritySides.get(p).getCoordinates().get(q).getLatitude() * 10000d) / 10000d == (double)Math.round(line.getPoints()[0].getLat() * 10000d) / 10000d 
									&& (double)Math.round(allPrioritySides.get(p).getCoordinates().get(q).getLongitude() * 10000d) / 10000d == (double)Math.round(line.getPoints()[0].getLon() * 10000d) / 10000d ) {
										List<LlaCoordinate> tempCoords = new LinkedList<>(allPrioritySides.get(p).getCoordinates());
										tempCoords.set(q, new LlaCoordinate(polyline.getPoints()[0].getLat(), polyline.getPoints()[0].getLon(), 0));
										allPrioritySides.get(p).setCoordinates(tempCoords);
									}
									if ((double)Math.round(allPrioritySides.get(p).getCoordinates().get(q).getLatitude() * 10000d) / 10000d == (double)Math.round(line.getPoints()[1].getLat() * 10000d) / 10000d 
									&& (double)Math.round(allPrioritySides.get(p).getCoordinates().get(q).getLongitude() * 10000d) / 10000d == (double)Math.round(line.getPoints()[1].getLon() * 10000d) / 10000d ) {
										List<LlaCoordinate> tempCoords = new LinkedList<>(allPrioritySides.get(p).getCoordinates());
										tempCoords.set(q, new LlaCoordinate(polyline.getPoints()[1].getLat(), polyline.getPoints()[1].getLon(), 0));
										allPrioritySides.get(p).setCoordinates(tempCoords);
									}
								}
							}
							line.setPoints(polyline.getPoints());
						}
						
						// highlight as priority line
						polyline.setColor("#ff5050");
						polyline.addMouseOverListener(new AMPrioritySideMouseOverListener(mapComponent, polyline));
						polyline.addMouseOutListener(new AMPriorityMouseOutListener(this));
						polyline.setOpacity(0.7);
					}
				}
				
				map.addComponent(polyline);
				allPolylines.add(polyline);
				

				// assigns the line that is moused over
				polyline.addMouseOverListener(new LeafletMouseOverListener() {
					@Override
					public void onMouseOver(LeafletMouseOverEvent event) {
						mousedOverLine = (LPolyline)event.getSource();
					}
				});
				
				// if the mouse is removed from the line, set mousedOverLine to null
				polyline.addMouseOutListener(new LeafletMouseOutListener() {
					@Override
					public void onMouseOut(LeafletMouseOutEvent event) {
						mousedOverLine = null;
					}
				});
	
				if (polylineClickListener != null) polyline.addClickListener(polylineClickListener);
				
				polylines.get(j).add(polyline);
			}
		}
		highlightUpstream();
		return polylines;
	}
	
	// Removes all lines from the map and the polylines arraylist unless drawing a polygon
	public void removeAllLines() {
		if (mapComponent.getEditPrioritiesController().getDrawingArea())
			return;
		Iterator<Component> it = map.iterator();
		LinkedList<LPolyline> linesToRemove = new LinkedList<>();
		while(it.hasNext()) {
			Component c = it.next();
			if (c.getClass() == LPolyline.class) {
				linesToRemove.add((LPolyline)c);
			}
		}
		for (LPolyline polyline : linesToRemove) {
			map.removeComponent(polyline);
		}
		allPolylines.clear();
	}
	
	// Removes all lines and points from the polygon that's currently being drawn
	public void deleteNewPolygon() {
		for (int i = 0; i < newPolygonPins.size(); i++) {
			map.removeComponent(newPolygonPins.get(i));
		}
		for (int i = 0; i < newPolygonLines.size(); i++) {
			map.removeComponent(newPolygonLines.get(i));
		}
		polygonVertices.clear();
		clearPolygonVertices();
	}
	
	// removes a polygon from the existing list
	public void deleteOldPolygon(PriorityArea areaToDelete) {
		for (int i = 0; i < allPriorityAreas.size(); i++) {
			if (allPriorityAreas.get(i).getId().equals(areaToDelete.getId())) {
				allPriorityAreas.remove(i);
			}
		}
		// redraw polygons to map (without the one that was deleted)
		LinkedList<PriorityArea> tempPolygonList = new LinkedList<>(allPriorityAreas);
		removeAllPriorityAreas();
		for (int i = 0; i < tempPolygonList.size(); i++) {
			for (int j = 0; j < tempPolygonList.get(i).getCoordinates().size(); j++) {
					addPolygonVertex(new Point(tempPolygonList.get(i).getCoordinates().get(j).getLatitude(), tempPolygonList.get(i).getCoordinates().get(j).getLongitude()));
			}
				drawPolygon();	
				addPriorityArea(tempPolygonList.get(i).getType(), tempPolygonList.get(i).getDescription(), tempPolygonList.get(i).getImportance());
		}
	}
	
	// redraws all the lines to the map
	public Vector<List<LPolyline>> redrawAllLines(boolean fromActive) {
		removeAllLines();
		Vector<List<UIEdgePoint>> mapPoints = getOrderedWayPoints();
		return drawLinesForWayPoints(mapPoints, fromActive);
	}
	
	// highlights the points that are upstream based on upstream variable
	public void highlightUpstream() {
		if (upstream == null || this.getOrderedWayPoints().get(0).size() == 0 || this.getOrderedWayPoints().get(1).size() == 0)
			return;
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource waterfallIcon = new FileResource(new File(basepath+"/VAADIN/img/waterfall.png"));
		FileResource greenIcon = new FileResource(new File(basepath+"/VAADIN/img/green-dot.png"));
		FileResource blueIcon = new FileResource(new File(basepath+"/VAADIN/img/blue-dot.png"));
		for (int i = 0; i < this.getPins().size(); i++) {
			// if upstream is false, make the last waypoints on both sides into the waterfall icon
			if ((((double)Math.round(getPins().get(i).getPoint().getLat() * 10000d) / 10000d == (double)Math.round(Double.parseDouble(getOrderedWayPoints().get(0).get(getOrderedWayPoints().get(0).size()-1).getLatitude()) * 10000d) / 10000d) 
			&& ((double)Math.round(getPins().get(i).getPoint().getLon() * 10000d) / 10000d == (double)Math.round(Double.parseDouble(getOrderedWayPoints().get(0).get(getOrderedWayPoints().get(0).size()-1).getLongitude())* 10000d) / 10000d)	
			&& upstream == false)
			|| (((double)Math.round(getPins().get(i).getPoint().getLat() * 10000d) / 10000d == (double)Math.round(Double.parseDouble(getOrderedWayPoints().get(1).get(getOrderedWayPoints().get(1).size()-1).getLatitude()) * 10000d) / 10000d) 
			&& ((double)Math.round(getPins().get(i).getPoint().getLon() * 10000d) / 10000d == (double)Math.round(Double.parseDouble(getOrderedWayPoints().get(1).get(getOrderedWayPoints().get(1).size()-1).getLongitude())* 10000d) / 10000d)	
			&& upstream == false)) {
				LMarker leafletMarker = this.getPins().get(i);
				leafletMarker.setIcon(waterfallIcon);
				leafletMarker.setIconSize(new Point(30, 30));
				leafletMarker.setIconAnchor(new Point(16, 10));
			}
			
			// if upstream is true, make the first waypoints on both sides into the waterfall icon
			else if ((((double)Math.round(getPins().get(i).getPoint().getLat() * 10000d) / 10000d == (double)Math.round(Double.parseDouble(getOrderedWayPoints().get(0).get(0).getLatitude())* 10000d) / 10000d)
			&& ((double)Math.round(getPins().get(i).getPoint().getLon() * 10000d) / 10000d == (double)Math.round(Double.parseDouble(getOrderedWayPoints().get(0).get(0).getLongitude()) * 10000d) / 10000d)
			&& upstream == true)
			|| (((double)Math.round(getPins().get(i).getPoint().getLat() * 10000d) / 10000d == (double)Math.round(Double.parseDouble(getOrderedWayPoints().get(1).get(0).getLatitude())* 10000d) / 10000d)
			&& ((double)Math.round(getPins().get(i).getPoint().getLon() * 10000d) / 10000d == (double)Math.round(Double.parseDouble(getOrderedWayPoints().get(1).get(0).getLongitude()) * 10000d) / 10000d)
			&& upstream == true)) {
				LMarker leafletMarker = this.getPins().get(i);
				leafletMarker.setIcon(waterfallIcon);
				leafletMarker.setIconSize(new Point(30, 30));
				leafletMarker.setIconAnchor(new Point(16, 10));
			}
			// color all other points normally
			else {
				LMarker leafletMarker = this.getPins().get(i);
				leafletMarker.setIcon(blueIcon);
				leafletMarker.setIconSize(new Point(15, 15));
				leafletMarker.setIconAnchor(new Point(7, 10));
				for (UIEdgePoint point : getOrderedWayPoints().get(0)) {
					if ((double)Math.round(Double.parseDouble(point.getLatitude()) * 10000d) / 10000d == (double)Math.round(this.getPins().get(i).getPoint().getLat() * 10000d) / 10000d
					&& (double)Math.round(Double.parseDouble(point.getLongitude()) * 10000d) / 10000d == (double)Math.round(this.getPins().get(i).getPoint().getLon()* 10000d) / 10000d) {
						leafletMarker.setIcon(greenIcon);
					}
				}
			}
		}
	}

	// get ordered waypoints that belong to each side of the mapping
	public Vector<List<UIEdgePoint>> getOrderedWayPoints() {
		Vector<List<UIEdgePoint>> wayPoints = new Vector<>();
		wayPoints.add(0, new LinkedList<UIEdgePoint>());
		wayPoints.add(1, new LinkedList<UIEdgePoint>());
		for (LMarker p : getPins()) {
			wayPoints.get(((UIEdgePoint)p.getData()).getSide()).add((UIEdgePoint)p.getData());
		}
		wayPoints.get(0).sort(new Comparator<UIEdgePoint>() {
	        @Override
	        public int compare(UIEdgePoint w1, UIEdgePoint w2) {
	        		return  w1.getOrder() - w2.getOrder();
	        }
	    });
		wayPoints.get(1).sort(new Comparator<UIEdgePoint>() {
	        @Override
	        public int compare(UIEdgePoint w1, UIEdgePoint w2) {
	        		return  w1.getOrder() - w2.getOrder();
	        }
	    }); 
		return wayPoints;
	}
	
	// Gets all of the pins that are on the map, excluding polygon pins
	public List<LMarker> getPins() {
		List<LMarker> pins = new ArrayList<>();
		Iterator<Component> it = map.iterator();
		while(it.hasNext()) {
			Component c = it.next();
			if (c.getClass() == LMarker.class) {
				if (c.getHeight() != 2.0) // height of polygon pin
					pins.add((LMarker)c);
			}
		}
		return pins;
	}
	
	// gets pin by its ID
	public LMarker getPinById(String id) {
		List<LMarker> pins = getPins();
		for (LMarker pin : pins) {
			if (pin.getId().equals(id))
				return pin;
		}
		return null;
	}
	
	// draw a polygon using the points that have been plotted on the map
	public void drawPolygon() {
		
		// convert each point to create an area to add
		areaToAdd= new PriorityArea(UUID.randomUUID().toString());
		for (int i = 0; i < polygonVertices.size(); i++) {
			areaToAdd.addCoordinate(new LlaCoordinate(polygonVertices.get(i).getLat(), polygonVertices.get(i).getLon(), 0));
		}
		
		// if polygon already in list, remove it so that copies don't exist
		for (int i = 0; i < allPriorityAreas.size(); i ++) {
			if (areaToAdd.getCoordinates().equals(allPriorityAreas.get(i).getCoordinates()))
				allPriorityAreas.remove(i);
		}
		
		// convert from list to array to create Polygon
		LPolygon newPolygon = new LPolygon();
		newPolygon.setColor("#ff5050");
		Point pointArray[] = new Point[polygonVertices.size()];
	    pointArray = polygonVertices.toArray(pointArray);
		newPolygon.setPoints(pointArray);
		map.addComponent(newPolygon);
		AMPriorityAreaMouseOverListener mouseOverListener = new AMPriorityAreaMouseOverListener(mapComponent, areaToAdd.getId());
		newPolygon.addMouseOverListener(mouseOverListener);
		newPolygon.addMouseOutListener(new AMPriorityMouseOutListener(this)); // closes the window from the mouseOverListener
		newPolygonPins.clear();
		newPolygonLines.clear();
	}
	
	// uses the polygonVertices to finish and create a new priority area
	public void addPriorityArea(String type, String description, Integer importance) {
		areaToAdd.setType(type);
		areaToAdd.setDescription(description);
		areaToAdd.setImportance(importance);
		allPriorityAreas.add(areaToAdd);
		polygonVertices.clear();
	}
	
	// iterate through map to find all polygons and polygon pins and delete them
	public void removeAllPriorityAreas() {
		List<LPolygon> polygons = new ArrayList<>();
		List<LMarker> pins = new ArrayList<>();
		List<LPolyline> lines = new ArrayList<>();
		Iterator<Component> it = map.iterator();
		while(it.hasNext()) {
			Component c = it.next();
			if (c.getClass() == LPolygon.class) {
					polygons.add((LPolygon)c);
			}
			if (c.getClass() == LMarker.class) {
				if (c.getHeight() == 2.0) // height of polygon pin
					pins.add((LMarker)c);
			}
			if (c.getClass() == LPolyline.class) {
				if (c.getHeight() == 2.0) // height of polygon line
					lines.add((LPolyline)c);
			}
		}

		// delete all polygons and pins
		for (int i = polygons.size() - 1; i >= 0; i--) {
			map.removeComponent(polygons.get(i));
		}
		for (int i = pins.size() -1; i >= 0; i--) {
			map.removeComponent(pins.get(i));
		}
		for (int i = lines.size() -1; i >= 0; i--) {
			map.removeComponent(lines.get(i));
		}
		
		polygons.clear();
		pins.clear();
		allPriorityAreas.clear();
		polygonVertices.clear();
	}
	
	// updates allPolygonPins based on the pins on the map
	public void setAllPolygonPins() {
		allPolygonPins.clear();
		Iterator<Component> it = map.iterator();
		while(it.hasNext()) {
			Component c = it.next();
			if (c.getClass() == LMarker.class) {
				if (c.getHeight() == 2.0) // height of polygon pin
					allPolygonPins.add((LMarker)c);
			}
		}
		allPolygonPinsCopy.clear();
		for (LMarker marker : allPolygonPins) {
			allPolygonPinsCopy.add(marker.getPoint());
		}
	}
	
	// draw the priorities using the mapping from dronology
	public void drawPriorities() {
		
		// if drawing a polygon, don't refresh all the others because this will interrupt the process
		if (this.getMapComponent().getMapUtilities().getPrioritiesAreEditable() || this.getMapComponent().getMapUtilities().getSidesAreEditable())
			return;

		removeAllPriorityAreas();
		
		// get area mapping from dronology
		IAreaMapping amapping = null;
		BaseServiceProvider provider = MyUI.getProvider();
		AreaMappingPersistenceProvider mappingPersistor = AreaMappingPersistenceProvider.getInstance();
		try {
			IAreaMappingRemoteService service = (IAreaMappingRemoteService) provider.getRemoteManager().getService(IAreaMappingRemoteService.class);
			ByteArrayInputStream inStream;
			String id = mapComponent.getMainLayout().getControls().getInfoPanel().getHighlightedAMInfoBox().getId();
			byte[] information = service.requestFromServer(id);
			inStream = new ByteArrayInputStream(information);
			amapping = mappingPersistor.loadItem(inStream);
		} catch (DronologyServiceException | RemoteException e1) {
		e1.printStackTrace();
		MyUI.setConnected(false);
		} catch (PersistenceException e1) {
		e1.printStackTrace();
		}
		// get and add priority areas/sides from area mapping 
		for (int i = 0; i < amapping.getLocationMappings().size(); i++) {
			if (amapping.getLocationMappings().get(i).getClass() == PriorityArea.class) {
				for (int j = 0; j < amapping.getLocationMappings().get(i).getCoordinates().size(); j++) {
					addPolygonVertex(new Point(amapping.getLocationMappings().get(i).getCoordinates().get(j).getLatitude(), amapping.getLocationMappings().get(i).getCoordinates().get(j).getLongitude()));
				}
				drawPolygon();	
				addPriorityArea(amapping.getLocationMappings().get(i).getType(), amapping.getLocationMappings().get(i).getDescription(), amapping.getLocationMappings().get(i).getImportance());
			}
			else { // priority side

				for (int j = 0; j < amapping.getLocationMappings().get(i).getCoordinates().size() - 1; j++) {
					// find line that connects two coordinates
					for (LPolyline line : this.getPolylines()) {
						double latPolylinePoint1 = (double)Math.round(line.getPoints()[0].getLat() * 1000000d) / 1000000d;
						double lonPolylinePoint1 = (double)Math.round(line.getPoints()[0].getLon() * 1000000d) / 1000000d;
						double latPolylinePoint2 = (double)Math.round(line.getPoints()[1].getLat() * 1000000d) / 1000000d;
						double lonPolylinePoint2 = (double)Math.round(line.getPoints()[1].getLon() * 1000000d) / 1000000d;
						double latPriorityPoint1 = (double)Math.round(amapping.getLocationMappings().get(i).getCoordinates().get(j).getLatitude() * 1000000d) / 1000000d;
						double lonPriorityPoint1 = (double)Math.round(amapping.getLocationMappings().get(i).getCoordinates().get(j).getLongitude() * 1000000d) / 1000000d;
						double latPriorityPoint2 = (double)Math.round(amapping.getLocationMappings().get(i).getCoordinates().get(j+1).getLatitude() * 1000000d) / 1000000d;
						double lonPriorityPoint2 = (double)Math.round(amapping.getLocationMappings().get(i).getCoordinates().get(j+1).getLongitude() * 1000000d) / 1000000d;
						if ((latPolylinePoint1 == latPriorityPoint1 && lonPolylinePoint1 == lonPriorityPoint1 && latPolylinePoint2 == latPriorityPoint2 && lonPolylinePoint2 == lonPriorityPoint2)
							|| (latPolylinePoint2 == latPriorityPoint1 && lonPolylinePoint2 == lonPriorityPoint1 && latPolylinePoint1 == latPriorityPoint2 && lonPolylinePoint1 == lonPriorityPoint2)) {
							addPriorityLine(line);
						}
					}
				}
				addPrioritySide(amapping.getLocationMappings().get(i).getId(), amapping.getLocationMappings().get(i).getType(), amapping.getLocationMappings().get(i).getDescription(), amapping.getLocationMappings().get(i).getImportance());
			}
		}
		
		// also, mark upstream so that the waterfall icon is correctly drawn
		if (amapping != null) {
			upstream = amapping.getUpstream();
		}
	}
	
	// switch out one of the areas in allPriorityAreas with one that was edited
	public void editPriorityArea(PriorityArea changedArea) {
		for (int i = 0; i < allPriorityAreas.size(); i++) {
			if (allPriorityAreas.get(i).getId().equals(changedArea.getId())) {
				allPriorityAreas.set(i, changedArea);
			}
		}
	}
	
	// switch out one of the sides in allPrioritySides with one that was edited
	public void editPrioritySide(PrioritySide changedSide) {
		for (int i = 0; i < allPrioritySides.size(); i++) {
			if (allPrioritySides.get(i).getId().equals(changedSide.getId())) {
				allPrioritySides.set(i, changedSide);
			}
		}
	}
	
	// adds a new priority line
	public void addPriorityLine(LPolyline lineToAdd) {

		Boolean foundInAllPriorityLines = false;
		
		// iterate through existing priority lines to make sure that it doesn't already exist
		for (LPolyline polyline : allPriorityLines) 
			if ((((double)Math.round(lineToAdd.getPoints()[0].getLat() * 1000000d) / 1000000d == (double)Math.round(polyline.getPoints()[0].getLat() * 1000000d) / 1000000d)
			&& ((double)Math.round(lineToAdd.getPoints()[0].getLon() * 1000000d) / 1000000d == (double)Math.round(polyline.getPoints()[0].getLon() * 1000000d) / 1000000d)
			&& ((double)Math.round(lineToAdd.getPoints()[1].getLat() * 1000000d) / 1000000d == (double)Math.round(polyline.getPoints()[1].getLat() * 1000000d) / 1000000d)
			&& ((double)Math.round(lineToAdd.getPoints()[1].getLon() * 1000000d) / 1000000d == (double)Math.round(polyline.getPoints()[1].getLon() * 1000000d) / 1000000d)) 
			|| (((double)Math.round(lineToAdd.getPoints()[0].getLat() * 1000000d) / 1000000d == (double)Math.round(polyline.getPoints()[1].getLat() * 1000000d) / 1000000d)
			&& ((double)Math.round(lineToAdd.getPoints()[0].getLon() * 1000000d) / 1000000d == (double)Math.round(polyline.getPoints()[1].getLon() * 1000000d) / 1000000d)
			&& ((double)Math.round(lineToAdd.getPoints()[1].getLat() * 1000000d) / 1000000d == (double)Math.round(polyline.getPoints()[0].getLat() * 1000000d) / 1000000d)
			&& ((double)Math.round(lineToAdd.getPoints()[1].getLon() * 1000000d) / 1000000d == (double)Math.round(polyline.getPoints()[0].getLon() * 1000000d) / 1000000d))) {
				foundInAllPriorityLines = true;
				break;
			}

		// if not found, add the new line
		if (!foundInAllPriorityLines) {
			lineToAdd.setColor("#ff5050");
			lineToAdd.setOpacity(0.7);
			newPrioritySideLines.add(lineToAdd);
			allPriorityLines.add(lineToAdd);
			map.addComponent(lineToAdd);
			lineToAdd.addMouseOverListener(new AMPrioritySideMouseOverListener(mapComponent, lineToAdd));
			lineToAdd.addMouseOutListener(new AMPriorityMouseOutListener(this));
		}
		polylineClickListener.resetPolylineIsClickedInThisEvent();
	}
	
	// adds the current priority side to allPrioritySides
	public void addPrioritySide(String id, String type, String description, int importance) {
		// return if no newPrioritySideLines or if the side already exists in allPrioritySides
		if (newPrioritySideLines.size() == 0) {
			return;
		}
		for (PrioritySide side : allPrioritySides) {
			if (side.getId().equals(id)) {
				return;
			}
		}

		PrioritySide newPrioritySide = new PrioritySide(id);
		newPrioritySide.setType(type);
		newPrioritySide.setDescription(description);
		newPrioritySide.setImportance(importance);
		// add all coords belonging the lines to the list of coords
		for (LPolyline line : newPrioritySideLines) {
			for (int i = 0; i <= 1; i++) {
				LlaCoordinate currCoord = new LlaCoordinate(line.getPoints()[i].getLat(), line.getPoints()[i].getLon(), 0);
				newPrioritySide.addCoordinate(currCoord);
			}
		}

		allPrioritySides.add(newPrioritySide);
		newPrioritySideLines.clear();
	}
	
	// used to add a full side when creating a sub-map
	public void addPrioritySide(PrioritySide side) {
		for (int j = 0; j < side.getCoordinates().size() - 1; j++) {
			// find line that connects two coordinates
			for (LPolyline line : this.getPolylines()) {
				double latPolylinePoint1 = (double)Math.round(line.getPoints()[0].getLat() * 1000000d) / 1000000d;
				double lonPolylinePoint1 = (double)Math.round(line.getPoints()[0].getLon() * 1000000d) / 1000000d;
				double latPolylinePoint2 = (double)Math.round(line.getPoints()[1].getLat() * 1000000d) / 1000000d;
				double lonPolylinePoint2 = (double)Math.round(line.getPoints()[1].getLon() * 1000000d) / 1000000d;
				double latPriorityPoint1 = (double)Math.round(side.getCoordinates().get(j).getLatitude() * 1000000d) / 1000000d;
				double lonPriorityPoint1 = (double)Math.round(side.getCoordinates().get(j).getLongitude() * 1000000d) / 1000000d;
				double latPriorityPoint2 = (double)Math.round(side.getCoordinates().get(j+1).getLatitude() * 1000000d) / 1000000d;
				double lonPriorityPoint2 = (double)Math.round(side.getCoordinates().get(j+1).getLongitude() * 1000000d) / 1000000d;
				if ((latPolylinePoint1 == latPriorityPoint1 && lonPolylinePoint1 == lonPriorityPoint1 && latPolylinePoint2 == latPriorityPoint2 && lonPolylinePoint2 == lonPriorityPoint2)
					|| (latPolylinePoint2 == latPriorityPoint1 && lonPolylinePoint2 == lonPriorityPoint1 && latPolylinePoint1 == latPriorityPoint2 && lonPolylinePoint1 == lonPriorityPoint2)) {
					addPriorityLine(line);
				}
			}
		}
		addPrioritySide(side.getId(), side.getType(), side.getDescription(), side.getImportance());
	}
	
	// Enables/disable sides editing.
	public void setSidesAreEditable (boolean isEditable) {
		sidesAreEditable = isEditable;
	}

	// Returns whether or not edit mode has been enabled.
	public boolean getSidesAreEditable() {
		return sidesAreEditable;
	}
	
	public List<Point> getAllPolygonPins() {
		return allPolygonPinsCopy;
	}
	
	// Enables/disable sides editing.
	public void setPrioritiesAreEditable (boolean isEditable) {
		prioritiesAreEditable = isEditable;
	}
	
	// Returns whether or not edit mode has been enabled.
	public boolean getPrioritiesAreEditable() {
		return prioritiesAreEditable;
	}
	
	// Gets all of the side polylines that are on the map
	public List<LPolyline> getPolylines() {
		return allPolylines;
	}
	
	// Returns the map.
	public LMap getMap() {
		return map;
	}
	
	// Returns the mapComponent (use if the functions in AMMapComponent are needed).
	public AMMapComponent getMapComponent() {
		return mapComponent;
	}
	
	public AMMapAddMarkerListener getMapAddMarkerListener() {
		return mapAddMarkerListener;
	}
	public AMMarkerMouseOverListener getMarkerMouseOverListener() {
		return markerMouseOverListener;
	}
	public AMPolylineClickListener getPolylineClickListener() {
		return polylineClickListener;
	}
	
	public LinkedList<Point> getPolygonVertices() {
		return polygonVertices;
	}
	
	// returns the two sides
	public Vector<List<UIEdgePoint>> getSides() {
		return sides;
	}
	
	// returns the polyline that's moused over
	public LPolyline getMousedOverLine() {
		return mousedOverLine;
	}
	
	// sets the priority area that's moused over
	public void setMousedOverArea(PriorityArea mousedOverArea) {
		this.mousedOverArea = mousedOverArea;
	}
	
	// sets the priority side that's moused over
	public void setMousedOverSide(PrioritySide mousedOverSide) {
		this.mousedOverSide = mousedOverSide;
	}
	
	// gets the priority area that's moused over
	public PriorityArea getMousedOverArea() {
		return mousedOverArea;
	}
	
	// gets the priority side that's moused over
	public PrioritySide getMousedOverSide() {
		return mousedOverSide;
	}
	
	// returns vector that contains a separate list of points for each polygon
	public Vector<PriorityArea> getAllPriorityAreas() {
		return allPriorityAreas;
	}
	
	public void setAllPriorityAreas(Vector<PriorityArea> allPriorityAreas) {
		this.allPriorityAreas = allPriorityAreas;
	}
	
	public void clearPolygonVertices() {
		polygonVertices.clear();
		newPolygonPins.clear();
		newPolygonLines.clear();
	}
	
	public void setPriorityInfoWindow(AMPriorityInfoWindow priorityInfoWindow) {
		this.priorityInfoWindow = priorityInfoWindow;
	}
	public AMPriorityInfoWindow getPriorityInfoWindow() {
		return priorityInfoWindow;
	}
	
	public List<LMarker> getNewPolygonPins() {
		return newPolygonPins;
	}
	
	public List<LPolyline> getNewPrioritySideLines() {
		return newPrioritySideLines;
	}
	
	public List<PrioritySide> getAllPrioritySides() {
		return allPrioritySides;
	}
	
	public List<LPolyline> getAllPriorityLines() {
		return allPriorityLines;
	}
	
	public List<UIEdgePoint> getAllSideWaypoints() {
		return new LinkedList<>(allSideWaypoints);
	}
	
	public Boolean getUpstream() {
		return upstream;
	}
	
	public void setUpstream(Boolean upstream) {
		this.upstream = upstream;
	}
} 