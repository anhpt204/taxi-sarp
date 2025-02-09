/* *********************************************************************** *
 * project: org.matsim.*
 * OTFFileWriterFactory
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.vis.otfvis;

import org.matsim.api.core.v01.Scenario;
import org.matsim.vis.snapshotwriters.SnapshotWriter;
import org.matsim.vis.snapshotwriters.SnapshotWriterFactory;

public class OTFFileWriterFactory implements SnapshotWriterFactory {

	@Override
	public SnapshotWriter createSnapshotWriter(String filename, Scenario scenario) {
		OTFFileWriter writer = new OTFFileWriter(scenario, filename);
		return writer;
	}

	@Override
	public String getPreferredBaseFilename() {
		return "otfvis.mvi";
	}

}
