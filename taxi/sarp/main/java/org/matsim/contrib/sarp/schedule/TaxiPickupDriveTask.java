package org.matsim.contrib.sarp.schedule;

import org.matsim.contrib.dvrp.router.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.schedule.DriveTaskImpl;
import org.matsim.contrib.sarp.data.AbstractRequest;

public class TaxiPickupDriveTask extends DriveTaskImpl
	implements TaxiTaskWithRequest
{
	private AbstractRequest request;

	public TaxiPickupDriveTask(VrpPathWithTravelData path, AbstractRequest request)
	{
		super(path);
		// TODO Auto-generated constructor stub
		if(request.getFromLink() != path.getToLink())
			throw new IllegalArgumentException();
		this.request = request;
		request.setPickupDriveTask(this);
		
	}

	@Override
	public TaxiTaskType getTaxiTaskType() {
		// TODO Auto-generated method stub
		return TaxiTaskType.PICKUP_DRIVE;
	}

	@Override
	public AbstractRequest getRequest() {
		// TODO Auto-generated method stub
		return this.request;
	}

	@Override
	public void removeFromRequest() {
		// TODO Auto-generated method stub
		this.request.setPickupDriveTask(null);
		
	}
	
    @Override
    protected String commonToString()
    {
        return "[" + getTaxiTaskType().name() + "]" + super.commonToString();
    }

}
