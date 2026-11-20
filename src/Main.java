import database.DBConnection;
// import dao.*;
// import enums.*;
// import model.*;

import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
import java.sql.SQLException;
// import java.time.LocalDate;

import javax.swing.SwingUtilities;

import UI.*;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();

            System.out.println(
                "Ket noi MySQL thanh cong: " + conn.getCatalog()
            );

            conn.close();

            SwingUtilities.invokeLater(() -> {
                new PhanQuyenFrame().setVisible(true);
            });

        } catch (SQLException e) {
            System.err.println(
                "Ket noi that bai: " + e.getMessage()
            );
        }
    }
}
