package com.bridgelabz.employeepayroll;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {

    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    private EmployeePayrollDBService(){
    }

    //JDBC Refactor UC-4
    public static EmployeePayrollDBService getInstance(){
        if(employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String passWord = "12345";
        Connection connection;
        System.out.println("Connecting to database"+jdbcURL);
        connection = DriverManager.getConnection(jdbcURL,userName,passWord);
        System.out.println("Connection is successful!!"+connection);
        return connection;
    }

    public List<EmployeePayrollData> readData() {
        String sql = "SELECT * FROM payroll_table;";
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    public int updateEmployeeData(String name, double salary) {
        return this.updateEmployeeDataUsingPreparedStatement(name,salary);
    }

    private int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
        String sql = String.format("UPDATE payroll_table SET salary = %.2f WHERE name = '%s';",salary,name);
        try(Connection connection = this.getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            return statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList = null;
        if(this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
        try{
            employeePayrollDataStatement.setString(1,name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }


    //JDBC UC-5
    public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = String.format("select * from payroll_table WHERE start BETWEEN '%s' AND '%s';",Date.valueOf(startDate),Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    //JDBC UC-6
    public Map<String, Double> getSalarySumByGender() {
        String sql = "SELECT gender, SUM(salary) AS sum_salary FROM payroll_table GROUP BY gender";
        Map<String,Double> genderToSalarySumMap = new HashMap<>();
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                String gender = resultSet.getString("gender");
                Double salary = resultSet.getDouble("sum_salary");
                genderToSalarySumMap.put(gender,salary);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return genderToSalarySumMap;
    }

    public Map<String, Double> getAverageSalaryByGender() {
        String sql = "SELECT gender, AVG(salary) AS avg_salary FROM payroll_table GROUP BY gender";
        Map<String,Double> genderToAverageSalaryMap = new HashMap<>();
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                String gender = resultSet.getString("gender");
                Double salary = resultSet.getDouble("avg_salary");
                genderToAverageSalaryMap.put(gender,salary);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return genderToAverageSalaryMap;
    }

    public Map<String, Double> getMinSalaryByGender() {
        String sql = "SELECT gender, MIN(salary) AS min_salary FROM payroll_table GROUP BY gender";
        Map<String,Double> genderToMinSalaryMap = new HashMap<>();
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                String gender = resultSet.getString("gender");
                Double salary = resultSet.getDouble("min_salary");
                genderToMinSalaryMap.put(gender,salary);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return genderToMinSalaryMap;
    }

    public Map<String, Double> getMaxSalaryByGender() {
        String sql = "SELECT gender, MAX(salary) AS max_salary FROM payroll_table GROUP BY gender";
        Map<String,Double> genderToMaxSalaryMap = new HashMap<>();
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                String gender = resultSet.getString("gender");
                Double salary = resultSet.getDouble("max_salary");
                genderToMaxSalaryMap.put(gender,salary);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return genderToMaxSalaryMap;
    }

    public Map<String, Integer> getCountSalaryByGender() {
        String sql = "SELECT gender, COUNT(salary) AS count_salary FROM payroll_table GROUP BY gender";
        Map<String, Integer> genderToCountSalaryMap = new HashMap<>();
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                String gender = resultSet.getString("gender");
                Integer salary = resultSet.getInt("count_salary");
                genderToCountSalaryMap.put(gender,salary);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return genderToCountSalaryMap;
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try{
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private void prepareStatementForEmployeeData() {
        try {
            Connection connection = this.getConnection();
            String sql = "SELECT * FROM payroll_table WHERE name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //JDBC UC-7
    public EmployeePayrollData addEmployeeToPayrollUC7(String name, String gender, double salary, LocalDate startDate) {
        int employeeID =-1;
        EmployeePayrollData employeePayrollData = null;
        String sql = String.format("INSERT INTO payroll_table ( name,gender,salary,start ) VALUES ('%s','%s','%s','%s')",name,gender,salary,Date.valueOf(startDate));
        try(Connection connection = this.getConnection()){
            Statement statement = connection.createStatement();
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if(rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    employeeID = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(employeeID,name,salary,startDate);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollData;
    }

    //JDBC UC-8
    public EmployeePayrollData addEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate) {
        int employeeID = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try{
            connection = this.getConnection();
        } catch (SQLException e){
            e.printStackTrace();
        }
        try(Statement statement = connection.createStatement()){
            String sql = String.format("INSERT INTO payroll_table (name,gender,salary,start) VALUES ('%s','%s','%s','%s')",name,gender,salary,Date.valueOf(startDate));
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if(rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next())
                    employeeID = resultSet.getInt(1);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        try(Statement statement = connection.createStatement()) {
            double deductions = salary * 0.2;
            double taxablePay = salary-deductions;
            double tax = taxablePay * 0.1;
            double netPay = salary - tax;
            String sql = String.format("INSERT INTO payroll_details "+
                                       "(employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) VALUES "+
                                       "(%s, %s, %s, %s, %s, %s)", employeeID,salary,deductions,taxablePay,tax,netPay);
            int rowAffected = statement.executeUpdate(sql);
            if(rowAffected == 1){
                employeePayrollData = new EmployeePayrollData(employeeID,name,salary,startDate);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollData;
    }
}
