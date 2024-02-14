package com.sumaengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.apache.log4j.Logger;
import java.sql.DriverManager;

public class DbmysqlLayer {

	/** Creates a new instance of DataBaseLayer */
	private String fileName = "db.properties";
    private Properties dataBaseProperties = new Properties();
    //private Connection conn;
    private static Connection mysqlConn;
    static Logger log = Logger.getLogger(DbmysqlLayer.class.getName());
    
	public DbmysqlLayer() {
	    //System.out.println("Creating mySQL DataBaseLayer class [DONE]");
	    //log.info("Creating mySQL DataBaseLayer class [DONE]");
	}
    
    public void loadProperties(String propFileName) throws SQLException, FileNotFoundException, IOException {
    	this.dataBaseProperties.load(new FileInputStream(new File("properties/db.properties")));    	
    }
	
    public void loadDblConnection() throws SQLException,IOException, ClassNotFoundException {
    	ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);
	  
		this.dataBaseProperties.load(inputStream);
		this.mysqlConn = connect();		
    }

    public void releaseDblConnection() throws SQLException {
    	this.mysqlConn.close();
    	this.mysqlConn = null;
    }
    
    public Connection getConnection() throws SQLException{
        return this.mysqlConn;
    }

    public Connection connect() throws SQLException, ClassNotFoundException {    	
    	
    	String JDBC_DRIVER = this.dataBaseProperties.getProperty("JDBC_DRIVER").toString();
        String DB_URL = this.dataBaseProperties.getProperty("URLConnection").toString();
        String USER = this.dataBaseProperties.getProperty("Username").toString();
        String PASS = this.dataBaseProperties.getProperty("Password").toString();
        
        //log.info("Database parameter loaded...");
        //log.info("JDBC_DRIVER: "+ JDBC_DRIVER);
        //log.info("URL: "+ DB_URL);
        //log.info("Username: " + USER);
        //log.info("Password: XXXXXXXX");
    	
    	if (mysqlConn == null) {
            try {
            	// register driver
            	//System.out.println("Connecting..");
            	//log.info("DB Connecting..");
                Class.forName(JDBC_DRIVER);
                mysqlConn = DriverManager.getConnection(DB_URL, USER, PASS);           
                //System.out.println("DB Connected!");
                log.info("DB Connected!");
                
            } catch (SQLException ex) {
            	//System.out.println(ex.toString());
            	log.error("FOUND ERROR method connect()" + ex.toString());
                throw ex;
            } catch (ClassNotFoundException ex2) {
            	//System.out.println(ex2.toString());
            	log.error("FOUND ERROR method connect()" + ex2.toString());
            	throw ex2;
            }
        } else {
        	//System.out.println("Not Null");
        	log.error("FOUND ERROR method connect()" + "object mysqlconn not null.");
        }
        return mysqlConn;
    }

    public void close() throws SQLException {
      this.mysqlConn.close();
    }

    public Properties getDataBaseProperties() {
      return this.dataBaseProperties;
    }

    public void setDataBaseProperties(Properties DataBaseProperties) {
      this.dataBaseProperties = DataBaseProperties;
    }
	
	public int updateQuery(String query) throws SQLException {
	    Statement queryStatement = this.mysqlConn.createStatement();
	    int i = queryStatement.executeUpdate(query);
	    queryStatement.close();
	    return i;
	}

	public ResultSet selectQuery(String query) throws SQLException{
		Statement queryStatement = mysqlConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = queryStatement.executeQuery(query);
		return rs;
	}
	
}
