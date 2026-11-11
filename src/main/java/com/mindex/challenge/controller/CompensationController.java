package com.mindex.challenge.controller;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/employee/{employeeId}/compensation")
    public Compensation create(@PathVariable String employeeId, @RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for employee [{}]", employeeId);
        ensureEmployeeExists(employeeId);

        compensation.setEmployeeId(employeeId);
        return compensationRepository.insert(compensation);
    }

    @GetMapping("/employee/{employeeId}/compensation")
    public List<Compensation> findAll(@PathVariable String employeeId) {
        LOG.debug("Received compensation list request for employee [{}]", employeeId);
        ensureEmployeeExists(employeeId);

        return compensationRepository.findByEmployeeId(employeeId);
    }

    @GetMapping("/employee/{employeeId}/compensation/mostRecent")
    public Compensation findMostRecent(@PathVariable String employeeId) {
        LOG.debug("Received most recent compensation request for employee [{}]", employeeId);
        ensureEmployeeExists(employeeId);

        return compensationRepository.findByEmployeeId(employeeId).stream()
                .max(Comparator.comparing(Compensation::getEffectiveDate))
                .orElseThrow(() -> new RuntimeException(
                        "No compensation found for employeeId: " + employeeId
                ));
    }

    @GetMapping("/employee/{employeeId}/compensation/{date}")
    public Compensation findAsOf( @PathVariable String employeeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LOG.debug("Received compensation as-of request for employee [{}] and date [{}]", employeeId, date);
    
        ensureEmployeeExists(employeeId);

        return compensationRepository.findByEmployeeIdAndEffectiveDateBefore(employeeId, date)
                .stream()
                .max(Comparator.comparing(Compensation::getEffectiveDate))
                .orElseThrow(() -> new RuntimeException(
                        "No compensation found for employeeId: " + employeeId
                                + " before date: " + date
                ));
    }

    private void ensureEmployeeExists(String employeeId) {
        if (employeeRepository.findByEmployeeId(employeeId) == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }
    }
}
