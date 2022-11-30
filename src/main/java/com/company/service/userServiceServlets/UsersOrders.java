package com.company.service.userServiceServlets;

import com.company.controller.DatabaseController;
import com.company.entity.CarOrder;
import com.company.entity.Result;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/MyOrders")
public class UsersOrders extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CarOrder> orders= DatabaseController.getUserOrders(String.valueOf(req.getSession().getAttribute("user")));

        req.setAttribute("myOrders",orders);
        req.getRequestDispatcher("/usersFrontend/UserOrders.jsp").forward(req,resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int orderId = Integer.parseInt(req.getParameter("orderId"));
        double totalPrice= Double.parseDouble(req.getParameter("totalPrice"));



        String command = req.getParameter("command");
        switch (command) {
            case "payed" -> {
                int card = Integer.parseInt(req.getParameter("cardIdForPay"));
                double balance = DatabaseController.getBalance(card);
                if (balance >= totalPrice) {
                    Result result = DatabaseController.payToOrder(card, balance - totalPrice, orderId);
                    req.setAttribute("editOrderStatus", result.getMessage());
                } else {
                    req.setAttribute("editOrderStatus", "You don't have enough money in your account");
                }
            }
            case "addCard" -> req.getRequestDispatcher("/usersFrontend/addCard.jsp").forward(req, resp);
            case "completed" -> DatabaseController.compiledOrder(orderId);
        }

        List<CarOrder> orders= DatabaseController.getUserOrders(String.valueOf(req.getSession().getAttribute("user")));
        req.setAttribute("myOrders",orders);
        req.getRequestDispatcher("/usersFrontend/UserOrders.jsp").forward(req,resp);

    }
}
