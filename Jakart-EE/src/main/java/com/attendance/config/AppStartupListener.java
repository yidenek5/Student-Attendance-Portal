package com.attendance.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.log("[AttendanceSystem] Startup validation starting...");
        context.log("[AttendanceSystem] Application context path: " + context.getContextPath());
        context.log("[AttendanceSystem] Servlet registrations:");
        for (Map.Entry<String, ? extends ServletRegistration> entry : context.getServletRegistrations().entrySet()) {
            ServletRegistration registration = entry.getValue();
            context.log("[AttendanceSystem]   " + registration.getName() + " => " + registration.getMappings());
        }

        loadConfiguration(context);
        validateDatabaseConnection(context);
    }

    private void loadConfiguration(ServletContext context) {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                context.log("[AttendanceSystem] Configuration load: db.properties not found on classpath.");
                return;
            }
            Properties props = new Properties();
            props.load(input);
            context.log("[AttendanceSystem] Loaded configuration file: db.properties");
            context.log("[AttendanceSystem]   db.url=" + props.getProperty("db.url", "(not set)"));
            context.log("[AttendanceSystem]   db.user=" + props.getProperty("db.user", "(not set)"));
        } catch (Exception e) {
            context.log("[AttendanceSystem] Failed to load configuration file db.properties", e);
        }
    }

    private void validateDatabaseConnection(ServletContext context) {
        try {
            Properties props = new Properties();
            try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (input != null) {
                    props.load(input);
                }
            }

            String url = System.getenv().getOrDefault("DB_URL", props.getProperty("db.url", ""));
            String user = System.getenv().getOrDefault("DB_USER", props.getProperty("db.user", ""));
            String password = System.getenv().getOrDefault("DB_PASSWORD", props.getProperty("db.password", ""));

            if (url == null || url.isBlank()) {
                context.log("[AttendanceSystem] Database validation skipped because db.url is not configured.");
                return;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                context.log("[AttendanceSystem] Database connection validated successfully.");
            }
        } catch (Exception e) {
            context.log("[AttendanceSystem] Database connection validation failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().log("[AttendanceSystem] Application shutdown complete.");
    }
}
