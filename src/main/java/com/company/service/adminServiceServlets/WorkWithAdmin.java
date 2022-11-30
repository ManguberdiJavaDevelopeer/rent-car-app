package com.company.service.adminServiceServlets;

import com.company.controller.DatabaseController;
import com.company.entity.Result;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/WWADMIN")
public class WorkWithAdmin extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/adminsFrontend/workWithAdmin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String phoneNumber = req.getParameter("phoneNumber");
        if (phoneNumber != null) {
            Result result = DatabaseController.deleteAdmin(phoneNumber);
            req.setAttribute("isDeleted", result.getMessage());
            req.getRequestDispatcher("/adminsFrontend/workWithAdmin.jsp").forward(req, resp);
        }
        String phoneNumberForAdd = req.getParameter("phoneNumberForAdd");
        if (phoneNumberForAdd != null) {
            Result result = DatabaseController.addToAdmins(phoneNumberForAdd);
            req.setAttribute("isDeleted", result.getMessage());
            req.getRequestDispatcher("/adminsFrontend/workWithAdmin.jsp").forward(req, resp);
        }
    }

}
