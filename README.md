# SimilarityEngine

![Logo](/doc/sim_engine_logo.png)

## Introduction 

This project provides an engine to calculate the similarity of entity.
Through a simple mechanism of conversion from entity to document, it uses [Apache Lucene](https://lucene.apache.org/) to define the similarity of its.

The similarity is computed using a VSM technique, with weight Tf-IDf (with [TFIDFSimilarity](https://lucene.apache.org/core/5_5_0/core/org/apache/lucene/search/similarities/TFIDFSimilarity.html) )


## Usage

First define the Entity, implementing the interface IDocumentable, for example

```java
public class EntityTest implements IDocumentable{

	private static int CLASS_ID = 0;
	
	private int id;
	private String titolo;
	private String messaggio;
	private String indirizzo;
			
	public EntityTest(String titolo, String messaggio, String indirizzo) {
		this.id = CLASS_ID;
		this.titolo = titolo;
		this.messaggio = messaggio;
		this.indirizzo = indirizzo;
		CLASS_ID++;
	}

	@Override
	public Document toDocument() {
		
		List<String> content = new ArrayList<>();
		content.add(titolo);
		content.add(messaggio);
		content.add(indirizzo);
		
		return SUtility.createDoc(id, content);	
	}

	@Override
	public String toString() {
		return "EntityTest [id=" + id + ", titolo=" + titolo + ", messaggio=" + messaggio + ", indirizzo="
				+ indirizzo + "]";
	}

	@Override
	public Integer getId() {
		return this.id;
	}
	
}

```

Then instanziate Similarity and use it

```java

/// setting IndexWriter + Analyzer 
IndexWriterConfig conf = new IndexWriterConfig(new ItalianAnalyzer());

/// 1 /// Set a Directory for Lucene
Directory directory = new SimpleFSDirectory(pathIndex);
//Directory directory = new RAMDirectory();

/// 2 /// Set a store for the results
ISimilarityManager simData = new RAMSimilarityStore();
//ISimilarityManager simData = new SQLITESimilarityStore("sim.db");
//ISimilarityManager simData = new MYSQLSimilarityStore("localhost:3306", "db_similarity", "root", "password");

Similarity sim = new Similarity(directory, simData, conf);
sim.delete();


//// Get the entities
List<EntityTest> data = TestData.getListData();


//// init + update
for( int i = 0; i<4; i++ ){
	sim.add(data.get(i));
}
sim.initSimilarity();


for( int i = 4; i<8; i++ ){
	sim.add(data.get(i));
}
sim.updateSimilarity();

// remove document 3
sim.remove(data.get(3));
sim.updateSimilarity();


sim.close();


System.out.println("Similarity 4-5 : " + sim.similarity(4, 5) );
System.out.println("Similarity 4-6 : " + sim.similarity(4, 6) );
System.out.println("Similarity 4-7 : " + sim.similarity(4, 7) );
System.out.println("Similarity 5-6 : " + sim.similarity(5, 6) );
System.out.println("Similarity 5-7 : " + sim.similarity(5, 7) );
System.out.println("Similarity 6-7 : " + sim.similarity(6, 7) );

```

For details, view the examples package.
