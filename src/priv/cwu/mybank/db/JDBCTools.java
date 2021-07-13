package priv.cwu.mybank.db;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines several basic functionalities to access a MySQL database.
 * These basic functionalities includes: create, retrieve, update, and delete...
 *
 * Reference: a CSDN user (MingCode)
 * (You can ask me for the link if interested. I didn't post it here because it's too long)
 */
public class JDBCTools {

    public static int executeUpdate(String sql, Object[] objects) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = JDBCUtils.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (objects != null) {
                for (int i = 0; i < objects.length; i++) {
                    preparedStatement.setObject(i + 1, objects[i]);
                }
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeAll(connection, preparedStatement, null);
        }
        return -1;
    }

    //TODO: Packaging query function here (JavaBeans)
}
