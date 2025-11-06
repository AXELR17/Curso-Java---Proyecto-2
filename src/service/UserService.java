package service;

import model.Action;
import model.Role;
import model.User;

public class UserService {
    private final User[] users;
    private Integer userCount;
    public static final Integer MAX_USERS = 50;

    public UserService() {
        this.users = new User[MAX_USERS];
        this.userCount = 0;
    }

    // Devuelve (índice) o -1
    private Integer findIndexById(String id) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getId().equals(id)) return i;
        }
        return -1;
    }

    private Integer findIndexByUsername(String username) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getUsername().equals(username)) return i;
        }
        return -1;
    }

    // Crear usuario: solo ADMIN puede crear
    public boolean createUser(User actor, String id, String username, String fullName, String password, Role role) {
        if (!isAdmin(actor)) {
            System.out.println("Permiso denegado: solo ADMIN puede crear usuarios.");
            return false;
        }
        if (userCount >= MAX_USERS) {
            System.out.println("Máximo de usuarios alcanzado.");
            return false;
        }
        if (findIndexById(id) != -1) {
            System.out.println("ID ya existe.");
            return false;
        }
        if (findIndexByUsername(username) != -1) {
            System.out.println("Username ya existe.");
            return false;
        }
        User u = new User(id, username, fullName, password, role);
        users[userCount++] = u; 
        actor.addAction(new Action("Creó usuario " + username));
        return true;
    }

    public User findById(String id) {
        Integer idx = findIndexById(id);
        return idx == -1 ? null : users[idx];
    }

    public User findByUsername(String username) {
        Integer idx = findIndexByUsername(username);
        return idx == -1 ? null : users[idx];
    }

    public boolean updateUser(User actor, String targetId, String newFullName, String currentPasswordForChange, String newPassword) {
        User target = findById(targetId);
        if (target == null) {
            System.out.println("Usuario objetivo no existe.");
            return false;
        }

        if (isAdmin(actor)) {
            if (newFullName != null) target.setFullName(newFullName);
            if (newPassword != null) target.setPasswordByAdmin(newPassword);
            actor.addAction(new Action("Actualizó usuario " + target.getUsername()));
            return true;
        }

        if (actor.getRole() == Role.STANDARD) {
            if (!actor.getId().equals(targetId)) {
                System.out.println("Permiso denegado: Usuario estándar solo puede actualizar su propio perfil.");
                return false;
            }
            boolean changed = false;
            if (newFullName != null) {
                actor.setFullName(newFullName);
                changed = true;
            }
            if (newPassword != null) {
                boolean ok = actor.changePassword(currentPasswordForChange, newPassword);
                if (!ok) {
                    System.out.println("Contraseña actual incorrecta. No se pudo cambiar la contraseña.");
                    return false;
                }
                changed = true;
            }
            if (changed) actor.addAction(new Action("Actualizó su perfil"));
            return changed;
        }

        return false;
    }

    public boolean deleteUser(User actor, String targetId) {
        if (!isAdmin(actor)) {
            System.out.println("Permiso denegado: solo ADMIN puede eliminar usuarios.");
            return false;
        }
        Integer idx = findIndexById(targetId);
        if (idx == -1) {
            System.out.println("Usuario a eliminar no existe.");
            return false;
        }
        String username = users[idx].getUsername();
        for (int i = idx; i < userCount - 1; i++) users[i] = users[i + 1];
        users[--userCount] = null; 
        actor.addAction(new Action("Eliminó usuario " + username));
        return true;
    }

    public boolean registerAction(User actor, String targetId, String description) {
        User target = findById(targetId);
        if (target == null) {
            System.out.println("Usuario objetivo no existe.");
            return false;
        }
        if (!isAdmin(actor) && !actor.getId().equals(targetId)) {
            System.out.println("Permiso denegado para registrar acción en otro usuario.");
            return false;
        }
        boolean ok = target.addAction(new Action(description));
        if (!ok) {
            System.out.println("No se pudo registrar acción: historial lleno.");
            return false;
        }
        actor.addAction(new Action("Registró acción '" + description + "' para " + target.getUsername()));
        return true;
    }

    public void showHistory(User actor, String targetId) {
        User target = findById(targetId);
        if (target == null) {
            System.out.println("Usuario objetivo no existe.");
            return;
        }
        if (!isAdmin(actor) && !actor.getId().equals(targetId)) {
            System.out.println("Permiso denegado: no puedes ver el historial de otro usuario.");
            return;
        }
        System.out.println("Historial de " + target.getUsername() + ":");
        Action[] acts = target.getActions();
        if (acts.length == 0) {
            System.out.println("  (sin acciones)");
            return;
        }
        for (Action a : acts) System.out.println("  " + a);
    }

    public boolean unlockUser(User actor, String targetId) {
        if (!isAdmin(actor)) {
            System.out.println("Permiso denegado: solo ADMIN puede desbloquear usuarios.");
            return false;
        }
        User target = findById(targetId);
        if (target == null) {
            System.out.println("Usuario objetivo no existe.");
            return false;
        }
        if (!target.isLocked()) {
            System.out.println("El usuario no está bloqueado.");
            return false;
        }
        target.unlock();
        actor.addAction(new Action("Desbloqueó usuario " + target.getUsername()));
        return true;
    }

    private boolean isAdmin(User u) {
        return u != null && u.getRole() == Role.ADMIN;
    }

    public boolean addInitialUser(User u) {
        if (userCount >= MAX_USERS) return false;
        if (findIndexById(u.getId()) != -1 || findIndexByUsername(u.getUsername()) != -1) return false;
        users[userCount++] = u;
        return true;
    }
}
