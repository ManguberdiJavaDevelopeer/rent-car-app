package com.company.service.adminServiceServlets;

import com.company.controller.DatabaseController;
import com.company.entity.Result;
import com.company.enums.CarStatus;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/showAllCars")
public class WorkWithCars extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         req.setAttribute("cars" , DatabaseController.getAllCars());
        req.getRequestDispatcher("/adminsFrontend/workWithCars.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String carNumber = req.getParameter("deleteCarNumber");
        String command = req.getParameter("command");
        if (command.equals("delete")) {
            Result result = DatabaseController.deleteCar(carNumber);
            req.setAttribute("cars" , DatabaseController.getAllCars());
            req.setAttribute("isDeleted", result.getMessage());
            req.getRequestDispatcher("/adminsFrontend/workWithCars.jsp").forward(req, resp);


        }
        else if (command.equals("repair")){
            Result result = DatabaseController.updateCarStatus(DatabaseController.getCarId(carNumber), String.valueOf(CarStatus.NOT_ON_RENT));
            req.setAttribute("isDeleted", result.getMessage());
            req.setAttribute("cars" , DatabaseController.getAllCars());
            req.getRequestDispatcher("/adminsFrontend/workWithCars.jsp").forward(req, resp);
        }
    }
}
