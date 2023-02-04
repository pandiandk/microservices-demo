package com.example.employeeservice.controller;

import com.example.employeeservice.model.Employee;
import com.example.employeeservice.repository.EmployeeRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@RestController
public class EmployeeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);


    private final EmployeeRepository repository;

    private Counter emplCounter;


    private final MeterRegistry meterRegistry;

    public EmployeeController(EmployeeRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
    }


    @PostMapping("/")
    public Employee add(@RequestBody Employee employee) {
        LOGGER.info("Employee add: {}", employee);
        return repository.save(employee);
    }

    @GetMapping("/{id}")
    public Employee findById(@PathVariable("id") String id) {
        LOGGER.info("Employee find: {}", keyValue("id", id), keyValue("path", "/{id}"));
        return repository.findById(id).get();
    }

    @GetMapping("/")
    public Iterable<Employee> findAll() {
        LOGGER.info("Employee find");
        this.emplCounter.increment();
        return repository.findAll();
    }

    @GetMapping("/department/{departmentId}")
    public List<Employee> findByDepartment(@PathVariable("departmentId") String departmentId) {
        LOGGER.info("Employee find: departmentId={}", departmentId);
        return repository.findByDepartmentId(departmentId);
    }

    @GetMapping("/organization/{organizationId}")
    public List<Employee> findByOrganization(@PathVariable("organizationId") String organizationId) {
        LOGGER.info("Employee find: organizationId={}", organizationId);
        return repository.findByOrganizationId(organizationId);
    }

    private void init() {
        this.emplCounter = Counter.builder("employee.get.counter")
                .tag("employee_api", "findAll")
                .description("return the count of findAll endpoint")
                .register(meterRegistry);
    }

}