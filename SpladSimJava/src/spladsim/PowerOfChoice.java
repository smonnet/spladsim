/**
 * 
 */
package spladsim;

import java.math.BigInteger;

/**
 * @author smonnet
 *
 */
public class PowerOfChoice implements PlacementPolicy {

	@Override
	public BigInteger getStorer(BigInteger dataUID) {
		SpladNode[] selectionRange = GlobalKnowledge.ring.getSelectionRange(GlobalKnowledge.config.selectionRange, 
				GlobalKnowledge.nodes.get(GlobalKnowledge.ring.getRoot(dataUID)));
		// first find randomly 2 that do not already store the data
		int a = GlobalKnowledge.rand.nextInt(GlobalKnowledge.config.selectionRange);
		while(selectionRange[a].dataStore.containsKey(dataUID)) {
			a = GlobalKnowledge.rand.nextInt(GlobalKnowledge.config.selectionRange);
		}
		int b = GlobalKnowledge.rand.nextInt(GlobalKnowledge.config.selectionRange);
		while(selectionRange[b].dataStore.containsKey(dataUID)) {
			b = GlobalKnowledge.rand.nextInt(GlobalKnowledge.config.selectionRange);
		}
		// then choose the less charged
		if(selectionRange[a].dataStore.size()<selectionRange[b].dataStore.size()) {
			return selectionRange[a].uid;
		} else {
			return selectionRange[b].uid;
		}
	}

}
