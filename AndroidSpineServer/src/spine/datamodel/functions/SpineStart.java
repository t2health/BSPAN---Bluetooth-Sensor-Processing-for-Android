/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute
modify it under the terms of the sub-license (below).

*****************************************************************/

/*****************************************************************
BSPAN - BlueTooth Sensor Processing for Android is a framework 
that extends the SPINE framework to work on Android and the 
Android Bluetooth communication services.

Copyright (C) 2011 The National Center for Telehealth and 
Technology

Eclipse Public License 1.0 (EPL-1.0)

This library is free software; you can redistribute it and/or
modify it under the terms of the Eclipse Public License as
published by the Free Software Foundation, version 1.0 of the 
License.

The Eclipse Public License is a reciprocal license, under 
Section 3. REQUIREMENTS iv) states that source code for the 
Program is available from such Contributor, and informs licensees 
how to obtain it in a reasonable manner on or through a medium 
customarily used for software exchange.

Post your updates and modifications to our GitHub or email to 
t2@tee2.org.

This library is distributed WITHOUT ANY WARRANTY; without 
the implied warranty of MERCHANTABILITY or FITNESS FOR A 
PARTICULAR PURPOSE.  See the Eclipse Public License 1.0 (EPL-1.0)
for more details.
 
You should have received a copy of the Eclipse Public License
along with this library; if not, 
visit http://www.opensource.org/licenses/EPL-1.0

*****************************************************************/

package spine.datamodel.functions;

public class SpineStart implements SpineObject {

	private static final long serialVersionUID = 1L;

	private int activeNodesCount = -1;
	private boolean radioAlwaysOn;
	private boolean enableTDMA;

	/**
	 * Sets the the size of the discovered WSN. Note that this info is actually
	 * used by the node only if it's requested to operate in TDMA mode.
	 * 
	 * @param activeNodesCount
	 *            the size of the discovered WSN
	 */
	public void setActiveNodesCount(int activeNodesCount) {
		this.activeNodesCount = activeNodesCount;
	}

	public int getActiveNodesCount() {
		return this.activeNodesCount;
	}

	/**
	 * Sets the control flag for enabling the radio low-power mode.
	 * 
	 * @param radioAlwaysOn
	 *            'true' for keeping the radio always turned on; 'false' to let
	 *            the node optimizing the radio consumption by turning the radio
	 *            off when it's not needed.
	 */
	public void setRadioAlwaysOn(boolean radioAlwaysOn) {
		this.radioAlwaysOn = radioAlwaysOn;
	}

	public boolean getRadioAlwaysOn() {
		return this.radioAlwaysOn;
	}

	/**
	 * Sets the control flag for enabling the on-node TDMA radio access scheme
	 * 
	 * @param enableTDMA
	 *            'true' if the radio access scheme must be TDMA; 'false' to
	 *            rely on the default one.
	 */
	public void setEnableTDMA(boolean enableTDMA) {
		this.enableTDMA = enableTDMA;
	}

	public boolean getEnableTDMA() {
		return this.enableTDMA;
	}
}
