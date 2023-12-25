package edu.nd.dronology.services.extensions.areamapping.util;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import edu.nd.dronology.core.coordinate.LlaCoordinate;
import edu.nd.dronology.core.util.Waypoint;
import edu.nd.dronology.services.core.areamapping.EdgeLla;
import edu.nd.dronology.services.core.items.FlightRoute;
import edu.nd.dronology.services.core.items.IFlightRoute;
import edu.nd.dronology.services.core.items.IMappedItem;
import edu.nd.dronology.services.extensions.areamapping.internal.Geometry;
import edu.nd.dronology.services.extensions.areamapping.internal.ImageWaypoint;
import edu.nd.dronology.services.extensions.areamapping.internal.ImageWaypoints;
import edu.nd.dronology.services.extensions.areamapping.internal.MapNode;
import edu.nd.dronology.services.extensions.areamapping.internal.RiverBank;
import edu.nd.dronology.services.extensions.areamapping.internal.RiverList;
import edu.nd.dronology.services.extensions.areamapping.internal.SourcePoints;
import edu.nd.dronology.services.extensions.areamapping.model.RiverSubsegment;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive;
import edu.nd.dronology.services.extensions.areamapping.model.RoutePrimitive.RouteType;


public class Utilities {

    /**
     * This function calculates the average latitude.
     * @return average latitude
     */
    public static double getAvgLatitude(double minLatitude, double maxLatitude){
        return (minLatitude + maxLatitude)/2;
    }
    
    public static RiverBank edgeLlaToRiverBank(List<EdgeLla> input){
    	RiverBank newBank = new RiverBank();
    	for(EdgeLla entry : input) {
    		newBank.add(new Point2D.Double(entry.getLatitude(), entry.getLongitude()));
    	}
    	return newBank;
    }
    
    public static List<RiverBank> processDronologyInput(RiverBank bank1, RiverBank bank2, Vector<Double> latitudeBounds){
    	List<RiverBank> bankList = new ArrayList<>();
	    double minLatitude = 1000;
	    double maxLatitude = -1000;
	    double latitude;
	    for(Point2D.Double node : bank1.get()){
	        latitude = node.getX();
	        if(latitude < minLatitude){
	            minLatitude = latitude;
	        }
	        if(maxLatitude < latitude){
	            maxLatitude = latitude;
	        }
	    }
	    for(Point2D.Double node : bank2.get()){
	        latitude = node.getX();
	        if(latitude < minLatitude){
	            minLatitude = latitude;
	        }
	        if(maxLatitude < latitude){
	            maxLatitude = latitude;
	        }
	    }
	    double avgLatitude = getAvgLatitude(minLatitude, maxLatitude);
	    latitudeBounds.add(minLatitude);
	    latitudeBounds.add(maxLatitude);
	    bank1 = gpsRiverBankToCartesianRiverBank(avgLatitude, bank1);
	    bank2 = gpsRiverBankToCartesianRiverBank(avgLatitude, bank2);
	    bankList.add(bank1);
	    bankList.add(bank2);
	    return bankList;
    	
    }

    /**
     * This splits the nodes from the JSON input into vectors for each bank of the river and puts these vectors
     * into a list.
     * @param riverList
     * @return a list of Vectors of Point2D.Double representing the banks of the river
     */
    
    public static List<RiverBank> makeBankVectors(RiverList riverList, Vector<Double> latitudeBounds){
        List<RiverBank> bankList = new ArrayList<>();
        RiverBank bank1 = new RiverBank();
        RiverBank bank2 = new RiverBank();
        double minLatitude = 1000;
        double maxLatitude = -1000;
        double latitude;
        for(MapNode node : riverList.getNodes()){
            latitude = node.getLatitude();
            if(latitude < minLatitude){
                minLatitude = latitude;
            }
            if(maxLatitude < latitude){
                maxLatitude = latitude;
            }
            if(node.get_riverSide() == 1) {
                bank1.add(new Point2D.Double(node.getLatitude(),node.getLongitude()));
            } else {
                bank2.add(new Point2D.Double(node.getLatitude(),node.getLongitude()));
            }
        }
        double avgLatitude = getAvgLatitude(minLatitude, maxLatitude);
        latitudeBounds.add(minLatitude);
        latitudeBounds.add(maxLatitude);
        bank1 = gpsRiverBankToCartesianRiverBank(avgLatitude, bank1);
        bank2 = gpsRiverBankToCartesianRiverBank(avgLatitude, bank2);
        bankList.add(bank1);
        bankList.add(bank2);
        return bankList;
    }
    
