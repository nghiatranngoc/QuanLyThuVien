package dao;

import java.sql.Date;
import java.time.LocalDate;

public class DaoUtil {
    private DaoUtil(){}

    static LocalDate toLocalDate(Date d){
        return d == null ? null : d.toLocalDate();
    }

    static Date tosqlDate(LocalDate d){
        return d == null ? null : Date.valueOf(d);
    }
}
