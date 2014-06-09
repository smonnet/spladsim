/**
 * 
 */
package spladsim;

import java.math.BigInteger;

import org.simgrid.msg.Msg;

/**
 * @author smonnet
 *
 */
public class RandomChoice implements PlacementPolicy {

	@Override
	public BigInteger getStorer(BigInteger dataUID) {
		SpladNode[] selectionRange = GlobalKnowledge.ring.getSelectionRange(GlobalKnowledge.config.selectionRange, 
				GlobalKnowledge.nodes.get(GlobalKnowledge.ring.getRoot(dataUID)));
		int ind = GlobalKnowledge.rand.nextInt(GlobalKnowledge.config.selectionRange);
		while(selectionRange[ind].dataStore.containsKey(dataUID)) {
			ind = GlobalKnowledge.rand.nextInt(GlobalKnowledge.config.selectionRange);
		}
		Msg.info(""+selectionRange[ind].uid);
		return selectionRange[ind].uid;
	}
}
