package model;

public class Category {
    private int id;
    private Integer userId;
    private String name;
    private String type;
    private String icon;

    public Category() {}

    public Category(String name, String type, Integer userId) {
        this.name = name;
        this.type = type;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}