    /**
     * This function converts the river bank vectors containing gps coordinates into vectors of cartesian
     * coordinates for all the calculations during the dynamic route generation algorithm.
     * @param avgLatitude
     * @param gpsBank
     * @return RiverBank in cartesian coordinates
     */
    private static RiverBank gpsRiverBankToCartesianRiverBank(double avgLatitude, RiverBank gpsBank) {
        RiverBank riverBank = new RiverBank();
        for(Point2D.Double entry : gpsBank.get()){
            riverBank.add(Geometry.gpsToCartesian(entry,avgLatitude));
        }
        return riverBank;
    }
    
    
    /**
     * This function converts the riverbank nodes from cartesian coordinates to gps coordinates.
     * @param avgLatitude
     * @param cartesianBank
     * @return RiverBank in gps coordinates
     */
    public static RiverBank cartesianRiverBankToGpsRiverBank(double avgLatitude, RiverBank cartesianBank) {
    	RiverBank riverBank = new RiverBank();
    	for(Point2D.Double entry : cartesianBank.get()){
            riverBank.add(Geometry.cartesianToGPS(entry,avgLatitude));
        }
        return riverBank;
    }
    
    
    /**
     * This function converts the route primitive to cartesian coordinates from gps coordinates.
     * @param l
     * @param avgLatitude
     * @return RoutePrimitive in cartesian coordinates
     */
    public static RoutePrimitive gpsRouteToCartesianRoute(RoutePrimitive l, double avgLatitude) {
    	for(int i = 0; i < l.size(); i++) {
    		l.setRouteWaypoint(i,Geometry.gpsToCartesian(l.getRouteWaypoint(i),avgLatitude));
    	}
    	return l;
    }

    /**
     * This function convertes the route primitive to gps coordinates from cartesian coordinates.
     * @param l
     * @return RoutePrimitive in gps coordinates
     */
    public static RoutePrimitive cartesianRouteToGpsRoute(RoutePrimitive l, double avgLatitude){
        for(int i = 0; i < l.size(); i++){
            l.setRouteWaypoint(i,Geometry.cartesianToGPS(l.getRouteWaypoint(i),avgLatitude));
        }
        return l;
    }

    /**
     * This function verifies the input order of the nodes from the JSON file to ensure that they are in the expected
     * order. It transforms them to the expected form if necessary.
     */
    
    /**
     * This function takes the RiverBanks created from the JSON file to make sure that they are in the necessary order.
     * @param gpsBank1
     * @param gpsBank2
     */
    public static void verifyInputOrder(RiverBank gpsBank1, RiverBank gpsBank2){
        Point2D.Double start1 = gpsBank1.get(0);
        Point2D.Double end1 = gpsBank1.get(gpsBank1.size()-1);
        Point2D.Double start2 = gpsBank2.get(0);
        Point2D.Double end2 = gpsBank2.get(gpsBank2.size()-1);
        boolean intersect = Line2D.linesIntersect(start1.getX(),start1.getY(),start2.getX(), start2.getY(),end1.getX(),end1.getY(),end2.getX(),end2.getY());
        if(intersect){
        	System.out.println("bank2 reversed");
            gpsBank2.reverse();
        }
    }

    /**
     * This function splits up the riverbank nodes into sections for each drone to search and returns a vector of lists
     * of vectors. Each list corresponds to the segment one drone will search, and each vector in the list
     * corresonds to a bank of the river.
     * @return a vector of lists corresponding to the section each drone will search
     */
    
