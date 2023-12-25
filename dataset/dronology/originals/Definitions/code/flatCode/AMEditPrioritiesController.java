package edu.nd.dronology.ui.vaadin.areamapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import edu.nd.dronology.services.core.areamapping.EdgeLla;
import edu.nd.dronology.services.core.info.AreaMappingInfo;
import edu.nd.dronology.services.core.items.IAreaMapping;
import edu.nd.dronology.services.core.persistence.AreaMappingPersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.remote.IAreaMappingRemoteService;

import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMPriorityAreaCreationWindow;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMPrioritySideCreationWindow;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMSaveAreaWithoutFinishWindow;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMSaveSideWithoutFinishWindow;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/**
 * This allows the user to create and edit mappings by changing the priority areas.
 * 
 * @author Andrew Slavin
 *
 */

public class AMEditPrioritiesController extends CustomComponent {
	/**
	 *  
	 */
	private static final long serialVersionUID = 212323831233690123L;
	private AMMapComponent mapComponent;
	private AMSaveAreaWithoutFinishWindow saveAreaWithoutFinishWindow;
	private AMSaveSideWithoutFinishWindow saveSideWithoutFinishWindow;
	private AMPriorityAreaCreationWindow priorityAreaCreationWindow;
	private AMPrioritySideCreationWindow prioritySideCreationWindow;
	private HorizontalLayout totalLayout = new HorizontalLayout();
	private Button editSidesButton = new Button("Sides");
	private Button editPrioritiesButton = new Button("Priorities");
	private Button drawAreaButton = new Button();
	private Button drawSidesButton = new Button();
	private Button cancelButton = new Button("Cancel");
	private Button saveButton = new Button("Save");
	private Label smallText = new Label("Select sides or areas to edit.");

	private List<UIEdgePoint> sidePoints0 = new ArrayList<>();
	private List<UIEdgePoint> sidePoints1 = new ArrayList<>();
	private String storedName = "";
	private String storedDescription = "";
	private Boolean drawingArea;
	private Boolean drawingSide;
	private Boolean finishAreaThenSave;
	private Boolean finishSideThenSave;
	private Boolean selectingUpstream;

