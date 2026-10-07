//this is the main class for the document editor application
public class DocumentEditor  {
    
    private Document document;
    private Persistence persistence;

    public DocumentEditor(Document document, Persistence persistence) { //condtructor for the document editor class
        this.document = document;
        this.persistence = persistence;
    }

    public void addText(String text) {
        document.addElement(new TextElement(text));
    }

    public void addImage(String imgPath) {
        document.addElement(new ImgElement(imgPath));
    }

    public void save(String filePath) {
        persistence.save(document, filePath);
    }

    public void open(String filePath) {
        document = persistence.open(filePath);
    }

}