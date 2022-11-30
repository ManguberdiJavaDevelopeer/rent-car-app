package com.company.controller.adminServiceServlets;


import com.company.controller.DatabaseController;
import com.company.entity.Car;
import com.company.entity.Result;
import com.company.enums.CarStatus;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/addCar")


public class AddCar extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            req.getRequestDispatcher("/adminsFrontend/adminAddCar.jsp").forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String carModel = req.getParameter("carModel");
        String carNumber = req.getParameter("carNumber");
        Integer countOfPlace = Integer.valueOf(req.getParameter("countOfPlace"));
        String carColor = req.getParameter("carColor");
        String carRegion = req.getParameter("carRegion");
        int carYear = Integer.parseInt(req.getParameter("carYear"));
        String pricePerDay = req.getParameter("pricePerDay");
        String additionInfo = req.getParameter("additionInfo");
        boolean infoTrue=true;
        List<String> values=  List.of(carModel,carNumber,carColor,String.valueOf(carYear),String.valueOf( countOfPlace),carRegion,pricePerDay);

        if (isEmptyInput(values)){
            String errorMessage = "Some fields is empty";
            req.setAttribute("errorAddCar", errorMessage);
            req.getRequestDispatcher("/adminsFrontend/adminAddCar.jsp").forward(req, resp);
        }else {
            if (!carNumber.matches("\\d\\d[a-zA-Z]\\d\\d\\d[a-zA-Z][a-zA-Z]")) {
                String errorMessage = "Car number don't much ";
                req.setAttribute("errorAddCar", errorMessage);
                req.getRequestDispatcher("/adminsFrontend/adminAddCar.jsp").forward(req, resp);
                infoTrue = false;
            }
            if (carYear > LocalDate.now().getYear()) {
                String errorMessage = "Car year don't much ";
                req.setAttribute("errorAddCar", errorMessage);
                req.getRequestDispatcher("/adminsFrontend/adminAddCar.jsp").forward(req, resp);
                infoTrue = false;
            }
            if (!pricePerDay.matches("[0-9]*\\.[0-9]+\\$")) {
                String errorMessage = "Price dont much don't much ";
                req.setAttribute("errorAddCar", errorMessage);
                req.getRequestDispatcher("/adminsFrontend/adminAddCar.jsp").forward(req, resp);
                infoTrue = false;
            }
            if (infoTrue) {
                Car car = new Car(carModel, carNumber, countOfPlace, carColor, carRegion, carYear, pricePerDay, additionInfo, CarStatus.NOT_ON_RENT);
                Result result = DatabaseController.addCar(car);
                if (result.isSuccess()) {
                    resp.sendRedirect("/FileUploadServlet");
                } else {
                    String errorMessage = result.getMessage();
                    req.setAttribute("errorAddCar", errorMessage);
                    req.getRequestDispatcher("/adminsFrontend/adminAddCar.jsp").forward(req, resp);
                }
            }
        }

    }
    private static boolean isEmptyInput(List<String> values){
        for (String value : values) {
            if (value.isEmpty() || value.isBlank()) {
                return true;
            }
        }
        return false;
    }

}

