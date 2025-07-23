package db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {

	private static Connection conn = null;

	public static Connection getConnection() {
		if (conn == null) {
			try {
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				String user = props.getProperty("user");
				String password = props.getProperty("password");

				if (url == null || user == null || password == null) {
					throw new DbException("Propriedades de conexão (dburl, user, password) incompletas no db.properties.");
				}

				if (!url.contains("?")) {
					url += "?useSSL=false&allowPublicKeyRetrieval=true";
				} else {
					if (!url.contains("useSSL")) {
						url += "&useSSL=false";
					}
					if (!url.contains("allowPublicKeyRetrieval")) {
						url += "&allowPublicKeyRetrieval=true";
					}
				}

				Class.forName("com.mysql.cj.jdbc.Driver");

				conn = DriverManager.getConnection(url, user, password);
			}
			catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
			catch (ClassNotFoundException e) {
				throw new DbException("Driver JDBC do MySQL (com.mysql.cj.jdbc.Driver) não encontrado: " + e.getMessage());
			}
		}
		return conn;
	}

	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	private static Properties loadProperties() {
		try (InputStream fs = DB.class.getClassLoader().getResourceAsStream("db.properties")) {
			Properties props = new Properties();
			if (fs == null) {
				throw new DbException("Arquivo db.properties não encontrado em src/main/resources.");
			}
			props.load(fs);
			return props;
		}
		catch (IOException e) {
			throw new DbException(e.getMessage());
		}
	}

	public static void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
}