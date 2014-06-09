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
		
		return null;
	}

}
