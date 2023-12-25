package edu.nd.dronology.ui.vaadin.activeflights;


import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.info.MissionInfo;
import edu.nd.dronology.services.core.info.UAVMappingInfo;
import edu.nd.dronology.services.core.items.IMissionPlan;
import edu.nd.dronology.services.core.items.IUAVMissionDescription;
import edu.nd.dronology.services.core.persistence.MissionPlanningPersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IMissionPlanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.extensions.missionplanning.persistence.PersistableMissionPlan;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.missionplanning.MPControlsComponent;
import edu.nd.dronology.ui.vaadin.missionplanning.MPInfoBox;
import edu.nd.dronology.ui.vaadin.missionplanning.MPInfoPanel;
import edu.nd.dronology.ui.vaadin.missionplanning.MPMainLayout;
import edu.nd.dronology.ui.vaadin.missionplanning.MissionPlanningIntoNameComparator;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/*
 * This is a window that will allow users to choose a mission to execute from the list of missions in the mission planning
 * tab as opposed to having to choose a json on their computer
 * 
 */
public class AFChooseMissionWindow extends Window {
	
	private static final long serialVersionUID=32682356984592L;
	
	private MPMainLayout mpLayout = new MPMainLayout();
	private HorizontalLayout buttonLayout = new HorizontalLayout();
	private MPControlsComponent controls = new MPControlsComponent(mpLayout);
	private MPInfoPanel infoPanel = new MPInfoPanel(controls);
	private Collection<IUAVProxy> drones = null;
	
