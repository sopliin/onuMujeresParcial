
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="org.example.onu_mujeres_crud.beans.Encuesta" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="description" content="Responsive Admin &amp; Dashboard Template based on Bootstrap 5">
	<meta name="author" content="AdminKit">
	<meta name="keywords" content="adminkit, bootstrap, bootstrap 5, admin, dashboard, template, responsive, css, sass, html, theme, front-end, ui kit, web">

	<link rel="preconnect" href="https://fonts.gstatic.com">
	<link rel="shortcut icon" href="img/icons/icon-48x48.png" />

	<link rel="canonical" href="https://demo-basic.adminkit.io/" />

	<title>AdminKit Demo - Bootstrap 5 Admin Template</title>

	<link rel="stylesheet" type="text/css" href="http://localhost:8080/onu_mujeres_crud_war_exploded/onu_mujeres/static/css/app.css">
	<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&display=swap" rel="stylesheet">
</head>

<body>
<script type="text/javascript" src="js/app.js"></script>
<div class="wrapper">
	<nav id="sidebar" class="sidebar js-sidebar">
		<div class="sidebar-content js-simplebar">
			<a class="sidebar-brand" href="index.html">
				<span class="align-middle">ONU Mujeres</span>
			</a>

			<ul class="sidebar-nav">
				<li class="sidebar-header">
					Encuestas
				</li>

				<li class="sidebar-item active">
					<a class="sidebar-link" href="EncuestadorServlet?action=total">
						<i class="align-middle" data-feather="sliders"></i> <span class="align-middle">Encuestas totales</span>
					</a>
				</li>

				<li class="sidebar-item">
					<a class="sidebar-link" href="EncuestadorServlet?action=terminados">
						<i class="align-middle" data-feather="book"></i> <span class="align-middle">Encuestas completadas</span>
					</a>
				</li>

				<li class="sidebar-item">
					<a class="sidebar-link" href="EncuestadorServlet?action=borradores">
						<i class="align-middle" data-feather="book"></i> <span class="align-middle">Encuestas en progreso</span>
					</a>
				</li>

				<li class="sidebar-item">
					<a class="sidebar-link" href="EncuestadorServlet?action=pendientes">
						<i class="align-middle" data-feather="book"></i> <span class="align-middle">Encuestas por hacer</span>
					</a>
				</li>

				<li class="sidebar-header">
					Formulario
				</li>

				<li class="sidebar-item">
					<a class="sidebar-link" href="onu_mujeres/static/encuestador_formulario.jsp">
						<i class="align-middle" data-feather="check-square"></i> <span class="align-middle">Forms</span>
					</a>
				</li>


			</ul>


		</div>
	</nav>

	<div class="main">
		<nav class="navbar navbar-expand navbar-light navbar-bg">
			<a class="sidebar-toggle js-sidebar-toggle">
				<i class="hamburger align-self-center"></i>
			</a>

			<div class="navbar-collapse collapse">
				<ul class="navbar-nav navbar-align">
					<li class="nav-item dropdown">


					<li class="nav-item dropdown">
						<a class="nav-icon dropdown-toggle d-inline-block d-sm-none" href="#" data-bs-toggle="dropdown">
							<i class="align-middle" data-feather="settings"></i>
						</a>

						<a class="nav-link dropdown-toggle d-none d-sm-inline-block" href="#" data-bs-toggle="dropdown">
							<img src="https://media.revistagq.com/photos/5ca5f6a77a3aec0df5496c59/1:1/w_1472,h_1472,c_limit/bob_esponja_9564.png" class="avatar img-fluid rounded me-1" alt="Charles Hall" /> <span class="text-dark">Charles Hall</span>
						</a>
						<div class="dropdown-menu dropdown-menu-end">
							<a class="dropdown-item" href="onu_mujeres/static/encuestador_ver_tu_perfil.jsp"><i class="align-middle me-1" data-feather="pie-chart"></i> Ver Perfil</a>
							<div class="dropdown-divider"></div>

							<a class="dropdown-item" href="onu_mujeres/static/pagina_inicio.jsp">Cerrar Sesión</a>


						</div>
					</li>
				</ul>
			</div>
		</nav>

		<main class="content">
			<div class="container-fluid p-0">

				<div class="mb-3">
					<h1 class="h3 d-inline align-middle">Encuestas Asignadas</h1>
				</div>
				<div class="row">
					<div class="col-12 col-lg-8 col-xxl-9 d-flex">
						<div class="card flex-fill">
							<div class="card-header">
								<h5 class="card-title mb-0">Lista de Encuestas</h5>
							</div>
							<table class="table table-hover my-0">
								<thead>
								<tr>
									<th>ID Encuesta</th>
									<th>Nombre</th>
									<th class="d-none d-xl-table-cell">Descripción</th>
									<th class="d-none d-xl-table-cell">Código</th>
									<th>Estado</th>
									<th class="d-none d-md-table-cell">Fecha Límite</th>
									<th>Acciones</th>
								</tr>
								</thead>
								<tbody>
								<%
									// Recupera la lista de encuestas del request
									// Asegúrate de que tu Servlet ponga esta lista en el request con el atributo "listaEncuestas"
									List<Encuesta> listaEncuestas = (List<Encuesta>) request.getAttribute("listaEncuestas");
									if (listaEncuestas != null && !listaEncuestas.isEmpty()) {
										for (Encuesta encuesta : listaEncuestas) {
								%>
								<tr>
									<td><%= encuesta.getEncuestaId() %></td>
									<td><%= encuesta.getNombre() %></td>
									<td class="d-none d-xl-table-cell"><%= encuesta.getDescripcion() %></td>
									<td class="d-none d-xl-table-cell">
										<%
											String estadoEncuesta = encuesta.getEstado();
										%>
									</td>
									<td>
										<%
											String statusClass = "";
											String statusText = "";
											if ("activo".equalsIgnoreCase(estadoEncuesta)) {
												statusClass = "badge bg-success";
												statusText = "Completada";
											} else if ("inactivo".equalsIgnoreCase(estadoEncuesta)) {
												statusClass = "badge bg-warning";
												statusText = "En progreso";
											} else { // Asumo "activo" o cualquier otro estado no completado/borrador
												statusClass = "badge bg-danger";
												statusText = "Por hacer";
											}
										%>
										<span class="<%= statusClass %>"><%= statusText %></span>
									</td>
									<td class="d-none d-md-table-cell">
										<%
											// Si el bean Encuesta tuviera un campo 'fechaLimite', lo usarías aquí:
											// out.print(encuesta.getFechaLimite() != null ? encuesta.getFechaLimite() : "N/A");

										%>
									</td>
									<td class="table-action">
										<%
											// La acción de "Rellenar Encuesta" solo si no está completada
											if (!"completo".equalsIgnoreCase(estadoEncuesta)) {
										%>
										<a href="<%= request.getContextPath() %>/EncuestadorServlet?action=obtenerBorrador&encuestaId=<%= encuesta.getEncuestaId() %>">
											<i class="align-middle" data-feather="edit-2"></i>
										</a>
										<% } %>
									</td>
								</tr>
								<%
									}
								} else {
								%>
								<tr>
									<td colspan="7" class="text-center">No hay encuestas asignadas.</td>
								</tr>
								<%
									}
								%>
								</tbody>
							</table>
						</div>
					</div>

				</div>

			</div>
		</main>

		<footer class="footer">
			<div class="container-fluid">
				<div class="row text-muted">
					<div class="col-6 text-start">
						<p class="mb-0">
							<a class="text-muted" href="https://adminkit.io/" target="_blank"><strong>AdminKit</strong></a> &copy;
						</p>
					</div>
					<div class="col-6 text-end">
						<ul class="list-inline">
							<li class="list-inline-item">
								<a class="text-muted" href="https://adminkit.io/" target="_blank">Soporte</a>
							</li>
							<li class="list-inline-item">
								<a class="text-muted" href="https://adminkit.io/" target="_blank">Centro de Ayuda</a>
							</li>
							<li class="list-inline-item">
								<a class="text-muted" href="https://adminkit.io/" target="_blank">Privacidad</a>
							</li>
							<li class="list-inline-item">
								<a class="text-muted" href="https://adminkit.io/" target="_blank">Términos</a>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</footer>
	</div>
</div>

<script src="js/app.js"></script>
<script type="text/javascript" src="js/app.js"></script>
<script type="text/javascript" src="http://localhost:8080/onu_mujeres_crud_war_exploded/onu_mujeres/static/js/app.js"></script>
</body>

</html>