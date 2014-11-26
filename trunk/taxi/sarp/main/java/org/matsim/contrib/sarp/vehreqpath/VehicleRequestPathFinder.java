/**
 * Project: taxi
 * Package: org.matsim.contrib.sarp.vehreqpath
 * Author: pta
 * Date: Nov 24, 2014
 */
package org.matsim.contrib.sarp.vehreqpath;

import java.awt.geom.IllegalPathStateException;
import java.util.ArrayList;
import java.util.Collection;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.data.Vehicle;
import org.matsim.contrib.dvrp.router.VrpPathCalculator;
import org.matsim.contrib.dvrp.router.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.util.LinkTimePair;
import org.matsim.contrib.sarp.data.*;
import org.matsim.contrib.sarp.scheduler.TaxiScheduler;
import org.matsim.contrib.sarp.util.CombinationGenerator;
import org.matsim.contrib.sarp.util.PermutationGenerator;

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
	public VehicleRequestsRoute setupRouteWithParcelsInserted(Vehicle vehicle, 
			PeopleRequest peopleRequest,
			Collection<ParcelRequest> parcelRequests,
			int maxNumberParcels,
			VehicleRequestPathCost costCalculator)
	{
		VehicleRequestsRoute bestRoute = null;
		double bestCost = Double.MAX_VALUE;
		// copy parcels?
		// I do not understand why doing this ???
		ArrayList<ParcelRequest> selectedParcels = new ArrayList<ParcelRequest>();
		for(ParcelRequest parcelRequest: parcelRequests)
		{
			selectedParcels.add(parcelRequest);
		}
		
		//get optimal route without parcels
		ArrayList<ParcelRequest> parcels = new ArrayList<ParcelRequest>();
		VehicleRequestsRoute route = this.computeBestCostRoute(vehicle, peopleRequest, parcels, costCalculator);
		if(route != null)
		{
			bestCost = route.getCost();
			bestRoute = route;
		}
		
		ArrayList<ParcelRequest> removedParcelRequests = null;
		//check with each size of parcels
		for(int k = 1; k < maxNumberParcels; k++)
		{
			if(k > selectedParcels.size()) break;
			
			//generate all combination of k elements in n elements (k < n)
			CombinationGenerator comGenerator = new CombinationGenerator(k, selectedParcels.size());
			comGenerator.generate();
			
			//with each combination
			for(int idx = 0; idx < comGenerator.size(); idx++)
			{
				int[] combination = comGenerator.get(idx);
				
				//get parcel requests
				parcels.clear();
				for(int i = 0; i < combination.length; i++)
				{
					parcels.add(selectedParcels.get(combination[i]));
				}
				//clear old route
				route = null;
				//calculate route
				route = this.computeBestCostRoute(vehicle, peopleRequest, parcels, costCalculator);
				//update best route
				if(route != null && route.getCost() < bestCost)
				{
					bestCost = route.getCost();
					bestRoute = route;
					removedParcelRequests = parcels;
				}
			}
		}
		
		return bestRoute;
		
	}
	
	/**
	 * Method from Dr.Dung's code
	 * @param vehicle
	 * @param peopleRequest
	 * @param parcels
	 * @return
	 */
	private VehicleRequestsRoute computeBestCostRoute(Vehicle vehicle,
			PeopleRequest peopleRequest, ArrayList<ParcelRequest> parcels,
			VehicleRequestPathCost costCalculator)
	{
		//generate permutation of pickup and dropoff points
		
		//create an array of Links
		Link[] links = new Link[2*parcels.size() + 2];
		links[0] = peopleRequest.getFromLink();
		links[1] = peopleRequest.getToLink();
		
		for(int i = 0; i < parcels.size(); i++)
		{
			links[2*i + 2] = parcels.get(i).getFromLink();
			links[2*i + 3] = parcels.get(i).getToLink();
		}
		//get all permutation??? should pickup people first?
		PermutationGenerator perm = new PermutationGenerator(links);
		perm.generate();
		
		double bestCost = Double.MAX_VALUE;
		VehicleRequestsRoute bestRoute = null;
		//with each permutation
		for(int k = 0; k < perm.size(); k++)
		{	
			VehicleRequestPath[] paths = getPath(vehicle, links, peopleRequest, parcels);
			
			if(paths == null)
				continue;
			else
			{
				VehicleRequestsRoute aRoute = new VehicleRequestsRoute(vehicle, 
						peopleRequest, parcels, paths );
				double cost = costCalculator.getCost(aRoute);
				if(cost < bestCost)
				{
					bestCost = cost;
					bestRoute = aRoute;
				}
			}
		}
		return bestRoute;
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
	
	/**
	 * Get path for a vehicle to go through links
	 * @param vehicle
	 * @param links: the list of links that vehicle must go through
	 * @param peopleRequest
	 * @param parcelRequests
	 * @return
	 */
	
	private VehicleRequestPath[] getPath(Vehicle vehicle, Link[] links, 
			PeopleRequest peopleRequest, ArrayList<ParcelRequest> parcelRequests)
	{
		//number of paths = links.length - 1 + 1 (from current location to first link)
		VehicleRequestPath[] paths = new VehicleRequestPath[links.length];
		
		//get earlist time, location when vehicle is idle
		LinkTimePair departure = this.scheduler.getEarliestIdleness(vehicle);
		VrpPathWithTravelData path = this.pathCalculator.calcPath(departure.link, links[0], departure.time);
		
		if(path == null)
			return null;
		
		//fist always for people request
		paths[0] = new VehicleRequestPath(vehicle, peopleRequest , path);
		
		
		for(int i = 1; i < paths.length; i++)
		{
			//departure time = arrival time of previous path + dropoff time
			path = this.pathCalculator.calcPath(links[i-1], 
					links[i], 
					paths[i-1].path.getArrivalTime() + this.scheduler.getParams().dropoffDuration);
			
			if(path == null)
				return null;
			
			//which request for this path from link i-1 to link i?
			//
			//there are two possible cases: 
			//1. drive to pickup at i-th link (serve request at i),
			//2. drive to dropoff at i-th link (serve some request)
			
			//check are there any request to pickup at link i
			AbstractRequest request = null;
			
			//check people drop off at i
			if(peopleRequest.getToLink() == links[i])
			{
				request = peopleRequest;
			}
			else // either parcel pickup request or parcel drop off request
			{
				for(ParcelRequest parcelRequest : parcelRequests)
				{
					if(parcelRequest.getFromLink().equals(links[i])
							|| parcelRequest.getToLink().equals(links[i]))
					{
						request = parcelRequest;
						break;
					}
				}
			}
			
			if(request == null) //is impossible
				throw new IllegalPathStateException();
			
			paths[i] = new VehicleRequestPath(vehicle, request, path);
		}
		
		return paths;
	}
}
