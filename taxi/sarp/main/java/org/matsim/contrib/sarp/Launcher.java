package org.matsim.contrib.sarp;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.MatsimVrpContext;
import org.matsim.contrib.dvrp.MatsimVrpContextImpl;
import org.matsim.contrib.dvrp.data.Request;
import org.matsim.contrib.dvrp.data.VrpDataImpl;
import org.matsim.contrib.dvrp.passenger.PassengerEngine;
import org.matsim.contrib.dvrp.router.LeastCostPathCalculatorWithCache;
import org.matsim.contrib.dvrp.router.VrpPathCalculator;
import org.matsim.contrib.dvrp.router.VrpPathCalculatorImpl;
import org.matsim.contrib.dvrp.run.VrpLauncherUtils;
import org.matsim.contrib.dvrp.run.VrpLauncherUtils.TravelTimeSource;
import org.matsim.contrib.dvrp.schedule.DriveTask;
import org.matsim.contrib.dvrp.schedule.Schedule;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.contrib.dvrp.util.time.TimeDiscretizer;
import org.matsim.contrib.dynagent.run.DynAgentLauncherUtils;
import org.matsim.contrib.sarp.optimizer.TaxiOptimizerConfiguration;
import org.matsim.contrib.sarp.optimizer.TaxiOptimizerConfiguration.Goal;
import org.matsim.contrib.sarp.scheduler.TaxiScheduler;
import org.matsim.contrib.sarp.scheduler.TaxiSchedulerParams;
import org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.TravelTimeCalculator;

import playground.michalm.taxi.optimizer.TaxiOptimizer;

public class Launcher
{
	private LauncherParams params;
	private MatsimVrpContext context;
	private final Scenario scenario;
	
	private TravelTimeCalculator travelTimeCalculator;
	private LeastCostPathCalculatorWithCache routerWithCache;
	private VrpPathCalculator pathCalculator;
	
	public Launcher(LauncherParams params)
	{
		this.params = params;
		//initialize a scenario
		this.scenario = VrpLauncherUtils.initScenario(params.netFile, params.plansFile);
		
		//load requests
		
	}
	
	private void initVrpPathCalculator()
	{
		TravelTime travelTime = travelTimeCalculator == null? 
				VrpLauncherUtils.initTravelTime(this.scenario, params.algorithmConfig.ttimeSource
						, params.eventsFile): travelTimeCalculator.getLinkTravelTimes();
				
		TravelDisutility travelDisutility = VrpLauncherUtils.initTravelDisutility(params.algorithmConfig.tdisSource,
				travelTime);
		
		LeastCostPathCalculator router = new Dijkstra(this.scenario.getNetwork(),
				travelDisutility, travelTime);
		
		TimeDiscretizer timeDiscretizer = (params.algorithmConfig.ttimeSource == TravelTimeSource.FREE_FLOW_SPEED && //
		        !scenario.getConfig().network().isTimeVariantNetwork()) ? //
		                TimeDiscretizer.CYCLIC_24_HOURS : //
		                TimeDiscretizer.CYCLIC_15_MIN;
		
        routerWithCache = new LeastCostPathCalculatorWithCache(router, timeDiscretizer);
        pathCalculator = new VrpPathCalculatorImpl(routerWithCache, travelTime, travelDisutility);

	}

	void clearVrpPathCalculator()
    {
        travelTimeCalculator = null;
        routerWithCache = null;
        pathCalculator = null;
    }
	
	void run()
	{
		MatsimVrpContextImpl contextImpl = new MatsimVrpContextImpl();
		this.context = contextImpl;
		
		//scenario: network + population
		contextImpl.setScenario(scenario);
		
		//vehicle data: vehicles + requests
		VrpDataImpl taxiData = new VrpDataImpl();
		contextImpl.setVrpData(taxiData);
		
		//optimizer
		TaxiOptimizerConfiguration optimizerConfig = createOptimizerConfig();
		//create optimizer
		
		QSim qsim = DynAgentLauncherUtils.initQSim(scenario);
		contextImpl.setMobsimTimer(qsim.getSimTimer());
		
		//add optimizer to be a listener in simulation 
		//qsim.addQueueSimulationListeners(optimizer);
		
		PassengerEngine passengerEngine = new PassengerEngine("taxi", new RequestCreator(), 
				optimizer, contextImpl);
		
	}
	private TaxiOptimizerConfiguration createOptimizerConfig() 
	{
		TaxiSchedulerParams schedulerParams = new TaxiSchedulerParams(this.params.destinationKnown,
				this.params.pickupDuration, this.params.dropoffDuration);
		
		TaxiScheduler scheduler = new TaxiScheduler(context, pathCalculator, schedulerParams);
		
		
		
		return new TaxiOptimizerConfiguration(this.context, scheduler, Goal.MIN_PICKUP_TIME);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
    	String paramsFile = "./input/params.in";
        LauncherParams params = LauncherParams.readParams(paramsFile);
        Launcher launcher = new Launcher(params);
        launcher.initVrpPathCalculator();
        //launcher.go(false);
        //launcher.generateOutput();
	}

}
