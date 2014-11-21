package org.matsim.contrib.sarp.datagenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.NetworkFactory;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.api.experimental.facilities.ActivityFacilities;
import org.matsim.core.api.experimental.facilities.ActivityFacilitiesFactory;
import org.matsim.core.api.experimental.facilities.ActivityFacility;
import org.matsim.core.population.PersonImpl;
import org.matsim.core.utils.misc.Time;
import org.matsim.utils.objectattributes.ObjectAttributes;


public class GeneratePopulation
{
	private Scenario scenario;
	
	private ObjectAttributes personHomeAndWorkLocations = new ObjectAttributes();

	
	public GeneratePopulation(Scenario scenario)
	{
		this.scenario = scenario;
		
	}
	
	public void generatePopulation()
	{
		Population population = this.scenario.getPopulation();
		PopulationFactory populationFactory = population.getFactory();
		
		//index
		int linkSize = this.scenario.getNetwork().getLinks().size();
		NetworkFactory networkFactory = this.scenario.getNetwork().getFactory();
		Object[] arrayLinks = this.scenario.getNetwork().getLinks().keySet().toArray();
		
		ActivityFacilities activityFacilities = this.scenario.getActivityFacilities();
		ActivityFacilitiesFactory activityFactory = activityFacilities.getFactory();
		
		
		int REQUEST_NUMBER = 100;
		

		for(int i = 0; i < REQUEST_NUMBER; i++)
		{
			Person person = populationFactory.createPerson(Id.createPersonId(i));
			Plan plan = populationFactory.createPlan();
			
			person.addPlan(plan);
			population.addPerson(person);
			((PersonImpl)person).setSelectedPlan(plan);
			
			
			
			//create randomly FromLink and ToLink
			int fromLinkIdx = (new Random()).nextInt(linkSize);
			int toLinkIdx = (new Random()).nextInt(linkSize);
			
			@SuppressWarnings("unchecked")
			Id<Link> fromLinkId = (Id<Link>)arrayLinks[fromLinkIdx];
			Id<Link> toLinkId = (Id<Link>)arrayLinks[toLinkIdx];

			
			Activity fromActivity = populationFactory.createActivityFromLinkId("from", fromLinkId);
			
			//generate start time and end time
			int hh= (new Random()).nextInt(24);
			int mm = (new Random()).nextInt(60);
			double seconds = (hh * 60 + mm)*60;
			
			fromActivity.setStartTime(seconds);
			
			//assume that time window length = 10 minutes
			fromActivity.setEndTime(seconds + 600);
			
			plan.addActivity(fromActivity);

			Activity toActivity = populationFactory.createActivityFromLinkId("to", toLinkId);			

			plan.addActivity(toActivity);
			
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
