package org.labgua.similarity.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.labgua.similarity.entity.IDocumentable;

/**
 * This class contains all the logic for the indexing of the document and the calculating
 * of the similarity of these.
 * From a defined Directory and configuration, it inserts, remove documents and
 * creates the vectors of TermByDocument with relative frequencies.
 * @author sergio
 *
 */
public class ContentDistance {
	
	private Directory dir;
	private IndexReader indexReader;
	private IndexWriter indexWriter;
	
	private IndexWriterConfig config;
	
    private Map<String,Integer> allTerms;
	

    /**
     * Define an instance with a Lucene directory and a configuration (so with an already defined analyzer).
     * @param dir Directory of Lucene
     * @param conf A configuration for this Directory
     * @throws IOException inherited by the constructor of IndexWriter
     */
    public ContentDistance(Directory dir, IndexWriterConfig conf) throws IOException{
    	
    	this.dir = dir;
    	this.indexWriter = new IndexWriter(dir, conf);
    	this.config = conf;
    	
    	this.allTerms = new HashMap<>();
    }
    
    
    //1 indicizza le entity nella directory
    /**
     * Indexes the entity in the directory.
     * This is the first step for the calculation of similarity
     * @param entities Collection of Entity, defined as IDocumentable
     * @throws IOException inherited by IndexWriter.addDocuments()
     */
    public void addEntities( Collection<? extends IDocumentable> entities ) throws IOException{
    	
    	ArrayList<Document> listDocble = new ArrayList<>();
    	for( IDocumentable d : entities ){
    		listDocble.add(d.toDocument());
    	}
    	
    	indexWriter.addDocuments(listDocble);
    	indexWriter.commit();
    	
    }
    
    //1b indicizza una sola entity..
    /**
     * Indexes only one entity in the directory
     * This is the first step for the calculation of similarity
     * @param entity an object that implements IDocumentable
     * @throws IOException inherited by IndexWriter.addDocument()
     */
    public void addEntity(IDocumentable entity) throws IOException{
    	indexWriter.addDocument( entity.toDocument() );
    	indexWriter.commit();
    }
    
    /**
     * Remove an entity from the directory
     * @param entity the object to remove (it must have setted the id!)
     * @throws IOException inherited by IndexWriter.deleteDocuments()
     */
    public void removeEntity(IDocumentable entity) throws IOException{
    	if( entity.getId() == null ) throw new RuntimeException("Can not remove ann entity with undefined ID");
    	indexWriter.deleteDocuments(new Term(IDocumentable.FIELD_ID, String.valueOf(entity.getId() ) ) );
    }
    
    
    /**
     * Remove all documents from the Directory
     * @throws IOException inherited by IndexWriter.deleteAll()
     */
    public void delete() throws IOException{
    	indexWriter.deleteAll();
    	indexWriter.commit();
    }
    
    //2 restituisci la matrice termine documento (vettore di DocVector)
    /**
     * Returns the matrix term-by-doc, defined as an array of DocVector.
     * This is the second step for the calculation of similarity
     * @return matrix term-by-doc
     * @throws IOException
     */
    public DocVector[] getDocumentVectors() throws IOException{
    	
    	indexReader = DirectoryReader.open(dir);
        
        //Map<String,Integer> allterms = new HashMap<>();
        Integer totalNoOfDocumentInIndex = indexReader.maxDoc();
        DocVector[] docVector = new DocVector[totalNoOfDocumentInIndex];

        
        int pos = 0;
        for (int docId = 0; docId < totalNoOfDocumentInIndex; docId++) {
            Terms vector = indexReader.getTermVector(docId, IDocumentable.FIELD_CONTENT);
            if( vector != null ){ // MIO : check sul vector...
	            TermsEnum termsEnum = null;
	            termsEnum = vector.iterator();
	            BytesRef text = null;
	            while ((text = termsEnum.next()) != null) {
	                String term = text.utf8ToString();
	                allTerms.put(term, pos++);
	            }
            }
        }       

        //Update postition
        pos = 0;
        for(Entry<String,Integer> s : allTerms.entrySet())
        {        
        	Logger.getLogger("Logger").info("key : " + s.getKey());
            s.setValue(pos++);
        }
        
        for (int docId = 0; docId < totalNoOfDocumentInIndex; docId++) {
            Terms vector = indexReader.getTermVector(docId, IDocumentable.FIELD_CONTENT);
            if( vector != null ){
            	TermsEnum termsEnum = null;
	            //termsEnum = vector.iterator(termsEnum);
	            termsEnum = vector.iterator();
	            BytesRef text = null;            
	            docVector[docId] = new DocVector(allTerms);            
	            while ((text = termsEnum.next()) != null) {
	                String term = text.utf8ToString();
	                int freq = (int) termsEnum.totalTermFreq();
	                docVector[docId].setEntry(term, freq);
	            }
	            docVector[docId].normalize();
	            
            }
        }
        
        return docVector;
    	
    }
    
    //3 ottieni un IndexSearher
    /**
     * Return an instance of IndexSeracher to interact with the Directory
     * @return instance of IndexSearch
     */
    public IndexSearcher getIndexSearcher(){
    	return new IndexSearcher(indexReader);
    }
    
    
    /**
     * Return the DocId used by Lucene of an entity with an id idEntity
     * @param idEntity id of the entity
     * @return DocId associated with the entity 
     * @throws ParseException
     * @throws IOException
     */
    public int getIdDoc( int idEntity ) throws ParseException, IOException{
    	
    	IndexSearcher indexSearcher = getIndexSearcher();
    	
    	//Query query = new TermQuery(new Term(IDocumentable.FIELD_ID, String.valueOf(idEntity) ));
    	Query query = NumericRangeQuery.newIntRange(IDocumentable.FIELD_ID, idEntity, idEntity, true, true);
        TopDocs td = indexSearcher.search(query, 1);
		
        Logger.getLogger("Logger").info("results : " + td.scoreDocs.length);
		Logger.getLogger("Logger").info("id string :: "+String.valueOf(idEntity));
		
		
        return td.scoreDocs[0].doc;
    }
  
    /**
     * Return the analyzer in use
     * @return analyzer in use
     */
    public Analyzer getAnalyzer(){
    	return this.config.getAnalyzer();
    }
    
    /**
     * Close all the stream in use
     * @throws IOException
     */
    public void close() throws IOException{
    	this.indexWriter.close();
    	this.indexReader.close();
    	this.dir.close();
    }

}




