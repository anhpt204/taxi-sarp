package org.matsim.contrib.sarp.enums;

import static org.matsim.contrib.dvrp.run.VrpLauncherUtils.TravelDisutilitySource.DISTANCE;
import static org.matsim.contrib.dvrp.run.VrpLauncherUtils.TravelDisutilitySource.TIME;
import static org.matsim.contrib.dvrp.run.VrpLauncherUtils.TravelTimeSource.EVENTS;
import static org.matsim.contrib.dvrp.run.VrpLauncherUtils.TravelTimeSource.FREE_FLOW_SPEED;

import org.matsim.contrib.dvrp.run.VrpLauncherUtils.TravelDisutilitySource;
import org.matsim.contrib.dvrp.run.VrpLauncherUtils.TravelTimeSource;
import org.matsim.contrib.sarp.optimizer.TaxiOptimizerConfiguration;
import org.matsim.contrib.sarp.optimizer.TaxiOptimizerConfiguration.Goal;


public enum AlgorithmConfig
{	
    NOS_TW_TD(AlgorithmType.NO_SCHEDULING, Goal.MIN_WAIT_TIME, FREE_FLOW_SPEED, DISTANCE),

	MIP_TW_FF(AlgorithmType.MIP_SCHEDULING, Goal.MIN_WAIT_TIME, FREE_FLOW_SPEED, TIME);
	 
	static enum AlgorithmType
    {
        NO_SCHEDULING, //
        ONE_TIME_SCHEDULING, //
        RE_SCHEDULING, //
        AP_SCHEDULING, //
        MIP_SCHEDULING;
    }
    
	final TravelTimeSource ttimeSource;
    final Goal goal;
    final TravelDisutilitySource tdisSource;
    final AlgorithmType algorithmType;


    AlgorithmConfig(AlgorithmType algorithmType, Goal goal, TravelTimeSource ttimeSource,
            TravelDisutilitySource tdisSource)
    {
        this.ttimeSource = ttimeSource;
        this.goal = goal;
        this.tdisSource = tdisSource;
        this.algorithmType = algorithmType;
    }

}
