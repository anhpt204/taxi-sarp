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

public class Optimizer1 extends AbstractTaxiOptimizer
{
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
		idleVehicles = new HashSet<>();
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
			
			Iterable<Vehicle> filteredVehs = this.vehicleFilter.filterVehiclesForRequest(idleVehicles, peopleRequest);
			
			
		}
		
		
	}

}
