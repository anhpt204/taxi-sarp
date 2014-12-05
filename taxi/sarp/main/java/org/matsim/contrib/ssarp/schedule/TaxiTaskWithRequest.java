package org.matsim.contrib.ssarp.schedule;

import org.matsim.contrib.ssarp.schedule.TaxiTask;
import org.matsim.contrib.ssarp.data.AbstractRequest;

public interface TaxiTaskWithRequest extends TaxiTask 
{
	AbstractRequest getRequest();
	
    //called (when removing a task) in order to update the request-2-task assignment 
	void removeFromRequest();

}
