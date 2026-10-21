package models;

import java.util.List;

public class Resto {
    private int restoId;
    private String name;
    private String loc;
    private List<MenuItems> menu;

    public Resto(int restoId, String name, String loc, List<MenuItems> menu) {
        this.restoId = restoId;
        this.name = name;
        this.loc = loc;
        this.menu = menu;
    }

    public void addMenuItem(MenuItems item) {
        menu.add(item);
    }

    // Lives here, not in the facade: the restaurant owns its menu, so it answers
    // questions about it (tell-don't-ask).
    public MenuItems findItemById(String itemId) {
        for (MenuItems item : menu) {
            if (item.getItemId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }

    public int getRestoId() {
        return restoId;
    }

    public void setRestoId(int restoId) {
        this.restoId = restoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public List<MenuItems> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuItems> menu) {
        this.menu = menu;
    }
}
