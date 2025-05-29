package org.example.onu_mujeres_crud.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.example.onu_mujeres_crud.beans.Encuesta;
import org.example.onu_mujeres_crud.beans.PreguntaEncuesta;
import org.example.onu_mujeres_crud.beans.RespuestaDetalle;
import org.example.onu_mujeres_crud.daos.EncuestaDAO;
import org.example.onu_mujeres_crud.daos.ContenidoEncuestaDAO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "EncuestadorServlet", value = "/EncuestadorServlet")
public class EncuestadorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action") == null ? "total" : request.getParameter("action");


        EncuestaDAO encuestaDAO = new EncuestaDAO();
        ContenidoEncuestaDAO contenidoEncuestaDAO = new ContenidoEncuestaDAO();
        RequestDispatcher view;

        //TODO: Obtener el id del encuestador de la sesión (Mover esto dentro de cada case donde se use)
        int encuestadorId = 100;
        if (encuestadorId == 0) {
            response.sendRedirect("login.jsp?error=notLoggedIn");
            return;
        }

        switch (action) {
            case "total":
                List<Encuesta> todasLasEncuestas = encuestaDAO.obtenerTodasLasEncuestas(encuestadorId);
                request.setAttribute("listaEncuestas", todasLasEncuestas);
                view = request.getRequestDispatcher("onu_mujeres/static/encuestador_encuestadas.jsp");
                view.forward(request, response);
                break;
            case "terminadas":
                List<Encuesta> encuestasTerminadas = encuestaDAO.obtenerFormulariosCompletados(encuestadorId);
                request.setAttribute("listaEncuestas", encuestasTerminadas);
                view = request.getRequestDispatcher("onu_mujeres/static/encuestador_encuestas_completadas.jsp");
                view.forward(request, response);
                break;
            case "pendientes":
                List<Encuesta> encuestasPendientes = encuestaDAO.obtenerFormulariosSinLlenar(encuestadorId);
                request.setAttribute("listaEncuestas", encuestasPendientes);
                view = request.getRequestDispatcher("onu_mujeres/static/encuestador_encuestas_sin_completar.jsp");
                view.forward(request, response);
                break;
            case "borradores":
                List<Encuesta> encuestasBorradores = encuestaDAO.obtenerFormulariosConBorradores(encuestadorId);
                request.setAttribute("listaEncuestas", encuestasBorradores);
                view = request.getRequestDispatcher("onu_mujeres/static/encuestador_encuestas_progreso.jsp");
                view.forward(request, response);
                break;
            case "obtenerBorrador":
                if (request.getParameter("encuestaId") != null) {
                    int encuestaId = Integer.parseInt(request.getParameter("encuestaId"));

                    //TODO: Obtener el respuestaId del borrador para la encuesta y el encuestador
                    int respuestaId = obtenerRespuestaIdDeBorrador(encuestadorId, encuestaId); // Replace with actual logi
                    if (respuestaId == 0) {

                        response.sendRedirect("EncuestadorServlet?action=pendientes");
                        return;
                    }

                    List<RespuestaDetalle> respuestasGuardadas = contenidoEncuestaDAO.obtenerRespuestasAnteriores(respuestaId);
                    request.setAttribute("respuestasGuardadas", respuestasGuardadas);
                    request.setAttribute("encuestaId", encuestaId);
                    List<PreguntaEncuesta> preguntas = contenidoEncuestaDAO.obtenerPreguntasDeEncuesta(encuestadorId, encuestaId);
                    request.setAttribute("preguntas", preguntas);
                    view = request.getRequestDispatcher("encuestador/encuestador_formulario.jsp");
                    view.forward(request, response);
                } else {
                    response.sendRedirect("EncuestadorServlet?action=borradores");
                }
                break;
            default:
                response.sendRedirect("EncuestadorServlet?action=total");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        //TODO: Obtener el id del encuestador de la sesión
        int encuestadorId = obtenerEncuestadorIdDeSesion(request);
        if (encuestadorId == 0) {
            response.sendRedirect("login.jsp?error=notLoggedIn");
            return;
        }

        ContenidoEncuestaDAO contenidoEncuestaDAO = new ContenidoEncuestaDAO();

        switch (action) {
            case "guardarRespuestas":
                int encuestaId = Integer.parseInt(request.getParameter("encuestaId"));

                //TODO: Obtener o crear el respuestaId (if it's a new or existing response)
                int respuestaId = obtenerOCrearRespuestaId(encuestadorId, encuestaId);
                if (respuestaId == 0) {
                    response.sendRedirect("EncuestadorServlet?error=dbError");
                    return;
                }

                java.util.Enumeration<String> paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    String paramName = paramNames.nextElement();
                    if (paramName.startsWith("pregunta_")) {
                        int preguntaId = Integer.parseInt(paramName.substring(9));
                        String respuesta = request.getParameter(paramName);
                        int opcionId = 0;

                        if (paramName.contains("opcion")) {
                            opcionId = Integer.parseInt(respuesta);
                            respuesta = null;
                        }

                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String fechaContestacion = now.format(formatter);

                        RespuestaDetalle detalle = new RespuestaDetalle();
                        detalle.setRespuestaId(respuestaId);
                        detalle.setPreguntaId(preguntaId);
                        detalle.setOpcionId(opcionId);
                        detalle.setRespuestaTexto(respuesta);
                        detalle.setFechaContestacion(fechaContestacion);

                        contenidoEncuestaDAO.guardarRespuesta(detalle);
                    }
                }
                response.sendRedirect("EncuestadorServlet?action=total");
                break;

            case "guardar":
                response.sendRedirect("index.jsp");
                break;
        }
    }

    //Metodos que me sugirio Gemini, lo dejo porsiacaso

    private int obtenerEncuestadorIdDeSesion(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer encuestadorId = (Integer) session.getAttribute("encuestadorId"); // Assuming you store it as "encuestadorId"
        if (encuestadorId == null) {
            return 0; // Or throw an exception, or handle as appropriate for your app
        }
        return encuestadorId;
    }

    private int obtenerRespuestaIdDeBorrador(int encuestadorId, int encuestaId) {
        // TODO: Implement this method to query the database and get the respuesta_id
        //       for the given encuestadorId and encuestaId where the response is a draft.
        //       You'll likely need a RespuestaDAO for this.
        //       Return 0 if no draft exists.
        // For now, return a placeholder:
        return 0;  // Placeholder
    }

    private int obtenerOCrearRespuestaId(int encuestadorId, int encuestaId) {
        // TODO: Implement this method.
        //       It should check if a respuesta_id exists for the encuestadorId and encuestaId.
        //       If it exists (a draft), return the existing ID.
        //       If it doesn't exist (new response), create a new Respuesta record in the database
        //       and return the generated respuesta_id.  Again, use a RespuestaDAO.
        //       Return 0 if there's an error.
        // For now, return a placeholder:
        return 0; // Placeholder
    }
}