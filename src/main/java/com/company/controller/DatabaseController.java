package com.company.controller;

import com.company.container.DatabaseContainer;
import com.company.entity.*;
import com.company.enums.CarStatus;
import com.company.enums.OrderStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseController {
    public static Boolean userIsAdmin(String email) {
        Boolean isAdmin = null;
        Connection connection = DatabaseContainer.getConnection();
        try {

            String query = "select is_admin from users where is_deleted=false and email= ?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                isAdmin = resultSet.getBoolean("is_admin");
                return isAdmin;

            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isAdmin;
    }

    public static Result addCar(Car car) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String checkCarNumber = "select count(*) from car where car_number=? ";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(checkCarNumber);
            statement.setString(1, car.getCarNumber());
            ResultSet resultSet = statement.executeQuery();
            int countCarByCarNumber = 0;
            if (resultSet.next()) {
                countCarByCarNumber = resultSet.getInt(1);
                resultSet.close();
            }
            if (countCarByCarNumber > 0) {
                statement.close();
                connection.close();
                return new Result("This car number number already exist", false);
            }
            statement.close();

            String insertCar = "insert into car(model, car_number, place_count, color, region_id, year, price_per_day, add_info)values (?,?,?,?,?,?,?,?)";
            PreparedStatement statement1 = Objects.requireNonNull(connection).prepareStatement(insertCar);
            statement1.setString(1, car.getCarModel());
            statement1.setString(2, car.getCarNumber());
            statement1.setInt(3, car.getCountOfPlace());
            statement1.setString(4, car.getCarColor());
            statement1.setInt(5, getRegionIdByName(car.getCarRegion()));
            statement1.setInt(6, car.getCarYear());
            statement1.setString(7, car.getPricePerDay());
            statement1.setString(8, car.getAdditionInfo());
            boolean execute = statement1.execute();
            if (execute) {
                statement1.close();
                connection.close();
                return new Result("Some Error in server", false);
            }
            statement1.close();
            connection.close();

        } catch (SQLException e) {
            return new Result("Error in server", false);
        }
        return new Result("Successfully yAdded", true);
    }

    public static Result addCarImage(Integer carId, String imageUrl) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "insert into photo(url, car_id)  values (?,?)";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setString(1, imageUrl);
            statement.setInt(2, carId);
            boolean execute = statement.execute();
            if (execute) {
                return new Result("Error in db", false);
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Result("Success", true);
    }

    public static Integer getCarId(String carNumber) {
        Integer carId = null;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select id from car where is_deleted=false and  car_number=?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setString(1, carNumber);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                carId = set.getInt(1);
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return carId;
    }

    public static Integer getCarId(Integer orderId) {
        Integer carId = null;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select car_id from order_table where  id=?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setInt(1, orderId);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                carId = set.getInt(1);
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return carId;
    }

    public static List<Users> getAdmins() {
        List<Users> users = new ArrayList<>();
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select * from users where is_deleted=false and is_admin=true";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String first_name = set.getString("first_name");
                String last_name = set.getString("last_name");
                String phone_number = set.getString("phone_number");
                users.add(new Users(first_name, last_name, phone_number));
            }

            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static Result deleteAdmin(String phoneNumber) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "update users set is_admin=false where  phone_number='" + phoneNumber + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            boolean execute = statement.execute(query);
            if (execute) {
                return new Result("Error in db", false);
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            return new Result("Error in server", false);
        }
        return new Result(phoneNumber + " Successfully deleted in admins list", true);
    }

    public static Result addToAdmins(String phoneNumberForAdd) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String checkPhoneNumber = "select count(*) from users where is_deleted=false and phone_number=? ";
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(checkPhoneNumber);
            preparedStatement.setString(1, phoneNumberForAdd);
            ResultSet resultSet = preparedStatement.executeQuery();
            int countUserByPhoneNumber = 0;
            if (resultSet.next()) {
                countUserByPhoneNumber = resultSet.getInt(1);
                resultSet.close();
            }
            if (countUserByPhoneNumber > 0) {
                String query = "update users set is_admin=true where phone_number='" + phoneNumberForAdd + "'";
                Statement statement = Objects.requireNonNull(connection).createStatement();
                boolean execute = statement.execute(query);
                if (execute) {
                    return new Result("Error in db", false);
                }
                statement.close();
            } else {
                return new Result("User not found", false);
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            return new Result("Error in server", false);
        }
        return new Result(phoneNumberForAdd + " Successfully added in admins list", true);
    }

    public static List<String> getImagesByCarNumber(String carNumber) {
        Integer carId = getCarId(carNumber);
        List<String> images = new ArrayList<>();
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select url from photo where is_deleted=false and  car_id='" + carId + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                images.add(set.getString(1));
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return images;
    }

    public static List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select * from car where is_deleted=false";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String model = set.getString("model");
                String car_number = set.getString("car_number");
                Integer place_count = set.getInt("place_count");
                String color = set.getString("color");
                int year = set.getInt("year");
                String price_per_day = set.getString("price_per_day");
                int region_id = set.getInt("region_id");
                String region = getRegionNameById(region_id);
                String add_info = set.getString("add_info");
                CarStatus car_status = CarStatus.valueOf(set.getString("car_status"));
                cars.add(new Car(model, car_number, place_count, color, region, year, price_per_day, add_info, car_status));
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cars;
    }

    public static List<Car> getAllAvailableCars() {
        List<Car> cars = new ArrayList<>();
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select * from car where is_deleted=false and car_status = '" + CarStatus.NOT_ON_RENT + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                String model = set.getString("model");
                String car_number = set.getString("car_number");
                Integer place_count = set.getInt("place_count");
                String color = set.getString("color");
                int year = set.getInt("year");
                String price_per_day = set.getString("price_per_day");
                int region_id = set.getInt("region_id");
                String region = getRegionNameById(region_id);
                String add_info = set.getString("add_info");
                CarStatus car_status = CarStatus.valueOf(set.getString("car_status"));
                boolean canItBeRented = canItBeRented(car_number);
                if (canItBeRented) {
                    cars.add(new Car(model, car_number, place_count, color, region, year, price_per_day, add_info, car_status));
                }
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cars;
    }

    private static boolean canItBeRented(String car_number) {
        Integer carId = getCarId(car_number);
        Integer orderId = getOrderId(carId);
        if (orderId != null) {
            LocalDateTime startTime = getOrderStartTime(orderId);
            if (startTime.equals(LocalDateTime.now()) || startTime.isBefore(LocalDateTime.now().minusHours(22))) {
                updateCarStatus(carId, String.valueOf(CarStatus.IN_RENT));
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public static Result deleteCar(String carNumber) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "update car set is_deleted=true where car_number='" + carNumber + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            boolean execute = statement.execute(query);
            if (execute) {
                return new Result("Error in db", false);
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            return new Result("Error in server", false);
        }
        return new Result(carNumber + " Successfully deleted in cars list", true);
    }

    public static Result updateCarStatus(Integer carId, String carStatus) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "update car set car_status = ? where is_deleted=false and id=?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setString(1, carStatus);
            statement.setInt(2, carId);
            boolean execute = statement.execute();
            if (execute) {
                return new Result("Error in db", false);
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            return new Result("Error in server", false);
        }
        return new Result(" Successfully updated  cars list", true);
    }

    public static Integer getUserId(String sessionUserEmail) {
        Integer userId = null;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select id from users where is_deleted=false and email=?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setString(1, sessionUserEmail);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                userId = set.getInt(1);
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }

    public static Result addOrder(Orders ordersForAdding) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String insert = "insert into order_table(user_id, car_id, start_time, end_time, status, passport, driver_license ,region_id ,total_price) values(?,?,?,?,?,?,?,? ,?)";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(insert);
            statement.setInt(1, ordersForAdding.getUserId());
            statement.setInt(2, ordersForAdding.getCarId());
            statement.setTimestamp(3, Timestamp.valueOf(ordersForAdding.getStartTime()));
            statement.setTimestamp(4, Timestamp.valueOf(ordersForAdding.getEndTime()));
            statement.setString(5, ordersForAdding.getStatus().toString());
            statement.setString(6, ordersForAdding.getPassport());
            statement.setString(7, ordersForAdding.getDriverLicense());
            statement.setInt(8, getRegionIdByName(ordersForAdding.getRegion()));
            statement.setDouble(9, ordersForAdding.getTotalPrice());
            statement.execute();
            statement.close();
            Integer orderId = getOrderId(ordersForAdding.getCarId());
            String insertHistory = "insert into order_history(order_id)values (?)";
            PreparedStatement statementHistory = Objects.requireNonNull(connection).prepareStatement(insertHistory);
            statementHistory.setInt(1, orderId);
            statementHistory.execute();
            statementHistory.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return new Result("Error in server", false);
        }
        return new Result("Order successfully sended to admins\nPlease wait admins answer", true);
    }

    public static Integer getOrderId(Integer carId) {
        Integer orderId = null;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select id from order_table where  is_deleted=false and car_id='" + carId + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            if (set.next()) {
                orderId = set.getInt(1);
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderId;
    }

    public static List<CarOrder> getUserOrders(String auth) {
        Integer userId = getUserId(auth);
        Car car = null;
        Orders order ;
        Timestamp created_time ;

        List<CarOrder> carOrders = new ArrayList<>();

        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select * from order_table where   user_id='" + userId + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            int orderId;
            while (set.next()) {
                int car_id = set.getInt("car_id");
                String queryForCar = "select * from car where id='" + car_id + "'";
                Statement statementCar = Objects.requireNonNull(connection).createStatement();
                ResultSet setCar = statementCar.executeQuery(queryForCar);
                while (setCar.next()) {
                    String model = setCar.getString("model");
                    String carNumber = setCar.getString("car_number");
                    String pricePerHour = setCar.getString("price_per_day");
                    car = new Car(model, carNumber, pricePerHour);
                }
                setCar.close();
                String queryForPhoto = "select url from photo  where car_id='" + car_id + "'";
                Statement statementPhoto = Objects.requireNonNull(connection).createStatement();
                ResultSet setPhoto = statementPhoto.executeQuery(queryForPhoto);
                List<String> urls = new ArrayList<>();
                while (setPhoto.next()) {
                    String url = setPhoto.getString("url");
                    urls.add(url);
                }
                Photo photos = new Photo(urls, car_id);
                setPhoto.close();

                created_time = set.getTimestamp("created_at");
                Timestamp start_time = set.getTimestamp("start_time");
                Timestamp endTime = set.getTimestamp("end_time");
                OrderStatus orderStatus = OrderStatus.valueOf(set.getString("status"));
                String passport = set.getString("passport");
                String driver_license = set.getString("driver_license");
                int region_id = set.getInt("region_id");
                orderId = set.getInt("id");
                double totalPrice = set.getDouble("total_price");
                order = new Orders(userId, car_id, start_time.toLocalDateTime(), endTime.toLocalDateTime(), orderStatus, passport, driver_license, getRegionNameById(region_id), totalPrice);
                carOrders.add(new CarOrder(orderId, car, order, photos, Objects.requireNonNull(created_time).toLocalDateTime(), null));
            }

            set.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return carOrders;
    }

    public static List<CarOrder> getAllUsersOrders() {
        Car car = null;
        Orders order ;
        Timestamp created_time ;

        List<CarOrder> carOrders = new ArrayList<>();

        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select *from order_table ";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            int orderId ;
            while (set.next()) {

                int car_id = set.getInt("car_id");
                String queryForCar = "select * from car where id='" + car_id + "'";
                Statement statementCar = Objects.requireNonNull(connection).createStatement();
                ResultSet setCar = statementCar.executeQuery(queryForCar);
                while (setCar.next()) {
                    String model = setCar.getString("model");
                    String carNumber = setCar.getString("car_number");
                    String pricePerHour = setCar.getString("price_per_day");
                    car = new Car(model, carNumber, pricePerHour);
                }
                setCar.close();
                String queryForPhoto = "select url from photo  where car_id='" + car_id + "'";
                Statement statementPhoto = Objects.requireNonNull(connection).createStatement();
                ResultSet setPhoto = statementPhoto.executeQuery(queryForPhoto);
                List<String> urls = new ArrayList<>();
                while (setPhoto.next()) {
                    String url = setPhoto.getString("url");
                    urls.add(url);
                }
                Photo photos = new Photo(urls, car_id);
                setPhoto.close();
                created_time = set.getTimestamp("created_at");
                Timestamp start_time = set.getTimestamp("start_time");
                Timestamp endTime = set.getTimestamp("end_time");
                OrderStatus orderStatus = OrderStatus.valueOf(set.getString("status"));
                String passport = set.getString("passport");
                String driver_license = set.getString("driver_license");
                int region_id = set.getInt("region_id");
                orderId = set.getInt("id");
                double totalPrice = set.getDouble("total_price");
                int userId = set.getInt("user_id");
                String phoneNumber = getUserPhoneNumberById(userId);
                order = new Orders(userId, car_id, start_time.toLocalDateTime(), endTime.toLocalDateTime(), orderStatus, passport, driver_license, getRegionNameById(region_id), totalPrice);
                carOrders.add(new CarOrder(orderId, car, order, photos, Objects.requireNonNull(created_time).toLocalDateTime(), phoneNumber));
            }

            set.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return carOrders;
    }

    public static Result editOrderStatus(String command, Integer orderId, String rejectCause, String infoPenalty, String pricePenalty) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "UPDATE order_table SET status = ? WHERE id=?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);

            switch (command) {
                case "accept" -> {
                    statement.setString(1, String.valueOf(OrderStatus.ACCEPTED));
                    statement.setInt(2, orderId);
                    statement.executeUpdate();
                    statement.close();
                    connection.close();
                    return new Result(orderId + "-order successfully accepted", true);
                }
                case "reject" -> {
                    String queryHistory = "UPDATE order_history SET info = ? WHERE order_id=?";
                    PreparedStatement statementHistory = Objects.requireNonNull(connection).prepareStatement(queryHistory);
                    statement.setString(1, String.valueOf(OrderStatus.REJECTED));
                    statement.setInt(2, orderId);
                    statementHistory.setString(1, "Reject cause: " + rejectCause);
                    statementHistory.setInt(2, orderId);
                    statement.executeUpdate();
                    statementHistory.executeUpdate();
                    statement.close();
                    statementHistory.close();
                    connection.close();
                    return new Result(orderId + "-order successfully rejected", true);
                }
                case "giveFine" -> {
                    Integer carId = getCarId(orderId);
                    updateCarStatus(carId, String.valueOf(CarStatus.BROKEN));
                    String queryHistory = "UPDATE order_history SET info = ? , is_broken=true, penalty=? WHERE order_id=?";
                    String queryOrder = "UPDATE order_table SET status=?  WHERE id=?";
                    PreparedStatement statementHistory = Objects.requireNonNull(connection).prepareStatement(queryHistory);
                    PreparedStatement statementOrder = Objects.requireNonNull(connection).prepareStatement(queryOrder);
                    statementOrder.setString(1, String.valueOf(OrderStatus.FineIsSetForTheOrDer));
                    statementOrder.setInt(2, orderId);
                    statementHistory.setString(1, "Penalty cause: " + infoPenalty);
                    statementHistory.setString(2, pricePenalty);
                    statementHistory.setInt(3, orderId);
                    statementHistory.executeUpdate();
                    statementOrder.executeUpdate();
                    statementHistory.close();
                    statementOrder.close();
                    connection.close();

                    return new Result("Penalty gave to  " + orderId + "order", true);
                }
                case "finishOrder" -> {
                    statement.setString(1, String.valueOf(OrderStatus.FINISHED));
                    statement.setInt(2, orderId);
                    statement.executeUpdate();
                    statement.close();
                    connection.close();
                    return new Result(orderId + "-order successfully finished", true);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Result("Some error in server", false);
    }

    public static Double getCarPrice(String carNumber) {

        String carPriceString ;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select price_per_day from car where is_deleted=false and car_number=?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setString(1, carNumber);
            ResultSet sets = statement.executeQuery();
            if (sets.next()) {
                carPriceString = sets.getString("price_per_day");
                carPriceString = carPriceString.substring(0, carPriceString.length() - 1);
                sets.close();
                statement.close();
                connection.close();
                return Double.parseDouble(carPriceString);
            }
            sets.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Result userHaveAvailableOrder(String passport, String driverLicense) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select status from order_table where is_deleted=false and passport='" + passport + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            if (set.next()) {
                String status = set.getString(1);
                if (!status.equals(String.valueOf(OrderStatus.COMPLETED))) {
                    set.close();
                    statement.close();
                    connection.close();
                    return new Result("This passport have got not completed order", true);
                }
            }
            set.close();
            statement.close();
            String queryDr = "select status from order_table where is_deleted=false and driver_license='" + driverLicense + "'";
            Statement statementDr = Objects.requireNonNull(connection).createStatement();
            ResultSet setDr = statementDr.executeQuery(queryDr);
            if (setDr.next()) {
                String status = setDr.getString(1);
                if (!status.equals(String.valueOf(OrderStatus.COMPLETED))) {
                    setDr.close();
                    statementDr.close();
                    connection.close();
                    return new Result("This driver licence have got not completed order", true);
                }

            } else {
                return new Result("ok", false);
            }
            setDr.close();
            statementDr.close();
            connection.close();


        } catch (SQLException e) {

            e.printStackTrace();
        }

        return new Result("Some error in server", false);
    }

    public static String getRejectCause(Integer orderId) {
        String info;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select info from order_history where order_id=?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setInt(1, orderId);
            ResultSet sets = statement.executeQuery();
            if (sets.next()) {
                info = sets.getString("info");

                sets.close();
                statement.close();
                connection.close();
                return info;
            }
            sets.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static LocalDateTime getOrderStartTime(Integer orderId) {
        LocalDateTime start_time = null;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select start_time from order_table where id='" + orderId + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            if (set.next()) {
                start_time = set.getTimestamp(1).toLocalDateTime();
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return start_time;
    }

    public static LocalDateTime getOrderEndTime(Integer orderId) {
        LocalDateTime end_time = null;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select end_time from order_table where id='" + orderId + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            if (set.next()) {
                end_time = set.getTimestamp(1).toLocalDateTime();
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return end_time;
    }

    public static List<String> getUserCards(Integer userId) {
        List<String> cards = new ArrayList<>();
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select card_number from card where user_id=? and is_deleted=false";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                cards.add(set.getString(1));
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cards;
    }

    public static double getBalance(int card) {
        double balance = 0;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select balance from card where is_deleted=false and id= ?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setInt(1, card);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                balance = resultSet.getDouble("balance");

            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    public static Result payToOrder(int card, double balance, int orderId) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "update card set balance = ? where id=?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setDouble(1, balance);
            statement.setInt(2, card);
            String query2 = "update order_table set status= ? where is_deleted=false and id=?";
            PreparedStatement statement2 = Objects.requireNonNull(connection).prepareStatement(query2);
            statement2.setString(1, String.valueOf(OrderStatus.Payed));
            statement2.setInt(2, orderId);
            statement2.executeUpdate();
            boolean execute = statement.execute();
            if (execute) {
                return new Result("Error in db", false);
            }
            statement2.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            return new Result("Error in server", false);
        }
        return new Result(" Successfully payed", true);
    }

    public static Result addCard(String cardNumber, Double cardBalance, String sessionUserEmail) {
        Integer userId = getUserId(sessionUserEmail);
        Connection connection = DatabaseContainer.getConnection();
        try {
            String checkCarNumber = "select count(*) from card where is_deleted=false and card_number=? ";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(checkCarNumber);
            statement.setString(1, cardNumber);
            ResultSet resultSet = statement.executeQuery();
            int countCardByCardNumber = 0;
            if (resultSet.next()) {
                countCardByCardNumber = resultSet.getInt(1);
                resultSet.close();
            }
            if (countCardByCardNumber > 0) {
                statement.close();
                connection.close();
                return new Result("This card number number already exist", false);
            }
            statement.close();

            String insertCar = "insert into card(user_id,card_number,balance)values (?,?,?)";
            PreparedStatement statement1 = Objects.requireNonNull(connection).prepareStatement(insertCar);
            String first = cardNumber.substring(0, 4);
            String middle = " **** **** ";
            String end = cardNumber.substring(12);
            cardNumber = first.concat(middle).concat(end);
            statement1.setInt(1, userId);
            statement1.setString(2, cardNumber);
            statement1.setDouble(3, cardBalance);

            boolean execute = statement1.execute();
            if (execute) {
                statement1.close();
                connection.close();
                return new Result("Some Error in server", false);
            }
            statement1.close();
            connection.close();

        } catch (SQLException e) {
            return new Result("Error in server", false);
        }
        return new Result("Card successfully Added", true);

    }

    public static Result deleteCard(String cardNumberForDelete) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "update card set is_deleted =true  where id='" + cardNumberForDelete + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            boolean execute = statement.execute(query);
            if (execute) {
                return new Result("Error in db", false);
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            return new Result("Error in server", false);
        }
        return new Result("card successfully deleted ", true);

    }

    public static void compiledOrder(int orderId) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query2 = "update order_table set status= ? where is_deleted=false and id=?";
            PreparedStatement statement2 = Objects.requireNonNull(connection).prepareStatement(query2);
            statement2.setString(1, String.valueOf(OrderStatus.COMPLETED));
            statement2.setInt(2, orderId);
            statement2.executeUpdate();
            statement2.close();
            String query = "update order_table set is_deleted=true  where id='" + orderId + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            statement.execute(query);
            statement.close();
            connection.close();
            updateCarStatus(getCarId(orderId), String.valueOf(CarStatus.NOT_ON_RENT));
        } catch (SQLException ignored) {
        }

    }

    public static int getCardId(String card) {
        int cardId = 0;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select id from card where is_deleted=false and  card_number=?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setString(1, card);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                cardId = set.getInt(1);
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cardId;
    }

    public static List<Penalty> getUsersOrdersPenalties(Integer userId) {
        List<Penalty> penalties = new ArrayList<>();

        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select order_history.penalty ,order_history.info , c.model , c.car_number , ot.id  from order_history" +
                    " join order_table ot on ot.id = order_history.order_id" +
                    " join car c on c.id = ot.car_id where order_history.is_broken=true and penalty_is_payed=false and ot.user_id='" + userId + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);

            while (set.next()) {
                int order_id = set.getInt("id");

                String amount = set.getString("penalty");
                String info = set.getString("info");
                String model = set.getString("model");
                String car_number = set.getString("car_number");
                Penalty penalty = new Penalty(order_id, userId, model, car_number, info, amount);
                penalties.add(penalty);
            }
            set.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return penalties;

    }

    public static Result payToPenalty(String orderId, String cardIdForPay, String penaltyAmount) {


        Connection connection = DatabaseContainer.getConnection();
        try {

            String query = "update order_history set  penalty_is_payed= true where is_deleted=false and order_id='" + orderId + "'";
            String query2 = "update card set  balance=balance-? where is_deleted=false and id=?";
            String query3 = "UPDATE order_table SET status = ? WHERE id=?";
            PreparedStatement statement3 = Objects.requireNonNull(connection).prepareStatement(query3);
            Statement statement = Objects.requireNonNull(connection).createStatement();
            PreparedStatement statement2 = Objects.requireNonNull(connection).prepareStatement(query2);
            statement2.setDouble(1, Double.parseDouble(penaltyAmount.substring(0, penaltyAmount.length() - 1)));
            statement2.setInt(2, Integer.parseInt(cardIdForPay));
            statement3.setString(1, String.valueOf(OrderStatus.FinePayment));
            statement3.setInt(2, Integer.parseInt(orderId));
            statement3.executeUpdate();
            statement3.close();
            boolean execute = statement.execute(query);
            boolean execute1 = statement2.execute();
            if (execute || execute1) {
                return new Result("Some error in server", false);
            }


            statement.close();
            connection.close();

        } catch (SQLException e) {

            e.printStackTrace();
            return new Result("Some error in server", false);
        }
        return new Result("ok ", true);
    }

    public Result registerUser(Users user) {
        Connection connection = DatabaseContainer.getConnection();
        try {
            String checkPhoneNumber = "select count(*) from users where is_deleted=false and phone_number=? ";
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(checkPhoneNumber);
            preparedStatement.setString(1, user.getPhoneNumber());
            ResultSet resultSet = preparedStatement.executeQuery();
            int countUserByPhoneNumber = 0;
            int countUserByUsername = 0;
            if (resultSet.next()) {
                countUserByPhoneNumber = resultSet.getInt(1);
                resultSet.close();
            }
            if (countUserByPhoneNumber > 0) {
                preparedStatement.close();
                connection.close();
                return new Result("Phone number already exist", false);
            }
            preparedStatement.close();
            String checkUserName = "select count(*) from users where is_deleted=false and email=? ";
            PreparedStatement preparedStatement1 = connection.prepareStatement(checkUserName);
            preparedStatement1.setString(1, user.getEmail());
            ResultSet resultSetUserName = preparedStatement1.executeQuery();
            if (resultSetUserName.next()) {
                countUserByUsername = resultSetUserName.getInt(1);
                resultSetUserName.close();
            }
            if (countUserByUsername > 0) {
                preparedStatement1.close();
                connection.close();
                return new Result("User name  already exist", false);
            }
            preparedStatement1.close();

            String insertUser = "insert into users(first_name, last_name, phone_number, email, address, password)values (?,?,?,?,?,?)";
            PreparedStatement preparedStatement2 = connection.prepareStatement(insertUser);
            preparedStatement2.setString(1, user.getFirstName());
            preparedStatement2.setString(2, user.getLastName());
            preparedStatement2.setString(3, user.getPhoneNumber());
            preparedStatement2.setString(4, user.getEmail());
            preparedStatement2.setString(5, user.getAddress());
            preparedStatement2.setString(6, user.getPassword());
            preparedStatement2.execute();
            preparedStatement2.close();
            connection.close();

        } catch (SQLException e) {

            return new Result("Error in server", false);
        }
        return new Result("Successfully registered", true);

    }

    public Users loginUser(String email, String password) {
        Users user = new Users();
        Connection connection = DatabaseContainer.getConnection();
        try {

            String query = "select * from users where is_deleted=false and email= ?";
            PreparedStatement statement = Objects.requireNonNull(connection).prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                user.setEmail(email);
                if (password.equals(resultSet.getString("password"))) {
                    user.setFirstName(resultSet.getString("first_name"));
                    return user;
                }
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    private static String getUserPhoneNumberById(int userId) {
        String phoneNumber = null;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select phone_number from users where is_deleted=false and id='" + userId + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            if (set.next()) {
                phoneNumber = set.getString(1);
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return phoneNumber;
    }

    public static List<String> getRegions() {
        List<String> regions = new ArrayList<>();
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select name from region ";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            while (set.next()) {
                regions.add(set.getString(1));
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return regions;
    }

    private static String getRegionNameById(int region_id) {
        String region = null;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select name from region where id='" + region_id + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            if (set.next()) {
                region = set.getString(1);
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return region;
    }

    public static Integer getRegionIdByName(String regionName) {
        Integer regionId = null;
        Connection connection = DatabaseContainer.getConnection();
        try {
            String query = "select id from region where name='" + regionName + "'";
            Statement statement = Objects.requireNonNull(connection).createStatement();
            ResultSet set = statement.executeQuery(query);
            if (set.next()) {
                regionId = set.getInt(1);
            }
            set.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return regionId;
    }
}