    //note: don't really need this function anymore
    public static Vector<RiverSubsegment> makeRiverVectors(List<RiverBank> bankList, int drones, int bank1SegmentSize, int bank2SegmentSize){
        RiverBank riverBank1 = bankList.get(0);
        RiverBank riverBank2 = bankList.get(1);
        Vector<RiverSubsegment> riverVectors = new Vector<RiverSubsegment>();

        int counter1 = 0;
        int counter2 = 0;
        for(int i = 0; i < drones-1; i++){
            RiverSubsegment riverSegment = new RiverSubsegment();
            RiverBank bank1 = new RiverBank();


            makeVector1(counter1,bank1,riverBank1,bank1SegmentSize);
            riverSegment.add(bank1);
            counter1 += bank1SegmentSize-1;

            RiverBank bank2 = new RiverBank();
            makeVector2(counter2,bank2,riverBank2,bank2SegmentSize);
            riverSegment.add(bank2);
            counter2 += bank2SegmentSize -1;

            riverVectors.add(riverSegment);
        }
        RiverSubsegment riverSegment = new RiverSubsegment();
        RiverBank bank1 = new RiverBank();
        RiverBank bank2 = new RiverBank();
        makeFinalVectors(counter1,riverSegment,bank1,riverBank1);
        makeFinalVectors(counter2,riverSegment,bank2,riverBank2);
        riverVectors.add(riverSegment);
        return riverVectors;
    }

    /**
     * This function is a helper function for makeRiverVectors(). It forms the vectors of nodes for one bank
     * of the river except for the very last vector on that side.
     * @param counter1
     * @param bank1
     */
    private static void makeVector1(int counter1,  RiverBank bank1, RiverBank riverBank1, int bank1SegmentSize){
        for(int j = counter1; j < counter1 + bank1SegmentSize; j++){
            bank1.add(riverBank1.get(j));
        }
    }

    /**
     * This function is a helper function for makeRiverVectors(). It forms the vectors of nodes for the other bank
     * of the river except for the last vector on this side.
     * @param counter2
     * @param bank2
     */
    private static void makeVector2(int counter2, RiverBank bank2, RiverBank riverBank2, int bank2SegmentSize){
        for(int j = counter2; j < counter2 + bank2SegmentSize; j++){
            bank2.add(riverBank2.get(j));
        }
    }

    /**
     * This function is a helper function for makeRiverVectors(). It makes the last vector of nodes for each bank
     * of the river.
     * @param counter1
     * @param counter2
     * @param riverSegment
     * @param bank1
     * @param bank2
     */
    private static void makeFinalVectors(int counter, RiverSubsegment riverSegment, RiverBank bank, RiverBank riverBank){
        for(int j = counter; j < riverBank.size(); j++) {
            bank.add(riverBank.get(j));
        }
        riverSegment.add(bank);
    }
    
    
    /**
     * This function makes a polygon of the entire river segment. This allows checking for points contained within the 
     * river segment.
     * @param bankList
     * @return Path2D.Double polygon of the river segment
     */
    public static Path2D.Double makeTotalRiverSegment(List<RiverBank> bankList){
    	Path2D.Double totalSegment = new Path2D.Double();
    	RiverBank bank1 = bankList.get(0);
    	RiverBank bank2 = bankList.get(1);
    	totalSegment.moveTo(bank1.get(0).getX(), bank1.get(0).getY());
    	for(int i = 1; i < bank1.size(); i++) {
    		totalSegment.lineTo(bank1.get(i).getX(), bank1.get(i).getY());
    	}
    	for(int i = bank2.size()-1; i >= 0; i--) {
    		totalSegment.lineTo(bank2.get(i).getX(), bank2.get(i).getY());
    	}
    	return totalSegment;
    }

    /**
     * This function creates Path2D.Double shapes to represent each subsegment of the riversegment. This is used for
     * checking if a point is inside the shape, which is done to determine the stopping point for each search route.
     * @return vector of Path2D.Double shapes representing subsegments of the riversegment
     */
    //Assumes riverVectors has been called beforehand so that riverBanks are in cartesian coordinates
    public static Vector<Path2D.Double> makeRiverSegments(List<RiverBank> bankList, int drones, int bank1SegmentSize, int bank2SegmentSize){
    		Vector<Path2D.Double> riverSegments = new Vector<Path2D.Double>();
            RiverBank riverBank1 = bankList.get(0);
            RiverBank riverBank2 = bankList.get(1);
            int counter1 = 0;
            int counter2 = 0;
            for(int i = 0; i < drones-1; i++){
                Path2D.Double riverSegment = new Path2D.Double();
                Point2D.Double node0 = riverBank1.get(counter1);
                riverSegment.moveTo(node0.getX(),node0.getY());

                makeRiverbank1(counter1,riverSegment,riverBank1,bank1SegmentSize);
                counter1 += bank1SegmentSize-1;

                makeRiverbank2(counter2,riverSegment,riverBank2,bank2SegmentSize);
                counter2 += bank2SegmentSize -1;

                riverSegment.closePath();
                riverSegments.add(riverSegment);
            }
            Path2D.Double riverSegment = new Path2D.Double();
            Point2D.Double node0 = riverBank1.get(counter1);
            riverSegment.moveTo(node0.getX(),node0.getY());
            makeFinalSegment(counter1,counter2,riverSegment,riverBank1,riverBank2);
            riverSegment.closePath();
            riverSegments.add(riverSegment);
            return riverSegments;
    }

