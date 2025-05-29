package org.example.onu_mujeres_crud.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.example.onu_mujeres_crud.beans.Usuario;
import org.example.onu_mujeres_crud.daos.CoordinadorDao;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "CoordinadorServlet", value = "/CoordinadorServlet")
public class CoordinadorServlet extends HttpServlet {
    CoordinadorDao coordinadorDAO = new CoordinadorDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action") == null ? "lista" : request.getParameter("action");
        RequestDispatcher view;

        switch (action) {
            case "lista":
                ArrayList<Usuario> listaEncuestadores = coordinadorDAO.listarEncuestadores();
                request.setAttribute("listaEncuestadores", listaEncuestadores);
                view = request.getRequestDispatcher("Coordinador/VistaListaEncuestador.jsp");
                view.forward(request, response);
                break;

            case "ver":
                int idVer = Integer.parseInt(request.getParameter("id"));
                Usuario encuestador = coordinadorDAO.obtenerEncuestadorPorId(idVer);
                request.setAttribute("encuestador", encuestador);
                view = request.getRequestDispatcher("Coordinador/verEncuestador.jsp");
                view.forward(request, response);
                break;

            case "estado":
                int idEstado = Integer.parseInt(request.getParameter("id"));
                String estadoActual = request.getParameter("estado");
                String nuevoEstado = estadoActual.equals("activo") ? "inactivo" : "activo";
                coordinadorDAO.cambiarEstadoEncuestador(idEstado, nuevoEstado);
                response.sendRedirect("CoordinadorServlet?action=lista");
                break;

            case "asignarFormulario":
                int idAsignar = Integer.parseInt(request.getParameter("id"));
                request.setAttribute("idEncuestador", idAsignar);
                view = request.getRequestDispatcher("Coordinador/asignarFormulario.jsp");
                view.forward(request, response);
                break;

            default:
                response.sendRedirect("CoordinadorServlet?action=lista");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("guardarAsignacion".equals(action)) {
            int encuestadorId = Integer.parseInt(request.getParameter("idEncuestador"));
            String nombreEncuesta = request.getParameter("nombreEncuesta");
            String carpeta = request.getParameter("carpeta");

            // En este ejemplo el coordinador que asigna es el ID 1 (en un caso real se obtiene del session)
            int coordinadorId = 1;

            coordinadorDAO.asignarEncuesta(nombreEncuesta, carpeta, encuestadorId, coordinadorId);
            response.sendRedirect("CoordinadorServlet?action=lista");
        }
    }
}
