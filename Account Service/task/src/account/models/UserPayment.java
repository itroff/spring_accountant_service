package account.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class UserPayment {
    private String name;
    private String lastname;
    private String period;
    private String salary;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final Map<Integer, String> months = Map.ofEntries(Map.entry(1, "January"),
            Map.entry(2, "February"),
            Map.entry(3, "March"),
            Map.entry(4, "April"),
            Map.entry(5, "May"),
            Map.entry(6, "June"),
            Map.entry(7, "Jule"),
            Map.entry(8, "August"),
            Map.entry(9, "September"),
            Map.entry(10, "October"),
            Map.entry(11, "November"),
            Map.entry(12, "December"));


    public UserPayment(User user, Payment payment) {
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.period = months.get(payment.getPeriodMonth()) + "-" + payment.getPeriodYear();
        this.salary = String.valueOf(payment.getSalary() / 100) + " dollar(s) " +
                String.valueOf(payment.getSalary() % 100) + " cent(s)";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

}
