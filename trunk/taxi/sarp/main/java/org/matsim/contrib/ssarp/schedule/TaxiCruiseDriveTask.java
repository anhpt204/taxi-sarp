package org.matsim.contrib.ssarp.schedule;

import org.matsim.contrib.dvrp.router.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.schedule.DriveTaskImpl;
import org.matsim.contrib.ssarp.schedule.TaxiTask;

public class TaxiCruiseDriveTask extends DriveTaskImpl
	implements TaxiTask
{

	public TaxiCruiseDriveTask(VrpPathWithTravelData path) {
		super(path);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TaxiTaskType getTaxiTaskType() {
		// TODO Auto-generated method stub
		return TaxiTaskType.CRUISE_DRIVE;
	}
	
	@Override
	protected String commonToString()
	{
        return "[" + getTaxiTaskType().name() + "]" + super.commonToString();
	}

}