	public AMEditPrioritiesController(AMMapComponent mapComponent) {
		this.mapComponent = mapComponent;
		priorityAreaCreationWindow = new AMPriorityAreaCreationWindow(mapComponent);
		prioritySideCreationWindow = new AMPrioritySideCreationWindow(mapComponent);
		saveAreaWithoutFinishWindow = new AMSaveAreaWithoutFinishWindow(this);
		saveSideWithoutFinishWindow = new AMSaveSideWithoutFinishWindow(this);
		drawAreaButton.setCaption("Start Area");
		drawSidesButton.setCaption("Start Side");
		
		drawingArea = false;
		drawingSide = false;
		finishAreaThenSave = false;
		finishSideThenSave = false;
		selectingUpstream = false;

		setStyleName("am_edit_bar");
		smallText.setStyleName("small_text");

		cancelButton.setHeight("25px");
		saveButton.setHeight("25px");
		editSidesButton.setHeight("100px");
		editSidesButton.addStyleName("sides_button");
		editPrioritiesButton.setHeight("35px");
		editPrioritiesButton.setEnabled(false);
		editSidesButton.addStyleName("toggle_button");
		editPrioritiesButton.addStyleName("toggle_button");
		drawAreaButton.setHeight("25px");
		drawSidesButton.setHeight("25px");

		totalLayout.addComponents(editSidesButton, editPrioritiesButton, smallText, drawSidesButton, drawAreaButton, cancelButton, saveButton);
		setCompositionRoot(totalLayout);

		// Click listeners for the cancel and saves buttons on edit bar
		
		// cancel button resets the other buttons and exits edit mode
		cancelButton.addClickListener(e -> {
			drawAreaButton.setEnabled(true);
			drawSidesButton.setEnabled(true);
			exitEditMode();
			cancelClick();
		});
		
		// save changes to priorities. If still in a drawing mode, prompt user to finish
		saveButton.addClickListener(e -> {
			
			// if the user hasn't selected upstream yet, prompt them to do so
			if (mapComponent.getMapUtilities().getUpstream() == null) {
				Notification.show("Please select the most upstream side A coordinate.", Notification.Type.ERROR_MESSAGE);
				selectingUpstream = true;
				return;
			}
			
			drawAreaButton.setEnabled(true);
			drawSidesButton.setEnabled(true);
			if (drawingArea) {
				if (mapComponent.getMapUtilities().getPolygonVertices().size() < 3) {
					Notification.show("Please finish your unfinished priority area.", Notification.Type.ERROR_MESSAGE);
					return;
				}
				UI.getCurrent().addWindow(saveAreaWithoutFinishWindow);
			}
			else if (drawingSide) {
				if (mapComponent.getMapUtilities().getNewPrioritySideLines().size() == 0) {
					Notification.show("Please finish your unfinished priority area.", Notification.Type.ERROR_MESSAGE);
				}
				UI.getCurrent().addWindow(saveSideWithoutFinishWindow);
			}
			else
				saveClick();
		});
		
		// switch over to editing sides rather than priorities
		editSidesButton.addClickListener(e -> {
			exitEditMode();
			mapComponent.getEditSidesController().enterEditMode();
		});
		
		// on each click, alternate between drawingArea mode and finishing the area
		drawAreaButton.addClickListener(e -> {
			if (!drawingArea) {// entering drawing mode
				drawingArea = !drawingArea;
				drawAreaButton.setCaption("Finish Area");
				smallText.setValue("Click or drag area points.");
				drawSidesButton.setEnabled(false);
			}
			else { // finishing polygon
				if (mapComponent.getMapUtilities().getPolygonVertices().size() < 3) {
					Notification.show("Priority area must be at least 3 sides.", Notification.Type.ERROR_MESSAGE);
					return;
				}
				drawingArea = !drawingArea;
				drawAreaButton.setCaption("Start Area");
				smallText.setValue("Select sides or areas to edit.");
				
				// window pops up for user to set info about polygon
				UI.getCurrent().addWindow(priorityAreaCreationWindow);
				drawSidesButton.setEnabled(true);
			}
		});
		
		// alternate between drawingSide mode and finshing side
		drawSidesButton.addClickListener(e -> {
			if (!drawingSide) {// entering drawing mode
				drawingSide = !drawingSide;
				drawSidesButton.setCaption("Finish Sides");
				smallText.setValue("Click consecutive side segments to prioritize.");
				drawAreaButton.setEnabled(false);
			}
			else { // finishing side
				if (mapComponent.getMapUtilities().getNewPrioritySideLines().size() == 0) {
					Notification.show("Please select at least one bank segment.", Notification.Type.ERROR_MESSAGE);
					return;
				}
				drawingSide = !drawingSide;
				drawSidesButton.setCaption("Draw Side");
				smallText.setValue("Select sides or areas to edit.");
				drawAreaButton.setEnabled(true);
				
				// pop-up window so that the user can set info about the priority side
				UI.getCurrent().addWindow(prioritySideCreationWindow);
			}
		});
	}

	// Called when the cancel button is clicked. Disables editing and reverts
	// changes back to the previous save
	public void cancelClick() {
		
		// if drawing an area or a side, clean it up
		if (drawingArea)
			cleanUnfinishedPolygon();
		if (drawingSide) {
			drawingSide = false;
			drawSidesButton.setCaption("Start Side");
		}

		// Reverts the changes by clearing mapPoints and adding storedPoints.
		mapComponent.getMapUtilities().getNewPrioritySideLines().clear();
		mapComponent.getMapUtilities().getAllPrioritySides().clear();
		mapComponent.getMapUtilities().getAllPriorityLines().clear();
		mapComponent.getMapUtilities().removeAllPins();
		mapComponent.getMapUtilities().removeAllLines();
		
		for (int i = 0; i < sidePoints0.size(); i++) {
			UIEdgePoint point = sidePoints0.get(i);
			mapComponent.getMapUtilities().addNewPin(point, -1);
		}
		for (int i = 0; i < sidePoints1.size(); i++) {
			UIEdgePoint point = sidePoints1.get(i);
			mapComponent.getMapUtilities().addNewPin(point, -1);
		}
 
		mapComponent.getMetaInfo().setMappingName(storedName);
		mapComponent.getMetaInfo().setMappingDescription(storedDescription);

		
		mapComponent.updateLinesAndGrid();
		mapComponent.getMainLayout().getControls().getInfoPanel().refreshMappings();
	}

