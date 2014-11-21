package org.matsim.contrib.sarp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.data.Request;
import org.matsim.contrib.dvrp.data.RequestImpl;
import org.matsim.contrib.dvrp.passenger.PassengerRequestCreator;
import org.matsim.contrib.sarp.data.PeopleRequest;
import org.matsim.core.mobsim.framework.MobsimPassengerAgent;

public class PeopleRequestCreator implements PassengerRequestCreator
{

	@Override
	public PeopleRequest createRequest(Id<Request> id,
			MobsimPassengerAgent passenger, Link fromLink, Link toLink,
			double t0, double t1, double now)
	{
		String fileRequest = "./input/requests.txt";
		//read requests files
		double l0=t0;
		double l1=t1;
		double submissionTime = now;
		double maxTravelDistance = 0;
		int maxNbStops = 2;
		
		int idIndex = 0;
		int timeCallIndex = 1;
		int pickupPointIndex = 2;
		int deliveryPointInddex = 3;
		int earlyPickupTimeIndex = 4;
		int latePickupTimeIndex = 5;
		int earlyDeliveryTimeIndex = 6;
		int lateDeliveryTimeIndex = 7;
		int maxTravelDistanceIndex = 8;
		int maxNbStopsIndex = 9;
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(fileRequest));
			String line = reader.readLine();
			
			while ((line=reader.readLine()) != null)
			{
				String[] parts = line.split(" ");
				if(Id.create((parts[idIndex].trim()), Request.class).compareTo(id) == 0)
				{
					l0 = Double.parseDouble(parts[earlyDeliveryTimeIndex]);
					l1 = Double.parseDouble(parts[lateDeliveryTimeIndex]);
					submissionTime = Double.parseDouble(parts[timeCallIndex]);
					maxTravelDistance = Double.parseDouble(parts[maxTravelDistanceIndex]);
					maxNbStops = Integer.parseInt(parts[maxNbStopsIndex]);
				}
			}
			
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new PeopleRequest(id, passenger, t0, t1, l0, l1, fromLink, toLink, submissionTime);
	}

}
