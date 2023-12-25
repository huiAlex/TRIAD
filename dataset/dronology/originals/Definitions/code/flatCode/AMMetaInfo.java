package edu.nd.dronology.ui.vaadin.areamapping;

import java.io.File;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.services.core.info.AreaMappingInfo;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMGenerateRouteDroneWindow;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMUnsavedChangesConfirmation.ChangeType;

/**
 * Implements the top panel, which displays information about the name of the mapping and the number of coordinates on each side.
 * 
 * @author Andrew Slavin
 *
 */


public class AMMetaInfo extends CustomComponent {
	/** 
	 * 
	 */
	private static final long serialVersionUID = -1112368712373412381L;
	private Label nameLabel;
	private Label sideANumLabel;
	private Label sideBNumLabel;
	private Label descriptionLabel;
	private Button generateRouteButton;
	private AreaMappingInfo mappingInfo;
	private int num0;
	private int num1;
	
	private HorizontalLayout allContent;
	private VerticalLayout leftSide;
	private VerticalLayout rightSide;
	private CheckBox autoZoomingCheckBox;
	
	private AMGenerateRouteDroneWindow generateRouteDroneWindow;

	public AMMetaInfo(AMMapComponent map) {
		
		sideANumLabel = new Label("");
		sideBNumLabel = new Label("");
		nameLabel = new Label("No Mapping Selected");

		// The two labels are initialized separately so that they can be changed
		// independently later.
		HorizontalLayout labels = new HorizontalLayout();
		labels.addComponents(nameLabel, sideANumLabel, sideBNumLabel);

		autoZoomingCheckBox = new CheckBox("Zoom to Route");
		autoZoomingCheckBox.setValue(true);

		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

		FileResource editIcon = new FileResource(new File(basepath + "/VAADIN/img/editButtonFull.png"));
		Button editButton = new Button("Edit");
		editButton.setIcon(editIcon);
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

		FileResource deleteIcon = new FileResource(new File(basepath + "/VAADIN/img/deleteButtonFull.png"));
		Button deleteButton = new Button("Delete");
		deleteButton.setIcon(deleteIcon);
		deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
		generateRouteButton = new Button("Generate Route");
		generateRouteButton.setWidth("150px");

		// A layout is used to hold the description label so that a LayoutClickListener
		// can be added later.
		HorizontalLayout descriptionHolder = new HorizontalLayout();
		descriptionLabel = new Label("");
		descriptionHolder.addComponent(descriptionLabel);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(generateRouteButton, editButton, deleteButton);

		HorizontalLayout checkboxes = new HorizontalLayout();
		checkboxes.addComponents(autoZoomingCheckBox);

		leftSide = new VerticalLayout();
		leftSide.addComponents(labels, descriptionHolder);

		rightSide = new VerticalLayout();
		rightSide.addComponents(buttons, checkboxes);

		// "leftSide" includes the labels and description, while "rightSide" includes
		// the buttons and checkboxes.
		allContent = new HorizontalLayout();
		allContent.setStyleName("fr_route_meta_info");
		allContent.addStyleName("has_route");
		
		// only add left side when initialized, right side is added when a route is
		// selected
		allContent.addComponent(leftSide);

		rightSide.addStyleName("route_meta_controls");
		leftSide.addStyleName("route_meta_label_description");

		TextField nameEditField = new TextField();
		TextField descriptionEditField = new TextField();

		nameEditField.setStyleName("name_edit_field");
		descriptionEditField.setStyleName("description_edit_field");
		nameLabel.setStyleName("name_lable");
		sideANumLabel.setStyleName("waypoint_num_lable");
		sideBNumLabel.setStyleName("waypoint_num_lable");
		descriptionLabel.setStyleName("description_lable");

		// Click listeners for the edit and delete buttons.
		editButton.addClickListener(e -> {
			map.getEditSidesController().enterEditMode();
		});
		
		// prompt user to confirm that they want to delete the mapping
		deleteButton.addClickListener(e -> {
			if (map.getMapUtilities().getSidesAreEditable() || map.getMapUtilities().getPrioritiesAreEditable()) {
				map.getMainLayout().getUnsavedChangesConfirmation().showWindow(map.getMainLayout().getControls()
						.getInfoPanel().getHighlightedAMInfoBox().getAreaMappingInfo().getName(),
						ChangeType.DELETE_MAPPING, e);
			} else {
				map.getMainLayout().getDeleteMappingConfirmation().showWindow(
						map.getMainLayout().getControls().getInfoPanel().getHighlightedAMInfoBox().getAreaMappingInfo(),
						e);
			}
		});
		
		// click listener for the generate route button
		// brings up a window to select drones
		generateRouteButton.addClickListener(e -> {
			generateRouteDroneWindow = new AMGenerateRouteDroneWindow(map);
			UI.getCurrent().addWindow(generateRouteDroneWindow);
			generateRouteDroneWindow.closeIfNoDrones();
		});

		// Double click allows user to edit label by turning it into a textbox.
		labels.addLayoutClickListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(LayoutClickEvent event) {
				if (!map.getMapUtilities().getSidesAreEditable() && !map.getMapUtilities().getPrioritiesAreEditable())
					return;
				if (event.getClickedComponent() == nameLabel) {
					if (event.isDoubleClick()) {
						// Change layout to accommodate for the textfield.
						allContent.removeAllComponents();

						HorizontalLayout nameArea = new HorizontalLayout();
						nameEditField.setValue(nameLabel.getValue());
						nameArea.addComponents(nameEditField, sideANumLabel, sideANumLabel);

						VerticalLayout textLayout = new VerticalLayout();
						textLayout.addComponents(nameArea, descriptionHolder);
						textLayout.addStyleName("mapping_meta_label_description");

						allContent.addComponents(textLayout, rightSide);
					}
				}
			}
		});
		// Double click allows user to edit description by turning it into a textbox.
		descriptionHolder.addLayoutClickListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void layoutClick(LayoutClickEvent event) {
				if (!map.getMapUtilities().getSidesAreEditable() && !map.getMapUtilities().getPrioritiesAreEditable())
					return;
				if (event.getClickedComponent() == descriptionLabel) {
					if (event.isDoubleClick()) {
						// Change layout to accommodate for the textfield.
						allContent.removeAllComponents();
						labels.removeAllComponents();

						VerticalLayout textLayout = new VerticalLayout();
						textLayout.addStyleName("mapping_meta_label_description");
						labels.addComponents(nameLabel, sideANumLabel, sideBNumLabel);

						descriptionEditField.setValue(descriptionLabel.getValue());
						textLayout.addComponents(labels, descriptionEditField);
						allContent.addComponents(textLayout, rightSide);
					}
				}
			}
		});
		// Textfield turns back into the correct label once the user clicks away.
		nameEditField.addBlurListener(e -> {
			nameLabel.setValue(nameEditField.getValue());

			labels.removeAllComponents();
			labels.addComponents(nameLabel, sideANumLabel, sideBNumLabel);

			leftSide.removeAllComponents();
			leftSide.addComponents(labels, descriptionHolder);

			allContent.removeAllComponents();
			allContent.addComponents(leftSide, rightSide);

			rightSide.addStyleName("route_meta_controls");
		});
		// Once the user clicks away from the description field, the correct label is
		// shown.
		descriptionEditField.addBlurListener(e -> {
			descriptionLabel.setValue(descriptionEditField.getValue());

			labels.removeAllComponents();
			labels.addComponents(nameLabel, sideANumLabel, sideBNumLabel);

			leftSide.removeAllComponents();
			leftSide.addComponents(labels, descriptionHolder);

			allContent.removeAllComponents();
			allContent.addComponents(leftSide, rightSide);

			rightSide.addStyleName("route_meta_controls");
		});

		setCompositionRoot(allContent);
	}

	public String getMappingName() {
		return nameLabel.getValue();
	}

	public void setMappingName(String name) {
		nameLabel.setValue(name);
	}

	public String getMappingDescription() {
		return descriptionLabel.getValue();
	}

	public void setMappingDescription(String description) {
		descriptionLabel.setValue(description);
	}

	public void showInfoForSelectedMapping(AreaMappingInfo info) {
		mappingInfo = info;
		nameLabel.setValue(info.getName());
		descriptionLabel.setValue(info.getDescription());
		setNumWaypoints(info.getCoordinates(0).size(), info.getCoordinates(1).size());

		allContent.addComponent(rightSide);
	}

	public void showInfoWhenNoMappingIsSelected() {
		nameLabel.setValue("No Mapping Selected");
		descriptionLabel.setValue("");
		sideANumLabel.setValue("");
		sideBNumLabel.setValue("");
		allContent.removeComponent(rightSide);
	}

	// Ensures that the correct description of coordinates is shown.
	public void setNumWaypoints(int num0, int num1) {
		if (num0 == 1) {
			sideANumLabel.setValue("(" + num0 + " Side A coordinate)");
			this.num0 = num0;
		} else {
			sideANumLabel.setValue("(" + num0 + " Side A coordinates)");
			this.num0 = num0;
		}
		if (num1 == 1) {
			sideBNumLabel.setValue("(" + num1 + " Side B coordinate)");
			this.num1 = num1;
		} else {
			sideBNumLabel.setValue("(" + num1 + " Side B coordinates)");
			this.num1 = num1;
		}
	}


	public boolean isAutoZoomingChecked() {
		return autoZoomingCheckBox.getValue() == true;
	}
	
	public AreaMappingInfo getMappingInfo() {
		return mappingInfo;
	}
	
	public int getNum0() {
		return num0;
	}
	
	public int getNum1() {
		return num1;
	}
	
}