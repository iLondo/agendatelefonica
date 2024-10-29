
package cudes.agendatelefonica;

import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AgendaContactos extends JFrame {
    private JTextField txtNombre, txtTelefono, txtEmail;
    private JButton btnAgregar, btnEliminar, btnModificar, btnListar;
    private JTable tableContactos;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    public AgendaContactos() {
        setLocationRelativeTo(null);
        // Configuración básica del JFrame
        setTitle("Agenda de Contactos");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        setLayout(null);

        // Campos de texto para ingresar datos
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(10, 10, 80, 25);
        add(lblNombre);
        txtNombre = new JTextField();
        txtNombre.setBounds(100, 10, 160, 25);
        add(txtNombre);

        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setBounds(10, 40, 80, 25);
        add(lblTelefono);
        txtTelefono = new JTextField();
        txtTelefono.setBounds(100, 40, 160, 25);
        add(txtTelefono);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(10, 70, 80, 25);
        add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setBounds(100, 70, 160, 25);
        add(txtEmail);

        // Botones
        btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(280, 10, 100, 25);
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAgregar.setBackground(Color.green);
        btnAgregar.setForeground(Color.WHITE);
        
        add(btnAgregar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(280, 40, 100, 25);
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEliminar.setBackground(Color.red);
        btnEliminar.setForeground(Color.WHITE);
        add(btnEliminar);

        btnModificar = new JButton("Modificar");
        btnModificar.setBounds(280, 70, 100, 25);
        btnModificar.setFont(new Font("Arial", Font.BOLD, 14));
        btnModificar.setBackground(Color.orange);
        btnModificar.setForeground(Color.WHITE);
        add(btnModificar);

        btnListar = new JButton("Listar Contactos");
        btnListar.setBounds(390, 10, 150, 85);
        btnListar.setFont(new Font("Arial", Font.BOLD, 14));
        btnListar.setBackground(Color.CYAN);
        btnListar.setForeground(Color.black);
        add(btnListar);

        // Crear JTable con DefaultTableModel
        model = new DefaultTableModel();
        model.addColumn("Nombre");
        model.addColumn("Teléfono");
        model.addColumn("Email");

        tableContactos = new JTable(model);
        scrollPane = new JScrollPane(tableContactos);
        scrollPane.setBounds(10, 110, 560, 300);
        add(scrollPane);

        // Acción del botón "Agregar"
        btnAgregar.addActionListener(e -> agregarContacto());

        // Acción del botón "Eliminar"
        btnEliminar.addActionListener(e -> eliminarContactoPorNombre(txtNombre.getText()));

        // Acción del botón "Modificar"
        btnModificar.addActionListener(e -> modificarContactoPorNombre(txtNombre.getText(), txtTelefono.getText(), txtEmail.getText()));

        // Acción del botón "Listar"
        btnListar.addActionListener(e -> listarContactos());

        setVisible(true);
        listarContactos();
    }

    // Método para agregar contacto a la base de datos
    private void agregarContacto() {
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();

        String sql = "INSERT INTO contactos (nombre, telefono, email) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda_db", "root", "");
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nombre);
            statement.setString(2, telefono);
            statement.setString(3, email);
            statement.executeUpdate();
            limpiar();
            JOptionPane.showMessageDialog(this, "Contacto agregado exitosamente.");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al agregar contacto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para eliminar contacto por nombre
    private void eliminarContactoPorNombre(String nombre) {
        String sql = "DELETE FROM contactos WHERE nombre = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda_db", "root", "");
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nombre);
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Contacto eliminado: " + nombre);
                limpiar();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún contacto con el nombre: " + nombre);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar contacto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para modificar contacto por nombre
    private void modificarContactoPorNombre(String nombre, String nuevoTelefono, String nuevoEmail) {
        String sql = "UPDATE contactos SET telefono = ?, email = ? WHERE nombre = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda_db", "root", "");
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nuevoTelefono);
            statement.setString(2, nuevoEmail);
            statement.setString(3, nombre);
            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Contacto modificado: " + nombre);
                limpiar();
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún contacto con el nombre: " + nombre);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al modificar contacto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para listar contactos y mostrar en JTable
    private void listarContactos() {
        String sql = "SELECT nombre, telefono, email FROM contactos";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/agenda_db", "root", "");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            // Limpiar el modelo de la tabla antes de agregar nuevos datos
            model.setRowCount(0);
            limpiar();
            // Recorrer el ResultSet y agregar filas a la tabla
            while (resultSet.next()) {
                String nombre = resultSet.getString("nombre");
                String telefono = resultSet.getString("telefono");
                String email = resultSet.getString("email");
                model.addRow(new Object[]{nombre, telefono, email});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al listar contactos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

     void limpiar(){
        txtNombre.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        
    }
    
    // Main para ejecutar la aplicación
    public static void main(String[] args) {
        new AgendaContactos();
        
    }
}
