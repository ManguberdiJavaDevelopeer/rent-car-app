package com.company.service.adminServiceServlets;

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

@WebServlet("/showOrders")
public class AdminShowAllOrders extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CarOrder> orders = DatabaseController.getAllUsersOrders();
        req.setAttribute("allOrders", orders);
        req.getRequestDispatcher("/adminsFrontend/adminShowAllOrders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String command = req.getParameter("command");
        Integer orderId = Integer.valueOf(req.getParameter("orderId"));
        String rejectCause = req.getParameter("rejectCause");
        String infoPenalty = req.getParameter("infoPenalty");
        String pricePenalty = req.getParameter("pricePenalty");

        Result result = DatabaseController.editOrderStatus(command, orderId ,rejectCause,infoPenalty,pricePenalty);
        List<CarOrder> orders = DatabaseController.getAllUsersOrders();
        req.setAttribute("editOrderStatus", result.getMessage());
        req.setAttribute("allOrders", orders);
        req.getRequestDispatcher("/adminsFrontend/adminShowAllOrders.jsp").forward(req, resp);

    }
}
