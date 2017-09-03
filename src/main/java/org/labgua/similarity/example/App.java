package org.labgua.similarity.example;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.labgua.similarity.memory.MYSQLSimilarityStore;
import org.labgua.similarity.engine.Similarity;
import org.labgua.similarity.example.TestData.EntityTest;
import org.labgua.similarity.memory.ISimilarityManager;
import org.labgua.similarity.memory.RAMSimilarityStore;
import org.labgua.similarity.memory.SQLITESimilarityStore;

public class App 
{	
    public static void main( String[] args ) throws IOException, SQLException
    {
        System.out.println( "Example SimilarityEngine" );
        Path path = FileSystems.getDefault().getPath( System.getProperty("user.dir") );
        System.out.println("Working Directory = " + path);
        Logger.getLogger("Logger").setLevel(Level.OFF);
        
        
        String indexname = "indextest";
		Path pathIndex = path.getFileSystem().getPath(path.toString(), indexname);
		IndexWriterConfig conf = new IndexWriterConfig(new ItalianAnalyzer());

		////// Set a Directory for Lucene
		Directory directory = new SimpleFSDirectory(pathIndex);
		//Directory directory = new RAMDirectory();
        
		////// Set a store for the results
        //ISimilarityManager simData = new RAMSimilarityStore();
        ISimilarityManager simData = new SQLITESimilarityStore("sim.db");
        //ISimilarityManager simData = new MYSQLSimilarityStore("localhost:3306", "db_similarity", "root", "password");
        
        Similarity sim = new Similarity(directory, simData, conf);
        sim.delete();        
        
        
        
        //// init + update
        List<EntityTest> data = TestData.getListData();
        for( EntityTest e : data ){
        	System.out.println("-> " + e);
        }
        
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
        
    }
}
