package org.matsim.contrib.sarp.vehreqpath;

import java.util.Collection;

import org.matsim.contrib.dvrp.data.*;
import org.matsim.contrib.dvrp.router.VrpPathWithTravelData;
import org.matsim.contrib.sarp.data.*;

/*
 * Data structure for a path
 * - vehicle: taxi
 * - peopleRequest: a person for this path
 * - parcelRequests: a set of parcel for this path
 * - path
 */
public class VehicleRequestPath
{
	public final Vehicle vehicle;
	public final PeopleRequest peopleRequest;
	public final Collection<ParcelRequest> parcelRequests;
	public final VrpPathWithTravelData path;
	
	public VehicleRequestPath(Vehicle vehicle, 
			PeopleRequest peopleRequest,
			Collection<ParcelRequest> parcelRequests,
			VrpPathWithTravelData path)
	{
		this.vehicle = vehicle;
		this.peopleRequest = peopleRequest;
		this.parcelRequests = parcelRequests;
		this.path = path;
	}

}
