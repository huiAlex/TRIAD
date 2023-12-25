package edu.nd.dronology.ui.vaadin.areamapping;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.services.core.info.AreaMappingInfo;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMUnsavedChangesConfirmation.ChangeType;

/**
 * Each Mapping has a different info box. It contains information about date creation. It also has buttons for editing or trashing a mapping.
 * 
 * @author Andrew Slavin
 *
 */


public class AMInfoBox extends CustomComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 763381012380912332L;

	private AreaMappingInfo areaMappingInfo;

	private HorizontalLayout titleBar = new HorizontalLayout();

	// Create AMInfoBox in Area Mapping view -- with edit and delete buttons
	public AMInfoBox(AMInfoPanel infoPanel, AreaMappingInfo areaMappingInfo) {
		this(areaMappingInfo);

		// Imports images for buttons.
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource editIcon = new FileResource(new File(basepath + "/VAADIN/img/edit.png"));
		FileResource trashIcon = new FileResource(new File(basepath + "/VAADIN/img/trashcan.png"));

		Button editButton = new Button();
		Button trashButton = new Button();

		editButton.setIcon(editIcon);
		trashButton.setIcon(trashIcon);
		editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		trashButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		
		// Adds listener to the delete button on the mapping box /
		trashButton.addListener(e -> {
			if (infoPanel.getControls().getMainLayout().getMapComponent().getMapUtilities().getSidesAreEditable() || infoPanel.getControls().getMainLayout().getMapComponent().getMapUtilities().getPrioritiesAreEditable()) {
				// Checks if the mapping is in edit mode.
				infoPanel.getControls().getMainLayout().getUnsavedChangesConfirmation().showWindow(
						infoPanel.getHighlightedAMInfoBox().getAreaMappingInfo().getName(), ChangeType.DELETE_MAPPING, e);
			} else {
				infoPanel.getControls().getMainLayout().getDeleteMappingConfirmation().showWindow(getAreaMappingInfo(),
						e);
			}
		});
		// A click on the edit button enables editing, unless edit mode is already
		// enabled, in which case the user is prompted about losing changes.
		editButton.addClickListener(e -> {
			if (!infoPanel.getControls().getMainLayout().getMapComponent().getMapUtilities().getSidesAreEditable() && !infoPanel.getControls().getMainLayout().getMapComponent().getMapUtilities().getPrioritiesAreEditable()) {
				// open edit mode choices window
				infoPanel.getControls().getMainLayout().switchMapping(this);
				infoPanel.getControls().getMainLayout().getMapComponent().getEditSidesController().enterEditMode();
			} else {
				if (infoPanel.getHighlightedAMInfoBox() != null
						&& areaMappingInfo.getId().equals(infoPanel.getHighlightedAMInfoBox().getId()))
					return;
				infoPanel.getControls().getMainLayout().getUnsavedChangesConfirmation().showWindow(
						infoPanel.getHighlightedAMInfoBox().getAreaMappingInfo().getName(), ChangeType.EDIT_ANOTHER, e, this, infoPanel);
			}
		});

		titleBar.addComponents(trashButton, editButton);
	}

	// set the creation and modification time
	public AMInfoBox(AreaMappingInfo areaMappingInfo) {
		this.areaMappingInfo = areaMappingInfo;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, hh:mm aaa");
		long creationTime = areaMappingInfo.getDateCreated();
		String creationFormatted = sdf.format(new Date(creationTime));
		long modifiedTime = areaMappingInfo.getDateModified();
		String modifiedFormatted = sdf.format(new Date(modifiedTime));

		this.addStyleName("info_box");
		this.addStyleName("fr_info_box");

		VerticalLayout mappingDescription = new VerticalLayout();
		mappingDescription.addStyleName("detailed_info_well");

		VerticalLayout allContent = new VerticalLayout();

		// Create name id label.
		Label nameIdLabel = new Label(areaMappingInfo.getName());
		nameIdLabel.addStyleName("info_box_name");

		// Creates 4 different labels
		Label createdLabel = new Label("Created:  " + creationFormatted);
		Label modifiedLabel = new Label("Last Modified:  " + modifiedFormatted);
		Label sideALabel = new Label(areaMappingInfo.getCoordinates(0).size() + " side A coordinates");
		Label sideBLabel = new Label(areaMappingInfo.getCoordinates(1).size() + " side B coordinates");
		
		mappingDescription.addComponents(createdLabel, modifiedLabel, sideALabel, sideBLabel);

		titleBar.addComponents(nameIdLabel);

		// Adds all content together and aligns the buttons on the right.
		allContent.addComponents(titleBar, mappingDescription);

		setCompositionRoot(allContent);
	}

	public AreaMappingInfo getAreaMappingInfo() {
		return areaMappingInfo;
	}

	// Gets the name of the route.
	public String getName() {
		return areaMappingInfo.getName();
	}

	// Gets the route id.
	@Override
	public String getId() {
		return areaMappingInfo.getId();
	}
}
