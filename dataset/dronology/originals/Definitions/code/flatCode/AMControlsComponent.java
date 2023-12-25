package edu.nd.dronology.ui.vaadin.areamapping;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

/**
 * This controls component shows the info panel (list of mappings) and contains the main layout.
 * 
 * @author Andrew Slavin
 *
 */

public class AMControlsComponent extends CustomComponent {
	
	private static final long serialVersionUID = 1L;
	private AMInfoPanel info = new AMInfoPanel(this);
	AMMainLayout mainLayout;
	
	public AMControlsComponent(AMMainLayout layout) {
		this.setWidth("100%");
		addStyleName("controls_component");
		
		VerticalLayout content = new VerticalLayout();

		content.addComponent(info);
		setCompositionRoot(content);
		mainLayout = layout;
	}
	
	// Returns the list of area mappings, which appears on the left hand side of the display.
	public AMInfoPanel getInfoPanel(){
		return info;
	}
	// Returns the table and grid combination, which appears on the right hand side of the display.
	public AMMainLayout getMainLayout() {
		return mainLayout;
		
	}

}
