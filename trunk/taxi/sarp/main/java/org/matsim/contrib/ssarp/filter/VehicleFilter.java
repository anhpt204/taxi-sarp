package org.matsim.contrib.ssarp.filter;

import org.matsim.contrib.dvrp.data.Vehicle;
import org.matsim.contrib.ssarp.data.AbstractRequest;

public interface VehicleFilter
{
	
	Iterable<Vehicle> filterVehiclesForRequest(Iterable<Vehicle> vehicles, AbstractRequest request);

	VehicleFilter NO_FILTER = new VehicleFilter()
	{
		
		@Override
		public Iterable<Vehicle> filterVehiclesForRequest(
				Iterable<Vehicle> vehicles, AbstractRequest request)
		{
			return vehicles;
		}
	};
}
