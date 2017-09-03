package org.labgua.similarity.entity;

import org.apache.lucene.document.Document;

/**
 * This is an interface of support for the calculation of the similarity
 * 
 * The classes that implements this interface must return a
 * Document that must have two Fields:
 *  - id : represents l'identifier of the entity
 *  - content : content to index
 *
 * In particular the field content must have the next properties for correct indexing :
 * 
 * - fieldContentType.setIndexed(true);
 * - fieldContentType.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
 * - fieldContentType.setStored(true);
 * - fieldContentType.setStoreTermVectors(true);
 * - fieldContentType.setTokenized(true);
 * 
 * @author sergio
 *
 */
public interface IDocumentable {
	public static final String FIELD_ID = "id";
	public static final String FIELD_CONTENT = "content"; // name of the field to index
	
	/**
	 * Return the identifier of the entity
	 * @return the identifier of the entity
	 */
	public Integer getId();
	
	/**
	 * Return an instance of Document that represents the entity
	 * @return Document that represents the entity
	 */
	public Document toDocument();
}
