/**
 * Project: taxi
 * Package: org.matsim.contrib.sarp.passenger
 * Author: pta
 * Date: Nov 29, 2014
 */
package org.matsim.contrib.sarp.passenger;

import org.matsim.contrib.dvrp.passenger.PassengerRequestCreator;
import org.matsim.contrib.sarp.data.AbstractRequest;

/**
 *
 *TODO
 */
public interface SSARPassengerRequestCreator
	extends PassengerRequestCreator
{
	AbstractRequest forcastFutureRequests();
}
