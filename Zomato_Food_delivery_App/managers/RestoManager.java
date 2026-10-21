package managers;

import java.util.ArrayList;
import java.util.List;
import models.Resto;

// Singleton: one registry of restaurants for the whole process.
public class RestoManager {
    private static RestoManager instance;

    private final List<Resto> restos = new ArrayList<>();

    private RestoManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized RestoManager getInstance() {
        if (instance == null) {
            instance = new RestoManager();
        }
        return instance;
    }

    public void addResto(Resto resto) {
        restos.add(resto);
    }

    public List<Resto> searchByLocation(String loc) {
        List<Resto> result = new ArrayList<>();
        for (Resto resto : restos) {
            if (resto.getLoc().equalsIgnoreCase(loc)) {
                result.add(resto);
            }
        }
        return result;
    }

    public Resto getRestoById(int restoId) {
        for (Resto resto : restos) {
            if (resto.getRestoId() == restoId) {
                return resto;
            }
        }
        return null;
    }

    public List<Resto> getAllRestos() {
        return List.copyOf(restos);
    }
}