	public AFChooseMissionWindow(AFInfoPanel AFinfo) {
		 // update the info panel with all missions from dronology and remove all the buttons that allow for editing and deleting missions
		 infoPanel.refreshMission();
		 infoPanel.removeButton();
		
		 // total layout for the whole page
		 VerticalLayout allContent = new VerticalLayout();
		 
		 // remove the edit and delete buttons from each info box
		 for (Component box : infoPanel.getMissions()) {
			 ((MPInfoBox) box).removeButtons();
		 }
		 
		 // execute and cancel buttons
		 Button cancel = new Button("Cancel");
		 Button select = new Button("Select");
		 select.addStyleName(ValoTheme.BUTTON_PRIMARY);
		 Button execute = new Button("Execute");
		 execute.addStyleName("btn-okay");
		 buttonLayout.addComponents(cancel, select, execute);
		 
		 // gets all active drones from dronology
		 BaseServiceProvider provider = MyUI.getProvider();
		IDroneSetupRemoteService service1;
		try {
			service1 = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
			Collection<IUAVProxy> activeDrones = service1.getActiveUAVs();
			drones = new ArrayList<>(activeDrones);
		} catch (DronologyServiceException | RemoteException e1) {
			MyUI.setConnected(false);
		}
		 
		// Listens for clicks on the panel and switches the highlighted mission accordingly
		VerticalLayout missions = infoPanel.getMissions();
		missions.addLayoutClickListener(e -> {
			Component childComponent = e.getChildComponent();
			int index = -1;
			if (childComponent != null && childComponent.getClass().equals(MPInfoBox.class)) {
				infoPanel.unhighlightAllInfoBoxes();
				MissionInfo info = ((MPInfoBox) childComponent).getMissionInfo();
				index = infoPanel.getMissionIndex(info);
				infoPanel.highlightInfoBox(index);
				
				// same as pressing select button
				if (e.isDoubleClick()) {
					MPInfoBox highlighted = infoPanel.getHighlightedMPInfoBox();
					MissionInfo missionInfo = highlighted.getMissionInfo();
					MissionPlanningPersistenceProvider planPersistor = MissionPlanningPersistenceProvider.getInstance();
					ByteArrayInputStream inStream;
					IMissionPlan missionPlan = null;
					IMissionPlanningRemoteService service;
					String id = missionInfo.getId();
					
					// pulls information from dronology, gets the info for the current mission
					try {
						service = (IMissionPlanningRemoteService) provider.getRemoteManager().getService(IMissionPlanningRemoteService.class);
						byte[] information = service.requestFromServer(id);
						inStream = new ByteArrayInputStream(information);
						missionPlan = planPersistor.loadItem(inStream);
					}
					catch (DronologyServiceException | RemoteException e1) {
						e1.printStackTrace();
						MyUI.setConnected(false);
					} catch (PersistenceException e1) {
						e1.printStackTrace();
					}
					
					List<IUAVMissionDescription> mission = ((PersistableMissionPlan) missionPlan).getMissionDescriptions();
					
					// let the user know that there are not currently enough active drones
					boolean enoughDrones = true;
					if (drones.size() < mission.size()) {
						enoughDrones = false;
						Notification.show("Sorry, but you do not have enough active UAVs to execute this mission.", Type.ERROR_MESSAGE);
					}
					
					//MissionHandler handler = new MissionHandler();
					//handler.setMissionContent(info);
					//handler.executeMission();
					if (enoughDrones) {
						AFMapDrones mapDrones = new AFMapDrones(mission, missionInfo);
						UI.getCurrent().addWindow(mapDrones);
						UI.getCurrent().removeWindow(this);
					}
				}
			}
		});
		
		// Execute Button click listener
		select.addClickListener(e -> {
			MPInfoBox highlighted = infoPanel.getHighlightedMPInfoBox();
			if (highlighted == null) {
				Notification.show("You must select a mission to execute.", Type.WARNING_MESSAGE);
			}
			else {
				MissionInfo missionInfo = highlighted.getMissionInfo();
				MissionPlanningPersistenceProvider planPersistor = MissionPlanningPersistenceProvider.getInstance();
				ByteArrayInputStream inStream;
				IMissionPlan missionPlan = null;
				IMissionPlanningRemoteService service;
				String id = missionInfo.getId();
				
				// pulls information from dronology, gets the info for the current mission
				try {
					service = (IMissionPlanningRemoteService) provider.getRemoteManager().getService(IMissionPlanningRemoteService.class);
					byte[] information = service.requestFromServer(id);
					inStream = new ByteArrayInputStream(information);
					missionPlan = planPersistor.loadItem(inStream);
				}
				catch (DronologyServiceException | RemoteException e1) {
					e1.printStackTrace();
					MyUI.setConnected(false);
				} catch (PersistenceException e1) {
					e1.printStackTrace();
				}
				
				List<IUAVMissionDescription> mission = ((PersistableMissionPlan) missionPlan).getMissionDescriptions();
				
				// let the user know that there are not currently enough active drones
				boolean enoughDrones = true;
				if (drones.size() < mission.size()) {
					enoughDrones = false;
					Notification.show("Sorry, but you do not have enough active UAVs to execute this mission.", Type.ERROR_MESSAGE);
				}
				
				//MissionHandler handler = new MissionHandler();
				//handler.setMissionContent(info);
				//handler.executeMission();
				if (enoughDrones) {
					AFMapDrones mapDrones = new AFMapDrones(mission, missionInfo);
					UI.getCurrent().addWindow(mapDrones);
					UI.getCurrent().removeWindow(this);
				}
			}
		});
		
		// Execute Button click listener
		execute.addClickListener(e -> {
			MPInfoBox highlighted = infoPanel.getHighlightedMPInfoBox();
			if (highlighted == null) {
				Notification.show("You must select a mission to execute.", Type.WARNING_MESSAGE);
			}
			else {
				MissionInfo missionInfo = highlighted.getMissionInfo();
				MissionPlanningPersistenceProvider planPersistor = MissionPlanningPersistenceProvider.getInstance();
				ByteArrayInputStream inStream;
				IMissionPlan missionPlan = null;
				IMissionPlanningRemoteService service = null;
				String id = missionInfo.getId();
				
				// pulls information from dronology, gets the info for the current mission
				try {
					service = (IMissionPlanningRemoteService) provider.getRemoteManager().getService(IMissionPlanningRemoteService.class);
					byte[] information = service.requestFromServer(id);
					inStream = new ByteArrayInputStream(information);
					missionPlan = planPersistor.loadItem(inStream);
				}
				catch (DronologyServiceException | RemoteException e1) {
					e1.printStackTrace();
					MyUI.setConnected(false);
				} catch (PersistenceException e1) {
					e1.printStackTrace();
				}
				
				List<IUAVMissionDescription> mission = ((PersistableMissionPlan) missionPlan).getMissionDescriptions();
				
				// let the user know that there are not currently enough active drones
				boolean enoughDrones = true;
				if (drones.size() < mission.size()) {
					enoughDrones = false;
					Notification.show("Sorry, but you do not have enough active UAVs to execute this mission.", Type.ERROR_MESSAGE);
				}
				
				// execute the mission
				if (enoughDrones)  {
					ArrayList<String> activeDroneNames = new ArrayList<>();
					for (IUAVProxy drone : drones) {
						activeDroneNames.add(drone.getID());
					}
					// match any uav that has the same name in the mission plan and active uav panel
					UAVMappingInfo mappingInfo = new UAVMappingInfo();
					ArrayList<String> mappedNames = new ArrayList<>();
					for (IUAVMissionDescription uav : mission) {
						if (activeDroneNames.contains(uav.getName())) {
							mappingInfo.addAttribute(uav.getName(), uav.getName());
							activeDroneNames.remove(uav.getName());
							mappedNames.add(uav.getName());
						}
					}
					// match the rest of the drones with whatever is first in the list of names
					for (IUAVMissionDescription uav : mission) {
						if (!mappedNames.contains(uav.getName())) {
							mappingInfo.addAttribute(uav.getName(), activeDroneNames.get(0));
							activeDroneNames.remove(0);
						}
					}
					// use service to execute the mission with the mapping
					try {
						service.executeMissionPlan(missionInfo, mappingInfo);
					} catch (RemoteException | DronologyServiceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					UI.getCurrent().removeWindow(this);
				}
			}
		});
		
		
		// Cancel Button click listener
		cancel.addClickListener(e -> {
			UI.getCurrent().removeWindow(this);
		});
		
		// Highlights missions selected with the search bar
		infoPanel.setUsingOtherSearchBar();
		infoPanel.refreshSearchBar();
		 
		infoPanel.getSearchBar().addValueChangeListener(event -> {
        	String name = infoPanel.getSearchBar().getValue();
        	
        	// go through all the missions in dronology and highlight the right one
        	IMissionPlanningRemoteService service;
        	try {
             	service=(IMissionPlanningRemoteService) provider.getRemoteManager().getService(IMissionPlanningRemoteService.class);
             	List<MissionInfo> allMissions=new ArrayList<>(service.getItems());
             	Collections.sort(allMissions, new MissionPlanningIntoNameComparator());
        	
	         	for(MissionInfo mission : allMissions )
	         	{
	         		if (name.equals(mission.getName())) {
	         			infoPanel.unhighlightAllInfoBoxes();
	        			infoPanel.highlightInfoBox(controls.getInfoPanel().getMissionIndex(mission));
	            		break;
	         		}
	    	 	}
         	
        	} catch(RemoteException | DronologyServiceException e) {
             	MyUI.setConnected(false);
             	e.printStackTrace();
             }
        	
        	infoPanel.getSearchBar().setValue("");
	    });
		 
		 allContent.addComponents(infoPanel, buttonLayout);
		 
		 // format the window 
		 this.setContent(allContent);
		 this.setResizable(false);
		 this.setClosable(true);
		 this.setModal(true);
		 this.setHeight("950");
		 this.setWidth("420");
	}	

}
