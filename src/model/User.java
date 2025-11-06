package model;


 //Representa un usuario en el sistema.
 
public class User {
    private final String id;
    private final String username;
    private String fullName;
    private String password;
    private Role role;

    // Historial de acciones
    private final Action[] actions;
    private Integer actionCount;

    // Bloqueo por intentos fallidos 
    private Integer failedLoginAttempts;
    private boolean locked;

    public static final Integer MAX_ACTIONS = 100;
    public static final Integer MAX_FAILED_ATTEMPTS = 3;

    // Constructor
    public User(String id, String username, String fullName, String password, Role role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.role = role;
        this.actions = new Action[MAX_ACTIONS]; // MAX_ACTIONS auto-unboxes
        this.actionCount = 0;
        this.failedLoginAttempts = 0;
        this.locked = false;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public Role getRole() { return role; }

    // Setter
    public void setFullName(String fullName) { this.fullName = fullName; }

    // Validación contraseña
    public boolean checkPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    public boolean changePassword(String currentPassword, String newPassword) {
        if (checkPassword(currentPassword)) {
            this.password = newPassword;
            addAction(new Action("Cambió su contraseña"));
            return true;
        }
        return false;
    }

    public void setPasswordByAdmin(String newPassword) {
        this.password = newPassword;
    }

    // Historial 
    public boolean addAction(Action action) {
        if (actionCount >= MAX_ACTIONS) return false; 
        actions[actionCount++] = action; 
        return true;
    }

    public Action[] getActions() {
        Action[] copy = new Action[actionCount];
        System.arraycopy(actions, 0, copy, 0, actionCount);
        return copy;
    }

    // Bloqueo 
    public boolean isLocked() { return locked; }

    /**
     * Incrementa contador de intentos fallidos. Si alcanza MAX_FAILED_ATTEMPTS bloquea la cuenta.
     * Retorna true si la cuenta queda bloqueada como resultado.
     */
    public boolean incrementFailedAttemptsAndLockIfNeeded() {
        if (locked) return true;
        failedLoginAttempts = failedLoginAttempts + 1; // evita ambigüedad con ++ 
        if (failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
            locked = true;
            return true;
        }
        return false;
    }

    public void resetFailedAttempts() { failedLoginAttempts = 0; }

    public void unlock() {
        locked = false;
        resetFailedAttempts();
        addAction(new Action("Cuenta desbloqueada por admin"));
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username + "', fullName='" + fullName + "', role=" + role +
               ", locked=" + locked + ", failedAttempts=" + failedLoginAttempts + "}";
    }
}


    

