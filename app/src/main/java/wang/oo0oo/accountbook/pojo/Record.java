package wang.oo0oo.accountbook.pojo;

import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Record extends DataSupport {

    private int id;
    private String type;
    private String category;
    private Date date;
    private double amount;
    private String note;

    public Record() {

    }

    public Record(String type, String category, Date date, double amount, String note) {
        this.type = type;
        this.category = category;
        this.date = date;
        this.amount = amount;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id + '\'' +
                "type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", date=" + (date != null ? new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date) : null) +
                ", amount=" + amount +
                ", note='" + note + '\'' +
                '}';
    }
}
