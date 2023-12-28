package com.example.project1task3;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
Explanation:
* Author: Aditi Gupta - argupta
* Last Modified: September 22, 2023
This code defines a Servlet (BaseballServlet) for handling HTTP GET requests.
It uses the BaseballModel class to interact with the business logic.
The init method initializes the BaseballModel when the servlet is created.
In the doGet method, the servlet processes GET requests from clients.
It checks if the user is on a mobile device and sets the appropriate DOCTYPE.
It retrieves the first name and last name parameters from the request.
Depending on the action parameter, it performs searches for image URLs and player data using the BaseballModel.
It sets attributes on the request object to pass data to the view.
Finally, it forwards the request to the appropriate view based on the nextView variable.
*/

@WebServlet(name = "BaseballServlet", urlPatterns = {"/getResult", "/getData", "/processData"})
public class BaseballServlet extends HttpServlet {

    BaseballModel bbm = null;  // The "business model" for this app

    @Override
    public void init() {
        bbm = new BaseballModel();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Get the first name and last name parameters if they exist
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        // Determine what type of device our user is
        String ua = request.getHeader("User-Agent");

        boolean mobile;
        // Prepare the appropriate DOCTYPE for the view pages based on user agent
        if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
            mobile = true;
            /*
             * This is the latest XHTML Mobile doctype. To see the difference it
             * makes, comment it out so that a default desktop doctype is used
             * and view on an Android or iPhone.
             */
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            mobile = false;
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }

        String nextView = null;
        String action = request.getParameter("action");

        /*
         * Check if the first name and last name parameters are present.
         * If not, then give the user instructions and prompt for input.
         * If they are present, then do the search and return the result.
         */
        if (firstName != null  && lastName != null && firstName.trim().length() > 0 && lastName.trim().length() > 0) {
            if (action != null) {
                if (action.equals("getResult") || action.equals("getData")) {
                    // Perform a search for image URLs and other data
                    String pictureURL = bbm.doSearch(firstName, lastName);
                    String imageURL = bbm.doFlagSearch(firstName, lastName);
                    request.setAttribute("imageURL", imageURL);
                    request.setAttribute("pictureURL", pictureURL);
                    // Pass the user inputs (firstName and lastName) also to the view.
                    request.setAttribute("firstName", firstName);
                    request.setAttribute("lastName", lastName);

                    if (action.equals("getData")) {
                        // Scrape additional player data
                        bbm.scrapePlayerData(firstName, lastName);
                        nextView = "data.jsp";
                    } else {
                        nextView = "result.jsp";
                    }
                } else {
                    nextView = "prompt.jsp";
                }
            }
        } else {
            // No first name and last name parameters, so choose the prompt view
            nextView = "prompt.jsp";
        }
        // Transfer control over to the correct "view"
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);
    }
}
