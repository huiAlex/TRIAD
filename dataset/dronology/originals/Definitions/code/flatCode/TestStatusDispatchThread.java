package edu.nd.dronology.gstation.connector.dispatch;

import edu.nd.dronology.core.IUAVPropertyUpdateNotifier;
import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.gstation.connector.messages.AbstractUAVMessage;
import edu.nd.dronology.gstation.connector.messages.UAVMessageFactory;
import edu.nd.dronology.gstation.connector.messages.UAVStateMessage;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.Test;
import org.junit.Assert;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestStatusDispatchThread {

    class DispatchWrapper {
        public volatile StatusDispatchThread dispatcher;
    }

    @Test
    public void testCurrentBehavior() throws Throwable {
        /*
        Create a mock IUAVPropertyUpdateNotifier that checks the stubbed data gets passed in
         */
        final DispatchWrapper dispatchWrapper = new DispatchWrapper();
        BlockingQueue<AbstractUAVMessage> queue = new LinkedBlockingQueue();
        IUAVPropertyUpdateNotifier updateNotifier = new IUAVPropertyUpdateNotifier() {
            @Override
            public void update(LlaCoordinate location, double batteryLevel, double speed, Vector3D velocity, Vector3D attitude) {
                updateCoordinates(location);
                updateBatteryLevel(batteryLevel);
                updateVelocity(speed);
                updateCollisionAvoidance(location, velocity, attitude);
            }

            @Override
            public void updateCoordinates(LlaCoordinate location) {
                dispatchWrapper.dispatcher.tearDown();
                LlaCoordinate expected = new LlaCoordinate(41.519495,-86.239937,266.19);
                Assert.assertEquals(expected, location);
            }

            @Override
            public void updateDroneState(String status) {
                dispatchWrapper.dispatcher.tearDown();
                //todo investigate if this is the behavior we want
                Assert.fail("This isn't called");
            }

            @Override
            public void updateBatteryLevel(double batteryLevel) {
                dispatchWrapper.dispatcher.tearDown();
                Assert.assertEquals(100, batteryLevel, 0.0);

            }

            @Override
            public void updateVelocity(double velocity) {
                dispatchWrapper.dispatcher.tearDown();
                Assert.assertEquals(5.0, velocity, 0.0);
            }

            @Override
            public void updateCollisionAvoidance(LlaCoordinate position, Vector3D velocity, Vector3D attitude) {
                LlaCoordinate expected = new LlaCoordinate(41.519495,-86.239937,266.19);
                Assert.assertEquals(expected, position);

                Vector3D expectedVelcity = new Vector3D(3.0, 4.0, 0.0);
                Assert.assertEquals(expectedVelcity, velocity);

                Vector3D expectedAttitude = new Vector3D(30.0, 31.0, 32.0);
                Assert.assertEquals(expectedAttitude, attitude);
            }

			@Override
			public void updateMode(String mode) {
				
			}
        };

        String batteryState = "{\n" +
                "\"voltage\": 12.19,\n" +
                "\"current\": 27.15,\n" +
                "\"level\": 100\n" +
                "}";
        String gsString ="{\n" +
                "\"type\": \"state\",\n" +
                "\"uavid\":\"TESTING\",\n" +
                "\"sendtimestamp\":" + System.currentTimeMillis() + ",\n" +
                "\"data\":{\n" +
                "\"location\": {\"x\": 41.519495, \"y\": -86.239937, \"z\": 266.19},\n" +
                "\"attitude\": {\"x\": 30.0, \"y\": 31.0, \"z\": 32.0},\n" +
                "\"velocity\": {\"x\": 3, \"y\": 4, \"z\": 0},\n" +
                "\"status\": \"ACTIVE\",\n" +
                "\"heading\": 53.1301025195,\n" +
                "\"airspeed\": 5,\n" +
                "\"groundspeed\": 5,\n" +
                "\"armed\": true,\n" +
                "\"armable\": true,\n" +
                "\"mode\": \"GUIDED\",\n" +
                "\"batterystatus\": " + batteryState + "\n" +
                "}\n" +
                "}";
        final UAVStateMessage x = (UAVStateMessage) UAVMessageFactory.create(gsString);
        queue.offer(x);
        dispatchWrapper.dispatcher = new StatusDispatchThread(queue, updateNotifier);
        dispatchWrapper.dispatcher.call();

    }
}
