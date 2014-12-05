package org.matsim.contrib.ssarp.filter;

import org.matsim.contrib.dvrp.data.Vehicle;
import org.matsim.contrib.ssarp.data.AbstractRequest;

public interface RequestFilter
{
	Iterable<AbstractRequest> filterRequestsForVehicle(Iterable<AbstractRequest> requests, Vehicle vehicle);
	

	RequestFilter NO_FILTER = new RequestFilter()
	{
		
		@Override
		public Iterable<AbstractRequest> filterRequestsForVehicle(
				Iterable<AbstractRequest> requests, Vehicle vehicle)
		{
			return requests;
		}
	};
}
