package carsharing;

import java.util.List;

public interface CompanyDAO {
    List<Company> getAllCompanies();
    void createCompany(String name);
    Company getCompanyById(int companyId);
}
