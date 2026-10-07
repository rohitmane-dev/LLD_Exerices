public class TextElement extends DocumentElement {
    private String text;

    public TextElement(String text) {
        this.text = text;
    }

    @Override
    public void render() {
        System.out.println("Rendering text: " + text);
    }
}