package edu.nd.dronology.ui.vaadin.areamapping;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.LinkedList;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.areamapping.EdgeLla;
import edu.nd.dronology.services.core.areamapping.GeneratedMappedArea;
import edu.nd.dronology.services.core.info.AreaMappingInfo;
import edu.nd.dronology.services.core.remote.IAreaMappingRemoteService;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMDeleteMappingConfirmation;
import edu.nd.dronology.ui.vaadin.areamapping.windows.AMUnsavedChangesConfirmation;
import edu.nd.dronology.ui.vaadin.start.MyUI;
import edu.nd.dronology.ui.vaadin.utils.WaitingWindow;
import edu.nd.dronology.ui.vaadin.utils.WaypointReplace;

/**
 * This is the main layout for the Area Mapping UI.
 * 
 * @author Andrew Slavin
 */

public class AMMainLayout extends CustomComponent {

	private static final long serialVersionUID = -4912347541234441237L;
	private AMControlsComponent controls = new AMControlsComponent(this);
	private AMMapComponent mapComponent;
	private AMDeleteMappingConfirmation deleteMappingConfirmation;
	private AMUnsavedChangesConfirmation unsavedChangesConfirmation;
	private WaitingWindow waitingWindow = new WaitingWindow();
	private Boolean deletingMapping = false;

	@WaypointReplace
	public AMMainLayout() {
		addStyleName("main_layout");
		CssLayout content = new CssLayout();
		content.setSizeFull();

		mapComponent = new AMMapComponent(this);

		deleteMappingConfirmation = new AMDeleteMappingConfirmation(this);
		unsavedChangesConfirmation = new AMUnsavedChangesConfirmation(this);

		content.addComponents(controls, mapComponent);
		setCompositionRoot(content);
	}

	public AMDeleteMappingConfirmation getDeleteMappingConfirmation() {
		return deleteMappingConfirmation;
	}

	public AMUnsavedChangesConfirmation getUnsavedChangesConfirmation() {
		return unsavedChangesConfirmation;
	}

	public WaitingWindow getWaitingWindow() {
		return waitingWindow;
	}

	// Displays the mapping that is clicked
	public void switchMapping(AMInfoBox switchToInfoBox) {
		// When one mapping is clicked, the others go back to default background color.
		controls.getInfoPanel().unhighlightAllInfoBoxes();
		controls.getInfoPanel()
				.highlightInfoBox(controls.getInfoPanel().getMappingIndex(switchToInfoBox.getAreaMappingInfo()));

		// Displays the mapping on map.
		mapComponent.displayAreaMapping(switchToInfoBox.getAreaMappingInfo());
	}

	// runs the algorithm when "generate route" is clicked in metainfo
	public void generateRouteFromMapping(Collection<IUAVProxy> dronesToSend) {
		// send stuff to Dronology
		try {
			IAreaMappingRemoteService service = (IAreaMappingRemoteService) MyUI.getProvider().getRemoteManager()
					.getService(IAreaMappingRemoteService.class);

			AreaMappingInfo info = mapComponent.getMetaInfo().getMappingInfo();
			
			// if upstream is opposite the way that it was drawn, flip the points around
			if (!mapComponent.getMapUtilities().getUpstream()) {
				LinkedList<EdgeLla> originalList1 = info.getCoordinates(0);
				LinkedList<EdgeLla> swappedList1 = new LinkedList<>();
				for (int i = originalList1.size()-1; i >= 0; i--) {
					swappedList1.add(originalList1.get(i));
					info.removeCoordinate(0, originalList1.get(i));
				}
				for (int i = 0; i < swappedList1.size(); i++) {
					info.addCoordinate(0, swappedList1.get(i));
				}
				LinkedList<EdgeLla> originalList2 = info.getCoordinates(1);
				LinkedList<EdgeLla> swappedList2 = new LinkedList<>();
				for (int i = originalList2.size()-1; i >= 0; i--) {
					swappedList2.add(originalList2.get(i));
					info.removeCoordinate(1, originalList2.get(i));
				}
				for (int i = 0; i < swappedList2.size(); i++) {
					info.addCoordinate(1, swappedList2.get(i));
				}
			}
			
			GeneratedMappedArea generated = service.generateAreaMapping(info, dronesToSend);
			service.executeAreaMapping(generated);

		} catch (RemoteException | DronologyServiceException e) {
			e.printStackTrace();
		}
	}

	// Gets the controls component that holds the infoPanel and mainLayout.
	public AMControlsComponent getControls() {
		return controls;
	}

	// Gets the currently displayed map.
	public AMMapComponent getMapComponent() {
		return mapComponent;
	}

	// sets the map component
	public void setMapComponent(AMMapComponent mapComponent) {
		this.mapComponent = mapComponent;
	}

	public Boolean getDeletingMapping() {
		return deletingMapping;
	}

	public void setDeletingMapping(Boolean deletingMapping) {
		this.deletingMapping = deletingMapping;
	}
}