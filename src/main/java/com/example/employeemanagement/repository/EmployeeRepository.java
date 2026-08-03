package com.example.employeemanagement.repository;

import java.util.List;
import java.util.Optional;

import com.example.employeemanagement.dto.response.DepartmentEmployeeCountProjection;
import com.example.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAllByOrderByNameAsc();

    List<Employee> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    List<Employee> findByDepartmentIdOrderByNameAsc(Long departmentId);

    List<Employee> findByNameContainingIgnoreCaseAndDepartmentIdOrderByNameAsc(String name, Long departmentId);

    Optional<Employee> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    long countByDepartmentId(Long departmentId);

    @Query("select count(e) from Employee e")
    long countTotalEmployees();

    @Query("""
            select d.id as departmentId, d.name as departmentName, count(e.id) as employeeCount
            from Department d left join d.employees e
            group by d.id, d.name
            order by d.name
            """)
    List<DepartmentEmployeeCountProjection> countEmployeesByDepartment();
}
