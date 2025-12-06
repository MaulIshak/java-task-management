package com.taskmanager.util;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConnectionTest {
    @Test
    void testConnection() {
        Connection conn = DBConnection.getInstance().getConnection();
        assertNotNull(conn, "Connection should not be null");
    }
}
