import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by diego on 16/06/2017.
 */
public class Main {

    public static void main(String [] args) throws Exception {

        String dbUrl = "jdbc:mariadb://localhost:3306/time_test?log=true";
        String dbUsername = "root";
        String dbPassword = "v@gRan2#";


        //log system info
        System.out.println(System.getProperty("java.version"));
        System.out.println(TimeZone.getDefault().getDisplayName());
        System.out.println(TimeZone.getDefault().getID());

        SimpleDateFormat df = new SimpleDateFormat( "HH:mm:ss" );

        try ( Connection conn = DriverManager.getConnection( dbUrl, dbUsername, dbPassword ) ) {
            // 5AM
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set( Calendar.HOUR, 5 );
            cal.set( Calendar.MINUTE, 0 );
            cal.set( Calendar.AM_PM, Calendar.AM );

            String uuid = UUID.randomUUID().toString();
            java.sql.Time timeValue = new java.sql.Time( cal.getTimeInMillis() );

            System.out.println( "Data to be inserted: " + df.format( timeValue ) );
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS test_test");
                stmt.execute("CREATE TABLE test_test (uuid varchar(256), time_field TIME)");
                ResultSet rs = stmt.executeQuery("SELECT @@time_zone, @@system_time_zone");
                rs.next();
                System.out.println("@@time_zone=" + rs.getString(1));
                System.out.println("@@system_time_zone=" + rs.getString(2));
            }

            try ( PreparedStatement stmt = conn.prepareStatement( "insert into test_test (uuid, time_field) values (?, ?)" ) ) {
                stmt.setString( 1, uuid );
                stmt.setTime( 2, timeValue );
                stmt.executeUpdate();
            }



            try ( PreparedStatement stmt = conn.prepareStatement( "select * from test_test where uuid = ?" ) ) {
                stmt.setString( 1, uuid );

                try ( ResultSet rs = stmt.executeQuery() ) {
                    rs.next();
                    java.sql.Time timeValueResult = rs.getTime( "time_field" );

                    System.out.println( "Query result: " + df.format( timeValueResult ) );
                }
            }
        }
    }

}
