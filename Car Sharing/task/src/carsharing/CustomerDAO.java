package carsharing;

import java.util.List;

public interface CustomerDAO {
    void createCustomer(String name);
    Customer getCustomerById(int customerId);
    void updateCustomerRentedCarId(int customerId, Integer carId); // Null `carId` indicates no car rented
    List<Customer> getAllCustomers();
}
