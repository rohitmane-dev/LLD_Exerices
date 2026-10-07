// This is the Document class for the document editor application
public class ImgElement extends DocumentElement {
    private String src;

    public ImgElement(String src) {
        this.src = src;
    }

    @Override
    public void render() {
        System.out.println("Rendering image: " + src);
    }
}
     