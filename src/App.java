
import model.Role;
import model.User;
import service.AuthService;
import service.UserService;

public class App {
    public static void main(String[] args) {
        UserService userService = new UserService();
        AuthService authService = new AuthService(userService);

        // Crear admin inicial (id y username como String)
        User admin = new User("1", "admin", "Administrador Principal", "admin123", Role.ADMIN);
        userService.addInitialUser(admin);

        // Login admin
        User loggedAdmin = authService.login("admin", "admin123");

        // Admin crea usuario 'Axel' (id "2", username "Axel")
        if (loggedAdmin != null) {
            boolean created = userService.createUser(loggedAdmin, "2", "Axel", "Axel Perez", "Axelpass", Role.STANDARD);
            System.out.println("Usuario 'Axel' creado: " + created);
        }

        System.out.println("\n-- Intentos fallidos para 'Axel' --");
        authService.login("Axel", "bad1");
        authService.login("Axel", "bad2");
        authService.login("Axel", "bad3"); // aquí se debe bloquear

        System.out.println("\n-- Intento con cuenta bloqueada --");
        authService.login("Axel", "Axel");

        System.out.println("\n-- Admin desbloquea a Axel --");
        boolean desbloqueado = userService.unlockUser(loggedAdmin, "2");
        System.out.println("Desbloqueado: " + desbloqueado);

        System.out.println("\n-- Intento con contraseña correcta tras desbloqueo --");
        User Axel = authService.login("Axel", "Axelpass");

        if (Axel != null) {
            boolean cambio = userService.updateUser(Axel, "2", null, "Axelpass", "nuevoPassAxel");
            System.out.println("Cambio de contraseña por Axel: " + cambio);
        }

        userService.showHistory(loggedAdmin, "2");
        userService.showHistory(loggedAdmin, "1");
    }
}
