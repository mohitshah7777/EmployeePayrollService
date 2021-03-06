package com.bridgelabz.employeepayroll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayrollService {

    public enum IOService {
        CONSOLE_IO, FILE_IO,DB_IO
    }

    private List<EmployeePayrollData> employeePayrollList;
    private EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
        this();
        this.employeePayrollList = employeePayrollList;
    }

    public EmployeePayrollService(){
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    //JDBC_UC-2
    public List<EmployeePayrollData> readEmployeePayrollDataDB(IOService ioService){
        if(ioService.equals(IOService.DB_IO))
            this.employeePayrollList = employeePayrollDBService.readData();
        return this.employeePayrollList;
    }

    //JDBC UC -3
    public void updateEmployeeSalary(String name, double salary) {
        int result = employeePayrollDBService.updateEmployeeData(name,salary);
        if(result == 0)
            return;
            EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if(employeePayrollData != null)
            employeePayrollData.salary = salary;
    }

    private EmployeePayrollData getEmployeePayrollData(String name) {
        return this.employeePayrollList.stream()
                    .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
                    .findFirst()
                    .orElse(null);
    }

    public boolean checkEmployeePayrollInSyncWithDB(String name) {
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }

    public void readEmployeePayrollData(Scanner consoleInputReader) {
        System.out.println("Enter Employee ID");
        int id = consoleInputReader.nextInt();
        System.out.println("Enter Employee name");
        String name = consoleInputReader.next();
        System.out.println("Enter Employee Salary");
        Double salary = consoleInputReader.nextDouble();
        employeePayrollList.add(new EmployeePayrollData(id, name, salary));
    }

    public void writeEmployeePayrollData(IOService ioService) {
        if (ioService.equals(IOService.CONSOLE_IO))
            System.out.println("\nWriting Employee Payroll Roaster to Console\n" + employeePayrollList);
        else
            new EmployeePayrollFileIOService().writeData(employeePayrollList);
    }

    public long readEmployeePayrollData(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            this.employeePayrollList = new EmployeePayrollFileIOService().readData();
        return employeePayrollList.size();
    }

    //JDBC UC-5
    public List<EmployeePayrollData> readEmployeePayrollDataForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeePayrollForDateRange(startDate,endDate);
        return null;
    }

    //JDBC UC-6
    public Map<String, Double> readAverageSalaryByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getAverageSalaryByGender();
        return null;
    }

    public Map<String, Double> readSalarySumByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getSalarySumByGender();
        return null;
    }

    public Map<String, Double> readMinSalaryByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getMinSalaryByGender();
        return null;
    }

    public Map<String, Double> readMaxSalaryByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getMaxSalaryByGender();
        return null;
    }

    public Map<String, Integer> readCountSalaryByGender(IOService ioService) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getCountSalaryByGender();
        return null;
    }

    //JDBC UC-7
    public void addEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate) {
        employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name,gender,salary,startDate));
    }

    //UC5 Ability for Employee Payroll Service to print the Employee Payrolls
    public void printData(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().printData();
    }

    public long countEntries(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return 0;
    }

    public static void main(String[] args) {
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        Scanner consoleInputReader = new Scanner(System.in);
        employeePayrollService.readEmployeePayrollData(consoleInputReader);
        employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
    }
}