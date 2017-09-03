package org.labgua.similarity.memory;

import java.util.Map;

/**
 * This interface define the operation of the structure that
 * must interact with the result of the similarity
 * @author sergio
 *
 */
public interface ISimilarityManager extends ISimilarityStore {

	/**
	 * Return all the ids of the similar entities of an entity
	 * @param id identifier of the entity
	 * @return Map of id and related similarity
	 */
	public Map<Integer,Double> getSimilar( Integer id );
	
	/**
	 * Return the similarity of two entity
	 * @param idSource the id of the first entity
	 * @param idDestination the id of the second entity
	 * @return double, in range [0.0; 1.0]
	 */
	public Double getSimilarity( Integer idSource, Integer idDestination );
	
}
