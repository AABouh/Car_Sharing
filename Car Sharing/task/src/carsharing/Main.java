package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(System.in); // Initialize scanner outside the try block to use it in the whole method

        // Database connection string
        String jdbcURL = "jdbc:h2:./src/carsharing/db/carsharing";

        // Try-with-resources block for managing database connection
        try (Connection conn = DriverManager.getConnection(jdbcURL)) {
            conn.setAutoCommit(true);

            // Create the COMPANY table
            try (Statement stmt = conn.createStatement()) {
                String query = "CREATE TABLE IF NOT EXISTS COMPANY (" + "ID INT PRIMARY KEY AUTO_INCREMENT, " + "NAME VARCHAR(255) UNIQUE NOT NULL);";
                stmt.executeUpdate(query);
                System.out.println("Table COMPANY created successfully.");
            }
            // Create the CAR table
            try (Statement stmt = conn.createStatement()) {
                String query = "CREATE TABLE IF NOT EXISTS CAR (" + "ID INT PRIMARY KEY AUTO_INCREMENT, " + "NAME VARCHAR(255) UNIQUE NOT NULL, " + "COMPANY_ID INT NOT NULL, " + "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID));";
                stmt.executeUpdate(query);
                System.out.println("Table CAR created successfully.");
            }
            // Create the CUSTOMER table
            try (Statement stmt = conn.createStatement()) {
                String query = "CREATE TABLE IF NOT EXISTS CUSTOMER (" + "ID INT PRIMARY KEY AUTO_INCREMENT, " + "NAME VARCHAR(255) UNIQUE NOT NULL, " + "RENTED_CAR_ID INT , " + "FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID));";
                stmt.executeUpdate(query);
                System.out.println("Table CAR created successfully.");
            }


            CompanyDAO companyDAO = new CompanyDAOImpl(conn); // Pass the connection to the DAO implementation
            CarDAO carDAO = new CarDAOImpl(conn);
            CustomerDAO customerDAO = new CustomerDAOImpl(conn);

            // Main loop for the menu
            while (true) {
                displayMainMenu();
                int mainMenuChoice = scan.nextInt();
                scan.nextLine(); // Consume the newline left-over
                switch (mainMenuChoice) {
                    case 0: // Exit
                        return;
                    case 1: // Log in as a manager
                        boolean back = false;
                        while (!back) {
                            displayManagerMenu();
                            int managerMenuChoice = scan.nextInt();
                            scan.nextLine(); // Consume the newline left-over
                            switch (managerMenuChoice) {
                                case 0: // Back
                                    back = true;
                                    break;
                                case 1: // Company list
                                    // Implementation to display company list
                                    displayCompanyList(companyDAO, scan, carDAO);
                                    break;
                                case 2: // Create a company
                                    createCompany(companyDAO, scan);
                                    break;
                            }
                        }
                        break;
                    case 2: // Log in as a customer
                        loginAsCustomer(scan, conn, customerDAO, carDAO, companyDAO);
                        break;
                    case 3: // Create a customer
                        createCustomer(scan, conn);
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            }
        } // The connection is automatically closed here
    }

    static void displayMainMenu() {
        System.out.println("1. Log in as a manager");
        System.out.println("2. Log in as a customer");
        System.out.println("3. Create a customer");
        System.out.println("0. Exit");
    }


    static void displayManagerMenu() {
        System.out.println("1. Company list\n2. Create a company\n0. Back");
    }

    static void createCompany(CompanyDAO companyDAO, Scanner scan) {
        System.out.println("Enter the company name:");
        String name = scan.nextLine(); // Use nextLine() to handle multi-word names
        companyDAO.createCompany(name);
    }

    static void displayCompanyList(CompanyDAO companyDAO, Scanner scan, CarDAO carDAO) {
        List<Company> companyList = companyDAO.getAllCompanies();
        if (companyList.isEmpty()) {
            System.out.println("The company list is empty!");
        } else {
            // Display the list of companies
            System.out.println("Choose a company:");
            for (int i = 0; i < companyList.size(); i++) {
                System.out.println((i + 1) + ". " + companyList.get(i).getName());
            }
            System.out.println("0. Back");

            // Get the user's choice
            int choice = scan.nextInt();
            scan.nextLine(); // Consume the newline left-over

            // Check if the user wants to go back
            if (choice == 0) {
                return;
            } else if (choice > 0 && choice <= companyList.size()) {
                // Display the company menu for the chosen company
                displayCompanyMenu(companyList.get(choice - 1), scan, carDAO);
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }

    static void displayCompanyMenu(Company company, Scanner scan, CarDAO carDAO) {
        boolean back = false;
        while (!back) {
            System.out.println("'" + company.getName() + "' company:");
            System.out.println("1. Car list");
            System.out.println("2. Create a car");
            System.out.println("0. Back");

            String input = scan.nextLine();
            int companyMenuChoice;
            try {
                companyMenuChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (companyMenuChoice) {
                case 1: // Car list
                    displayCarList(company, scan, carDAO);
                    break;
                case 2: // Create a car
                    createCar(company, scan, carDAO);
                    break;
                case 0: // Back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }


    static void displayCarList(Company company, Scanner scan, CarDAO carDAO) {
        List<Car> cars = carDAO.getAllCarsByCompanyId(company.getId());
        if (cars.isEmpty()) {
            System.out.println("The car list is empty!");
        } else {
            System.out.println("'" + company.getName() + "' cars:");
            for (int i = 0; i < cars.size(); i++) {
                System.out.println((i + 1) + ". " + cars.get(i).getName());
            }
        }

    }


    static void createCar(Company company, Scanner scan, CarDAO carDAO) {
        System.out.println("Enter the car name:");
        String carName = scan.nextLine();
        carDAO.createCar(carName, company.getId());
        System.out.println("The car was added!");
    }

    static void loginAsCustomer(Scanner scan, Connection conn, CustomerDAO customerDAO, CarDAO carDAO, CompanyDAO companyDAO) {

        List<Customer> customers = customerDAO.getAllCustomers();

        if (customers.isEmpty()) {
            System.out.println("The customer list is empty!");
            return;
        }

        System.out.println("Choose a customer:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName());
        }
        System.out.println("0. Back");

        int choice = Integer.parseInt(scan.nextLine());
        if (choice == 0) {
            return;
        } else if (choice > 0 && choice <= customers.size()) {
            displayCustomerMenu(scan, conn, customers.get(choice - 1).getId(), customerDAO, carDAO, companyDAO);
        } else {
            System.out.println("Invalid option. Please try again.");
        }
    }

    static void displayCustomerMenu(Scanner scan, Connection conn, int customerId, CustomerDAO customerDAO, CarDAO carDAO, CompanyDAO companyDAO) {
        boolean back = false;
        while (!back) {
            System.out.println("1. Rent a car");
            System.out.println("2. Return a rented car");
            System.out.println("3. My rented car");
            System.out.println("0. Back");

            int choice = Integer.parseInt(scan.nextLine());
            switch (choice) {
                case 1: // Rent a car
                    rentACar(scan, conn, customerId, companyDAO, carDAO, customerDAO);
                    break;
                case 2: // Return a rented car
                    returnRentedCar(scan, conn, customerId, customerDAO);
                    break;
                case 3: // My rented car
                    viewRentedCar(scan, conn, customerId, customerDAO, carDAO, companyDAO);
                    break;
                case 0: // Back
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }


    static void returnRentedCar(Scanner scan, Connection conn, int customerId, CustomerDAO customerDAO) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null || customer.getRentedCarId() == null) {
            System.out.println("You didn't rent a car!");
            return;
        }
        customerDAO.updateCustomerRentedCarId(customerId, null);
        System.out.println("You've returned a rented car!");
    }

    static void viewRentedCar(Scanner scan, Connection conn, int customerId, CustomerDAO customerDAO, CarDAO carDAO, CompanyDAO companyDAO) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null || customer.getRentedCarId() == null) {
            System.out.println("You didn't rent a car!");
            return;
        }

        Car rentedCar = carDAO.getCarById(customer.getRentedCarId());
        Company carCompany = companyDAO.getCompanyById(rentedCar.getCompanyId());

        System.out.println("Your rented car:");
        System.out.println(rentedCar.getName());
        System.out.println("Company:");
        System.out.println(carCompany.getName());
    }

    static void rentACar(Scanner scan, Connection conn, int customerId, CompanyDAO companyDAO, CarDAO carDAO, CustomerDAO customerDAO) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer.getRentedCarId() != null) {
            System.out.println("You've already rented a car!");
            return;
        }

        List<Company> companies = companyDAO.getAllCompanies();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
            return;
        }

        System.out.println("Choose a company:");
        for (int i = 0; i < companies.size(); i++) {
            System.out.println((i + 1) + ". " + companies.get(i).getName());
        }
        System.out.println("0. Back");
        int companyChoice = Integer.parseInt(scan.nextLine());
        if (companyChoice == 0) return;

        Company selectedCompany = companies.get(companyChoice - 1);
        List<Car> availableCars = carDAO.getAvailableCarsByCompanyId(selectedCompany.getId());
        if (availableCars.isEmpty()) {
            System.out.println("No available cars in the '" + selectedCompany.getName() + "' company.");
            return;
        }

        System.out.println("Choose a car:");
        for (int i = 0; i < availableCars.size(); i++) {
            System.out.println((i + 1) + ". " + availableCars.get(i).getName());
        }
        System.out.println("0. Back");
        int carChoice = Integer.parseInt(scan.nextLine());
        if (carChoice == 0) return;

        Car selectedCar = availableCars.get(carChoice - 1);
        customerDAO.updateCustomerRentedCarId(customerId, selectedCar.getId());
        System.out.println("You rented '" + selectedCar.getName() + "'");
    }

    static void createCustomer(Scanner scan, Connection conn) {
        System.out.println("Enter the customer name:");
        String customerName = scan.nextLine();
        try {
            CustomerDAO customerDAO = new CustomerDAOImpl(conn);
            customerDAO.createCustomer(customerName);
            System.out.println("The customer was added!");
        } catch (Exception e) {
            System.out.println("Error creating customer: " + e.getMessage());
        }
    }


}
