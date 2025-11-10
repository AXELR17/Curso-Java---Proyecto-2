import model.Role;
import model.User;
import service.AuthService;
import service.UserService;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        UserService userService = new UserService();
        AuthService authService = new AuthService(userService);

        // Crear admin inicial
        User admin = new User("1", "admin", "Administrador Principal", "admin123", Role.ADMIN);
        userService.addInitialUser(admin);

        Scanner scanner = new Scanner(System.in);
        User currentUser = null;
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== SISTEMA DE GESTIÓN DE USUARIOS ===");
            System.out.println("Usuario actual: " + (currentUser == null ? "(ninguno)" : currentUser.getUsername() + " - " + currentUser.getRole()));
            System.out.println("1) Iniciar sesión");
            System.out.println("2) Cerrar sesión");
            System.out.println("3) Crear usuario (ADMIN)");
            System.out.println("4) Actualizar usuario (ADMIN o propio)");
            System.out.println("5) Cambiar mi contraseña (STANDARD o ADMIN)");
            System.out.println("6) Eliminar usuario (ADMIN)");
            System.out.println("7) Desbloquear usuario (ADMIN)");
            System.out.println("8) Registrar acción para usuario (ADMIN o propio)");
            System.out.println("9) Mostrar historial de usuario");
            System.out.println("10) Listar usuarios (IDs y usernames)");
            System.out.println("0) Salir");
            System.out.print("Elija opción: ");
            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1": // login
                    if (currentUser != null) {
                        System.out.println("Ya hay un usuario logueado. Cierre sesión primero.");
                        break;
                    }
                    System.out.print("Username: ");
                    String u = scanner.nextLine().trim();
                    System.out.print("Password: ");
                    String p = scanner.nextLine().trim();
                    currentUser = authService.login(u, p);
                    break;

                case "2": // logout
                    if (currentUser == null) {
                        System.out.println("No hay usuario logueado.");
                    } else {
                        System.out.println("Se cerró sesión de: " + currentUser.getUsername());
                        currentUser = null;
                    }
                    break;

                case "3": // create user
                    if (currentUser == null) {
                        System.out.println("Debe iniciar sesión como ADMIN.");
                        break;
                    }
                    System.out.print("Nuevo ID: "); String id = scanner.nextLine().trim();
                    System.out.print("Nuevo username: "); String nuser = scanner.nextLine().trim();
                    System.out.print("Nombre completo: "); String nfull = scanner.nextLine().trim();
                    System.out.print("Password: "); String npass = scanner.nextLine().trim();
                    System.out.print("Role (ADMIN/STANDARD): "); String r = scanner.nextLine().trim().toUpperCase();
                    Role role;
                    try { role = Role.valueOf(r); }
                    catch (Exception ex) { System.out.println("Role inválido."); break; }
                    boolean created = userService.createUser(currentUser, id, nuser, nfull, npass, role);
                    System.out.println("Creado: " + created);
                    break;

                case "4": // update (admin or own)
                    if (currentUser == null) { System.out.println("Inicie sesión."); break; }
                    System.out.print("Target ID a actualizar: "); String tid = scanner.nextLine().trim();
                    System.out.print("Nuevo fullName (enter para omitir): "); String nf = scanner.nextLine();
                    System.out.print("Nueva password (enter para omitir): "); String np = scanner.nextLine();
                    System.out.print("Contraseña actual (si cambia password): "); String curp = scanner.nextLine();
                    boolean upd = userService.updateUser(currentUser, tid,
                            nf.isBlank() ? null : nf,
                            curp.isBlank() ? null : curp,
                            np.isBlank() ? null : np);
                    System.out.println("Actualizado: " + upd);
                    break;

                case "5": // change own password
                    if (currentUser == null) { System.out.println("Inicie sesión."); break; }
                    System.out.print("Contraseña actual: "); String cur = scanner.nextLine();
                    System.out.print("Nueva contraseña: "); String neu = scanner.nextLine();
                    boolean ok = currentUser.changePassword(cur, neu);
                    System.out.println("Cambio de contraseña: " + ok);
                    break;

                case "6": // delete user
                    if (currentUser == null) { System.out.println("Inicie sesión."); break; }
                    System.out.print("ID a eliminar: "); String delId = scanner.nextLine().trim();
                    boolean del = userService.deleteUser(currentUser, delId);
                    System.out.println("Eliminado: " + del);
                    break;

                case "7": // unlock
                    if (currentUser == null) { System.out.println("Inicie sesión."); break; }
                    System.out.print("ID a desbloquear: "); String unlockId = scanner.nextLine().trim();
                    boolean un = userService.unlockUser(currentUser, unlockId);
                    System.out.println("Desbloqueado: " + un);
                    break;

                case "8": // register action
                    if (currentUser == null) { System.out.println("Inicie sesión."); break; }
                    System.out.print("Target ID: "); String targ = scanner.nextLine().trim();
                    System.out.print("Descripción acción: "); String desc = scanner.nextLine();
                    boolean reg = userService.registerAction(currentUser, targ, desc);
                    System.out.println("Registró acción: " + reg);
                    break;

                case "9": // show history
                    System.out.print("ID a mostrar historial: "); String hid = scanner.nextLine().trim();
                    userService.showHistory(currentUser, hid);
                    break;

                case "10": // listar usuarios (simple)
                    System.out.println("-- Usuarios registrados --");
                    // recorremos internamente userService (no hay getter) — usamos findById por índices
                    // como no hay método público para listar, podrías añadir uno; aquí demos un atajo:
                    for (int i = 0; i < 50; i++) {
                        // Intentamos usar findById con una convención: si existe un usuario con id=i+1
                        // Mejor: agregar método público listUserSummaries() en UserService; por simplicidad:
                    }
                    System.out.println("(Si quieres listar usuarios, agrega un método público en UserService para mostrar IDs/usernames.)");
                    break;

                case "0":
                    exit = true;
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        }

        scanner.close();
        System.out.println("Saliendo... ¡Hasta luego!");
    }
}
