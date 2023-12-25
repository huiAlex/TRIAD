package edu.nd.dronology.ui.vaadin.activeflights;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.info.MissionInfo;
import edu.nd.dronology.services.core.info.UAVMappingInfo;
import edu.nd.dronology.services.core.items.IUAVMissionDescription;
import edu.nd.dronology.services.core.remote.IDroneSetupRemoteService;
import edu.nd.dronology.services.core.remote.IMissionPlanningRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.connector.BaseServiceProvider;
import edu.nd.dronology.ui.vaadin.start.MyUI;

/*
 * This class produces a window that is used to map the drones in a mission to the drones available in active flights
 * 
 * @author Jack Hill
 */
public class AFMapDrones extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8656528814008366370L;
	
	private Map<String, String> droneMapping = new HashMap<String, String>();
	private VerticalLayout totalLayout = new VerticalLayout();
	private VerticalLayout leftMappingLayout = new VerticalLayout();
	private VerticalLayout rightMappingLayout = new VerticalLayout();
	private HorizontalLayout mappingLayout = new HorizontalLayout();
	private Collection<IUAVProxy> drones;
	private IDroneSetupRemoteService service;
	private BaseServiceProvider provider = MyUI.getProvider();
	private ArrayList<String> droneList = new ArrayList<>();
	private ArrayList<String> comboList = new ArrayList<>();
	private ArrayList<ComboBox<String>> allBoxes = new ArrayList<>();
	private UAVMappingInfo mappingInfo = new UAVMappingInfo();

	public AFMapDrones(List<IUAVMissionDescription> mission, MissionInfo missionInfo) {
		// pulls all of the active drones from dronology
		try {
			service = (IDroneSetupRemoteService) provider.getRemoteManager().getService(IDroneSetupRemoteService.class);
			Collection<IUAVProxy> activeDrones = service.getActiveUAVs();
			drones = new ArrayList<>(activeDrones);
		} catch (DronologyServiceException | RemoteException e1) {
			MyUI.setConnected(false);
		}
		
		// create two lists of all the drone name, one that will stay full and one that will be used in the combo boxes and decremented
		// whenever an active UAV is assigned to a mission UAV
		for (IUAVProxy drone : drones) {
			comboList.add(drone.getID());
			droneList.add(drone.getID());
		}
		Collections.sort(comboList);
		Collections.sort(droneList);
		
		// create a combo box for each UAV in the mission
		int numDrones = mission.size();
		int index = 0;
		for (IUAVMissionDescription uav : mission) {
			ComboBox<String> uavMap = new ComboBox<>(uav.getName());
			uavMap.setItems(comboList);
			// add the pair to a mapping once an active UAV is assigned to a mission UAV
			uavMap.addValueChangeListener(e -> {
				droneMapping.put(uavMap.getCaption(), uavMap.getValue());
				uavMap.setId(uavMap.getValue());
				refreshBoxes();
			});
			
			allBoxes.add(uavMap);
			if (numDrones > 5) {
				//Notification.show("HERE0", Type.ERROR_MESSAGE);
				//Notification.show("index: " + index, Type.ERROR_MESSAGE);
				if (index < (numDrones + 1)/2) {
					leftMappingLayout.addComponent(uavMap);
				}
				else {
					rightMappingLayout.addComponent(uavMap);
					//Notification.show("HERE1", Type.ERROR_MESSAGE);
				}
			}
			else {
				leftMappingLayout.addComponent(uavMap);
			}
			index++;
		}
		
		// make a list with all the names of the active uavs
		ArrayList<String> activeDroneNames = new ArrayList<>();
		for (IUAVProxy drone : drones) {
			activeDroneNames.add(drone.getID());
		}
		
		// match any uav that has the same name in the mission plan and active uav panel
		for (IUAVMissionDescription uav : mission) {
			if (activeDroneNames.contains(uav.getName())) {
				droneMapping.put(uav.getName(), uav.getName());
				activeDroneNames.remove(uav.getName());
				for (ComboBox<String> comboBox : allBoxes) {
					if (comboBox.getCaption().equals(uav.getName())) {
						comboBox.setValue(uav.getName());
						comboBox.setId(uav.getName());
						refreshBoxes();
						break;
					}
				}
			}
		}
		
		Button execute = new Button("Execute");
		execute.addStyleName("btn-okay");
		Button cancel = new Button("Cancel");
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(cancel, execute);
		
		// execute the mission with the current mapping
		execute.addClickListener(e -> {
			// canClose will be set to false if the user has not assigned every UAV in the mission to an active UAV
			boolean canClose = true;
			for (ComboBox<String> box : allBoxes) {
				if (box.getId() == null) {
					canClose = false;
				}
			}
			if (canClose) {
				// create the map object that will be used to execute the mission
				for (Map.Entry<String, String> entry : droneMapping.entrySet()) {
				    String key = entry.getKey();
				    String value = entry.getValue();
				    mappingInfo.addAttribute(key, value);
				}
				
				// use service to execute the mission with the mapping
				IMissionPlanningRemoteService service = null;
				try {
					service = (IMissionPlanningRemoteService) provider.getRemoteManager().getService(IMissionPlanningRemoteService.class);
				}
				catch (DronologyServiceException | RemoteException e1) {
					e1.printStackTrace();
					MyUI.setConnected(false);
				}
				try {
					service.executeMissionPlan(missionInfo, mappingInfo);
				} catch (RemoteException | DronologyServiceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				UI.getCurrent().removeWindow(this);
			}
			else {
				Notification.show("You must assign an active UAV for each drone in the mission.", Type.ERROR_MESSAGE);
			}
		});
		
		// cancel and return to the active flights tab
		cancel.addClickListener(e -> {
			UI.getCurrent().removeWindow(this);
		});
		
		mappingLayout.addComponent(leftMappingLayout);
		if (numDrones > 5) {
			mappingLayout.addComponent(rightMappingLayout);
		}
		totalLayout.addComponents(mappingLayout, buttons);
		
		this.setContent(totalLayout);
		this.setResizable(false);
		this.setClosable(false);
		this.setModal(true);
		this.center();
	}
	
	// only show the remaining active UAVs in the combo boxes so that a user does not try and assign one active UAV to multiple UAVs in the mission
	public void refreshBoxes() {
		for (String name : droneList) {
			if (!comboList.contains(name)) {
				comboList.add(name);
			}
		}
		Collections.sort(comboList);
		for (ComboBox<String> box : allBoxes) {
			if (box.getId() != null) {
				comboList.remove(box.getId());
			}
			box.setItems(comboList);
		}
	}
}
