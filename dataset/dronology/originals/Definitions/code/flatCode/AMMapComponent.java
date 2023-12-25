package edu.nd.dronology.ui.vaadin.areamapping;

import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;

import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.areamapping.EdgeLla;
import edu.nd.dronology.services.core.info.AreaMappingInfo;
import edu.nd.dronology.services.core.items.IAreaMapping;
import edu.nd.dronology.services.core.persistence.AreaMappingPersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.remote.IAreaMappingRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMDeleteWayPointConfirmation;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMWayPointPopupView;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.map.LeafletmapFactory;
import edu.nd.dronology.ui.vaadin.start.MyUI;
import edu.nd.dronology.ui.vaadin.utils.WaypointReplace;

/**
 * This is the map component for area mapping. It holds most of the visual elements, and is largely controlled by AMMapMarkerUtilities
 * 
 * @author Andrew Slavin
 */

public class AMMapComponent extends CustomComponent{
	
	private static final long serialVersionUID = 8009738721123823238L;
	private AMMainLayout mainLayout;
	private AMMapMarkerUtilities utilities;
	
	private AMWayPointPopupView waypointPopupView;
	private AMDeleteWayPointConfirmation deleteWayPointConfirmation = new AMDeleteWayPointConfirmation(this);
	
	private VerticalLayout content = new VerticalLayout();
	private AbsoluteLayout mapAndComponentsLayout = new AbsoluteLayout();
	
	private AMEditSidesController editSidesController = new AMEditSidesController(this);
	private AMEditPrioritiesController editPrioritiesController = new AMEditPrioritiesController(this);
	private AMSubMapController subMapController = new AMSubMapController(this);
	private AMMetaInfo metaInfo = new AMMetaInfo(this);
	private LMap leafletMap;
	
	public AMMapComponent(AMMainLayout mainLayout) {
		this.mainLayout = mainLayout;
		this.setWidth("100%");
		leafletMap = LeafletmapFactory.generateMap();
		
		// create a popup when user mouses over waypoint via listener
		waypointPopupView = new AMWayPointPopupView(this);
		
		mapAndComponentsLayout.addComponents(waypointPopupView, leafletMap, subMapController, editSidesController, editPrioritiesController);
		content.addComponents(metaInfo, mapAndComponentsLayout);
		setCompositionRoot(content);		
		
		this.addStyleName("map_component");
		this.addStyleName("am_map_component"); 
		mapAndComponentsLayout.addStyleName("am_mapabsolute_layout");
		leafletMap.addStyleName("am_leaflet_map");
		leafletMap.addStyleName("bring_back");
		
		editSidesController.addStyleName("bring_front");
		editSidesController.setVisible(false);
		editPrioritiesController.addStyleName("bring_front");
		editPrioritiesController.setVisible(false);
		subMapController.addStyleName("bring_front");
		subMapController.setVisible(false);
		
		utilities = new AMMapMarkerUtilities(this);
	}
	
		// Displays with the map. Called when a mapping is deleted so that
		// its waypoints are no longer displayed.
		public void displayNoMapping() {
			metaInfo.showInfoWhenNoMappingIsSelected();
			utilities.removeAllPins();
			updateLinesAndGrid();
		}
		
		@WaypointReplace
		public void displayAreaMapping(AreaMappingInfo info) {
			metaInfo.showInfoForSelectedMapping(info);

			// Removes old pins and show the new pins
			utilities.removeAllPins();
			for (EdgeLla coordinate : info.getCoordinates(0)) {
				Waypoint waypoint = new Waypoint(coordinate);
				UIEdgePoint way = new UIEdgePoint(waypoint, 0);
				utilities.addNewPin(way, -1);
			}
			for (EdgeLla coordinate : info.getCoordinates(1)) {
				Waypoint waypoint = new Waypoint(coordinate);
				UIEdgePoint way = new UIEdgePoint(waypoint, 1);
				utilities.addNewPin(way, -1);
			}

			// redraw the lines and priorities to the map
			utilities.getAllPriorityLines().clear();
			utilities.getAllPrioritySides().clear();
			utilities.getNewPrioritySideLines().clear();
			updateLinesAndGrid();
			utilities.drawPriorities();
			utilities.highlightUpstream();
			setMappingCenter();
		}
	
		// Displays the waypoints in edit mode depending on whether or not the route is new.
		public void updateWayPointCount(AMMapMarkerUtilities mapUtilities) {
			metaInfo.setNumWaypoints(mapUtilities.getOrderedWayPoints().get(0).size(), mapUtilities.getOrderedWayPoints().get(1).size());
		}

