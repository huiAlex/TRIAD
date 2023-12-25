package edu.nd.dronology.ui.vaadin.areamapping;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.ui.vaadin.areamapping.windows.AMSaveAreaWithoutFinishWindow;

/**
 * This allows the user to create and edit mappings. However, it is primarily a shell with the necessary buttons.
 * The save button and cancel button functionality is primarily carried out by saveClick and cancelClick in the areas controller.
 * 
 * @author Andrew Slavin
 *
 */

public class AMEditSidesController extends CustomComponent {
	/**
	 *  
	 */
	private static final long serialVersionUID = 212323831233690123L;
	private AMMapComponent mapComponent;
	private HorizontalLayout totalLayout = new HorizontalLayout();
	private Button cancelButton = new Button("Cancel");
	private Button saveButton = new Button("Save");
	private Button editPrioritiesButton = new Button("Priorities");
	private Button editSidesButton = new Button("Sides");
	private RadioButtonGroup<String> group = new RadioButtonGroup<>();
	private Label smallText = new Label("Left click to add a side coordinate. Drag waypoints to move.");
	
	private List<UIEdgePoint> storedPoints0 = new ArrayList<>();
	private List<UIEdgePoint> storedPoints1 = new ArrayList<>();
	private Boolean sideA = false;

	public AMEditSidesController(AMMapComponent mapComponent) {
		this.mapComponent = mapComponent;

		setStyleName("am_edit_bar");
		smallText.setStyleName("small_text");

		cancelButton.setHeight("25px");
		saveButton.setHeight("25px");
		editPrioritiesButton.setHeight("35px");
		editSidesButton.setHeight("30px");
		editSidesButton.addStyleName("sides_button");
		editSidesButton.setEnabled(false);
		editSidesButton.addStyleName("toggle_button");
		editPrioritiesButton.addStyleName("toggle_button");
		group.setHeight("10px");
		group.setItems("Side A", "Side B");
		group.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		group.setSelectedItem("Side A");
		totalLayout.addComponents(editSidesButton, editPrioritiesButton, smallText, group, cancelButton, saveButton);
		setCompositionRoot(totalLayout);

		// Click listeners for the cancel and saves buttons on edit bar
		cancelButton.addClickListener(e -> {
			exitEditMode();
			mapComponent.getEditPrioritiesController().cancelClick();
		});
		saveButton.addClickListener(e -> {
			
			// if the user hasn't selected upstream yet, prompt them to do so
			if (mapComponent.getMapUtilities().getUpstream() == null) {
				Notification.show("Please select the most upstream side A coordinate.", Notification.Type.ERROR_MESSAGE);
				mapComponent.getEditPrioritiesController().setSelectingUpstream(true);
				return;
			}
			
			// reset buttons and check that area is correct number of sides
			mapComponent.getEditPrioritiesController().getDrawAreaButton().setEnabled(true);
			mapComponent.getEditPrioritiesController().getDrawSideButton().setEnabled(true);
			if (mapComponent.getEditPrioritiesController().getDrawingArea()) {
				if (mapComponent.getMapUtilities().getPolygonVertices().size() < 3) {
					Notification.show("Please finish your unfinished priority area.", Notification.Type.ERROR_MESSAGE);
					return;
				}
				UI.getCurrent().addWindow(new AMSaveAreaWithoutFinishWindow(mapComponent.getEditPrioritiesController()));
			}
			else if (mapComponent.getEditPrioritiesController().getDrawingSide()) {
				if (mapComponent.getMapUtilities().getNewPrioritySideLines().size() == 0) {
					Notification.show("Please finish your unfinished priority area.", Notification.Type.ERROR_MESSAGE);
				}
				UI.getCurrent().addWindow(new AMSaveAreaWithoutFinishWindow(mapComponent.getEditPrioritiesController()));
			}
			else {
				mapComponent.getEditPrioritiesController().saveClick();
				exitEditMode();
			}
		});
		
		// if radio button is switched, change side of mapping
		group.addValueChangeListener(e -> {
			sideA = !sideA;
		});
		
		editPrioritiesButton.addClickListener(e -> {
			exitEditMode();
			mapComponent.getEditPrioritiesController().enterEditMode();
		});
		
	}


	// Enables editing, adds the edit bar, and calls the enableMappingEditing function
	// from MapMarkerUtilities.
	public void enterEditMode() {
		storedPoints0 = mapComponent.getMapUtilities().getOrderedWayPoints().get(0);
		storedPoints1 = mapComponent.getMapUtilities().getOrderedWayPoints().get(1);

		mapComponent.getMapUtilities().setSidesAreEditable(true);
		mapComponent.getMapUtilities().setPrioritiesAreEditable(false);

		this.setVisible(true);

		mapComponent.getMap().addStyleName("fr_leaflet_map_edit_mode");
	}

	// Disables editing, removes the edit bar, and changes the component styles
	// accordingly.
	public void exitEditMode() {
		storedPoints0.clear();
		storedPoints1.clear();

		mapComponent.getMapUtilities().setSidesAreEditable(false);
		this.setVisible(false);

		mapComponent.getMap().removeStyleName("fr_leaflet_map_edit_mode");
	}
	public Boolean getSideA() {
		return sideA;
	}
	
	public void setSideA() {
		group.setSelectedItem("Side A");
	}
	
	public Button getSaveButton() {
		return saveButton;
	}
	
}
