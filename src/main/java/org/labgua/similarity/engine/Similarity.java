package org.labgua.similarity.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.labgua.similarity.entity.IDocumentable;
import org.labgua.similarity.memory.ISimilarityManager;
import org.labgua.similarity.memory.RAMSimilarityStore;
import org.labgua.similarity.memory.SQLITESimilarityStore;

/**
 * This class manage the process of the similarity.
 * In particular wraps all the components allows you to set the type 
 * of Directory of Lucene, update and save the results of the process
 * @author sergio
 *
 */
public class Similarity {

	private ContentDistance cd;
	private Directory directory;
	private IndexWriterConfig conf;
	
	private Map<Integer,IDocumentable> toUpdate;
	private Map<Integer,IDocumentable> toDelete;
	
	private ISimilarityManager simData;
	
	/**
	 * Creates an instance using RAM space, using a default WhitespaceAnalyzer
	 * @throws IOException
	 */
	public Similarity() throws IOException{
		Directory directory = new RAMDirectory();
		ISimilarityManager simData = new RAMSimilarityStore();
		
		Analyzer analyzer = new WhitespaceAnalyzer();
		init(directory, simData, new IndexWriterConfig(analyzer));
		
	}
	
	/**
	 * Creates an instance using a FileSystem space, using a default WhitespaceAnalyzer
	 * @param path path where instanziate the Lucene Directory
	 * @param indexname folder in path where instanziate the Lucene Directory
	 * @throws IOException
	 * @throws SQLException
	 */
	public Similarity(Path path, String indexname) throws IOException, SQLException{
		
		Path pathIndex = path.getFileSystem().getPath(path.toString(), indexname);
		Directory directory = new SimpleFSDirectory(pathIndex);
		ISimilarityManager simData = new SQLITESimilarityStore("result_" + indexname + ".db");
		
		Analyzer analyzer = new WhitespaceAnalyzer();

		init(directory, simData, new IndexWriterConfig(analyzer));
		
	}
	
	/**
	 * Creates an instance using a FileSystem space, using a custom analyzer
	 * @param path path where instanziate the Lucene Directory
	 * @param analyzer custom analyzer for the indexing
	 * @param indexname folder in path where instanziate the Lucene Directory
	 * @throws IOException
	 * @throws SQLException
	 */
	public Similarity(Path path, Analyzer analyzer, String indexname) throws IOException, SQLException{
		Path pathIndex = path.getFileSystem().getPath(path.toString(), indexname);
		Directory directory = new SimpleFSDirectory(pathIndex);
		ISimilarityManager simData = new SQLITESimilarityStore("result_" + indexname + ".db");
		
		init(directory, simData, new IndexWriterConfig(analyzer));
	}
	
	/**
	 * Creates an instance, using a custom Directory, IndexWriterConfig to indexing and a custom datastruct for result
	 * @param dir Directory for Lucene
	 * @param simData datastruct to store/manage the results
	 * @param conf configuration to write in directory
	 * @throws IOException
	 */
	public Similarity(Directory dir, ISimilarityManager simData, IndexWriterConfig conf ) throws IOException{
		init(dir, simData, conf);
	}
	
	
	
	/**
	 * init this instance
	 * @param dir
	 * @param simData
	 * @param conf
	 * @throws IOException
	 */
	private void init(Directory dir, ISimilarityManager simData, IndexWriterConfig conf) throws IOException{
		this.directory = dir;
		this.simData = simData;
		this.conf = conf;
		this.cd = new ContentDistance(dir, conf);
		
		this.toUpdate = new HashMap<>();
		this.toDelete = new HashMap<>();
	}
	
	
	/**
	 * Deletes all documents from the directory and from the results
	 * @throws IOException
	 */
	public void delete() throws IOException{
		this.cd.delete();
		this.simData.delete();
	}
	
	/**
	 * Indexes a list of entity
	 * @param entities
	 * @throws IOException
	 */
	public void add( List<? extends IDocumentable> entities ) throws IOException{
		this.cd.addEntities(entities);
		for( IDocumentable d : entities ){
			this.toUpdate.put(d.getId(), d);
		}
	}
	
	/**
	 * Indexes an entity in the directory
	 * @param entity
	 * @throws IOException
	 */
	public void add( IDocumentable entity ) throws IOException{
		this.cd.addEntity(entity);
		this.toUpdate.put(entity.getId(), entity);
	}
	
