package com.google.appengine.musicnow;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.rdbms.AppEngineDriver;

@SuppressWarnings("serial")
public class MusicNowAppEngineServlet extends HttpServlet {

	private static Connection connection = null;
	private PreparedStatement selectAllStatement;

	public MusicNowAppEngineServlet() {
		super();

		// connect to database
		openDatabaseConnection();

		// create prepared statements
		if (connection != null) {
			createPreparedStatements();
		}
	}

	private static void openDatabaseConnection() {
		try {
			// create new app engine driver
			DriverManager.registerDriver(new AppEngineDriver());

			// connect to database
			connection = DriverManager
					.getConnection("jdbc:google:rdbms://musicnow-team2:musicnow/musicnow_test");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void closeDatabaseConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createPreparedStatements() {
		try {
			// query statement to list all from test database
			String selectAll = "select * from mn_main";
			selectAllStatement = connection.prepareStatement(selectAll);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.getWriter().println(
				"Received a doGet request!!! " + req.getLocalName());

		processRequest(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.getWriter().println("received a doPost request!!!");
		processRequest(req, resp);

	}

	private void processRequest(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {
			if (connection != null) {
				resp.getWriter().println(
						"   Successfully connect to MusicNow database!   ");

				String fname = req.getParameter("fname");
				String content = req.getParameter("content");
				if (fname == "" || content == "") {
					resp.getWriter()
							.println(
									"<html><head></head><body>You are missing either a message or a name! Try again! Redirecting in 3 seconds...</body></html>");
				} else {
					resp.getWriter()
							.println("   getting params worked...   \n");
				}

				// execute the query
				ResultSet resultSet = this.selectAllStatement.executeQuery();
				resp.getWriter().println("  query worked ok  ");

				while (resultSet.next()) {
					resp.getWriter().println(
							"  Query Result: band = "
									+ resultSet.getString("band"));
				}
			}
		} catch (SQLException exception) {
			resp.getWriter().println(
					"  MusicNow database connection failed: "
							+ exception.getMessage());
			exception.printStackTrace();
		}

	}
}
