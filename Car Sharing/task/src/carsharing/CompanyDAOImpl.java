package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAOImpl implements CompanyDAO {

    private final Connection conn;

    // Constructor to inject the database connection
    public CompanyDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Company> getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM COMPANY ORDER BY ID";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                companies.add(new Company(id, name));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching companies: " + e.getMessage());
        }

        return companies;
    }

    @Override
    public void createCompany(String name) {
        String sql = "INSERT INTO COMPANY (NAME) VALUES (?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            System.out.println("Company was created successfully.");

        } catch (SQLException e) {
            System.out.println("Error creating company: " + e.getMessage());
        }
    }
    @Override
    public Company getCompanyById(int companyId) {
        String sql = "SELECT * FROM COMPANY WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Company(
                        rs.getInt("ID"),
                        rs.getString("NAME")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if company is not found
    }
}
