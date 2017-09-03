package org.labgua.similarity.memory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an implementation of the manager of results in MYSQL database
 * It uses the JDBC java
 * @author sergio
 *
 */
public class MYSQLSimilarityStore implements ISimilarityManager{

	private static final String INIT_MYSQL_TABLE = "CREATE TABLE IF NOT EXISTS similarity (source INTEGER NOT NULL, destination INTEGER NOT NULL, rank DECIMAL(17,16) NOT NULL, CONSTRAINT role_pair UNIQUE (source, destination));";
	
	
	Connection connection;
	
	public MYSQLSimilarityStore(String address, String schema, String username, String password) throws SQLException {
		this.connection = DriverManager.getConnection("jdbc:mysql://"+ address +"/"+ schema +"?user="+ username +"&password=" + password + "&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
		try{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			statement.executeUpdate(INIT_MYSQL_TABLE);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	

	
	@Override
	public void add(Integer idSource, Integer idDestination, Double rank) {
		// TODO Auto-generated method stub
		try {
			Statement statement = connection.createStatement();
	        statement.setQueryTimeout(30);  // set timeout to 30 sec.
	        statement.executeUpdate("INSERT INTO similarity (source, destination, rank) values ("+ String.valueOf(idSource) +", "+ String.valueOf(idDestination) +" , "+ String.valueOf(rank) +") ON DUPLICATE KEY UPDATE rank = " + String.valueOf(rank));
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void remove(Integer idSrc) {
		try {
			Statement statement = connection.createStatement();
	        statement.setQueryTimeout(30);  // set timeout to 30 sec.
	        statement.executeUpdate("DELETE FROM similarity WHERE source="+ String.valueOf(idSrc) + " OR destination=" + String.valueOf(idSrc));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void delete() {
		try {
			Statement statement = connection.createStatement();
	        statement.setQueryTimeout(30);  // set timeout to 30 sec.
	        statement.executeUpdate("DELETE FROM similarity;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public Map<Integer, Double> getSimilar(Integer id) {
		//select * from similarity  s where s.source=4 or s.destination=4 order by rank desc
		Map<Integer, Double> outList = new HashMap<>();
		try {
			Statement statement = connection.createStatement();
	        statement.setQueryTimeout(30);  // set timeout to 30 sec.
	        ResultSet rs = statement.executeQuery("SELECT * FROM similarity s WHERE s.source="+ String.valueOf(id) +" OR s.destination="+ String.valueOf(id));
	        
	        
	        while( rs.next() ){
	        	int currSource = rs.getInt("source");
	        	int currDestination = rs.getInt("destination");
	        	double sim = rs.getDouble("rank");
	        	
	        	if( currSource != id ) outList.put(currSource, sim);
	        	else if( currDestination != id ) outList.put(currDestination, sim);
	        }
	        
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return outList;
	}

	@Override
	public Double getSimilarity(Integer idSource, Integer idDestination) {
		//select * from similarity  s where s.source=4 and s.destination=5
		try {
			Statement statement = connection.createStatement();
	        statement.setQueryTimeout(30);  // set timeout to 30 sec.
	        ResultSet rs = statement.executeQuery("SELECT * FROM similarity s WHERE s.source="+ String.valueOf(idSource) +" AND s.destination=" + String.valueOf(idDestination));
		
	        rs.next();
	        
	        return rs.getDouble("rank");
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void close() throws SQLException{
		this.connection.close();
	}


}
