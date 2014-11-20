package org.matsim.contrib.sarp.schedule;

import org.matsim.contrib.dvrp.schedule.Task;

public interface TaxiTask extends Task 
{
	public static enum TaxiTaskType
    {
        PICKUP_DRIVE, PICKUP_STAY, DROPOFF_DRIVE, DROPOFF_STAY, CRUISE_DRIVE, WAIT_STAY;

        //TODO consider shorter names:
        //TO_PICKUP, PICKUP, TO_DROPOFF, DROPOFF, CRUISE, WAIT;
    }


    TaxiTaskType getTaxiTaskType();

}