    /**
     * This function is a helper function for makeRiverSegments(). It adds the nodes from the first bank of the river
     * to the shape.
     * @param counter1
     * @param riverSegment
     */
    private static void makeRiverbank1(int counter1, Path2D.Double riverSegment, RiverBank riverBank1, int bank1SegmentSize){
        for(int j = counter1 + 1; j < counter1 + bank1SegmentSize; j++){
            Point2D.Double node = riverBank1.get(j);
            riverSegment.lineTo(node.getX(),node.getY());
        }
    }

    /**
     * This function is a helper function for makeRiverSegments(). It adds the nodes from the second bank of the river
     * to the shape.
     * @param counter2
     * @param riverSegment
     */
    private static void makeRiverbank2(int counter2, Path2D.Double riverSegment, RiverBank riverBank2, int bank2SegmentSize){
        for(int j = counter2 + bank2SegmentSize - 1; j >= counter2; j--){
            Point2D.Double node = riverBank2.get(j);
            riverSegment.lineTo(node.getX(),node.getY());
        }
    }

    /**
     * This function is a helper function for makeRiverSegments(). It adds the remaining nodes from each side of the
     * river to make the last subsegment of the riversegment.
     * @param counter1
     * @param counter2
     * @param riverSegment
     */
    private static void makeFinalSegment(int counter1,int counter2,Path2D.Double riverSegment, RiverBank riverBank1, RiverBank riverBank2) {
        for (int j = counter1 + 1; j < riverBank1.size(); j++) {
            Point2D.Double node = riverBank1.get(j);
            riverSegment.lineTo(node.getX(), node.getY());
        }
        for (int j = riverBank2.size() - 1; j >= counter2; j--) {
            Point2D.Double node = riverBank2.get(j);
            riverSegment.lineTo(node.getX(), node.getY());
        }
    }
    
    
    /**
     * This function generates image waypoints at a specified distance along a RoutePrimitive
     * @param route
     * @param APERATURE_HEIGHT
     * @param OVERLAP_FACTOR
     */
    public static void generateImageWaypoints(RoutePrimitive route, double APERATURE_HEIGHT, double OVERLAP_FACTOR) {
		Point2D.Double newPoint = new Point2D.Double();
		ImageWaypoint imagePoint = new ImageWaypoint();
		Point2D.Double point1;
		Point2D.Double point2;
		ImageWaypoints imagePoints = route.getIWP();
		double routeSegmentDistance;
		double traverseDistance = APERATURE_HEIGHT*OVERLAP_FACTOR;;
		double distanceRatio;
		double theta = Geometry.getAngle(route.getRouteWaypoint(0), route.getRouteWaypoint(1));
		imagePoints.add(new ImageWaypoint(route.getRouteWaypoint(0),theta));
		for(int i = 0; i < route.size()-1; i++) {
			point1 = route.getRouteWaypoint(i);
			point2 = route.getRouteWaypoint(i+1);
			theta = Geometry.getAngle(point1, point2);
			if(Geometry.findCartesianDistance(point1, imagePoints.get(imagePoints.size()-1).getWaypoint()) > 2.0) {
				imagePoints.add(new ImageWaypoint(point1,theta));
			}
			//imagePoints.add(point1);
			routeSegmentDistance = Geometry.findCartesianDistance(point1, point2);
			while(traverseDistance <= routeSegmentDistance) {
					distanceRatio = traverseDistance / routeSegmentDistance;
					newPoint = Geometry.findOffsetPoint(point1, point2, distanceRatio);
					imagePoints.add(new ImageWaypoint(newPoint,theta));
					routeSegmentDistance = routeSegmentDistance - traverseDistance;
					point1 = newPoint;
			} 
		}
		imagePoints.add(new ImageWaypoint(route.getRouteWaypoint(route.size()-1),theta));
    }
    
