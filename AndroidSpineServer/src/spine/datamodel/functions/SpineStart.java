/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
 
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

/**
 * This class represents a SPINE Start command.
 * It contains the logic for encoding a start command from an high level Start object.
 * 
 * Note that this class is only used internally at the framework.   
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

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
