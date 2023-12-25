package edu.nd.dronology.monitoring.tree;

/**
 * 
 * Factory Class for Artifact Identifier for creating Artifact Identifier for
 * the VarModel <br>
 * <br>
 * <b> Only use the factory methods for creating Artifact Identifier! </b>
 * <p/>
 * 
 * @author Michael Vierhauser
 * 
 */
public class IdentifierCreator {

	

	// public static ArtifactIdentifier createIdentifier(String gid, UAVStateMessage
	// message) {
	// String uavid = message.getUavid();
	// return new ArtifactIdentifier(ArtifactIdentifier.ROOT, gid, uavid,
	// UAVStateMessage.class.getSimpleName());
	//
	// }
	//
	// public static ArtifactIdentifier createIdentifier(String gid,
	// UAVMonitoringMessage message) {
	// String uavid = message.getUavid();
	// return new ArtifactIdentifier(ArtifactIdentifier.ROOT, gid, uavid,
	// UAVMonitoringMessage.class.getSimpleName());
	//
	// }

	// public static ArtifactIdentifier createIdentifier(String gid,
	// IMonitorableMessage message) {
	// if (message instanceof UAVStateMessage) {
	// return createIdentifier(gid, (UAVStateMessage) message);
	// }
	// if (message instanceof UAVMonitoringMessage) {
	// return createIdentifier(gid, (UAVMonitoringMessage) message);
	// }
	// return null;
	// }

}
