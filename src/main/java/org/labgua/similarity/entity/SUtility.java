package org.labgua.similarity.entity;

import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.IndexOptions;

/**
 * This class is an utility for the engine.
 * It expose statics methods used in the many phases of the process
 * @author sergio
 *
 */
public class SUtility {

	/**
	 * Create a document with an id and a content
	 * @param id identifier of the entity
	 * @param content list of fields of the entity
	 * @return
	 */
	public static Document createDoc(int id, List<String> content){

		String concatContent = "";
		for( String value : content ){
			concatContent += value + " ";
		}
		return createDoc(id, concatContent);
		
	}
	
	/**
	 * Create a document with an id and a content
	 * @param id identifier of the entity
	 * @param content stringifyed content of the entity
	 * @return
	 */
	public static Document createDoc(int id, String content){
		
		Document doc = new Document();
		
		FieldType fieldType = new FieldType();

		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		fieldType.setStored(true);
		fieldType.setStoreTermVectors(true);
		fieldType.setTokenized(true);
		Field contentField = new Field(IDocumentable.FIELD_CONTENT, content, fieldType);
		
		doc.add(contentField);
		doc.add(new IntField(IDocumentable.FIELD_ID, id, Field.Store.YES));
		
		return doc;
		
	}

    /**
     * Return a printable string of a Matrix 
     * @param m matrix of Apache Math
     * @return
     */
    public static String printableMatrix(RealMatrix m){
    	DecimalFormat df = new DecimalFormat("#0.0000");
    	String out = "";
    	
    	for( int i = 0; i < m.getRowDimension(); i++ ){
    		for( int j = 0; j < m.getColumnDimension(); j++){
    			out += df.format( m.getEntry(i, j) ) + "\t";
    		}
    		out += "\n";
    	}
    	
    	return out;
    }
	
}
