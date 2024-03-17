package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {
    private final Connection conn;

    public CustomerDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void createCustomer(String name) {
        String sql = "INSERT INTO CUSTOMER (NAME) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM CUSTOMER WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("ID"),
                        rs.getString("NAME"),
                        (Integer) rs.getObject("RENTED_CAR_ID") // Handles nulls
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateCustomerRentedCarId(int customerId, Integer carId) {
        String sql = "UPDATE CUSTOMER SET RENTED_CAR_ID = ? WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (carId == null) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, carId);
            }
            pstmt.setInt(2, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM CUSTOMER ORDER BY ID";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(new Customer(rs.getInt("ID"), rs.getString("NAME"), (Integer) rs.getObject("RENTED_CAR_ID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
}
