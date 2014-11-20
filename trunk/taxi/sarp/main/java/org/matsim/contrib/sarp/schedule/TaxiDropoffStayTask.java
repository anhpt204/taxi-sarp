package org.matsim.contrib.sarp.schedule;

import org.matsim.contrib.dvrp.schedule.StayTaskImpl;
import org.matsim.contrib.sarp.data.AbstractRequest;

public class TaxiDropoffStayTask extends StayTaskImpl
	implements TaxiTaskWithRequest
{
	AbstractRequest request;

	public TaxiDropoffStayTask(double beginTime, double endTime, 
			AbstractRequest request) 
	{
		super(beginTime, endTime, request.getToLink());
		// TODO Auto-generated constructor stub
		this.request = request;
		request.setDropoffStayTask(this);
	}

	@Override
	public TaxiTaskType getTaxiTaskType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractRequest getRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeFromRequest() {
		// TODO Auto-generated method stub
		this.request.setDropoffStayTask(null);
		
	}
	
    @Override
    protected String commonToString()
    {
        return "[" + getTaxiTaskType().name() + "]" + super.commonToString();
    }

}
