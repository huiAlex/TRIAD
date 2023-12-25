package edu.nd.dronology.services.core.remote;

import java.rmi.RemoteException;
import java.util.Collection;

import edu.nd.dronology.core.vehicle.IUAVProxy;
import edu.nd.dronology.services.core.areamapping.GeneratedMappedArea;
import edu.nd.dronology.services.core.info.AreaMappingCategoryInfo;
import edu.nd.dronology.services.core.info.AreaMappingInfo;
import edu.nd.dronology.services.core.util.DronologyServiceException;

/**
 * 
 * @author Michael Vierhauser
 * 
 */
public interface IAreaMappingRemoteService extends IRemoteableService, IFileTransmitRemoteService<AreaMappingInfo> {

	Collection<AreaMappingCategoryInfo> getMappingPathCategories() throws RemoteException;

	GeneratedMappedArea generateAreaMapping(AreaMappingInfo info) throws DronologyServiceException, RemoteException;

	void executeAreaMapping(GeneratedMappedArea info) throws DronologyServiceException, RemoteException;

	GeneratedMappedArea generateAreaMapping(AreaMappingInfo info, Collection<IUAVProxy> selectedUAVs)
			throws DronologyServiceException, RemoteException;

}
