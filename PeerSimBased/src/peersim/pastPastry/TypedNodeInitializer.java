package peersim.pastPastry;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.transport.Transport;

public class TypedNodeInitializer implements peersim.core.Control {

	//private int[] typesmean;
	private static final String PAR_PAST = "past";
	public int pastryid;
	public int pastid;

	public TypedNodeInitializer(String prefix) {
		pastid = Configuration.getPid(prefix + "." + PAR_PAST);
	}

	/**
	 * initialize le type des noeuds du reseau
	 */
	public boolean execute() {
		for (int i = 0; i < Network.size(); ++i) {
			PastFamilyProtocol past = (PastFamilyProtocol) Network.get(i)
					.getProtocol(pastid);
			past.routeLayer = (MSPastryProtocol) Network.get(i).getProtocol(
					past.mspastryid);
			past.bandwidth = (Transport) Network.get(i).getProtocol(
					PastFamilyProtocol.bdwid);
		}
		return false;
	}
}