    private static int determineRouteSplit(RoutePrimitive route) {
    	int splitNumber = 1;
    	if(route.getRouteType() == RoutePrimitive.RouteType.CRISSCROSS || route.getRouteType() == RoutePrimitive.RouteType.INNER_CRISSCROSS) {
    		splitNumber = 4;
    	}
    	else if(route.getRouteType() == RoutePrimitive.RouteType.RIVERBANK) {
    		splitNumber = 2;
    	}
    	return splitNumber;
    }
  
    public static List<RoutePrimitive> splitRoutePrimitives(List<RoutePrimitive> currentRoutes, double APERATURE_HEIGHT, double OVERLAP_FACTOR){
    	List<RoutePrimitive> newRoutes = new ArrayList<>();
    	for(RoutePrimitive route : currentRoutes) {
    		int splitNumber = determineRouteSplit(route);
    		int nodesNumber = route.size() / splitNumber + 1;
    		int counter = 0;
    		boolean lastSplit = false;
    		for(int i = 0; i < splitNumber - 1; i++) {
    			newRoutes.add(smallerRoutePrimitive(route, nodesNumber, counter, lastSplit, APERATURE_HEIGHT, OVERLAP_FACTOR));
    			counter += nodesNumber - 1;
    		}
    		lastSplit = true;
    		newRoutes.add(smallerRoutePrimitive(route, nodesNumber, counter, lastSplit, APERATURE_HEIGHT, OVERLAP_FACTOR));
    	}
    	return newRoutes;
    }
    
    private static RoutePrimitive smallerRoutePrimitive(RoutePrimitive route, int nodesNumber, int startPoint, boolean lastSplit, double APERATURE_HEIGHT, double OVERLAP_FACTOR){
    	RoutePrimitive newRoute = new RoutePrimitive(route.getRouteType(),route.getRouteWeight());
    	if(lastSplit) {
    		nodesNumber  = route.size() - startPoint;
    	}
    	for(int i = startPoint; i < startPoint + nodesNumber; i++) {
    		newRoute.addRouteWaypoint(route.getRouteWaypoint(i));
    	}
    	generateImageWaypoints(newRoute, APERATURE_HEIGHT, OVERLAP_FACTOR);
    	return newRoute;
    }
//    // NOTE: change all of these to expect IFlightRoute instead of ExportRoutePrimitive
//    public static ExportAllocationInformation makeExportAllocationInformation(AllocationInformation allocationInformation) {
//    	ExportAllocationInformation newAllocationInformation = new ExportAllocationInformation();
//    	double altitude = 15;
//    	for(Drone drone : allocationInformation.getDroneAllocations()) {
//    		newAllocationInformation.addDroneAllocation(makeExportDrone(drone, altitude));
//    		altitude += 5;
//    	}
//    	newAllocationInformation.setMetricsStatistics(allocationInformation.getMetricStatistics());
//    	return newAllocationInformation;
//    }
    
//    public static ExportDrone makeExportDrone(Drone drone, double altitude) {
//    	ExportDrone newDrone = new ExportDrone();
//    	newDrone.setDroneStartPoint(new LlaCoordinate(drone.getDroneStartPoint().getX(), drone.getDroneStartPoint().getY(), altitude));
//    	newDrone.setDroneHomeLocation(new LlaCoordinate(drone.getDroneHomeLocation().getX(), drone.getDroneHomeLocation().getY(), altitude));
//    	newDrone.setUAVId(drone.getUAVId());
//    	newDrone.setDroneRouteAssignment(makeExportDroneRouteAssignment(drone.getDroneRouteAssignment(), altitude));
//    	return newDrone;
//    }
//    	
//    private static ExportDroneRouteAssignment makeExportDroneRouteAssignment(DroneRouteAssignment routeAssignment, double altitude) {
//    	ExportDroneRouteAssignment newRouteAssignment = new ExportDroneRouteAssignment();
//    	HashMap<RouteType, Integer> routePrimitiveCounters = new HashMap<>();
//    	routePrimitiveCounters.put(RouteType.CRISSCROSS, 0);
//    	routePrimitiveCounters.put(RouteType.RIVERBANK, 0);
//    	routePrimitiveCounters.put(RouteType.PRIORITYAREA, 0);
//    	for(RoutePrimitive routePrimitive : routeAssignment.get()) {
//    		newRouteAssignment.add(makeFlightRoute(routePrimitive, altitude, routePrimitiveCounters));
//    	}
//    	return newRouteAssignment;
//    }
    
