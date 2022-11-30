package com.company.service.userServiceServlets;

import com.company.controller.DatabaseController;
import com.company.entity.Penalty;
import com.company.entity.Result;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@WebServlet("/Penalties")
public class WorkWithPenalties extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        sendAttribute(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String penaltyAmount = req.getParameter("penaltyAmount");
        String cardIdForPay = req.getParameter("cardIdForPay");
        String orderId = req.getParameter("orderId");
        Result result=DatabaseController.payToPenalty(orderId,cardIdForPay,penaltyAmount);
        if (!Objects.requireNonNull(result).isSuccess()){
            req.setAttribute("userPenaltiesError", result.getMessage());
        }
        sendAttribute(req, resp);
    }

    private void sendAttribute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionUser = String.valueOf(req.getSession().getAttribute("user"));
        Integer userId = DatabaseController.getUserId(sessionUser);
        List<Penalty> penalties = DatabaseController.getUsersOrdersPenalties(userId);
        req.setAttribute("userPenalties", penalties);
        req.getRequestDispatcher("/usersFrontend/penalties.jsp").forward(req,resp);
    }
}
