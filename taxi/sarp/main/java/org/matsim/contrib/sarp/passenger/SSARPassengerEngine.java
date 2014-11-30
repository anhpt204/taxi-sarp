/**
 * taxi
 * org.matsim.contrib.sarp.passenger
 * tuananh
 * Nov 28, 2014
 */
package org.matsim.contrib.sarp.passenger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.dvrp.MatsimVrpContext;
import org.matsim.contrib.dvrp.data.Request;
import org.matsim.contrib.dvrp.optimizer.VrpOptimizer;
import org.matsim.contrib.dvrp.passenger.AdvancedRequestStorage;
import org.matsim.contrib.dvrp.passenger.AwaitingPickupStorage;
import org.matsim.contrib.dvrp.passenger.PassengerEngine;
import org.matsim.contrib.dvrp.passenger.PassengerPickupActivity;
import org.matsim.contrib.dvrp.passenger.PassengerRequest;
import org.matsim.contrib.dvrp.passenger.PassengerRequestCreator;
import org.matsim.contrib.sarp.data.AbstractRequest;
import org.matsim.contrib.sarp.enums.RequestType;
import org.matsim.contrib.sarp.util.RequestEntry;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.framework.MobsimPassengerAgent;
import org.matsim.core.mobsim.qsim.InternalInterface;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.core.mobsim.qsim.agents.PopulationAgentSource;
import org.matsim.core.mobsim.qsim.interfaces.DepartureHandler;
import org.matsim.core.mobsim.qsim.interfaces.MobsimEngine;

/**
 * @author tuananh
 * Stochastic Share A Ride Passenger Engine
 * cooperate between taxi drivers, passengers (requests) and dispatcher
 * 
 * PassengerEngine class is for dynamic VRP, it is not support for stochastic
 * future requests
 */
public class SSARPassengerEngine
	extends PassengerEngine
{
	
	private final String mode;
	
	private final MatsimVrpContext context;
	private final SSARPassengerRequestCreator requestCreator;
	private final VrpOptimizer optimizer;
	
    private final AdvancedRequestStorage advancedRequestStorage;
    private final AwaitingPickupStorage awaitingPickupStorage;

    private final Queue<RequestEntry> sortedSubmissionTimeQueue;
    
    private final ArrayList<AbstractRequest> unplannedRequests = new ArrayList<>();
    
    private final QSim qsim;
    
	private InternalInterface internalInterface;
	//last time when we take a system snapshot
	private double preTimeSnapshot; 
	
	// time between each snapshot
	private double timeStep;

	private Map<Id<Person>, AbstractRequest> scheduledRequests = new HashMap<>();
	
	public SSARPassengerEngine(String mode,
			SSARPassengerRequestCreator requestCreator, VrpOptimizer optimizer,
			MatsimVrpContext context, 
			Queue<RequestEntry> sortedSubmissionTimeQueue,
			QSim qsim,
			double timeStep)
	{
		super(mode, requestCreator, optimizer, context);
		
		this.mode = mode;
		this.requestCreator = requestCreator;
		this.context = context;
		this.optimizer = optimizer;
		this.sortedSubmissionTimeQueue = sortedSubmissionTimeQueue;
		this.qsim = qsim;
		this.timeStep = timeStep;
		
		
		
		//start from 0
		preTimeSnapshot = 0;
		
        advancedRequestStorage = new AdvancedRequestStorage(context);
        awaitingPickupStorage = new AwaitingPickupStorage();
        
        
	}
	
	public String getMode()
	{
		return mode;
	}
	
	@Override
	public boolean handleDeparture(double now, MobsimAgent agent,
			Id<Link> fromLinkId)
	{
		if(!agent.getMode().equals(this.getMode()))
			return false;
		
		
		MobsimPassengerAgent passenger = (MobsimPassengerAgent)agent;
		
		Id<Link> toLinkId = passenger.getDestinationLinkId();
		double departureTime = now;
		
		this.internalInterface.registerAdditionalAgentOnLink(passenger);

		AbstractRequest request = scheduledRequests.get(passenger.getId());

		//this is an immediate request
		if(request == null)
		{
			request = createAbstractRequest(passenger, fromLinkId, toLinkId, departureTime, now);
			optimizer.requestSubmitted(request);
		}
		else // notify ready to depart
		{
			PassengerPickupActivity awaitingPickup = awaitingPickupStorage.retrieveAwaitingPickup(request);
			
			if(awaitingPickup != null)
				awaitingPickup.notifyPassengerIsReadyForDeparture(passenger, now);
		}
		
		return !request.isRejected();
	}

	private int nextId = 0;
	
	private AbstractRequest createAbstractRequest(
			MobsimPassengerAgent passenger, Id<Link> fromLinkId,
			Id<Link> toLinkId, double departureTime, double submissionTime)
	{
		Map<Id<Link>, ? extends Link> links = this.context.getScenario().getNetwork().getLinks();
		Link fromLink = links.get(fromLinkId);
		Link toLink = links.get(toLinkId);
		Id<Request> id = Id.create(mode + "_" + nextId, Request.class);
		
		double t0 = passenger.getActivityEndTime();
		double t1 = t0 + 10*60;
		
		AbstractRequest request = (AbstractRequest) requestCreator.createRequest(id, 
				passenger, fromLink, toLink, t0, t1, submissionTime);
		
		this.context.getVrpData().addRequest(request);
		return request;
	}

	@Override
	public void doSimStep(double time)
	{
		//before taking system snapshot
		if(time < preTimeSnapshot + timeStep)
		{
			//add all requests that have been submitted
			while(sortedSubmissionTimeQueue.peek() != null)
			{
				
				if(sortedSubmissionTimeQueue.peek().submissionTime <= time)
				{
					RequestEntry entry = sortedSubmissionTimeQueue.poll();
					for(MobsimAgent agent : qsim.getAgents())
					{
						if(agent.getId().equals(entry.person.getId()))
						{
							//create new request
							
							MobsimPassengerAgent passenger = (MobsimPassengerAgent)agent;
							AbstractRequest request = createAbstractRequest(passenger, passenger.getCurrentLinkId(), 
									passenger.getDestinationLinkId(), agent.getActivityEndTime(), time);
							
							//add this request into unplannedRequest
							unplannedRequests.add(request);
						}
					}
				}
			}
		}
		else
		{
			//create setPopulationAgentSource and from Qsim call this method
			//for this engine
			
			//forcast future requests, 
			//and then submit all unplanned and future request to optimizer
			
			//get requests in next timeStep
			//while(sortedSubmissionTimeQueue.peek().submissionTime < time + this.timeStep)
			//{
				
			//}
			//this.requestCreator.
			
			for(AbstractRequest request: unplannedRequests)
			{
				this.optimizer.requestSubmitted(request);
			}
		}
		
	}

	@Override
	public void onPrepareSim()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterSim()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInternalInterface(InternalInterface internalInterface)
	{
		this.internalInterface = internalInterface;
		
	}
	
	

}