		// Gets the mapping description using the currently selected mapping stored by
		// "selectedMapping".
		public String getMappingDescription() {
			AreaMappingPersistenceProvider mappingPersistor = AreaMappingPersistenceProvider.getInstance();
			ByteArrayInputStream inStream;
			IAreaMapping amapping;

			IAreaMappingRemoteService service;
			BaseServiceProvider provider = MyUI.getProvider();

			String description = null;
			// Sends the information to dronology to be saved.
			try {
				service = (IAreaMappingRemoteService) provider.getRemoteManager()
						.getService(IAreaMappingRemoteService.class);

				String id = this.getMainLayout().getControls().getInfoPanel().getHighlightedAMInfoBox().getId();

				byte[] information = service.requestFromServer(id);
				inStream = new ByteArrayInputStream(information);
				amapping = mappingPersistor.loadItem(inStream);

				description = amapping.getDescription();
				if (description == null) {
					description = "No Description";
				}
			} catch (DronologyServiceException | RemoteException e1) {
				MyUI.setConnected(false);
				e1.printStackTrace();
			} catch (PersistenceException e1) {
				e1.printStackTrace();
			}
			return description;
		}

		// Sets the center of the route based on the stored waypoints such that the map
		// is as visible as possible.
		public void setMappingCenter() {
			if (metaInfo.isAutoZoomingChecked()) {
				// Calculates the mean point and sets the route.
				double meanLat = 0;
				double meanLon = 0;
				int numberPoints;
				double farthestLat = 0;
				double farthestLon = 0;
				double zoom;

				Vector<List<UIEdgePoint>> currentWayPoints = utilities.getOrderedWayPoints();
				numberPoints = currentWayPoints.get(0).size() + currentWayPoints.get(1).size();

				for (UIEdgePoint p : currentWayPoints.get(0)) {
					meanLat += Double.valueOf(p.getLatitude());
					meanLon += Double.valueOf(p.getLongitude());
				}
				for (UIEdgePoint p : currentWayPoints.get(1)) {
					meanLat += Double.valueOf(p.getLatitude());
					meanLon += Double.valueOf(p.getLongitude());
				}

				meanLat /= (numberPoints * 1.0);
				meanLon /= (numberPoints * 1.0);

				// Finds farthest latitude and longitude from mean.
				for (UIEdgePoint p : currentWayPoints.get(0)) {
					if ((Math.abs(Double.valueOf(p.getLatitude()) - meanLat) > farthestLat)) {
						farthestLat = (Math.abs((Double.valueOf(p.getLatitude())) - meanLat));
					}
					if ((Math.abs(Double.valueOf(p.getLongitude()) - meanLon) > farthestLon)) {
						farthestLon = (Math.abs((Double.valueOf(p.getLongitude()) - meanLon)));
					}
				}
				for (UIEdgePoint p : currentWayPoints.get(1)) {
					if ((Math.abs(Double.valueOf(p.getLatitude()) - meanLat) > farthestLat)) {
						farthestLat = (Math.abs((Double.valueOf(p.getLatitude())) - meanLat));
					}
					if ((Math.abs(Double.valueOf(p.getLongitude()) - meanLon) > farthestLon)) {
						farthestLon = (Math.abs((Double.valueOf(p.getLongitude()) - meanLon)));
					}
				}

				// Used to calculate zoom level.
				Point centerPoint = new Point(meanLat, meanLon);
				if (farthestLat == 0 && farthestLon == 0) {
					zoom = 17;
				} else {
					zoom = Math.floor(Math.log10(180.0 / Math.max(farthestLat, farthestLon)) / Math.log10(2));
				}

				leafletMap.setCenter(centerPoint, zoom + 1);
			}
		}

		// Refreshes the map and grid by removing lines, redrawing them, and then
		// setting the map again.
		public void updateLinesAndGrid() {
			utilities.redrawAllLines(false);
			// if deleting a polygon, don't try to redraw polygons
			if (this.getMainLayout().getDeletingMapping()) {
				utilities.removeAllPriorityAreas();
				this.getMainLayout().setDeletingMapping(false);
				return;
			}
		}

		public ComponentPosition getWaypointPopupViewPosition() {
			return mapAndComponentsLayout.getPosition(waypointPopupView);
		}

		public void setWaypointPopupViewPosition(ComponentPosition position) {
			mapAndComponentsLayout.setPosition(waypointPopupView, position);
		}

		// Gets the class that represents the utilities.
		public AMMapMarkerUtilities getMapUtilities() {
			return utilities;
		}

		public LMap getMap() {
			return leafletMap;
		}

		// Gets the main layout (passed into constructor).
		public AMMainLayout getMainLayout() {
			return mainLayout;
		}

		// Gets the route information bar above the map.
		public AMMetaInfo getMetaInfo() {
			return metaInfo;
		}
	 
		public AMEditSidesController getEditSidesController() {
			return editSidesController;
		}
		
		public AMEditPrioritiesController getEditPrioritiesController() {
			return editPrioritiesController;
		}
		
		public AMSubMapController getSubMapController() {
			return subMapController;
		}

		public AMWayPointPopupView getWaypointPopupView() {
			return waypointPopupView;
		}

		public AMDeleteWayPointConfirmation getDeleteWayPointConfirmation() {
			return deleteWayPointConfirmation;
		}
		
}
