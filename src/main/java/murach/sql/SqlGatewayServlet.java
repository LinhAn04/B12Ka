package murach.sql;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.*;

import java.sql.*;

public class SqlGatewayServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String sqlStatement = request.getParameter("sqlStatement");
        String sqlResult = ""; // Initialize sqlResult

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver"); // Update to the latest driver class

            // Get a connection to the database
            String dbURL = "jdbc:mysql://mysql-ka-kimanh.d.aivencloud.com:11788/murach_test?useSSL=true&requireSSL=true&serverTimezone=UTC"; // Adjust URL parameters as needed
            String username = "avnadmin"; // Use appropriate username
                String password = "AVNS_1tvUwI03hUAJ6hbo5A8"; // Use appropriate password
            Connection connection = DriverManager.getConnection(dbURL, username, password);

            // Create a statement
            Statement statement = connection.createStatement();

            // Parse the SQL string
            sqlStatement = sqlStatement.trim();
            if (sqlStatement.length() >= 6) {
                String sqlType = sqlStatement.substring(0, 6);
                if (sqlType.equalsIgnoreCase("select")) {
                    // Execute query for SELECT statement
                    ResultSet resultSet = statement.executeQuery(sqlStatement);
                    sqlResult = SQLUtil.getHtmlTable(resultSet);
                    resultSet.close(); // Close the ResultSet
                } else {
                    // Execute update for DDL or DML statements
                    int affectedRows = statement.executeUpdate(sqlStatement);
                    if (affectedRows == 0) { // A DDL statement
                        sqlResult = "<p>The statement executed successfully.</p>";
                    } else { // An INSERT, UPDATE, or DELETE statement
                        sqlResult = "<p>The statement executed successfully.<br>" + affectedRows + " row(s) affected.</p>";
                    }
                }
            }
            statement.close(); // Close the Statement
            connection.close(); // Close the Connection
        } catch (ClassNotFoundException e) {
            sqlResult = "<p>Error loading the database driver: <br>" + e.getMessage() + "</p>";
        } catch (SQLException e) {
            sqlResult = "<p>Error executing the SQL statement: <br>" + e.getMessage() + "</p>";
        }

        // Store results in session attributes
        HttpSession session = request.getSession();
        session.setAttribute("sqlResult", sqlResult);
        session.setAttribute("sqlStatement", sqlStatement);

        // Redirect to the index.jsp page
        String url = "/index.jsp";
        getServletContext().getRequestDispatcher(url).forward(request, response);
    }
}
