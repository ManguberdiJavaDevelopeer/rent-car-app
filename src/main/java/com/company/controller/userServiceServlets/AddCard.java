package com.company.controller.userServiceServlets;

import com.company.controller.DatabaseController;

import com.company.entity.Result;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/addCard")
public class AddCard extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/usersFrontend/addCard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cardNumber = req.getParameter("cardNumber");
        String cardBalance = req.getParameter("cardBalance");
        String sessionUserEmail = (String) req.getSession().getAttribute("user");
        String cardNumberForDelete = req.getParameter("cardNumberForDelete");


        if (cardNumberForDelete != null) {
            Result result = DatabaseController.deleteCard(cardNumberForDelete);
            req.setAttribute("cardStatus", result.getMessage());
            req.getRequestDispatcher("/usersFrontend/addCard.jsp").forward(req, resp);
        } else if (cardNumber != null && cardBalance != null && cardNumber.length() == 16) {
            if (!cardBalance.matches("[0-9]*\\.[0-9]+\\$")) {
                String errorMessage = " Balance don't match ";
                req.setAttribute("cardStatus", errorMessage);
                req.getRequestDispatcher("/usersFrontend/addCard.jsp").forward(req, resp);
            } else {
                cardBalance = cardBalance.substring(0, cardBalance.indexOf("$"));

                Result result = DatabaseController.addCard(cardNumber, Double.valueOf(cardBalance), sessionUserEmail);
                req.setAttribute("cardStatus", result.getMessage());
                if (result.isSuccess()) {
                    req.getRequestDispatcher("/usersFrontend/userMainPage.jsp").forward(req, resp);
                } else {
                    req.getRequestDispatcher("/usersFrontend/addCard.jsp").forward(req, resp);
                }
            }
        } else {
            req.setAttribute("cardStatus", " Wrong card");
            req.getRequestDispatcher("/usersFrontend/addCard.jsp").forward(req, resp);
        }

    }
}

