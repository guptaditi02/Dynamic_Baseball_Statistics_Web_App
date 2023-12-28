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
    BaseballModel bbm = new BaseballModel();
%><%
    java.util.Map<String, String> playerDataMap = bbm.scrapePlayerData(request.getParameter("firstName"), request.getParameter("lastName"));
    if (playerDataMap != null && !playerDataMap.isEmpty()) {
%>
<h2>Player Data:</h2>
<ul>
    <% for (java.util.Map.Entry<String, String> entry : playerDataMap.entrySet()) { %>
    <li><strong><%= entry.getKey() %>:</strong> <%= entry.getValue() %></li>
    <% } %>
</ul>
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
