package edu.nd.dronology.ui.vaadin.areamapping;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import edu.nd.dronology.services.core.info.AreaMappingInfo;
import edu.nd.dronology.services.core.remote.IAreaMappingRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMUnsavedChangesConfirmation.ChangeType;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/**
 * This is the side panel that shows the list of existing mappings.
 * 
 * @author Andrew Slavin
 *
 */

public class AMInfoPanel extends CustomComponent{
	
	private static final long serialVersionUID = 3661239148456256352L;

	private VerticalLayout mappingListLayout = new VerticalLayout(); // contains list of mappings
	private MapCreationComponent mapCreation; // contains new mapping and sub-map buttons
	private VerticalLayout inPanelLayout = new VerticalLayout(); // contains mapCreation and mappingListLayout
	private AMControlsComponent controlComponent;
	private Panel panel = new Panel();
	private List<String> mappingList = new ArrayList<>();
	ComboBox<String> comboBox = new ComboBox<>(); // search bar for existing mappings
	
	public AMInfoPanel(AMControlsComponent controls) {
		controlComponent = controls;
		mapCreation = new MapCreationComponent(this, controls);
		inPanelLayout.addComponents(mapCreation, comboBox, mappingListLayout);
		panel.setContent(inPanelLayout);
		panel.addStyleName("am_info_panel");
		panel.addStyleName("control_panel");

		setCompositionRoot(panel);
		
        BaseServiceProvider provider=MyUI.getProvider();
		
		refreshMappings();
		refreshSearchBar();
		comboBox.setItems(mappingList);
		comboBox.setSizeFull();
		
	    //highlight the route chosen on the map 
		comboBox.addValueChangeListener(event -> {
        	
        	String name=comboBox.getValue();
        	
        	IAreaMappingRemoteService service;
        	 try {
             	service=(IAreaMappingRemoteService) provider.getRemoteManager().getService(IAreaMappingRemoteService.class);
             	List<AreaMappingInfo> allMappings = new ArrayList<>(service.getItems());
             	Collections.sort(allMappings, new AreaMappingIntoNameComparator());
             	
             	//puts the routes into the list that gets loaded onto the search bar 
             	for(AreaMappingInfo mapping : allMappings ) {
             		if (name.equals(mapping.getName())) {
             			controls.getInfoPanel().unhighlightAllInfoBoxes();
            			controls.getInfoPanel().highlightInfoBox(controls.getInfoPanel().getMappingIndex(mapping));

                		// Displays the mapping on map also.
            			controlComponent.getMainLayout().setMapComponent(controls.getMainLayout().getMapComponent());
            			controlComponent.getMainLayout().getMapComponent().getMetaInfo().showInfoForSelectedMapping(mapping);
            			controlComponent.getMainLayout().getMapComponent().displayAreaMapping(mapping);
            			controlComponent.getMainLayout().getMapComponent().setMappingCenter();
                		break;
             		}
        	 	}
             	
             } catch(RemoteException | DronologyServiceException e) {
             	MyUI.setConnected(false);
             	e.printStackTrace();
             }
        	 // reset search bar
        	 comboBox.setValue("");
        });
		
		// click listener for when another mapping is selected
		mappingListLayout.addLayoutClickListener(e -> {
			// if editing, bring up unsaved changes box
			if (controls.getMainLayout().getMapComponent().getMapUtilities().getSidesAreEditable() || controls.getMainLayout().getMapComponent().getMapUtilities().getPrioritiesAreEditable()) {
				controls.getMainLayout().getUnsavedChangesConfirmation()
						.showWindow(this.getHighlightedAMInfoBox().getAreaMappingInfo().getName(), ChangeType.SWITCH_MAPPING, e);
			} else {
				// If map is not in edit mode, then just switch to the other mapping.
				Component childComponent = e.getChildComponent();
				if (childComponent != null && childComponent.getClass().equals(AMInfoBox.class)) {
					controls.getMainLayout().switchMapping((AMInfoBox) childComponent);
				}
			}
		});
		
	}
	
	

	// Ensures mappings are updated by removing and re-adding mappings.
	public void refreshMappings() {
		AMInfoBox highlightedBox = this.getHighlightedAMInfoBox();
		String highlightedId = highlightedBox == null ? "" : highlightedBox.getId();

		mappingListLayout.removeAllComponents();
		Collection<AreaMappingInfo> allMappings = getMappingsFromDronology();
		panel.setCaption(allMappings.size() + " Mappings");

		// Iterates through the mappings, gets the fields of each, and creates an infobox.
		for (AreaMappingInfo info : allMappings) {
			AMInfoBox mappingBox = addMapping(info);
			// To preserve previous route selection
			if (highlightedId.equals(mappingBox.getId())) {
				this.getControls().getMainLayout().switchMapping(mappingBox);
			}
		}
	}
	