    private static String assignFlightRouteName(RoutePrimitive route, HashMap<RoutePrimitive.RouteType, Integer> counters) {
    	RouteType routeType = route.getRouteType();
    	String name;
    	if(routeType == RouteType.CRISSCROSS) {
    		name = "CrissCross Route " + counters.get(RouteType.CRISSCROSS);
    		counters.put(RouteType.CRISSCROSS, counters.get(RouteType.CRISSCROSS) + 1);
    	}
    	else if(routeType == RouteType.RIVERBANK) {
    		name = "RiverBank Route " + counters.get(RouteType.RIVERBANK);
    		counters.put(RouteType.RIVERBANK, counters.get(RouteType.RIVERBANK) + 1);
    	} else {
    		name = "PriorityArea Route " + counters.get(RouteType.PRIORITYAREA);
    		counters.put(RouteType.PRIORITYAREA, counters.get(RouteType.PRIORITYAREA) + 1);
    	}
    	return name;
    }
    
    //edit to return IFlightRoute with name as something with type of route
    public static IFlightRoute makeFlightRoute(RoutePrimitive route, double altitude, HashMap<RoutePrimitive.RouteType, Integer> counters) {
    	IFlightRoute newRoute = new FlightRoute();
    	for(Point2D.Double entry : route.getRoute()) {
    		newRoute.addWaypoint(new Waypoint(new LlaCoordinate(entry.getX(), entry.getY(), altitude)));
    	}
    	newRoute.setName(assignFlightRouteName(route, counters));
    	return newRoute;
    }
    
    public static SourcePoints transformPriorityArea(IMappedItem area, double centralLatitude){
    	SourcePoints newArea = new SourcePoints(area.getImportance());
    	for(LlaCoordinate entry : area.getCoordinates()) {
    		Point2D.Double newPoint = new Point2D.Double(entry.getLatitude(), entry.getLongitude());
    		newArea.addSourcePoint(Geometry.gpsToCartesian(newPoint, centralLatitude));
    	}
    	return newArea;
    }
    
    public static void printRiverSegment(Vector<RiverSubsegment> riverVectors) {
    	System.out.println("riverVectors: ");
		//printout for graph visualization
		System.out.printf("[");
		for(RiverSubsegment l : riverVectors){
			System.out.printf("[");
			for(RiverBank v : l.get()){
				System.out.printf("[");
				for(Point2D.Double entry : v.get()){
					System.out.printf("["+entry.getX()+","+entry.getY()+"],");
				}
				System.out.printf("],");
			}
			System.out.printf("],");
		}
		System.out.println("]");
    }
    
    public static void printRoutePrimitives(List<RoutePrimitive> routes) {
    	System.out.println("routePrimitives: ");
		System.out.printf("[");
		for(RoutePrimitive rList : routes){
			System.out.printf("[");
			for(Point2D.Double entry: rList.getRoute()){
				System.out.printf("["+entry.getX()+", "+entry.getY()+"],");
			}
			System.out.printf("],");
		}
		System.out.println("]");
    }
    
    public static void printImageWaypoints(List<RoutePrimitive> routes) {
    	System.out.println("image Waypoints: ");
		System.out.printf("[");
		for(RoutePrimitive rList : routes){
			ImageWaypoints imagePoints = rList.getIWP();
			System.out.printf("[");
			for(ImageWaypoint entry: imagePoints.get()){
				System.out.printf("["+entry.getWaypoint().getX()+", "+entry.getWaypoint().getY()+"],");
			}
			System.out.printf("],");
		}
		System.out.println("]");
    }
    
    public static void printSourcePoints(List<SourcePoints> sourcePoints) {
    	System.out.println("sourcePoints: ");
		System.out.printf("[");
		for(SourcePoints points : sourcePoints){
			System.out.printf("[");
			for(Point2D.Double entry: points.getSourcePoints()){
				System.out.printf("["+entry.getX()+", "+entry.getY()+"],");
			}
			System.out.printf("],");
		}
		System.out.println("]");
    }
    
    /**
     * This function prints out the information for python visualization 
     * @param riverVectors
     * @param routes
     */
    public static void debugPrintOut(Vector<RiverSubsegment> riverVectors,List<RoutePrimitive> routes){
    	printRiverSegment(riverVectors);
    	printRoutePrimitives(routes);
    	printImageWaypoints(routes);
	}
}


