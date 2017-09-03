package org.labgua.similarity.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is an implementation of the manager of results in RAM
 * @author sergio
 *
 */
public class RAMSimilarityStore implements ISimilarityManager {

	protected static class Position{
		protected Integer source;
		protected Integer destination;
		protected Double rank;
		
		public Position(Integer source, Integer destination, Double rank) {
			this.source = source;
			this.destination = destination;
			this.rank = rank;
		}
		
	}
	
	private Map<Integer,Map<Integer,Position>> data;
	
	public RAMSimilarityStore() {
		this.data = new HashMap<>();
	}
	
	
	@Override
	public void add(Integer idSource, Integer idDestination, Double rank) {
		if( !this.data.containsKey(idSource) )
			this.data.put(idSource, new HashMap<Integer,Position>());
		
		if( !this.data.containsKey(idDestination) )
			this.data.put(idDestination, new HashMap<Integer,Position>());
		
		Position newPos = new Position(idSource, idDestination, rank);
		
		this.data.get(idSource).put(idDestination, newPos);
		this.data.get(idDestination).put(idSource, newPos);
		
	}

	@Override
	public void remove(Integer idSrc) {
		if( this.data.containsKey(idSrc) )
			this.data.remove(idSrc);
		
		for (Integer i : this.data.keySet()) {
			Map<Integer,Position> currLine = this.data.get(i);
			if( currLine.containsKey(idSrc) )
				currLine.remove(idSrc);
		}
		
	}
	
	@Override
	public void delete() {
		this.data.clear();
	}
	

	@Override
	public Map<Integer,Double> getSimilar(Integer id) {
		
		Map<Integer,Double> outMap = new HashMap<>();
		
		
		if( this.data.containsKey(id) ){
			Map<Integer, Position> idLine = this.data.get(id);
			for( Integer i : this.data.get(id).keySet() ){
				Position p = idLine.get(i);
				outMap.put(p.destination, p.rank);
			}
		}
		
		
		Set<Integer> remainingKeys = outMap.keySet();
		remainingKeys.remove(id);	
		for (Integer i : remainingKeys) {
			Map<Integer,Position> currLine = this.data.get(i);
			if( currLine.containsKey(id) ){
				Position p = currLine.get(id);
				outMap.put(p.source, p.rank);
			}
		}
		
		return outMap;
		
	}

	@Override
	public Double getSimilarity(Integer idSource, Integer idDestination) {
		return this.data.get(idSource).get(idDestination).rank;
	}


	
	@Override
	public String toString() {
		String outString = "RAMSimilarityStore\n";
		for (Integer firstIndex : this.data.keySet()) {
			Map<Integer,Position> currLine = this.data.get(firstIndex);
			for( Integer secondIndex : currLine.keySet() ){
				Position p = currLine.get(secondIndex);
				outString += p.source + " - " + p.destination + " => " + p.rank + "\n";
			}
			outString += "\n\n";
		}
		return outString;
	}

}
