package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.crimes.model.Adiacenza;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<String> getCategoria(){
		String sql = "SELECT DISTINCT e.offense_category_id as id "
				+ "FROM `events` e "
				+ "ORDER BY e.offense_category_id " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<String> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(res.getString("id"));
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Integer> getMese(){
		String sql = "SELECT DISTINCT MONTH(e.reported_date) AS mese "
				+ "FROM `events` e "
				+ "ORDER BY MONTH(e.reported_date) " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Integer> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(res.getInt("mese"));
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public void getVertici(List<String> vertici,Integer mese,String categoria){
		String sql = "SELECT DISTINCT e.offense_type_id AS id "
				+ "FROM `events` e "
				+ "WHERE MONTH(e.reported_date)=? "
				+ "AND e.offense_category_id=? " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, mese);
			st.setString(2, categoria);
			
			
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				if(!vertici.contains(res.getString("id")))
				{
					vertici.add(res.getString("id"));
				}
			}
			
			conn.close();
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	public List<Adiacenza> getAdiacenze(List<String> vertici,Integer mese,String categoria){
		String sql = "SELECT e1.offense_type_id AS id1, e2.offense_type_id AS id2, COUNT(DISTINCT e1.neighborhood_id) AS peso "
				+ "FROM `events` e1,`events` e2 "
				+ "WHERE e1.offense_type_id> e2.offense_type_id "
				+ "AND e1.neighborhood_id=e2.neighborhood_id "
				+ "AND MONTH(e1.reported_date)=MONTH(e2.reported_date) "
				+ "AND MONTH(e1.reported_date)=? "
				+ "AND e1.offense_category_id=e2.offense_category_id "
				+ "AND e1.offense_category_id=? "
				+ "GROUP BY e1.offense_type_id, e2.offense_type_id " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, mese);
			st.setString(2, categoria);
			
			List<Adiacenza> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				if(vertici.contains(res.getString("id1")) && vertici.contains(res.getString("id2")))
				{
					Adiacenza a=new Adiacenza(res.getString("id1"),res.getString("id2"),res.getInt("peso"));
					list.add(a);
				}
				
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	
}
