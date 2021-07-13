package priv.cwu.mybank.db;

import java.sql.*;

public class TestQuery {
    public static void main(String[] args) throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBCPUtils.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM person");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBCPUtils.closeAll(connection, preparedStatement, null);
        }
    }
}
