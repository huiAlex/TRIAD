package edu.nd.dronology.services.extensions.areamapping.output;

import org.junit.Test;

public class CoverageTest {
	
	final static double APERATURE_WIDTH = 10;
	final static double APERATURE_HEIGHT = APERATURE_WIDTH*0.8;
	final static double OVERLAP_FACTOR = 0.7;
	
	@Test
	public void testCalculateRouteCoverage() {
//		MapRiver riverMapper = new MapRiver();
//		List<RoutePrimitive> routes = riverMapper.generateRoutePrimitives();
//		MetricsRunner metricsRunner = new MetricsRunner(routes, riverMapper.getTotalRiverSegment(), riverMapper.getBankList(), APERATURE_WIDTH, APERATURE_HEIGHT, 4);
//		metricsRunner.droneSetup();
//		MetricsStatistics statistics = metricsRunner.runMetrics();
//		System.out.println(statistics.getAllocationCoverage());
//		assertTrue(statistics.getAllocationCoverage() > 0.98);
	}

}
