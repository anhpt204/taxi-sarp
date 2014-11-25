/**
 * Project: taxi
 * Package: org.matsim.contrib.sarp.vehreqpath
 * Author: pta
 * Date: Nov 24, 2014
 */
package org.matsim.contrib.sarp.vehreqpath;

import java.util.ArrayList;
import java.util.Collection;

import org.matsim.contrib.dvrp.data.Vehicle;
import org.matsim.contrib.dvrp.router.VrpPathCalculator;
import org.matsim.contrib.dvrp.router.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.util.LinkTimePair;
import org.matsim.contrib.sarp.data.*;
import org.matsim.contrib.sarp.scheduler.TaxiScheduler;

/**
 * 
 *
 *TODO
 * Methods for optimizing routes
 */
public class VehicleRequestPathFinder
{
	public final VrpPathCalculator pathCalculator;
	public final TaxiScheduler scheduler;
	
	
	public VehicleRequestPathFinder(VrpPathCalculator pathCalculator,
			TaxiScheduler scheduler)
	{
		this.pathCalculator = pathCalculator;
		this.scheduler = scheduler;
	}
	
	/**
	 * @param idleVehicles: idle vehicles
	 * @param peopleRequest: People request
	 * @return a feasible taxi: can go to pickup this person
	 * in time window
	 */
	public Vehicle findFeasibleTaxi(Iterable<Vehicle> idleVehicles,
			PeopleRequest peopleRequest)
	{
		Vehicle feasibleVehicle = null;
		double bestCost = Double.MAX_VALUE;
		
		for(Vehicle veh : idleVehicles)
		{
			VrpPathWithTravelData path = getPathForPeopleRequest(veh, peopleRequest);
			double cost = path.getTravelCost();
			
			//
			if(cost < bestCost)
			{
				bestCost = cost;
				feasibleVehicle = veh;				
			}
		}
		
		return feasibleVehicle;
	}
	
	/**
	 * Implement the method of Dr. Dung
	 * @param vehicle
	 * @param peopleRequest
	 * @param maxNumberParcels: maximum number of parcels in this route
	 * @return null if not exist
	 */
	public VehicleRequestPath setupRouteWithParcelsInserted(Vehicle vehicle, 
			PeopleRequest peopleRequest,
			Collection<ParcelRequest> parcelRequests,
			int maxNumberParcels)
	{
		VehicleRequestPath path = null;
		double maxBenefits = 0;
		// copy parcels?
		// I do not understand why doing this ???
		ArrayList<ParcelRequest> selectedParcels = new ArrayList<ParcelRequest>();
		for(ParcelRequest parcelRequest: parcelRequests)
		{
			selectedParcels.add(parcelRequest);
		}
		
		//get optimal route without parcels
		ArrayList<ParcelRequest> PR = new ArrayList<ParcelRequest>();
		VehicleRequestPath route = this.computeBenefitsOptimizeRoute(vehicle, peopleRequest, PR);
		
		return path;
		
	}
	
	private VehicleRequestPath computeBenefitsOptimizeRoute(Vehicle vehicle,
			PeopleRequest peopleRequest, ArrayList<ParcelRequest> pR)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public VehicleRequestPath findBestVehicleForRequests(
			PeopleRequest peopleRequest,
			Collection<ParcelRequest> parcelRequests,
			Iterable<Vehicle> vehicles,
			VehicleRequestPathCost costCalculator)
	{
		VehicleRequestPath bestPath = null;
		
		//the smaller cost, the better
		double bestCost = Double.MAX_VALUE;
		
		for(Vehicle veh : vehicles)
		{
			//check path for people request
			VrpPathWithTravelData peopleTempPath = getPathForPeopleRequest(veh, peopleRequest);
			
		}
		return null;
	}
	
	private VrpPathWithTravelData getPathForPeopleRequest(Vehicle vehicle, PeopleRequest request)
	{
		LinkTimePair departure = this.scheduler.getEarliestIdleness(vehicle);
		
		return departure == null? null : this.pathCalculator.calcPath(departure.link, 
				request.getFromLink(), 
				departure.time);
	}
}
