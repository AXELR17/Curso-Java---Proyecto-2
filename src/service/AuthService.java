package service;

import model.Action;
import model.User;

public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public User login(String username, String password) {
        User u = userService.findByUsername(username);
        if (u == null) {
            System.out.println("Login fallido: usuario no encontrado.");
            return null;
        }

        if (u.isLocked()) {
            System.out.println("Login fallido: cuenta bloqueada. Contacte a un administrador.");
            return null;
        }

        if (!u.checkPassword(password)) {
            boolean lockedNow = u.incrementFailedAttemptsAndLockIfNeeded();
            if (lockedNow) {
                System.out.println("Login fallido: contraseña incorrecta. La cuenta ha sido bloqueada tras múltiples intentos.");
                u.addAction(new Action("Cuenta bloqueada por intentos fallidos"));
            } else {
                System.out.println("Login fallido: contraseña incorrecta. Intentos fallidos incrementados.");
                u.addAction(new Action("Intento fallido de inicio de sesión"));
            }
            return null;
        }

        u.resetFailedAttempts();
        boolean ok = u.addAction(new Action("Inició sesión"));
        if (!ok) System.out.println("Advertencia: no se pudo registrar acción (historial lleno).");
        System.out.println("Login exitoso: " + u.getUsername());
        return u;
    }
}
