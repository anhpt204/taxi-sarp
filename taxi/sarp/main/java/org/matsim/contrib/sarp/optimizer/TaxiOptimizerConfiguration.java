package org.matsim.contrib.sarp.optimizer;

import org.matsim.contrib.dvrp.MatsimVrpContext;
import org.matsim.contrib.sarp.scheduler.TaxiScheduler;

public class TaxiOptimizerConfiguration 
{
	public static enum Goal
    {
        MIN_WAIT_TIME, MIN_PICKUP_TIME, DEMAND_SUPPLY_EQUIL, NULL
    };
    
    public final MatsimVrpContext context;
    public final TaxiScheduler scheduler;
    public final Goal goal;
    
    public TaxiOptimizerConfiguration(MatsimVrpContext context, TaxiScheduler scheduler,
    		Goal goal)
    {
    	this.context = context;
    	this.scheduler = scheduler;
    	this.goal = goal;
    }

}
