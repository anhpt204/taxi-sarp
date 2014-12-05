package org.matsim.contrib.ssarp.optimizer;

import java.util.Collection;

import org.matsim.contrib.dvrp.data.Request;
import org.matsim.contrib.dvrp.schedule.DriveTask;
import org.matsim.contrib.dvrp.schedule.Schedule;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.contrib.ssarp.optimizer.TaxiOptimizer;
import org.matsim.contrib.ssarp.optimizer.TaxiOptimizerConfiguration;
import org.matsim.contrib.ssarp.data.AbstractRequest;
import org.matsim.contrib.ssarp.data.ParcelRequest;
import org.matsim.contrib.ssarp.data.PeopleRequest;
import org.matsim.contrib.ssarp.enums.RequestType;
import org.matsim.contrib.ssarp.schedule.TaxiTask;
import org.matsim.contrib.ssarp.schedule.TaxiTask.TaxiTaskType;
import org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent;

public abstract class AbstractTaxiOptimizer 
	implements TaxiOptimizer
{
	protected final TaxiOptimizerConfiguration optimConfig;

	protected final Collection<AbstractRequest> unplannedPeopleRequests;
	protected final Collection<AbstractRequest> unplannedParcelRequests;

	protected boolean requiresReoptimization = false;

	public AbstractTaxiOptimizer(TaxiOptimizerConfiguration optimConfig,
            Collection<AbstractRequest> unplannedPeopleRequests,
            Collection<AbstractRequest> unplannedParcelRequests)
	{
		this.optimConfig = optimConfig;
		this.unplannedPeopleRequests = unplannedPeopleRequests;
		this.unplannedParcelRequests = unplannedParcelRequests;
	}
	
	
	protected abstract void scheduleUnplannedRequests();
	 
	 
	@Override
	public void nextLinkEntered(DriveTask driveTask) {
		@SuppressWarnings("unchecked")
		Schedule<TaxiTask> schedule = (Schedule<TaxiTask>)driveTask.getSchedule();
		
		double predictedEndTime = driveTask.getTaskTracker().predictEndTime(optimConfig.context.getTime());
		
		optimConfig.scheduler.updateCurrentAndPlannedTasks(schedule, predictedEndTime);
		
		
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.matsim.contrib.dvrp.optimizer.VrpOptimizer#requestSubmitted(org.matsim.contrib.dvrp.data.Request)
	 * when a new request arrive
	 */
	@Override
	public void requestSubmitted(Request request) 
	{
		if (request == null)
			return;
		
		AbstractRequest absRequest = (AbstractRequest)request;
		
		if(((AbstractRequest)request).getType() == RequestType.PEOPLE)
		{
			unplannedPeopleRequests.add(absRequest);
			requiresReoptimization = true;
		}
		else
		{
			unplannedParcelRequests.add(absRequest);
			
			//need a strategy to re-optimize (ex: number of parcel requests)
			//if(unplannedParcelRequests.size() >= 5)
			//	requiresReoptimization = true;			
		}
		
	}

	@Override
	public void nextTask(Schedule<? extends Task> schedule) 
	{
		@SuppressWarnings("unchecked")
		Schedule<TaxiTask> taxiSchedule = (Schedule<TaxiTask>)schedule;
		//update schedule
		optimConfig.scheduler.updateBeforeNextTask(taxiSchedule);
		
		TaxiTask newCurrentTask = taxiSchedule.nextTask();
		
		if(!optimConfig.scheduler.getParams().destinationKnown)
		{
			if(newCurrentTask != null
					&& newCurrentTask.getTaxiTaskType() == TaxiTaskType.PEOPLE_DROPOFF_DRIVE)
				requiresReoptimization = true;
		}
		
	}

	@Override
	public void notifyMobsimBeforeSimStep(MobsimBeforeSimStepEvent e) {
		if(requiresReoptimization)
		{
			scheduleUnplannedRequests();
			requiresReoptimization = false;
		}
		
	}

}
