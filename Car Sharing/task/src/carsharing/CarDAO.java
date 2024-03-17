package carsharing;

import java.util.List;

public interface CarDAO {
    void createCar(String name, int companyId);
    List<Car> getAllCarsByCompanyId(int companyId);
    Car getCarById(int carId);
    List<Car> getAvailableCarsByCompanyId(int companyId); // Implement this method
}