	/*
	 * Called when the save button on the edit bar is clicked. It exits edit mode,
	 * sends the points to dronology, and uses stored points to display the correct
	 * waypoints on the map. 
	 */
	public void saveClick() {
		
		Vector<List<UIEdgePoint>> newWaypoints = mapComponent.getMapUtilities().getOrderedWayPoints();
				
		AreaMappingPersistenceProvider mappingPersistor = AreaMappingPersistenceProvider.getInstance();
		ByteArrayInputStream inStream;
		IAreaMapping amapping = null;

		IAreaMappingRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();

		String id = mapComponent.getMainLayout().getControls().getInfoPanel().getHighlightedAMInfoBox().getId();

		// Sends the information to dronology to be saved.
		try {
			service = (IAreaMappingRemoteService) provider.getRemoteManager()
					.getService(IAreaMappingRemoteService.class);

			byte[] information = service.requestFromServer(id);
			inStream = new ByteArrayInputStream(information);
			amapping = mappingPersistor.loadItem(inStream);

			amapping.setName(mapComponent.getMetaInfo().getMappingName());
			amapping.setDescription(mapComponent.getMetaInfo().getMappingDescription());

			// differentiate between parts
			ArrayList<EdgeLla> oldCoords0 = new ArrayList<>(amapping.getMappedPoints(0));
			for (EdgeLla cord : oldCoords0) {
				amapping.removeCoordinate(0, cord);
			}
			ArrayList<EdgeLla> oldCoords1 = new ArrayList<>(amapping.getMappedPoints(1));
			for (EdgeLla cord : oldCoords1) {
				amapping.removeCoordinate(1, cord);
			}

			// add all the new waypoints to the area mapping			
			for (int i = 0; i < newWaypoints.size(); i++) {
				for (UIEdgePoint way : newWaypoints.get(i)) {
					double lon = 0;
					double lat = 0; 
					double side = 0;
	
					try {
						lon = Double.parseDouble(way.getLongitude());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					try {
						lat = Double.parseDouble(way.getLatitude());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					try {
						side = (double)way.getSide();
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					
					amapping.addMappedArea(i, new EdgeLla(lat, lon, side));
				}
			}
			
			// save upstream side and clear it in mapUtilities
			amapping.setUpstream(mapComponent.getMapUtilities().getUpstream());
			mapComponent.getMapUtilities().setUpstream(null);
			
			// clear old location mappings
			amapping.getLocationMappings().clear();
			
			// save priority areas
			if (mapComponent.getMapUtilities().getAllPriorityAreas().size() > 0) {
				for (int i = 0; i < mapComponent.getMapUtilities().getAllPriorityAreas().size(); i++) {
					amapping.addMappedLocation(mapComponent.getMapUtilities().getAllPriorityAreas().get(i));
				}
			}
			
			// save priority sides
			if (mapComponent.getMapUtilities().getAllPrioritySides().size() > 0) {
				for (int i = 0; i < mapComponent.getMapUtilities().getAllPrioritySides().size(); i++) {
					amapping.addMappedLocation(mapComponent.getMapUtilities().getAllPrioritySides().get(i));
				}
			}
			
			// clear old priority sides
			mapComponent.getMapUtilities().getAllPrioritySides().clear();
			mapComponent.getMapUtilities().getAllPriorityLines().clear();
			mapComponent.getMapUtilities().getNewPrioritySideLines().clear();
			
			ByteArrayOutputStream outs = new ByteArrayOutputStream();
			mappingPersistor.saveItem(amapping, outs);
			byte[] bytes = outs.toByteArray();

			service.transmitToServer(amapping.getId(), bytes);
		} catch (DronologyServiceException | RemoteException e1) {
			e1.printStackTrace();
			MyUI.setConnected(false);
		} catch (PersistenceException e1) {
			e1.printStackTrace();
		}

		// differentiate between sides
		List<EdgeLla> newCoordinatesToSave0 = amapping.getMappedPoints(0);
		List<EdgeLla> newCoordinatesToSave1 = amapping.getMappedPoints(1);
		String newMappingNameToSave = amapping.getName();
		mapComponent.getMainLayout().getWaitingWindow().showWindow("Saving mapping...", () -> {
			// Test if the mapping is updated in dronology
			Collection<AreaMappingInfo> mappings = mapComponent.getMainLayout().getControls().getInfoPanel()
					.getMappingsFromDronology();
			AreaMappingInfo mappingFromDronology = null;
			for (AreaMappingInfo mapping : mappings) {
				if (mapping.getId().equals(id)) {
					mappingFromDronology = mapping;
					break;
				}
			}
			
			// if the coordinate sizes are different, then it is not updated
			if (mappingFromDronology == null || mappingFromDronology.getCoordinates(0).size() != newCoordinatesToSave0.size() || 
					mappingFromDronology.getCoordinates(1).size() != newCoordinatesToSave1.size() || !newMappingNameToSave.equals(mappingFromDronology.getName())) {
				return false;
			} else {
				for (int i = 0; i < newCoordinatesToSave0.size(); ++i) {
					// if the waypoint info is different, then it is not updated
					if (!newCoordinatesToSave0.get(i).equals(mappingFromDronology.getCoordinates(0).get(i))) {
						return false;
					}
				}
				for (int i = 0; i < newCoordinatesToSave1.size(); ++i) {
					// if the waypoint info is different, then it is not updated
					if (!newCoordinatesToSave1.get(i).equals(mappingFromDronology.getCoordinates(1).get(i))) {
						return false;
					}
				}
				// otherwise, it is updated
				return true;
			}
			
		}, closeEvent -> {
			// upon closing, reset and refresh everything
			drawingSide = false;
			drawSidesButton.setCaption("Start Side");
			exitEditMode();
			mapComponent.getMainLayout().getControls().getInfoPanel().refreshMappings();
			mapComponent.getMainLayout().getControls().getInfoPanel().refreshSearchBar();
			mapComponent.getMainLayout()
					.switchMapping(mapComponent.getMainLayout().getControls().getInfoPanel().getMappingInfoBox(id));
		});
	}

	// remove the lines and points from an unfinished polygon
	public void cleanUnfinishedPolygon() {
		drawingArea = false;
		drawAreaButton.setCaption("Start Area");
		mapComponent.getMapUtilities().clearPolygonVertices();
	}
	
	// Enables editing, gets the sides, and adds the edit bar
	public void enterEditMode() {
		sidePoints0 = mapComponent.getMapUtilities().getOrderedWayPoints().get(0);
		sidePoints1 = mapComponent.getMapUtilities().getOrderedWayPoints().get(1);
		storedName = mapComponent.getMetaInfo().getMappingName();
		storedDescription = mapComponent.getMetaInfo().getMappingDescription();

		mapComponent.getMapUtilities().setPrioritiesAreEditable(true);
		mapComponent.getMapUtilities().setSidesAreEditable(false);
		this.setVisible(true);
		mapComponent.getMap().addStyleName("fr_leaflet_map_edit_mode");
	}

	// Disables editing, clears the points, removes the edit bar, 
	// and changes the component styles accordingly.
	public void exitEditMode() {
		sidePoints0.clear();
		sidePoints1.clear();

		storedName = "";
		storedDescription = "";

		mapComponent.getMapUtilities().setPrioritiesAreEditable(false);
		this.setVisible(false);
		mapComponent.getEditSidesController().setVisible(false);

		mapComponent.getMap().removeStyleName("fr_leaflet_map_edit_mode");
	}
	
	// returns true if an area is currently being drawn
	public Boolean getDrawingArea() {
		return drawingArea;
	}
	
	// returns true if a priority side is currenty being drawn
	public Boolean getDrawingSide() {
		return drawingSide;
	}
	
	public Button getDrawAreaButton() {
		return drawAreaButton;
	}
	
	public Button getDrawSideButton() {
		return drawSidesButton;
	}
	
	// ensures that a save click first finishes the area in progress
	public void setFinishAreaThenSave(Boolean finishThenSave) {
		this.finishAreaThenSave = finishThenSave;
	}

	public Boolean getFinishAreaThenSave() {
		return finishAreaThenSave;
	}
	
	// ensures that a save click first finishes the side in progress
	public void setFinishSideThenSave(Boolean finishThenSave) {
		this.finishSideThenSave = finishThenSave;
	}
	
	public Boolean getFinishSideThenSave() {
		return finishSideThenSave;
	}
	
	public AMMapComponent getMapComponent() {
		return mapComponent;
	}
	
	public Boolean getSelectingUpstream() {
		return selectingUpstream;
	}
	
	public void setSelectingUpstream(Boolean selectingUpstream) {
		this.selectingUpstream = selectingUpstream;
	}
	
	public Button getSaveButton() {
		return saveButton;
	}
}
