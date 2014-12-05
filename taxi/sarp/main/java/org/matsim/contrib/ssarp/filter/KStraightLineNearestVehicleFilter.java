package org.matsim.contrib.ssarp.filter;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.data.Vehicle;
import org.matsim.contrib.dvrp.util.DistanceUtils;
import org.matsim.contrib.ssarp.filter.VehicleFilter;
import org.matsim.contrib.ssarp.data.AbstractRequest;
import org.matsim.contrib.ssarp.scheduler.TaxiScheduler;

import pl.poznan.put.util.collect.PartialSort;

public class KStraightLineNearestVehicleFilter
	implements VehicleFilter
{
	private final TaxiScheduler scheduler;
	private final int k;

	public KStraightLineNearestVehicleFilter(TaxiScheduler scheduler, int k)
	{
		this.scheduler = scheduler;
		this.k = k;
	}
	
	@Override
	public Iterable<Vehicle> filterVehiclesForRequest(
			Iterable<Vehicle> vehicles, AbstractRequest request)
	{
		Link toLink = request.getFromLink();
		PartialSort<Vehicle> nearestVehicleSort = new PartialSort<Vehicle>(k);
		
		for(Vehicle veh:vehicles)
		{
			Link fromLink = scheduler.getEarliestIdleness(veh).link;
			double squareDistance = DistanceUtils.calculateSquaredDistance(fromLink, toLink);
			nearestVehicleSort.add(veh, squareDistance);
		}
		return nearestVehicleSort.retriveKSmallestElements();
	}
	
	
}
