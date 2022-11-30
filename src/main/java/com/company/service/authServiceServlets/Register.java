package com.company.service.authServiceServlets;


import com.company.controller.DatabaseController;
import com.company.controller.InputController;
import com.company.entity.Result;
import com.company.entity.Users;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/register")
public class Register extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getRequestDispatcher("/authFrontend/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String phoneNumber = req.getParameter("phoneNumber");
        if (!phoneNumber.matches("\\d{9}")) {
            String errorMessage = "Number don't much";
            req.setAttribute("errorRegister", errorMessage);
            req.getRequestDispatcher("/authFrontend/register.jsp").forward(req, resp);
        } else {
            String email = req.getParameter("email");
            String region = req.getParameter("region");
            String address = req.getParameter("address");
            String password = req.getParameter("password");
            String conPassword = req.getParameter("conPassword");
            List<String> values = List.of(firstName, lastName, phoneNumber, email, region, address, password, conPassword);
            if (InputController.isEmptyInput(values)) {
                String errorMessage = "Some fields is empty";
                req.setAttribute("errorRegister", errorMessage);
                req.getRequestDispatcher("/authFrontend/register.jsp").forward(req, resp);
            } else {
                if (password.equals(conPassword)) {
                    DatabaseController databaseController = new DatabaseController();
                    Result result = databaseController.registerUser(new Users(firstName, lastName, phoneNumber, email, region, address, password));

                    if (result.isSuccess()) {
                        HttpSession session = req.getSession();
                        session.setAttribute("user", email);
                        session.setMaxInactiveInterval(3600);
                        req.getRequestDispatcher("/usersFrontend/userMainPage.jsp").forward(req, resp);
                    } else {
                        String errorMessage = result.getMessage();
                        req.setAttribute("errorRegister", errorMessage);
                        req.getRequestDispatcher("/authFrontend/register.jsp").forward(req, resp);
                    }
                } else {
                    String errorMessage = "Password's don't match!";
                    req.setAttribute("errorRegister", errorMessage);
                    req.getRequestDispatcher("/authFrontend/register.jsp").forward(req, resp);
                }
            }
        }
    }
}
