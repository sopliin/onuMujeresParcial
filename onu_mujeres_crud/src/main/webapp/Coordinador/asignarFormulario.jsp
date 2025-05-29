<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Asignar Encuesta</title>
    <link rel="stylesheet" href="../css/estilos.css">
</head>
<body>
<h2>Asignar Encuesta al Encuestador</h2>

<form method="post" action="CoordinadorServlet">
    <input type="hidden" name="action" value="guardarAsignacion"/>
    <input type="hidden" name="idEncuestador" value="${idEncuestador}"/>

    <label for="nombreEncuesta">Nombre de la Encuesta:</label><br>
    <input type="text" id="nombreEncuesta" name="nombreEncuesta" required><br><br>

    <label for="carpeta">Carpeta:</label><br>
    <input type="text" id="carpeta" name="carpeta" required><br><br>

    <button type="submit">Asignar Encuesta</button>
</form>

<br>
<a href="CoordinadorServlet?action=lista">← Volver a la lista</a>
</body>
</html>
