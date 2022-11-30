package com.company.service.adminServiceServlets;

import com.company.controller.DatabaseController;
import com.company.entity.Result;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@WebServlet("/FileUploadServlet")
@MultipartConfig

public class FileUploadServlet extends HttpServlet {
    static Integer carId = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("addPhoto", "add");
        req.getRequestDispatcher("/adminsFrontend/adminAddCarPhoto.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("addPhoto")!=null) {
            String carNumber = request.getParameter("carNumber");
            if (!carNumber.matches("\\d\\d[a-zA-Z]\\d\\d\\d[a-zA-Z][a-zA-Z]")) {
                String errorMessage = "Car number don't much ";
                request.setAttribute("errorAddCar", errorMessage);
                request.getRequestDispatcher("/adminsFrontend/adminAddCarPhoto.jsp").forward(request, response);
            } else {
                carId = DatabaseController.getCarId(carNumber);
                if (carId == null) {
                    String errorMessage = "Car not found";
                    request.setAttribute("errorAddCar", errorMessage);
                    request.getRequestDispatcher("/adminsFrontend/adminAddCarPhoto.jsp").forward(request, response);
                } else {
                    for (Part p : request.getParts()) {
                        System.out.println(p);
                        if (p != null) {
                            p.getSubmittedFileName();
                            System.out.println(p.getSubmittedFileName());
                            if (p.getSubmittedFileName() != null && (!p.getSubmittedFileName().isBlank()) && (!p.getSubmittedFileName().isEmpty())) {
                                String imageName = p.getSubmittedFileName();
                                String uuid = String.valueOf(UUID.randomUUID());
                                String imageDbName = imageName.substring(0,imageName.lastIndexOf(".")) + uuid + imageName.substring(imageName.lastIndexOf("."));
                                String uploadPath = "D:/Javalessons/Java Advanced Lesson/rent_car-app/web/carPhotos/" + imageDbName;
                                Result result = DatabaseController.addCarImage(carId, uploadPath);
                                if (Objects.requireNonNull(result).isSuccess()) {
                                    FileOutputStream stream = new FileOutputStream(uploadPath);
                                    InputStream inputStream = p.getInputStream();
                                    byte[] bytes = new byte[inputStream.available()];
                                     inputStream.read(bytes);
                                    inputStream.close();
                                    stream.write(bytes);
                                    stream.close();
                                    session.setAttribute("addPhoto", null);
                                    request.setAttribute("successAddCarPhoto", "Car successfully added");
                                    request.getRequestDispatcher("/adminsFrontend/adminMainPage.jsp").forward(request, response);
                                } else {
                                    String errorMessage = result.getMessage();
                                    request.setAttribute("errorAddCar", errorMessage);
                                    request.getRequestDispatcher("/adminsFrontend/adminAddCarPhoto.jsp").forward(request, response);
                                }

                            }
                        }
                    }
                }
            }
        }else {
            request.getRequestDispatcher("adminMainPage.jsp").forward(request, response);
        }

    }
}
