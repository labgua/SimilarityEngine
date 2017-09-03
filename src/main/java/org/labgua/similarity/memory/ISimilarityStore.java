package org.labgua.similarity.memory;

/**
 * This interface define the operation of the structure that
 * must store the result of the similarity
 * @author sergio
 *
 */
public interface ISimilarityStore {
	/**
	 * Add a result of similarity between two entity
	 * @param idSource identifier of the first entity
	 * @param idDestination identifier of the second entity
	 * @param rank the similarity, a double in [0.0; 1.0]
	 */
	public void add(Integer idSource, Integer idDestination, Double rank);
	
	/**
	 * Remove all the results of an entity
	 * @param idSrc identifier of the entity
	 */
	public void remove(Integer idSrc);
	
	/**
	 * Delete all results
	 */
	public void delete();
}
