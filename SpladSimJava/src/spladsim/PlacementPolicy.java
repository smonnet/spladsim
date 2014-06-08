/**
 * 
 */
package spladsim;

import java.math.BigInteger;

/**
 * @author smonnet
 *
 */
public interface PlacementPolicy {
	public BigInteger getStorer(BigInteger dataUID);
}
