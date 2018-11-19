package jetty;

import java.sql.*;

import javax.servlet.http.*;

import org.json.*;

public abstract class Main {

	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String dbName = "jdbcDemoDB";
	private static final String connectionURL = "jdbc:derby:" + dbName + ";create=true";
	//private static final String connectionURL = "jdbc:derby:memory:" + dbName + ";create=true";
	private static Connection conn;
	private static Main main;
	private JSONObject json;
	static final String INSERT = "INSERT INTO Log (WHO, JSON) VALUES (?, ?)";
	static final String DELETE = "DELETE FROM Log WHERE ID < (SELECT MAX(ID)-50 FROM Log)";

	protected Main() {
		String createString = null;
		
		createString = "CREATE TABLE Log (ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, "
				+ " WHO VARCHAR(256), WHEN TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
				+ " JSON VARCHAR(2048) NOT NULL) ";
		try {
			if (conn == null) conn = DriverManager.getConnection(connectionURL);
			Statement s = null;
			try {
				s = conn.createStatement();
				s.execute("DROP TABLE Log");
			} catch (SQLException e) {
			}
			s = conn.createStatement();
			s.execute(createString);
			
			createString = "CREATE TABLE News (" +
					"ID INT NOT NULL GENERATED ALWAYS AS IDENTITY," +
					"URL VARCHAR(512) NOT NULL," +
					"TITULO VARCHAR(512) NOT NULL," +
					"TEXTO VARCHAR(32672) NOT NULL," +
					"CARTEIRA VARCHAR(95) NOT NULL," +
					"PRECO INTEGER NOT NULL DEFAULT 0," +
					"PAGO BOOLEAN NOT NULL DEFAULT false," +
					"DATACRIACAO TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
					"DATAUPDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
					"STATUS VARCHAR(256)," +
					"CURTI INTEGER NOT NULL DEFAULT 0," +
					"NAOCURTI INTEGER NOT NULL DEFAULT 0" +
					")";
			try {
				s = conn.createStatement();
				s.execute(createString);
			} catch (SQLException e) {
				e.printStackTrace();
			}


		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	protected void execute(String sql) throws SQLException {
		Statement s = conn.createStatement();
		s.execute(sql);
	}

	protected ResultSet executeQuery(String sql) throws SQLException {
		Statement s = conn.createStatement();
		return s.executeQuery(sql);
	}

	static Main getInstance() {
		if (main == null) main = new your.YourMain();
		return main;
	}

	void setJSON(JSONObject json) {
		this.json = json;
	}

	protected JSONObject getJSON() {
		return json;
	}

	protected void print(String s) {
		json.put("text", s);
	}

	public void main(String[] args, HttpServletRequest request, HttpServletResponse response) {
		main(args);
	}

	public abstract void main(String[] args);

	protected long insert(String userId, String json) throws SQLException {
		long ret = -1;
		try (PreparedStatement statement = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);) {
			statement.setString(1, userId);
			statement.setString(2, json);

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) { throw new SQLException("Insert failed, no rows affected."); }

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					ret = generatedKeys.getLong(1);
				} else {
					throw new SQLException("Creating user failed, no ID obtained.");
				}
			}
		}
		return ret;
	}

	protected void cleanLog() throws SQLException {
		execute(DELETE);
	}
}
