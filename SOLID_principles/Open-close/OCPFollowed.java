    //           UserRepository
    //                 ▲
    //       ┌─────────┼──────────┐
    //       │         │          │
    //    SQLRepo   FileRepo   MongoRepo


    /**
 * Open-Closed Principle (OCP) - Followed
 *
 * OCP:
 * A class should be open for extension but closed for modification.
 *
 * We achieve this using an abstraction:
 *
 *        UserService
 *             |
 *             v
 *       UserRepository
 *             ^
 *             |
 *  +---+----------+-----------+
 *  |              |           |
 * SQLRepository FileRepository MongoRepository
 *
 * To add a new persistence mechanism, we create a new implementation
 * of UserRepository without modifying UserService.
 */
public class OCPFollowed {

    // ------------------------------------------------------------
    // User
    // ------------------------------------------------------------

    static class User {

        private final String id;
        private final String name;
        private final String email;

        public User(
                String id,
                String name,
                String email
        ) {
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
    // Abstraction
    // ------------------------------------------------------------

    /**
     * Defines what persistence should be able to do.
     *
     * UserService doesn't care HOW the user is stored.
     */
    interface UserRepository {

        void save(User user);
    }


    // ------------------------------------------------------------
    // SQL Persistence
    // ------------------------------------------------------------

    static class SQLUserRepository
            implements UserRepository {

        @Override
        public void save(User user) {

            System.out.println(
                    "Saving user to SQL database:"
            );

            System.out.println(
                    "INSERT INTO users " +
                    "(id, name, email) VALUES (" +
                    "'" + user.getId() + "', " +
                    "'" + user.getName() + "', " +
                    "'" + user.getEmail() + "')"
            );
        }
    }


    // ------------------------------------------------------------
    // File Persistence
    // ------------------------------------------------------------

    static class FileUserRepository
            implements UserRepository {

        @Override
        public void save(User user) {

            System.out.println(
                    "Saving user to file:"
            );

            System.out.println(
                    "users.txt <- " +
                    user.getId() + "," +
                    user.getName() + "," +
                    user.getEmail()
            );
        }
    }


    // ------------------------------------------------------------
    // MongoDB Persistence
    // ------------------------------------------------------------

    static class MongoUserRepository
            implements UserRepository {

        @Override
        public void save(User user) {

            System.out.println(
                    "Saving user to MongoDB:"
            );

            System.out.println(
                    "{ " +
                    "\"_id\": \"" + user.getId() + "\", " +
                    "\"name\": \"" + user.getName() + "\", " +
                    "\"email\": \"" + user.getEmail() + "\"" +
                    " }"
            );
        }
    }


    // ------------------------------------------------------------
    // Business Logic
    // ------------------------------------------------------------

    /**
     * UserService is CLOSED for modification.
     *
     * It does not know:
     *
     * - SQL
     * - File
     * - MongoDB
     *
     * It only knows UserRepository.
     */
    static class UserService {

        private final UserRepository repository;

        public UserService(UserRepository repository) {
            this.repository = repository;
        }

        public void saveUser(User user) {

            repository.save(user);
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


        // --------------------------------------------------------
        // SQL
        // --------------------------------------------------------

        UserRepository sqlRepository =
                new SQLUserRepository();

        UserService sqlUserService =
                new UserService(sqlRepository);

        sqlUserService.saveUser(user);


        // --------------------------------------------------------
        // FILE
        // --------------------------------------------------------

        UserRepository fileRepository =
                new FileUserRepository();

        UserService fileUserService =
                new UserService(fileRepository);

        fileUserService.saveUser(user);


        // --------------------------------------------------------
        // MONGODB
        // --------------------------------------------------------

        UserRepository mongoRepository =
                new MongoUserRepository();

        UserService mongoUserService =
                new UserService(mongoRepository);

        mongoUserService.saveUser(user);
    }
}