//An Interface for the persistence layer of the application. It defines the methods that need to be implemented for data storage and retrieval.
public interface Persistence {
    void save(Document document, String filePath);
    Document open(String filePath);
}