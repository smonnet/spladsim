/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.pastPastry;

/**
 *
 * @author Veronique Simon <veronique.simon@lip6.fr>
 */
  public class Blocmapentry {
        public  long birth;
        public  int nbCopies;
        public Blocmapentry(long birth, int nbCopies){
            this.birth = birth;
            this.nbCopies = nbCopies;
        }
        public void reset(){
            nbCopies = 0;
        }
    }