	//ensures that the routes in the search bar are updated by removing and re-adding them
	public void refreshSearchBar() {
		
		//puts the routes into the list that gets loaded onto the search bar
		mappingList.clear();
		
		Collection<AreaMappingInfo> allMappings = getMappingsFromDronology();
		for(AreaMappingInfo mapping : allMappings )
    	{
    		mappingList.add(mapping.getName());
    	}
		comboBox.setItems(mappingList);
	}
	
	// fetch mappings information from dronology
	public Collection<AreaMappingInfo> getMappingsFromDronology() {
		IAreaMappingRemoteService service;
		BaseServiceProvider provider = MyUI.getProvider();
		try {
			service = (IAreaMappingRemoteService) provider.getRemoteManager()
					.getService(IAreaMappingRemoteService.class);
			List<AreaMappingInfo> allMappings = new ArrayList<>(service.getItems());
			Collections.sort(allMappings, new AreaMappingIntoNameComparator());

			return allMappings;
		} catch (RemoteException | DronologyServiceException e) {
			MyUI.setConnected(false);
			e.printStackTrace();
		}
		return null;
	}
	
	// Gets AreaMappingInfo based on mapping index.
	public AreaMappingInfo getAreaMappingInfo(int index) {
		return ((AMInfoBox) mappingListLayout.getComponent(index)).getAreaMappingInfo();
	}

	// Gets the mapping index based on the AreaMappingInfo.
	public int getMappingIndex(AreaMappingInfo info) {
		for (int i = 0; i < mappingListLayout.getComponentCount(); i++) {
			if (info.equals(((AMInfoBox) mappingListLayout.getComponent(i)).getAreaMappingInfo()))
				return i;
		}
		return -1;
	}

	// Gets the mapping info box based on the AreaMappingInfo.
	public AMInfoBox getMappingInfoBox(AreaMappingInfo info) {
		for (int i = 0; i < mappingListLayout.getComponentCount(); i++) {
			if (info.equals(((AMInfoBox) mappingListLayout.getComponent(i)).getAreaMappingInfo()))
				return (AMInfoBox) mappingListLayout.getComponent(i);
		}
		return null;
	}

	// Gets the mapping info box based on the AreaMappingInfo id.
	public AMInfoBox getMappingInfoBox(String id) {
		for (int i = 0; i < mappingListLayout.getComponentCount(); i++) {
			if (id.equals(((AMInfoBox) mappingListLayout.getComponent(i)).getAreaMappingInfo().getId()))
				return (AMInfoBox) mappingListLayout.getComponent(i);
		}
		return null;
	}

	// Adds a mapping to the infobox based on parameters.
	public AMInfoBox addMapping(AreaMappingInfo areaMappingInfo) {
		AMInfoBox mappingBox = new AMInfoBox(this, areaMappingInfo);
		mappingListLayout.addComponent(mappingBox);
		return mappingBox;
	}

	public void unhighlightAllInfoBoxes() {
		for (int i = 0; i < mappingListLayout.getComponentCount(); i++) {
			mappingListLayout.getComponent(i).removeStyleName("info_box_focus");
		}
	}

	public void highlightInfoBox(int index) {
		mappingListLayout.getComponent(index).addStyleName("info_box_focus");
	}

	public AMInfoBox getHighlightedAMInfoBox() {
		for (int i = 0; i < mappingListLayout.getComponentCount(); i++) {
			if (mappingListLayout.getComponent(i).getStyleName().contains("info_box_focus"))
				return (AMInfoBox) mappingListLayout.getComponent(i);
		}
		return null;
	}

	// Gets the mapping layout.
	public VerticalLayout getMappings() {
		return mappingListLayout;
	}

	// Gets the controls component that was passed in through the constructor.
	public AMControlsComponent getControls() {
		return controlComponent;
	}

	// Gets the button used to display the mapping creation window.
	public Button getNewMappingButtonFromCreationComponent() {
		return mapCreation.getNewMappingButton();
	}

	// Removes the current window (used to remove mapping creation window).
	public void removeNewMappingWindow() {
		mapCreation.removeNewMappingWindow();
	}	
}
