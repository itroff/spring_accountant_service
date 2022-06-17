package account.services;

import account.models.Payment;
import account.models.User;
import account.models.UserPayment;
import account.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository repository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.repository = paymentRepository;
    }

    @Transactional
    public void savePayments(List<Payment> payments) throws RuntimeException {
        for (Payment payment : payments) {
            repository.save(payment);
        }

    }

    public void updatePayment(Payment payment) throws RuntimeException {
        Optional<Payment> op = repository.findByEmployeeAndPeriod(payment.getEmployee(), payment.getPeriodMonth(), payment.getPeriodYear());
        if (!op.isPresent()) {
            throw new RuntimeException();
        }
        op.get().setSalary(payment.getSalary());
        repository.save(op.get());
    }

    public List<UserPayment> getPayment(String period, User user) throws ParseException {
        List<UserPayment> ups = new ArrayList<>();
        if (period == null || period.isEmpty()) {
            List<Payment> payments = repository.findByEmail(user.getEmail().toLowerCase());
            Comparator<Payment> userPaymentComparator = (st1, st2) -> {
                if (st1.getPeriodYear() == st2.getPeriodYear()) {
                    return Integer.compare(st1.getPeriodMonth(), st2.getPeriodMonth());
                }
                return Integer.compare(st2.getPeriodYear(), st1.getPeriodYear());
            };
            payments.sort(userPaymentComparator.reversed());
            for (Payment p : payments) {
                ups.add(new UserPayment(user, p));
            }

            return ups;
        }
        String[] parts = period.split("-");
        if (parts.length != 2) {
            throw new ParseException("errro parsing period of salary", 1);
        }
        if(Integer.parseInt(parts[0]) < 1 || Integer.parseInt(parts[0]) > 12) {
            throw new ParseException("errro parsing period of salary", 1);
        }
        Optional<Payment> pm = repository.findByEmployeeAndPeriod(user.getEmail().toLowerCase(),
                Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        pm.ifPresent(payment -> ups.add(new UserPayment(user, payment)));
        return ups;
    }

}
