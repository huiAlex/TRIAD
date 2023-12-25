package edu.nd.dronology.monitoring;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.nd.dronology.core.monitoring.ArtifactIdentifier;
import edu.nd.dronology.core.monitoring.IMonitorableMessage;
import edu.nd.dronology.core.monitoring.IMonitoringDataHandler;
import edu.nd.dronology.monitoring.service.DroneMonitoringService;
import edu.nd.dronology.util.NamedThreadFactory;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class MonitoringDataHandler3 implements Runnable, IMonitoringDataHandler {

	private static final ILogger LOGGER = LoggerProvider.getLogger(MonitoringDataHandler3.class);
	private static final int NUM_THREADS = 10;
	private BlockingQueue<IMonitorableMessage> queue;
	private AtomicBoolean cont = new AtomicBoolean(true);

	private static final ExecutorService SERVICE_EXECUTOR = Executors.newFixedThreadPool(NUM_THREADS,
			new NamedThreadFactory("SubscriberDistributionThreads"));

	public MonitoringDataHandler3() {
	}

	@Override
	public void run() {
		while (cont.get()) {

			try {
				IMonitorableMessage message = queue.take();

				ArtifactIdentifier<?> identifier = message.getIdentifier();

				Set<IRemoteMonitoringMessageHandler> handler = DroneMonitoringService.getInstance()
						.getSubscribedHandler(identifier);
				for (IRemoteMonitoringMessageHandler h : handler) {
					SERVICE_EXECUTOR.submit(new SendToSubscriberCallable(h, message));
				}

			} catch (Throwable e) {
				LOGGER.error(e);
			}

		}
	}

	@Override
	public void setQueue(BlockingQueue<IMonitorableMessage> queue) {
		this.queue = queue;

	}
}
