<%@ page import="java.util.Map" %>
<%@ page import="com.example.project1task3.BaseballModel" %>
<%@ page import="com.example.project1task3.BaseballServlet" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>

<html>
<head>
    <title>Interesting Picture</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<%
    String firstName = request.getParameter("firstName");
    String lastName = request.getParameter("lastName");

    if (request.getAttribute("pictureURL") != null) {
%>
<h1>Here is a picture of <%= firstName %> <%= lastName %> </h1><br>
<img src="<%= request.getAttribute("pictureURL")%>"><br><br>
<h1>Here is a picture of flag of his team</h1><br>
<img src="<%= request.getAttribute("imageURL")%>" style="height: 200px; width: 200px;"><br><br>
<% } else { %>
<h1>A picture of <%= firstName %> <%= lastName %> could not be found</h1><br>
<% } %>

<form action="processData" method="GET">
    <label for="firstName">First Name:</label>
    <input type="text" name="firstName" value="" /><br>

    <label for="lastName">Last Name:</label>
    <input type="text" name="lastName" value="" /><br>

    <label>Select an Action:</label><br>
    <input type="radio" name="action" value="getResult" checked>Photo<br>
    <input type="radio" name="action" value="getData">Data<br>

    <input type="submit" value="Submit" />
</form>
</body>
</html>
