// This is the Document class for the document editor application
import java.util.*;
public class Document{

    private final List<DocumentElement> content;

    public Document() {
        content = new ArrayList<>();
    }

    public void addElement(DocumentElement element) {
        content.add(element);
    }
    public List<DocumentElement> getElements() {
        return Collections.unmodifiableList(content); // Return an unmodifiable view of the elements list to prevent external modification
    }

    public void render() {
        for (DocumentElement element : content) {
            element.render();
        }
    }


}