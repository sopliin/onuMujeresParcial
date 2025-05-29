<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Lista de Encuestadores</title>
  <link rel="stylesheet" href="../css/estilos.css"> <!-- tu propio CSS -->
</head>
<body>
<h2>Lista de Usuarios Encuestadores</h2>

<table border="1" cellpadding="10">
  <thead>
  <tr>
    <th>ID</th>
    <th>Nombre</th>
    <th>Email</th>
    <th>Estado</th>
    <th>Formulario</th>
    <th>Acciones</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="u" items="${listaEncuestadores}">
    <tr>
      <td>${u.usuarioId}</td>
      <td>${u.nombre}</td>
      <td>${u.correo}</td>
      <td>${u.estado}</td>
      <td>
        <c:if test="${u.estado == 'activo'}">
          <form method="get" action="CoordinadorServlet">
            <input type="hidden" name="action" value="asignarFormulario"/>
            <input type="hidden" name="id" value="${u.usuarioId}"/>
            <button type="submit">Asignar</button>
          </form>
        </c:if>
      </td>
      <td>
        <form method="get" action="CoordinadorServlet" style="display:inline;">
          <input type="hidden" name="action" value="ver"/>
          <input type="hidden" name="id" value="${u.usuarioId}"/>
          <button type="submit">Perfil</button>
        </form>

        <form method="get" action="CoordinadorServlet" style="display:inline;">
          <input type="hidden" name="action" value="estado"/>
          <input type="hidden" name="id" value="${u.usuarioId}"/>
          <input type="hidden" name="estado" value="${u.estado}"/>
          <button type="submit">
            <c:choose>
              <c:when test="${u.estado == 'activo'}">Banear</c:when>
              <c:otherwise>Activar</c:otherwise>
            </c:choose>
          </button>
        </form>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>
</body>
</html>
