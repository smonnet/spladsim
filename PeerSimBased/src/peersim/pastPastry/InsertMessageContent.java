/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.pastPastry;

/**
 *
 * @author Veronique Simon <veronique.simon@lip6.fr>
 */
public class InsertMessageContent {
    public ReplicaSetLease bloclease;
    public Bloc bloc;
    
    public InsertMessageContent(ReplicaSetLease lease, Bloc bloc){
        this.bloc = bloc;
        this.bloclease = lease;
    }
}
