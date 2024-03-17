package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAOImpl implements CarDAO {
    private final Connection conn;

    public CarDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void createCar(String name, int companyId) {
        String sql = "INSERT INTO CAR (NAME, COMPANY_ID) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, companyId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Car> getAllCarsByCompanyId(int companyId) {
        List<Car> carList = new ArrayList<>();
        String sql = "SELECT * FROM CAR WHERE COMPANY_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Car car = new Car(rs.getInt("ID"), rs.getString("NAME"), companyId);
                carList.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carList;
    }

    @Override
    public List<Car> getAvailableCarsByCompanyId(int companyId) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM CAR WHERE COMPANY_ID = ? AND ID NOT IN (SELECT RENTED_CAR_ID FROM CUSTOMER WHERE RENTED_CAR_ID IS NOT NULL)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cars.add(new Car(rs.getInt("ID"), rs.getString("NAME"), companyId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    @Override
    public Car getCarById(int carId) {
        String sql = "SELECT * FROM CAR WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Car(
                        rs.getInt("ID"),
                        rs.getString("NAME"),
                        rs.getInt("COMPANY_ID")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if car is not found
    }
}
