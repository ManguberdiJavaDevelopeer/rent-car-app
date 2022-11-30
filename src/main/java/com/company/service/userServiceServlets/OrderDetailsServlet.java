package com.company.service.userServiceServlets;

import com.company.controller.DatabaseController;
import com.company.entity.Orders;
import com.company.entity.Result;
import com.company.enums.OrderStatus;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Objects;

@WebServlet("/order_detail")
public class OrderDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/usersFrontend/getOrderDetail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String passport = req.getParameter("passport");
        String carNumber = req.getParameter("car_number");
        if (passport != null && passport.matches("[a-zA-Z][a-zA-Z]\\d\\d\\d\\d\\d\\d\\d")) {
            String driverLicense = req.getParameter("driverLicense");
            if (driverLicense.matches("[a-zA-Z][a-zA-Z]\\d\\d\\d\\d\\d\\d\\d")) {
                String startDate = req.getParameter("startDate");
                startDate = startDate.trim();
                startDate = startDate.concat(" 00:00:00");
                String[] s = startDate.split(" ");
                String t = s[0].concat("T").concat(s[1]);

                if (isValidDate(startDate)) {
                    LocalDateTime start_date = LocalDateTime.parse(t);
                    String endDate = req.getParameter("endDate");
                    endDate=endDate.trim();
                    endDate = endDate.concat(" 00:00:00");
                    String[] end = endDate.split(" ");
                    String ent = end[0].concat("T").concat(end[1]);

                    if (isValidDate(endDate) ) {
                        LocalDateTime end_date = LocalDateTime.parse(ent);
                        if (start_date.isBefore(end_date) && (start_date.isAfter(LocalDateTime.now().minusDays(1))) && start_date.getYear() == LocalDateTime.now().getYear()
                                && end_date.getYear() == LocalDateTime.now().getYear()) {
                            Result resultAvailableOrder =DatabaseController.userHaveAvailableOrder(passport,driverLicense);
                            if (Objects.requireNonNull(resultAvailableOrder).isSuccess()){
                                req.setAttribute("choiceCar",carNumber);
                                req.setAttribute("detailError", resultAvailableOrder.getMessage());
                                req.getRequestDispatcher("/usersFrontend/getOrderDetail.jsp").forward(req, resp);
                            }
                            else {
                                Result resultRenting=timeCalculator(carNumber,start_date ,end_date);
                                if (resultRenting.isSuccess()){
                                String sessionUserEmail = (String) req.getSession().getAttribute("user");
                                String region = req.getParameter("region");
                                Double carPrice = DatabaseController.getCarPrice(carNumber);
                                int i = end_date.getDayOfYear() - start_date.getDayOfYear();
                                Double totalPrice = i * Objects.requireNonNull(carPrice);
                                Orders orders = new Orders(DatabaseController.getUserId(sessionUserEmail), DatabaseController.getCarId(carNumber), start_date, end_date, OrderStatus.REQUESTED, passport, driverLicense, region, totalPrice);
                                Result result = DatabaseController.addOrder(orders);
                                req.setAttribute("detailError", result.getMessage());
                                if (result.isSuccess()) {
                                    resp.sendRedirect("/MyOrders");
                                } else {

                                    req.setAttribute("choiceCar",carNumber);
                                    req.getRequestDispatcher("/usersFrontend/getOrderDetail.jsp").forward(req, resp);
                                }
                                }
                                else {
                                    req.setAttribute("choiceCar",carNumber);
                                    req.setAttribute("detailError", resultRenting.getMessage());
                                    req.getRequestDispatcher("/usersFrontend/getOrderDetail.jsp").forward(req, resp);
                                }
                            }
                        } else {
                            req.setAttribute("choiceCar",carNumber);
                            req.setAttribute("detailError", " Dates don't match");
                            req.getRequestDispatcher("/usersFrontend/getOrderDetail.jsp").forward(req, resp);
                        }

                    } else {
                        req.setAttribute("choiceCar",carNumber);
                        req.setAttribute("detailError", "End date don't match");
                        req.getRequestDispatcher("/usersFrontend/getOrderDetail.jsp").forward(req, resp);
                    }


                } else {
                    req.setAttribute("choiceCar",carNumber);
                    req.setAttribute("detailError", "Start date don't match");
                    req.getRequestDispatcher("/usersFrontend/getOrderDetail.jsp").forward(req, resp);
                }
            } else {
                req.setAttribute("choiceCar",carNumber);
                req.setAttribute("detailError", "Driver license don't match");
                req.getRequestDispatcher("/usersFrontend/getOrderDetail.jsp").forward(req, resp);
            }
        } else {
            req.setAttribute("choiceCar",carNumber);
            req.setAttribute("detailError", "Passport don't match");
            req.getRequestDispatcher("/usersFrontend/getOrderDetail.jsp").forward(req, resp);
        }

    }

    private Result timeCalculator(String carNumber, LocalDateTime start_date, LocalDateTime end_date) {

        Integer carId = DatabaseController.getCarId(carNumber);
        Integer orderId = DatabaseController.getOrderId(carId);
        if (orderId!=null){
            LocalDateTime orderEndTime = DatabaseController.getOrderEndTime(orderId);
            LocalDateTime orderStartTime =DatabaseController.getOrderStartTime(orderId);
            if (start_date.isAfter(orderEndTime)|| orderStartTime.isAfter(end_date)){
                return  new Result("ok", true);
            }
            else return new Result("This car is busy during this interval",false);
        }

        else return new Result("ok",true);

    }

    static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static boolean isValidDate(String input) {
        try {
            if ( input.length()==19) {
                format.setLenient(false);
                format.parse(input);
                return true;
            }
            return false;
        } catch (ParseException e) {
            return false;
        }
    }
}
