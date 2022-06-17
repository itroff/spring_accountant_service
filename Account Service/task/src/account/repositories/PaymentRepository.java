package account.repositories;

import account.models.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    @Query("SELECT u FROM payment u where lower(employee)=:employee AND period_month=:period_month AND period_year=:period_year")
    Optional<Payment> findByEmployeeAndPeriod(@Param("employee") String employee, @Param("period_month") int period_month,
                                          @Param("period_year") int period_year);

    @Query("SELECT u FROM payment u where lower(employee)=:employee")
    List<Payment> findByEmail(@Param("employee") String employee);
}
