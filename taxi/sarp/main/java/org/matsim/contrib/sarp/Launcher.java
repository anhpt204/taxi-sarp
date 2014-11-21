package org.matsim.contrib.sarp;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.dvrp.MatsimVrpContext;
import org.matsim.contrib.dvrp.router.LeastCostPathCalculatorWithCache;
import org.matsim.contrib.dvrp.router.VrpPathCalculator;
import org.matsim.contrib.dvrp.router.VrpPathCalculatorImpl;
import org.matsim.contrib.dvrp.run.VrpLauncherUtils;
import org.matsim.contrib.dvrp.run.VrpLauncherUtils.TravelTimeSource;
import org.matsim.contrib.dvrp.util.time.TimeDiscretizer;
import org.matsim.core.router.Dijkstra;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
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
		//Viet lai RequestCreator, chi can 1 Creator thoi.
		//trong file requests.txt, them mot truong chi ra no la people hay parcel
		//dua vao do de biet luc nao tao PeopleRequest, luc nao tao ParcelRequest
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
