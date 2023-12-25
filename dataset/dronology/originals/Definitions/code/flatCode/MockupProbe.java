package edu.nd.dronology.monitoring.reminds;

import java.util.UUID;

import at.jku.mevss.eventdistributor.core.commandsocket.AbstractCommand;
import at.jku.mevss.eventpublisher.core.api.AbstractProbePoint;
import at.jku.mevss.eventpublisher.core.internal.Publisher;

public class MockupProbe extends AbstractProbePoint {

	// private static final String SOURCE = "CL2-MOCKUP";
	// private EventGeneratorTask generator;
	private int nrOfEventsPerSend;
	private String id = UUID.randomUUID().toString();
	// private String source;

	public MockupProbe(String id, String container, String source) {

		super(id, container, source);
	
	}

	@Override
	protected void doHandleCommand(AbstractCommand command) {
		// TODO Auto-generated method stub

	}

	public void run() {
		super.start();
	}

	// public void publish(long timestamp, String typeName, List<Map<String, Serializable>> data) {
	// ArrayList<TransmittableEventDataObject> list = new ArrayList<>();
	// for (Map m : data) {
	// TransmittableEventDataObject eventdata = TransmittableObjectFactory.createEventData(m);
	// list.add(eventdata);
	// }
	//
	// if (nrOfEventsPerSend == 1) {
	// TransmittableEventObject obj = TransmittableObjectFactory.createEventObject(source, timestamp, typeName, list);
	// publish(source, obj);
	// } else {
	// List<TransmittableEventObject> events = new ArrayList<>();
	// for (int i = 0; i < nrOfEventsPerSend; i++) {
	// TransmittableEventObject obj = TransmittableObjectFactory.createEventObject(source, timestamp, typeName, new ArrayList(list));
	// events.add(obj);
	// }
	// sendData(source, events.toArray(new TransmittableEventObject[0]));
	// }
	//
	// }

}
