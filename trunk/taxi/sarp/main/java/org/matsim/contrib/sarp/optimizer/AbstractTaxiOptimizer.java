package org.matsim.contrib.sarp.optimizer;

import java.util.Collection;

import org.matsim.contrib.dvrp.data.Request;
import org.matsim.contrib.dvrp.schedule.DriveTask;
import org.matsim.contrib.dvrp.schedule.Schedule;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.contrib.sarp.data.AbstractRequest;
import org.matsim.contrib.sarp.schedule.TaxiTask;
import org.matsim.contrib.sarp.schedule.TaxiTask.TaxiTaskType;
import org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent;

import playground.michalm.taxi.optimizer.TaxiOptimizer;

public abstract class AbstractTaxiOptimizer 
	implements TaxiOptimizer
{
	protected final TaxiOptimizerConfiguration optimConfig;

	protected final Collection<AbstractRequest> unplannedRequests;

	protected boolean requiresReoptimization = false;

	public AbstractTaxiOptimizer(TaxiOptimizerConfiguration optimConfig,
            Collection<AbstractRequest> unplannedRequests)
	{
		this.optimConfig = optimConfig;
		this.unplannedRequests = unplannedRequests;
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
	public void requestSubmitted(Request request) {
		unplannedRequests.add((AbstractRequest)request);
		requiresReoptimization = true;
		
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
					&& newCurrentTask.getTaxiTaskType() == TaxiTaskType.DROPOFF_DRIVE)
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
