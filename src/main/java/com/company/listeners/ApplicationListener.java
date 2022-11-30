package com.company.listeners;

import com.company.container.DatabaseContainer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;
@WebListener
public class ApplicationListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        Connection connection = DatabaseContainer.getConnection();
        sce.getServletContext().setAttribute("dbConnection",connection);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        Connection dbConnection = (Connection) sce.getServletContext().getAttribute("dbConnection");
        try {
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
