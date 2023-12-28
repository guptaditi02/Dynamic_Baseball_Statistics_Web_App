<%@page contentType="text/html" pageEncoding="UTF-8"%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JSP Page</title>
</head>
<body>
<p>Give me a first name and last name of a baseball player.</p>
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
