/**
 * 
 */
package spladsim;

import java.math.BigInteger;

/**
 * @author smonnet
 *
 */
public class LessChargedChoice implements PlacementPolicy {

	@Override
	public BigInteger getStorer(BigInteger dataUID) {
		SpladNode[] selectionRange = GlobalKnowledge.ring.getSelectionRange(GlobalKnowledge.config.selectionRange, 
				GlobalKnowledge.nodes.get(GlobalKnowledge.ring.getRoot(dataUID)));
		int minInd;
		int ind=0;
		// find the first that do not already store the data
		while(selectionRange[ind].dataStore.containsKey(dataUID)) { // selection range is greater that the replication factor.
			ind++;
		}
		minInd=ind;  // init to the first that does not already store the data
		for(int i = ind+1; i<GlobalKnowledge.config.selectionRange; i++) {
			if( (!selectionRange[i].dataStore.containsKey(dataUID)) && (selectionRange[i].dataStore.size()<selectionRange[minInd].dataStore.size())) {
				minInd = i;
			}
		}
		return selectionRange[minInd].uid;
	}
}
