package org.matsim.contrib.sarp;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.MatsimVrpContext;
import org.matsim.contrib.dvrp.router.LeastCostPathCalculatorWithCache;
import org.matsim.contrib.dvrp.router.VrpPathCalculator;
import org.matsim.contrib.dvrp.run.VrpLauncherUtils;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.trafficmonitoring.TravelTimeCalculator;

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
				VrpLauncherUtils.initTravelTime(this.scenario, ttimeSource, eventsFileName)
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
