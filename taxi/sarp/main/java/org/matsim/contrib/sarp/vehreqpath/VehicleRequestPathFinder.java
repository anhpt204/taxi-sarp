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
import java.util.HashSet;
import java.util.Random;
import java.util.TreeSet;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.data.Requests;
import org.matsim.contrib.dvrp.data.Vehicle;
import org.matsim.contrib.dvrp.router.VrpPathCalculator;
import org.matsim.contrib.dvrp.router.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.util.LinkTimePair;
import org.matsim.contrib.sarp.data.*;
import org.matsim.contrib.sarp.schedule.TaxiTask.TaxiTaskType;
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
			AbstractRequest peopleRequest)
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
	/*public VehicleRequestsRoute setupRouteWithParcelsInserted(Vehicle vehicle, 
			AbstractRequest peopleRequest,
			Collection<AbstractRequest> parcelRequests,
			int maxNumberParcels,
			VehicleRequestPathCost costCalculator)
	{
		VehicleRequestsRoute bestRoute = null;
		double bestCost = Double.MAX_VALUE;
		// copy parcels?
		// I do not understand why doing this ???
		ArrayList<AbstractRequest> selectedParcels = new ArrayList<AbstractRequest>();
		for(AbstractRequest parcelRequest: parcelRequests)
		{
			selectedParcels.add(parcelRequest);
		}
		
		//get optimal route without parcels
		ArrayList<AbstractRequest> parcels = new ArrayList<AbstractRequest>();
		VehicleRequestsRoute route = this.computeBestCostRoute(vehicle, peopleRequest, parcels, costCalculator);
		if(route != null)
		{
			bestCost = route.getCost();
			bestRoute = route;
		}
		
		ArrayList<AbstractRequest> removedParcelRequests = null;
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
	*/
	/**
	 * route = pickup people, [pickup parcel i, dropoff parcel i], dropoff people
	 * @param vehicle
	 * @param peopleRequest
	 * @param parcelRequests
	 * @param maxNumberParcels
	 * @param costCalculator
	 * @return
	 */
	public VehicleRequestsRoute simpleSetupRouteWithParcelsInserted(Vehicle vehicle, 
			AbstractRequest peopleRequest,
			Collection<AbstractRequest> parcelRequests,
			int maxNumberParcels,
			VehicleRequestPathCost costCalculator)
	{
		VehicleRequestsRoute bestRoute = null;
		double bestCost = Double.MAX_VALUE;
		
				
		
		VehicleRequestsRoute route = this.computeCostRoute(vehicle, peopleRequest, parcelRequests, costCalculator);
		if(route != null)
		{
			bestCost = route.getCost();
			bestRoute = route;
		}
				
		
		return bestRoute;
		
	}
	
	/**
	 * route = pickup people, [pickup parcel i, dropoff parcel i], dropoff people
	 * @param vehicle
	 * @param peopleRequest
	 * @param parcelRequests
	 * @param costCalculator
	 * @return
	 */
	private VehicleRequestsRoute computeCostRoute(Vehicle vehicle,
			AbstractRequest peopleRequest,
			Collection<AbstractRequest> parcelRequests,
			VehicleRequestPathCost costCalculator)
	{		
		
		VehicleRequestPath[] paths = getPath(vehicle, peopleRequest, parcelRequests);
		
		if(paths == null)
			return null;
		else
		{
			VehicleRequestsRoute aRoute = new VehicleRequestsRoute(vehicle, 
					peopleRequest, parcelRequests, paths );
			double cost = costCalculator.getCost(aRoute);
			
			return aRoute;
		}
	}

	/**
	 * Method from Dr.Dung's code
	 * @param vehicle
	 * @param peopleRequest
	 * @param parcels
	 * @return
	 */
	/*private VehicleRequestsRoute computeBestCostRoute(Vehicle vehicle,
			AbstractRequest peopleRequest, ArrayList<AbstractRequest> parcels,
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
	//		VehicleRequestPath[] paths = getPath(vehicle, links, peopleRequest, parcels);
			
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
	}*/

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
	
	private VrpPathWithTravelData getPathForPeopleRequest(Vehicle vehicle, AbstractRequest request)
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
	
	private VehicleRequestPath[] getPath(Vehicle vehicle,  
			AbstractRequest peopleRequest, Collection<AbstractRequest> parcelRequests)
	{
		//create an array of Links
		Link[] links = new Link[2*parcelRequests.size() + 2];
		links[0] = peopleRequest.getFromLink();
		links[links.length-1] = peopleRequest.getToLink();
		
		int i = 1;
		for(AbstractRequest parcel: parcelRequests)
		{
			links[i] = parcel.getFromLink();
			links[i + 1] = parcel.getToLink();
			i += 2;
		}
		//number of paths = links.length - 1 + 1 (from current location to first link)
		VehicleRequestPath[] paths = new VehicleRequestPath[links.length];
		
		//get earlist time, location when vehicle is idle
		LinkTimePair departure = this.scheduler.getEarliestIdleness(vehicle);
		VrpPathWithTravelData path = this.pathCalculator.calcPath(departure.link, links[0], departure.time);
		
		if(path == null)
			return null;
		
		//fist always for people request
		paths[0] = new VehicleRequestPath(vehicle, peopleRequest , path, TaxiTaskType.PEOPLE_PICKUP_DRIVE);
		
		VrpPathWithTravelData[] vrpPaths = new VrpPathWithTravelData[paths.length];
		vrpPaths[0] = path;
		
		for(i = 1; i < paths.length; i++)
		{
			if(links[i-1] == null || links[i] == null)
				return null;
			//departure time = arrival time of previous path + dropoff time
			vrpPaths[i] = this.pathCalculator.calcPath(links[i-1], 
					links[i], 
					vrpPaths[i-1].getArrivalTime());// + this.scheduler.getParams().dropoffDuration);
			
			if(vrpPaths[i-1] == null)
				return null;
			
			//which request for this path from link i-1 to link i?
			//
			//there are two possible cases: 
			//1. drive to pickup at i-th link (serve request at i),
			//2. drive to dropoff at i-th link (serve some request)
			
			//check are there any request to pickup at link i
						
		}
		
		i = 1;
		for(AbstractRequest r : parcelRequests)
		{
			paths[i] = new VehicleRequestPath(vehicle, r, vrpPaths[i], TaxiTaskType.PARCEL_PICKUP_DRIVE);
			paths[i+1] = new VehicleRequestPath(vehicle, r, vrpPaths[i+1], TaxiTaskType.PARCEL_DROPOFF_DRIVE);
			
			i += 2;
		}
		
		paths[paths.length-1] = new VehicleRequestPath(vehicle, peopleRequest, vrpPaths[vrpPaths.length-1], TaxiTaskType.PEOPLE_DROPOFF_DRIVE);

		
		//for(VehicleRequestPath p : paths)
		//{
		//	System.err.println(p.request.getType().toString() + ": " + p.path.getDepartureTime() + ", " + p.path.getArrivalTime());
		//}
		
		return paths;
	}
}
