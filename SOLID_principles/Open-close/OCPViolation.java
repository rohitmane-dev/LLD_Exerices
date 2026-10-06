/**
 * Open-Closed Principle (OCP) - Violation
 *
 * OCP:
 * A class should be open for extension but closed for modification.
 *
 * This example violates OCP because every time we introduce a new
 * persistence mechanism, we must modify UserService.
 *
 * Current supported storage:
 * - SQL
 * - File
 * - MongoDB
 */
public class OCPViolation {

    // ------------------------------------------------------------
    // User
    // ------------------------------------------------------------

    static class User {

        private final String id;
        private final String name;
        private final String email;

        public User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }


    // ------------------------------------------------------------
    // ❌ OCP VIOLATION
    // ------------------------------------------------------------

    static class UserService {

        public void saveUser(User user, String storageType) {

            if (storageType.equalsIgnoreCase("SQL")) {

                // SQL persistence logic
                System.out.println(
                        "Saving user to SQL database..."
                );

            } else if (storageType.equalsIgnoreCase("FILE")) {

                // File persistence logic
                System.out.println(
                        "Saving user to file..."
                );

            } else if (storageType.equalsIgnoreCase("MONGODB")) {

                // MongoDB persistence logic
                System.out.println(
                        "Saving user to MongoDB..."
                );

            } else {

                throw new IllegalArgumentException(
                        "Unsupported storage type: " + storageType
                );
            }
        }
    }


    // ------------------------------------------------------------
    // Main
    // ------------------------------------------------------------

    public static void main(String[] args) {

        User user = new User(
                "101",
                "Rohit",
                "rohit@example.com"
        );

        UserService userService = new UserService();

        userService.saveUser(user, "SQL");

        userService.saveUser(user, "FILE");

        userService.saveUser(user, "MONGODB");
    }
}