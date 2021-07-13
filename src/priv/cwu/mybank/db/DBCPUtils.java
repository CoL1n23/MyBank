package priv.cwu.mybank.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBCPUtils {
    private static BasicDataSource dataSource;

    static {
        try {
            InputStream inp = JDBCUtils.class.getClassLoader().getResourceAsStream("dbcp.properties");
            Properties prop = new Properties();
            prop.load(inp);
            dataSource = BasicDataSourceFactory.createDataSource(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeAll(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
