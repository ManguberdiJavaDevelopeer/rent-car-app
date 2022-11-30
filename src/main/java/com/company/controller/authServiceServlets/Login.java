package com.company.controller.authServiceServlets;

import com.company.controller.DatabaseController;
import com.company.entity.Users;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(value = "/login")
public class Login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/authFrontend/login.jsp").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        DatabaseController databaseController = new DatabaseController();

        Users users = databaseController.loginUser(email, password);

        if (users.getEmail() == null) {
           String errorMessage="Email or username error";
            req.setAttribute("error",errorMessage);
            req.getRequestDispatcher("/authFrontend/login.jsp").forward(req,resp);
        } else if (users.getFirstName() == null) {
           String errorMessage="Password error ";
            req.setAttribute("error",errorMessage);
        req.getRequestDispatcher("/authFrontend/login.jsp").forward(req,resp);
        } else {
            HttpSession session=req.getSession();
            Boolean isAdmin= DatabaseController.userIsAdmin(users.getEmail());
            if (Boolean.TRUE.equals(isAdmin)) {
                if (session.getAttribute("user")!=null){
                    session.setAttribute("user",null);
                }
                session.setAttribute("admin",users.getEmail());
                session.setMaxInactiveInterval(3600);
                resp.sendRedirect("/adminCabinet");

            }
            else if (Boolean.FALSE.equals(isAdmin)){
                if (session.getAttribute("admin")!=null){
                    session.setAttribute("admin" , null);
                }
                session.setAttribute("user",users.getEmail());
                session.setMaxInactiveInterval(3600);
                resp.sendRedirect("/userCabinet");

            }
            else {
                session.invalidate();
                String errorMessage="Some Error with server ";
                req.setAttribute("error",errorMessage);
                req.getRequestDispatcher("/authFrontend/login.jsp").forward(req,resp);
            }
        }

    }
}
