package org.matsim.contrib.sarp.optimizer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.matsim.contrib.dvrp.data.Requests;
import org.matsim.contrib.dvrp.data.Vehicle;
import org.matsim.contrib.sarp.data.ParcelRequest;
import org.matsim.contrib.sarp.data.PeopleRequest;
import org.matsim.contrib.sarp.filter.*;
import org.matsim.contrib.sarp.vehreqpath.PathCostCalculators;
import org.matsim.contrib.sarp.vehreqpath.VehicleRequestPath;
import org.matsim.contrib.sarp.vehreqpath.VehicleRequestsRoute;

public class Optimizer1 extends AbstractTaxiOptimizer
{
	public final int MAXNUMBERPARCELS = 5;
	
	private Set<Vehicle> idleVehicles;
	
	//private final RequestFilter requestFilter;
	private final VehicleFilter vehicleFilter;
	
	public Optimizer1(TaxiOptimizerConfiguration optimConfig)
	{
		super(optimConfig, new TreeSet<PeopleRequest>(Requests.ABSOLUTE_COMPARATOR),
				new TreeSet<ParcelRequest>(Requests.ABSOLUTE_COMPARATOR));
		
		this.vehicleFilter = new KStraightLineNearestVehicleFilter(optimConfig.scheduler, optimConfig.params.nearestVehiclesLimit);
		
	}
	
	private void getIdleVehicles()
	{
		idleVehicles = new HashSet<Vehicle>();
		for(Vehicle veh: optimConfig.context.getVrpData().getVehicles())
		{
			if(optimConfig.scheduler.isIdle(veh))
				idleVehicles.add(veh);
		}
		
	}

	@Override
	protected void scheduleUnplannedRequests()
	{
		getIdleVehicles();
		
		Iterator<PeopleRequest> iterPeopleRequests = unplannedPeopleRequests.iterator();
		
		//while there is a people request and have an idle vehicle
		while(iterPeopleRequests.hasNext() && !idleVehicles.isEmpty())
		{
			PeopleRequest peopleRequest = iterPeopleRequests.next();
			
			Vehicle feasibleVehicle = this.optimConfig.vrpFinder.findFeasibleTaxi(idleVehicles, peopleRequest);
			
			//if there is not any taxi satisfied, then reject
			if(feasibleVehicle == null)
			{
				unplannedPeopleRequests.remove(peopleRequest);
			}
			else 
			{
				//find a route with some parcel requests
				VehicleRequestsRoute bestRoute = optimConfig.vrpFinder.setupRouteWithParcelsInserted(feasibleVehicle, 
						peopleRequest, 
						unplannedParcelRequests, 
						MAXNUMBERPARCELS, 
						PathCostCalculators.BEST_COST);
				
				//if found the best route
				if(bestRoute != null)
				{
					//then build schedule for this vehicle
					optimConfig.scheduler.scheduleRequests(bestRoute);
					//and then remove all parcel request from unplannedParcelRequests
					for(ParcelRequest p: bestRoute.parcelRequests)
						unplannedParcelRequests.remove(p);
					// and remove peopleRequest and feasibleVehicle
					unplannedPeopleRequests.remove(peopleRequest);
					idleVehicles.remove(feasibleVehicle);
					
					
					
				}
				
			}
			
		}
		
		
	}

}
