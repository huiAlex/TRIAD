package edu.nd.dronology.services.extensions.areamapping.instances;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.api.IFileChangeNotifyable;
import edu.nd.dronology.services.core.areamapping.EdgeLla;
import edu.nd.dronology.services.core.areamapping.ExportDrone;
import edu.nd.dronology.services.core.areamapping.GeneratedMappedArea;
import edu.nd.dronology.services.core.base.AbstractFileTransmitServiceInstance;
import edu.nd.dronology.services.core.info.AreaMappingCategoryInfo;
import edu.nd.dronology.services.core.info.AreaMappingInfo;
import edu.nd.dronology.services.core.items.IAreaMapping;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.persistence.AreaMappingPersistenceProvider;
import edu.nd.dronology.services.core.persistence.PersistenceException;
import edu.nd.dronology.services.core.util.DronologyConstants;
import edu.nd.dronology.services.core.util.DronologyServiceException;
import edu.nd.dronology.services.core.util.ServiceIds;
import edu.nd.dronology.services.extensions.areamapping.AreaMappingGenerator;
import edu.nd.dronology.services.instances.dronesetup.DroneSetupService;
import edu.nd.dronology.services.instances.flightmanager.FlightManagerService;
import edu.nd.dronology.services.supervisor.SupervisorService;
import edu.nd.dronology.util.FileUtil;
import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public class AreaMappingServiceInstance extends AbstractFileTransmitServiceInstance<AreaMappingInfo>
		implements IFileChangeNotifyable, IAreaMappingServiceInstance {

	private static final ILogger LOGGER = LoggerProvider.getLogger(AreaMappingServiceInstance.class);

	public static final String EXTENSION = DronologyConstants.EXTENSION_AREA;

	private Collection<AreaMappingCategoryInfo> categories = new ArrayList<>();

	public AreaMappingServiceInstance() {
		super(ServiceIds.SERVICE_AREAMAPPING, "Area Mapping", EXTENSION);
	}

	@Override
	protected Class<?> getServiceClass() {
		return AreaMappingService.class;
	}

	@Override
	protected int getOrder() {
		return 2;
	}

	@Override
	protected String getPropertyPath() {
		return null;
	}

	@Override
	protected void doStartService() throws Exception {
		reloadItems();
	}

	@Override
	protected void doStopService() throws Exception {
		fileManager.tearDown();
	}

	@Override
	public AreaMappingInfo createItem() throws DronologyServiceException {
		AreaMappingPersistenceProvider persistor = AreaMappingPersistenceProvider.getInstance();
		IAreaMapping areaMapping = DronologyElementFactory.createNewAreaMapping();
		areaMapping.setName("New-AreaMapping");
		String savePath = FileUtil.concat(storagePath, areaMapping.getId(), EXTENSION);

		try {
			persistor.saveItem(areaMapping, savePath);
		} catch (PersistenceException e) {
			throw new DronologyServiceException("Error when creating area mapping: " + e.getMessage());
		}
		return new AreaMappingInfo(areaMapping.getName(), areaMapping.getId());
	}

	@Override
	protected String getPath() {
		String path = SupervisorService.getInstance().getAreaMappingLocation();
		return path;
	}

	@Override
	protected AreaMappingInfo fromFile(String id, File file) throws Throwable {
		IAreaMapping atm = AreaMappingPersistenceProvider.getInstance().loadItem(file.toURI().toURL());
		AreaMappingInfo info = new AreaMappingInfo(atm.getName(), id);
		for (int i = 0; i <= 1; i++)
			for (EdgeLla coordinate : atm.getMappedPoints(i)) {
				info.addCoordinate(i, coordinate);
			}

		BasicFileAttributes attr = Files.readAttributes(Paths.get(file.toURI()), BasicFileAttributes.class);
		info.setDateCreated(attr.creationTime().toMillis());
		info.setDateModified(attr.lastModifiedTime().toMillis());
		info.setDescription(atm.getDescription());
		return info;
	}

	@Override
	public Collection<AreaMappingCategoryInfo> getMappingPathCategories() {
		return Collections.unmodifiableCollection(categories);
	}

	@Override
	public AreaMappingInfo getItem(String name) throws DronologyServiceException {
		for (AreaMappingInfo item : itemmap.values()) {
			if (item.getId().equals(name)) {
				return item;
			}
		}
		throw new DronologyServiceException("Area mapping '" + name + "' not found");
	}

	@Override
	public AreaMappingInfo getMappingByName(String mappingName) throws DronologyServiceException {
		for (AreaMappingInfo item : itemmap.values()) {
			if (item.getName().equals(mappingName)) {
				return item;
			}
		}
		throw new DronologyServiceException("Area mapping '" + mappingName + "' not found");
	}

	@Override
	public GeneratedMappedArea generateAreaMapping(AreaMappingInfo info) throws DronologyServiceException {

		Collection<IUAVProxy> allUAVs = DroneSetupService.getInstance().getActiveUAVs();
		return generateAreaMapping(info, allUAVs);

	}

	@Override
	public GeneratedMappedArea generateAreaMapping(AreaMappingInfo info, Collection<IUAVProxy> selectedUAVs)
			throws DronologyServiceException {
		try {
			File file = fileManager.getFile(info.getId());
			IAreaMapping mapping;
			mapping = AreaMappingPersistenceProvider.getInstance().loadItem(file.toURI().toURL());
			AreaMappingGenerator generator = new AreaMappingGenerator(mapping, selectedUAVs);

			return generator.generateMapping();
		} catch (MalformedURLException | PersistenceException e) {
			throw new DronologyServiceException(e.getMessage());
		}

	}

	@Override
	public void executeAreaMapping(GeneratedMappedArea area) throws DronologyServiceException {

		for (ExportDrone ex : area.getUAVAssignments()) {
			String uavid = ex.getUAVId();
			int i = 0;
			List<IFlightRoute> assignments = ex.getDroneRouteAssignment();
			if (assignments.size() > 0) {

				try {
					FlightManagerService.getInstance().takeoff(uavid,
							assignments.get(0).getWaypoints().get(0).getCoordinate().getAltitude());
				} catch (DronologyServiceException e) {
					LOGGER.error(e);
				}
			} else {
				throw new DronologyServiceException("Error when creating route assignments - no assignments available");
			}
			for (IFlightRoute assignment : assignments) {
				List<Waypoint> waypoints = assignment.getWaypoints();
				try {
					FlightManagerService.getInstance().planFlight(uavid, "Generated-RouteAssignment-" + i, waypoints);
				} catch (Exception e) {
					LOGGER.error(e);
				}

				i++;
			}

			//
			// try {
			// FlightManagerService.getInstance().planFlight(uavid,
			// "Generated-RouteAssignment" + i, waypoints);
			// } catch (Exception e) {
			// LOGGER.error(e);
			// }

		}

	}

	private List<Waypoint> convertToWaypoints(List<LlaCoordinate> droneFullRoute) {
		List<Waypoint> wp = new ArrayList<>();

		droneFullRoute.forEach(coord -> {
			wp.add(new Waypoint(coord));
		});
		return wp;
	}

}
