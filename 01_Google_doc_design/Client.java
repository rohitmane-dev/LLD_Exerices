//this is a cleint side implementation of the google doc design

public class Client {

    public static void main(String[] args) {
        // create a new files and get them...

        // Client
        Document doc = new Document();
        DocumentEditor editor = new DocumentEditor(doc, new FileStorage());
        
        
        editor.addText("Hello People!!");
        editor.addText("This is my first try to implement a lld design.");
        editor.addImage("ekImage.png");
        
        //storage wala part
        editor.save("notes01.doc");

        //redering wala part
        editor.open("notes01.doc");
        //or 
        DocumentRenderer renderer = new DocumentRenderer();
        renderer.render(doc);


        //Testing the document content
        assert doc.getElements().size() == 3 : "expected 3 elements";
        try {
            doc.getElements().clear();
            assert false : "getElements() must be read-only";
        } catch (UnsupportedOperationException expected) {
        }
        System.out.println("All checks passed");

    }
}