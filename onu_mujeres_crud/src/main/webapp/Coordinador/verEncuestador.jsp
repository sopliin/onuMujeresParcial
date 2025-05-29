<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Perfil del Encuestador</title>
    <link rel="stylesheet" href="../css/estilos.css">
</head>
<body>
<h2>Perfil del Encuestador</h2>

<table border="1" cellpadding="10">
    <tr>
        <th>ID</th>
        <td>${encuestador.usuarioId}</td>
    </tr>
    <tr>
        <th>Nombre</th>
        <td>${encuestador.nombre}</td>
    </tr>
    <tr>
        <th>Correo</th>
        <td>${encuestador.correo}</td>
    </tr>
    <tr>
        <th>DNI</th>
        <td>${encuestador.dni}</td>
    </tr>
    <tr>
        <th>Dirección</th>
        <td>${encuestador.direccion}</td>
    </tr>
    <tr>
        <th>Distrito</th>
        <td>${encuestador.distrito.nombre}</td>
    </tr>
    <tr>
        <th>Estado</th>
        <td>${encuestador.estado}</td>
    </tr>
</table>

<br>
<a href="CoordinadorServlet?action=lista">← Volver a la lista</a>
</body>
</html>
