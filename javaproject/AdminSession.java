public final class AdminSession {
    private static boolean authenticated = false;
    private AdminSession() {
    }
    public static void login() {
        authenticated = true;
    }
    public static void logout() {
        authenticated = false;
    }
    public static boolean active() {
        return authenticated;
    }
}
