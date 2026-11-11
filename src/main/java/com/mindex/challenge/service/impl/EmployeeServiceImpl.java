package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReports(String id) {
        Employee employee = read(id);
        Set<String> visitedEmployeeIds = new HashSet<>();
        Deque<Employee> employeesToVisit = new ArrayDeque<>();
        int numberOfReports = 0;

        visitedEmployeeIds.add(employee.getEmployeeId());
        addDirectReports(employee, employeesToVisit, visitedEmployeeIds);

        while (!employeesToVisit.isEmpty()) {
            Employee reportReference = employeesToVisit.pop();
            numberOfReports++;
            
            addDirectReports(reportReference, employeesToVisit, visitedEmployeeIds);
        }

        return new ReportingStructure(employee, numberOfReports);
    }

    private void addDirectReports(Employee employee, Deque<Employee> employeesToVisit, Set<String> visitedEmployeeIds) {

        List<Employee> dirReps = employee.getDirectReports();
            
        if (dirReps == null) {
            return;
        }

        for (Employee report : dirReps) {
            String id = report.getEmployeeId();

            if (report != null && visitedEmployeeIds.add(id)) {
                employeesToVisit.push(report);
            }
        }
    }
}
