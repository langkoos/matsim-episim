/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package RunAbfall;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.freight.carrier.Carrier;
import org.matsim.contrib.freight.carrier.CarrierVehicle;
import org.matsim.contrib.freight.carrier.CarrierVehicleType;
import org.matsim.contrib.freight.carrier.Carriers;
import org.matsim.contrib.freight.carrier.CarrierCapabilities.FleetSize;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.EngineInformation.FuelType;
import org.opengis.feature.simple.SimpleFeature;

/**
 * @author nagel
 *
 */
public class AbfallUtilsTest {

	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();

	@Before
	public final void setUp() {

	}

	@Test
	public final void testCreateCarrierMap() {
		HashMap<String, Carrier> carrierMap = AbfallUtils.createCarrier();
		Assert.assertEquals(4, carrierMap.size());
		Assert.assertTrue(carrierMap.containsKey("Nordring"));
		Assert.assertTrue(carrierMap.containsKey("MalmoeerStr"));
		Assert.assertTrue(carrierMap.containsKey("Gradestrasse"));
		Assert.assertTrue(carrierMap.containsKey("Forckenbeck"));
		for (Carrier singleCarrier : carrierMap.values()) {
			Assert.assertNotNull(singleCarrier);
		}
	}

	@Test
	public final void testCreateAndAddVehicles() {
		AbfallUtils.createAndAddVehicles(true);
		Assert.assertEquals(FuelType.electricity, AbfallUtils.carrierVehType.getEngineInformation().getFuelType());
		Assert.assertEquals(0., AbfallUtils.carrierVehType.getVehicleCostInformation().perTimeUnit, 0);
		Assert.assertTrue(AbfallUtils.vehicleTypes.getVehicleTypes().containsKey(AbfallUtils.carrierVehType.getId()));
		AbfallUtils.createAndAddVehicles(false);
		Assert.assertEquals(FuelType.diesel, AbfallUtils.carrierVehType.getEngineInformation().getFuelType());
		Assert.assertEquals(0., AbfallUtils.carrierVehType.getVehicleCostInformation().perTimeUnit, 0);
		Assert.assertTrue(AbfallUtils.vehicleTypes.getVehicleTypes().containsKey(AbfallUtils.carrierVehType.getId()));
	}

	@Test
	public final void testCreateGarbageTruck() {
		AbfallUtils.carrierVehType = CarrierVehicleType.Builder.newInstance(Id.create("truckType", VehicleType.class))
				.build();
		CarrierVehicle truck = AbfallUtils.createGarbageTruck("testTruck", "12345", 3600., 7200.);
		Assert.assertEquals("testTruck", truck.getVehicleId().toString());
		Assert.assertEquals("12345", truck.getLocation().toString());
		Assert.assertEquals(3600., truck.getEarliestStartTime(), MatsimTestUtils.EPSILON);
		Assert.assertEquals(7200., truck.getLatestEndTime(), MatsimTestUtils.EPSILON);

	}

	@Test
	public final void testCreateCarriersBerlin() {
		final String berlinDistrictsWithGarbageInformations = "scenarios/garbageInput/districtsWithGarbageInformations.shp";
		Carriers testCarriers = new Carriers();
		HashMap<String, Carrier> carrierMap = AbfallUtils.createCarrier();
		Collection<SimpleFeature> districtsWithGarbage = ShapeFileReader
				.getAllFeatures(berlinDistrictsWithGarbageInformations);
		AbfallUtils.carrierVehType = CarrierVehicleType.Builder.newInstance(Id.create("truckType", VehicleType.class))
				.build();
		AbfallUtils.createCarriersBerlin(districtsWithGarbage, testCarriers, carrierMap, FleetSize.INFINITE);

		for (Carrier singleCarrier : carrierMap.values()) {
			Assert.assertEquals(FleetSize.INFINITE, singleCarrier.getCarrierCapabilities().getFleetSize());
			Assert.assertEquals(1, singleCarrier.getCarrierCapabilities().getVehicleTypes().size());
			Assert.assertTrue(
					singleCarrier.getCarrierCapabilities().getVehicleTypes().contains(AbfallUtils.carrierVehType));
			Assert.assertEquals(1, singleCarrier.getCarrierCapabilities().getCarrierVehicles().size());
		}
		
	}

	@Test
	public final void testCreateMapWithLinksInDistricts() {
		
	}
	
	@Test
	public final void testCreateDumpMap() {
		HashMap<String, Id<Link>> garbageDumps = AbfallUtils.createDumpMap();
		Assert.assertEquals(5, garbageDumps.size());
		for (Id<Link> link : garbageDumps.values()) {
			Assert.assertNotNull(link);
		}
		
	}
	
	@Test
	public final void testShapeFile() {
		final String berlinDistrictsWithGarbageInformations = "scenarios/garbageInput/districtsWithGarbageInformations.shp";
		Collection<SimpleFeature> districtsWithGarbage = ShapeFileReader
				.getAllFeatures(berlinDistrictsWithGarbageInformations);
		for (SimpleFeature districtInformation : districtsWithGarbage) {
			Assert.assertNotNull(districtInformation.getAttribute("Depot"));
			Assert.assertNotNull(districtInformation.getAttribute("Ortsteil"));
			//...
		}
		Assert.assertEquals(96,districtsWithGarbage.size());
	}
}
