package com.board.board.persistence.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ConnectionConfig {

    public static Connection getConnection() throws SQLException {
        var url = "jdbc:mysql://193.203.175.91:3306/u572677979_PDK_CR2"
                + "?useUnicode=true&characterEncoding=UTF-8"
                + "&serverTimezone=UTC"
                + "&allowPublicKeyRetrieval=true"
                + "&useSSL=true"; // use false só se SSL der erro
        var user = "u572677979_pdk02";
        var password = "$b!NcaqPd/C8zwY>RZkU+7";

        var connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false); // se quiser controlar transações manualmente
        return connection;
    }
}
