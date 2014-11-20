package org.matsim.contrib.sarp.data;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.Route;
import org.matsim.contrib.sarp.enums.RequestType;
import org.matsim.core.population.routes.GenericRoute;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.utils.io.MatsimXmlWriter;
import org.matsim.core.utils.misc.Time;

/**
 * @author PTA
 */
	public class RequestWriter
	{

	private static final int MAX_WARN_UNKNOWN_ROUTE = 10;
	private int countWarnUnkownRoute = 0;

	private void writeHeaderAndStartElement(final BufferedWriter out) throws IOException {
		out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		out.write("<!DOCTYPE request generation SYSTEM \"" + MatsimXmlWriter.DEFAULT_DTD_LOCATION + "\">\n\n");
	}

	private void startPlans(final BufferedWriter out) throws IOException {
		out.write("<population");
		out.write(">\n\n");
	}

	private void writeRequest(AbstractRequest[] requests, final BufferedWriter out) throws IOException 
	{
		for(AbstractRequest req:requests)
		{
			this.writeSeparator(out);
			if (req.getType() == RequestType.PEOPLE_REQUEST)
			{
				writePeopleRequest((PeopleRequest)req, out);
			}
			else
			{
				writeParcelRequest((ParcelRequest)req, out);
				
			}
		}
	}
		

	private void writePeopleRequest(PeopleRequest req, BufferedWriter out) throws IOException
	{
		startPeopleRequest(req, out);
		startPlan(out);
		
		//FROM
		out.write("\t\t\t<act type=\"from\"");
		
		if (req.getFromLink().getId() != null) {
			out.write(" link=\"");
			out.write(req.getFromLink().getId().toString());
			out.write("\"");
		}
		if (req.getFromLink().getCoord() != null) {
			out.write(" x=\"");
			out.write(Double.toString(req.getFromLink().getCoord().getX()));
			out.write("\" y=\"");
			out.write(Double.toString(req.getFromLink().getCoord().getY()));
			out.write("\"");
		}
		if (req.getT0() != Time.UNDEFINED_TIME) {
			out.write(" t0=\"");
			out.write(Time.writeTime(req.getT0()));
			out.write("\"");
		}
		if (req.getT1() != Time.UNDEFINED_TIME) {
			out.write(" t1=\"");
			out.write(Time.writeTime(req.getT1()));
			out.write("\"");
		}
		out.write(" />\n");
		
		startLeg(out);
		endLeg(out);
		
		//To
		out.write("\t\t\t<act type=\"to\"");
		
		if (req.getToLink().getId() != null) {
			out.write(" link=\"");
			out.write(req.getToLink().getId().toString());
			out.write("\"");
		}
		if (req.getToLink().getCoord() != null) {
			out.write(" x=\"");
			out.write(Double.toString(req.getToLink().getCoord().getX()));
			out.write("\" y=\"");
			out.write(Double.toString(req.getToLink().getCoord().getY()));
			out.write("\"");
		}
		if (req.getL0() != Time.UNDEFINED_TIME) {
			out.write(" l0=\"");
			out.write(Time.writeTime(req.getL0()));
			out.write("\"");
		}
		if (req.getL1() != Time.UNDEFINED_TIME) {
			out.write(" l1=\"");
			out.write(Time.writeTime(req.getL1()));
			out.write("\"");
		}
		out.write(" />\n");

		endPlan(out);
		endPeopleRequest(out);
	}
	
	
	private void writeParcelRequest(ParcelRequest req, BufferedWriter out) throws IOException
	{
		startParcelRequest(req, out);
		startPlan(out);
		
		//FROM
		out.write("\t\t\t<act type=\"from\"");
		
		if (req.getFromLink().getId() != null) {
			out.write(" link=\"");
			out.write(req.getFromLink().getId().toString());
			out.write("\"");
		}
		if (req.getFromLink().getCoord() != null) {
			out.write(" x=\"");
			out.write(Double.toString(req.getFromLink().getCoord().getX()));
			out.write("\" y=\"");
			out.write(Double.toString(req.getFromLink().getCoord().getY()));
			out.write("\"");
		}
		if (req.getT0() != Time.UNDEFINED_TIME) {
			out.write(" t0=\"");
			out.write(Time.writeTime(req.getT0()));
			out.write("\"");
		}
		if (req.getT1() != Time.UNDEFINED_TIME) {
			out.write(" t1=\"");
			out.write(Time.writeTime(req.getT1()));
			out.write("\"");
		}
		out.write(" />\n");
		
		startLeg(out);
		endLeg(out);
		
		//To
		out.write("\t\t\t<act type=\"to\"");
		
		if (req.getToLink().getId() != null) {
			out.write(" link=\"");
			out.write(req.getToLink().getId().toString());
			out.write("\"");
		}
		if (req.getToLink().getCoord() != null) {
			out.write(" x=\"");
			out.write(Double.toString(req.getToLink().getCoord().getX()));
			out.write("\" y=\"");
			out.write(Double.toString(req.getToLink().getCoord().getY()));
			out.write("\"");
		}
		if (req.getL0() != Time.UNDEFINED_TIME) {
			out.write(" l0=\"");
			out.write(Time.writeTime(req.getL0()));
			out.write("\"");
		}
		if (req.getL1() != Time.UNDEFINED_TIME) {
			out.write(" l1=\"");
			out.write(Time.writeTime(req.getL1()));
			out.write("\"");
		}
		out.write(" />\n");

		endPlan(out);
		endParcelRequest(out);
	}

	private void endPlans(final BufferedWriter out) throws IOException {
		out.write("</population>\n");
	}

	private void startPeopleRequest(final PeopleRequest p, final BufferedWriter out) throws IOException {
		out.write("\t<person id=\"");
		out.write(p.getId().toString());
		out.write("\" max_nb_stops=");
		out.write("\"" + p.getMaxNbStops() + "\"");
		out.write("\" max_travel_distance=");
		out.write("\"" + p.getMaxTravelDistance() + "\"");
				
		out.write(">\n");
	}

	private void endPeopleRequest(final BufferedWriter out) throws IOException {
		out.write("\t</person>\n\n");
	}

	private void startParcelRequest(final ParcelRequest p, final BufferedWriter out) throws IOException {
		out.write("\t<parcel id=\"");
		out.write(p.getId().toString());
		out.write("\"");
		out.write(">\n");
	}

	private void endParcelRequest(final BufferedWriter out) throws IOException {
		out.write("\t</parcel>\n\n");
	}

	private void startPlan(final BufferedWriter out) throws IOException {
		out.write("\t\t<plan");
		out.write(" selected=\"yes\"");
		out.write(">\n");
	}

	private void endPlan(final BufferedWriter out) throws IOException {
		out.write("\t\t</plan>\n\n");
	}


	private void startLeg(final BufferedWriter out) throws IOException {
		out.write("\t\t\t<leg mode=\"");
		out.write("taxi");
		out.write("\"");
		out.write(">\n");
	}

	private void endLeg(final BufferedWriter out) throws IOException {
		out.write("\t\t\t</leg>\n");
	}

	private void startGenericRoute(final GenericRoute route, final BufferedWriter out) throws IOException {
		out.write("\t\t\t\t<route ");
		out.write("type=\"");
		out.write(route.getRouteType());
		out.write("\"");
		out.write(" start_link=\"");
		out.write(route.getStartLinkId().toString());
		out.write("\"");
		out.write(" end_link=\"");
		out.write(route.getEndLinkId().toString());
		out.write("\"");
		out.write(" trav_time=\"");
		out.write(Time.writeTime(route.getTravelTime()));
		out.write("\"");
		out.write(" distance=\"");
		out.write(Double.toString(route.getDistance()));
		out.write("\"");
		out.write(">");
		String rd = route.getRouteDescription();
		if (rd != null) {
			out.write(rd);
		}
	}

	private void startNetworkRoute(final NetworkRoute route, final BufferedWriter out) throws IOException {
		out.write("\t\t\t\t<route ");
		if ( route.getVehicleId()!=null ) {
			out.write("vehicleRefId=\""+ route.getVehicleId() +"\" ") ;
		}
		out.write("type=\"links\"");
		out.write(" trav_time=\"");
		out.write(Time.writeTime(route.getTravelTime()));
		out.write("\"");
		out.write(" distance=\"");
		out.write(Double.toString(route.getDistance()));
		out.write("\"");
		out.write(">");
		out.write(route.getStartLinkId().toString());
		for (Id<Link> linkId : route.getLinkIds()) {
			out.write(" ");
			out.write(linkId.toString());
		}
		// If the start links equals the end link additionally check if its is a round trip. 
		if (!route.getEndLinkId().equals(route.getStartLinkId()) || route.getLinkIds().size() > 0) {
			out.write(" ");
			out.write(route.getEndLinkId().toString());
		}
	}

	private void endRoute(final BufferedWriter out) throws IOException {
		out.write("</route>\n");
	}

	private void writeSeparator(final BufferedWriter out) throws IOException {
		out.write("<!-- ====================================================================== -->\n\n");
	}

	public void write(AbstractRequest[] requests, String filename) throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		writeHeaderAndStartElement(out);
		
		startPlans(out);
		writeRequest(requests, out);
		endPlans(out);
	}
	
	
}
