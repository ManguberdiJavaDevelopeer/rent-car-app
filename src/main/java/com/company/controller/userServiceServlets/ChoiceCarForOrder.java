package com.company.controller.userServiceServlets;

import com.company.controller.DatabaseController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/carsShow")
public class ChoiceCarForOrder extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         req.setAttribute("cars" , DatabaseController.getAllAvailableCars());
        req.getRequestDispatcher("/usersFrontend/ChoiceCarForOrder.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String carNumber = req.getParameter("choiceCarNumber");
        req.setAttribute("choiceCar",carNumber);
        req.getRequestDispatcher("usersFrontend/getOrderDetail.jsp").forward(req, resp);

    }
}
