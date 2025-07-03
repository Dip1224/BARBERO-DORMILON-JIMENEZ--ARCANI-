package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBService {
    public static void guardarBarbero(String nombre, double dineroGanado) {
        String sql = "INSERT INTO barberos (nombre, dinero_ganado) VALUES (?, ?) ON DUPLICATE KEY UPDATE dinero_ganado = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setDouble(2, dineroGanado);
            ps.setDouble(3, dineroGanado);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static int crearNuevoDia() {
        String sql = "INSERT INTO dias () VALUES ()";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }

    public static void guardarTransaccion(String nombreCliente, int idBarbero, double montoPago, int idDia) {
        String sql = "INSERT INTO transacciones (nombre_cliente, id_barbero, monto_pago, id_dia) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreCliente);
            ps.setInt(2, idBarbero);
            ps.setDouble(3, montoPago);
            ps.setInt(4, idDia);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static List<TransaccionResumen> obtenerTransaccionesPorDia(int idDia) {
        List<TransaccionResumen> lista = new ArrayList<>();
        String sql = "SELECT nombre_cliente, id_barbero, monto_pago FROM transacciones WHERE id_dia = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new TransaccionResumen(
                        rs.getString("nombre_cliente"),
                        rs.getInt("id_barbero"),
                        rs.getDouble("monto_pago")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return lista;
    }

    // Clase auxiliar para la UI
    public static class TransaccionResumen {
        private final String nombreCliente;
        private final int idBarbero;
        private final double montoPago;
        public TransaccionResumen(String nombreCliente, int idBarbero, double montoPago) {
            this.nombreCliente = nombreCliente;
            this.idBarbero = idBarbero;
            this.montoPago = montoPago;
        }
        public String getNombreCliente() { return nombreCliente; }
        public int getIdBarbero() { return idBarbero; }
        public double getMontoPago() { return montoPago; }
    }
}
