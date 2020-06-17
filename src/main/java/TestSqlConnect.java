import java.sql.*;

public class TestSqlConnect {
    public static void main(String[] args) throws Exception {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/finalcourse?serverTimezone=UTC", "root", "259796")) {
            System.out.println(conn);
            // create new connect
            Statement stmt = conn.createStatement();
            // run
            ResultSet rs = stmt.executeQuery("select * from adminuser");
            System.out.println("id\tname\tpasswd\t\tmarketname");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "\t" + rs.getString(2)
                        + "\t\t" + rs.getString(3) + "\t\t" + rs.getString(4));
            }
        }
    }
}
