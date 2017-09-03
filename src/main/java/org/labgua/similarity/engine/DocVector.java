package org.labgua.similarity.engine;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealVectorFormat;

/**
 * This class represent a vector of the term-frequency of a document
 * @author Mubin
 */
public class DocVector {

   public Map<String,Integer> terms;
   public OpenMapRealVector vector;
   
   /**
    * Create a document vector instance
    * @param terms map of all terms used in directory
    */
   public DocVector(Map<String,Integer> terms) {
       this.terms = terms;
       this.vector = new OpenMapRealVector(terms.size());        
   }

   /**
    * Set a term with its frequency
    * @param term
    * @param freq
    */
   public void setEntry(String term, int freq) {
       if (terms.containsKey(term)) {
           int pos = terms.get(term);
           vector.setEntry(pos, (double) freq);
       }
   }

   public void normalize() {
       double sum = vector.getL1Norm();
       vector = (OpenMapRealVector) vector.mapDivide(sum);
   }

	/**
	 * Method to calculate cosine similarity between two document
	 * @param d1 DocVector of the document 1
	 * @param d2 DocVector of the document 2
	 * @return similarity, a double in range [0.0; 1.0]
	 */
   public static double calculate(DocVector d1,DocVector d2) {
       double cosinesimilarity;
       try {
           cosinesimilarity = ((d1.vector.dotProduct(d2.vector))
                   / (d1.vector.getNorm() * d2.vector.getNorm()));
       } catch (Exception e) {
           return 1.0;
       }
       
       Logger.getLogger("Logger").info("d1Vect_dot_d2vect : " + d1.vector.dotProduct(d2.vector) );
       Logger.getLogger("Logger").info("d1VectNorm : " + d1.vector.getNorm() );
       Logger.getLogger("Logger").info("d2VectNorm : " + d2.vector.getNorm() );
       
       Logger.getLogger("Logger").info("cosinesimilarity : " + cosinesimilarity);
       return cosinesimilarity;
   }
   
   
   @Override
   public String toString() {
       RealVectorFormat formatter = new RealVectorFormat();
       return formatter.format(vector);
   }
}
