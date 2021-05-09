package com.bridgelabz.employeepayroll;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.bridgelabz.employeepayroll.EmployeePayrollService.IOService.DB_IO;
import static com.bridgelabz.employeepayroll.EmployeePayrollService.IOService.FILE_IO;
public class EmployeePayrollServiceTest {

    //UC4 Create an Employee Payroll Service to store Employee Payroll into a File
    @Test
    public void given3EmployeeWhenWrittenToFile_shouldMatchEmployeeEntries() {
        EmployeePayrollData[] employeeData = {
                new EmployeePayrollData(1,"Jeff Bezos", 100000.0),
                new EmployeePayrollData(2,"Bill Gates",200000.0),
                new EmployeePayrollData(3,"Mark Zuckerberg",300000.0)
        };

        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(employeeData));
        employeePayrollService.writeEmployeePayrollData(FILE_IO);
        employeePayrollService.printData(FILE_IO);          //UC5 Ability for Employee Payroll Service to print the Employee Payrolls
        long entries = employeePayrollService.countEntries(FILE_IO);
        Assert.assertEquals(3,entries);
    }

    //JDBC UC-2
    @Test
    public void givenEmployeePayrollInDB_whenRetrieved_shouldMatchEmployeeCount(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollDataDB(DB_IO);
        Assert.assertEquals(3,employeePayrollData.size());
    }

    //JDBC UC-3
    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_shouldSyncDB(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollDataDB(DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa",3000000);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
        Assert.assertTrue(result);
    }

    //JDBC UC-4
    @Test
    public void givenNewSalaryForEmployee_WhenUpdatedUsingPreparedStatement_shouldSyncDB(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollDataDB(DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa",3000000);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
        Assert.assertTrue(result);
    }

    //JDBC UC-5
    @Test
    public void givenDateRange_whenRetrieved_shouldMatchEmployeeCount(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollDataDB(DB_IO);
        LocalDate startDate = LocalDate.of(2018,01,01);
        LocalDate endDate = LocalDate.now();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollDataForDateRange(DB_IO,startDate,endDate);
        Assert.assertEquals(3,employeePayrollData.size());
    }

    //JDBC UC-6
    @Test
    public void givenPayrollData_whenAverageSalaryRetrievedByGender_shouldReturnProperValue(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollDataDB(DB_IO);

        Map<String,Double> salarySumByGender = employeePayrollService.readSalarySumByGender(DB_IO);
        Map<String,Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(DB_IO);
        Map<String,Double> minSalaryByGender = employeePayrollService.readMinSalaryByGender(DB_IO);
        Map<String,Double> maxSalaryByGender = employeePayrollService.readMaxSalaryByGender(DB_IO);
        Map<String,Integer> countSalaryByGender = employeePayrollService.readCountSalaryByGender(DB_IO);

        Assert.assertTrue(salarySumByGender.get("M").equals(4000000.00) && salarySumByGender.get("F").equals(3000000.00));
        Assert.assertTrue(averageSalaryByGender.get("M").equals(2000000.00) && averageSalaryByGender.get("F").equals(3000000.00));
        Assert.assertTrue(minSalaryByGender.get("M").equals(1000000.00) && minSalaryByGender.get("F").equals(3000000.00));
        Assert.assertTrue(maxSalaryByGender.get("M").equals(3000000.00) && maxSalaryByGender.get("F").equals(3000000.00));
        Assert.assertTrue(countSalaryByGender.get("M").equals(2) && countSalaryByGender.get("F").equals(1));
    }

    //JDBC UC-7
    @Test
    public void givenNewEmployee_whenAdded_shouldSyncWithDB(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollDataDB(DB_IO);
        employeePayrollService.readEmployeePayrollData(DB_IO);
        employeePayrollService.addEmployeeToPayroll("Mark","M", 5000000.00, LocalDate.now());
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
        Assert.assertTrue(result);
    }
}