package com.test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilpatil on 1/11/17.
 */
public class JDBCHelper {
    public static void main(String[] argv) throws Exception{
        if(argv.length != 1) {
            System.err.println("Please specify table name");
            System.exit(1);
        }
        String tableName = argv[0];
        Class.forName("com.mysql.jdbc.Driver");

        Connection connect = DriverManager
                .getConnection("jdbc:mysql://localhost/test1?"
                        + "user=test1&password=test1");

        Statement statement = connect.createStatement();

        ResultSet rs = statement.executeQuery("select * from "+tableName);

        List<String> columnNameList = new ArrayList<>();
        ResultSetMetaData rsMetadata = rs.getMetaData();
        for(int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
            if(i != 1)
                System.out.print(",");
            columnNameList.add(rsMetadata.getColumnName(i));
            System.out.print(rsMetadata.getColumnName(i));
        }
        System.out.println(" ");
        while(rs.next()){
            for(int i = 1 ; i<= columnNameList.size() ;i++) {
                if(i != 1)
                    System.out.print(",");
                System.out.print(rs.getObject(i));

            }
            System.out.println(" ");
        }
    }
}
