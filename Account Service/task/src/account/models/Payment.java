package account.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.text.ParseException;

@Entity(name = "payment")
@Table(name = "payment", uniqueConstraints = {@UniqueConstraint(columnNames = {"employee", "period_month", "period_year"})})
public class Payment {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotNull
    private String employee;

    @Column(name = "period_month")
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Min(1)
    @Max(12)
    int periodMonth;

    @Column(name = "period_year")
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    int periodYear;

    @Transient
    String period;

    @Column
    @NotNull
    @Min(0)
    long salary;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public int getPeriodMonth() {
        return periodMonth;
    }

    public void setPeriodMonth(int periodMonth) {
        this.periodMonth = periodMonth;
    }

    public int getPeriodYear() {
        return periodYear;
    }

    public void setPeriodYear(int periodYear) {
        this.periodYear = periodYear;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) throws ParseException, NumberFormatException {
        this.period = period;
        String[] parts = period.split("-");
        if (parts.length != 2) {
            throw new ParseException("errro parsing period of salary", 1);
        }
        this.periodMonth = Integer.parseInt(parts[0]);
        this.periodYear = Integer.parseInt(parts[1]);
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

}
