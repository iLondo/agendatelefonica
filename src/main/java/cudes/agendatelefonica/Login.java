

package cudes.agendatelefonica;

/**
 *
 * @author Jorge Castro
 */
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;

    public Login() {
         setLocationRelativeTo(null);
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(30, 30, 80, 25);
        add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(120, 30, 120, 25);
        add(txtUsuario);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(30, 70, 80, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(120, 70, 120, 25);
        add(txtPassword);

        btnLogin = new JButton("Iniciar sesión");
        btnLogin.setBounds(30, 110, 120, 25);
        add(btnLogin);

        btnRegister = new JButton("Registrarse");
        btnRegister.setBounds(160, 110, 120, 25);
        add(btnRegister);

        // Acción para iniciar sesión
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSesion();
            }
        });

        // Acción para registrarse
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuario();
            }
        });
    }

    // Método para iniciar sesión
    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM usuario WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, usuario);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Bienvenido " + usuario);
                new AgendaContactos().setVisible(true); // Abrir la agenda de contactos
                this.dispose(); // Cerrar la ventana de login
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para registrar usuario
    private void registrarUsuario() {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO usuario (username, password) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, usuario);
            statement.setString(2, password);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente");
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar usuario", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Método para limpiar los campos de texto
    private void limpiarCampos() {
        txtUsuario.setText("");
        txtPassword.setText("");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}