	/**
	 * Removes an entity
	 * The entity must have an id
	 * @param entity
	 */
	public void remove( IDocumentable entity ){
		
		if( entity.getId() == null ) throw new RuntimeException("The entity must have an id");
		
		// case1 - doc not updated yet
		if( this.toUpdate.containsKey( entity.getId() ) ){
			this.toUpdate.remove(entity).getId();
			return;
		}
		
		//case2 - in directory, maybe...
		this.toDelete.put(entity.getId(), entity);
	}
	
	/**
	 * Init the calculation of the similarity.
	 * This method executes all the process for all documents
	 * Use updateSimilarity for the next update
	 * @throws IOException
	 */
	public void initSimilarity() throws IOException{
		
		///first, delete al document to delete!
		for( Entry<Integer, IDocumentable> dd : this.toDelete.entrySet() ){	
			this.cd.removeEntity(dd.getValue());
		}
		this.toDelete.clear();
		
		
		DocVector[] docVector = cd.getDocumentVectors();
		
		
		for( int i = 0; i < docVector.length; i++  ){
			
			Document di = this.cd.getIndexSearcher().doc( i );
			Integer idSource = Integer.valueOf( di.get(IDocumentable.FIELD_ID) );
			
			//this.simResult.setEntry(idSource, idSource, 1.0);
			this.simData.add(idSource, idSource, 1.0);
			
			for( int j = i+1; j < docVector.length; j++ ){
				
				Document dj = this.cd.getIndexSearcher().doc( j );
				Integer idDestination = Integer.valueOf( dj.get(IDocumentable.FIELD_ID) );
				
				
				double sim = DocVector.calculate(docVector[i], docVector[j]);
				
				this.simData.add(idSource, idDestination, sim);
				this.simData.add(idDestination, idSource, sim);
				
			}
				
		}
		
		this.toUpdate.clear();
		
	}
	
	/**
	 * Updates the similarity of last changes (like insert or remove of entity)
	 * It updates only the similarity of the document in question
	 * 
	 * es. 
	 * if E:{1,2,3,4} if the space of the Entity 
	 * and there is an insert with E = E U {5}
	 * this method calculates and store only the similarity S1:{(5-1), (5-2), (5-3), (5-4)}
	 * 
	 * @throws IOException
	 */
	public void updateSimilarity() throws IOException{
		
		DocVector[] docVector = cd.getDocumentVectors();
		
		///first, delete al document to delete + update matrix
		for( Entry<Integer, IDocumentable> dd : this.toDelete.entrySet() ){	
			this.simData.remove(dd.getKey());
			this.cd.removeEntity(dd.getValue());
		}
		this.toDelete.clear();
		
		
		for( Entry<Integer, IDocumentable> dd : this.toUpdate.entrySet() ){		

			try {
				
				//1) prendere l'oggetto documento in relazione con dd
				Logger.getLogger("Logger").info("dd key : " + dd.getKey());
				int j = this.cd.getIdDoc( dd.getKey() );
				Integer idDestination = dd.getKey();
				
				
				
				//2) metterlo in relazione con tutti i documenti gia inseriti
				//   --> calcolare la similarità e quindi aggiornare la matrice				
				for( int i = 0; i < docVector.length; i++  ){
					Document di = this.cd.getIndexSearcher().doc( i );
					Integer idSource = Integer.valueOf( di.get(IDocumentable.FIELD_ID) );
					
					double sim = DocVector.calculate(docVector[i], docVector[j]);
					
					this.simData.add(idSource, idDestination, sim);
					this.simData.add(idDestination, idSource, sim);
				}
				
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		this.toUpdate.clear();
	}
	

	
	/**
	 * Returns the similarity between two entity
	 * @param idSource the id of the first entity
	 * @param idDestination the id of the second entity
	 * @return double, in range [0.0; 1.0]
	 */
	public double similarity(int idSource, int idDestination){
		return this.simData.getSimilarity(idSource, idDestination);
	}
	
	/**
	 * Returns the result manager of the similarity
	 * @return the result manager of the similarity
	 */
	public ISimilarityManager getData(){
		return this.simData;
	}
	
	/**
	 * Closes all streams opened
	 * @throws IOException
	 */
	public void close() throws IOException{
		this.cd.close();
	}
	
}
