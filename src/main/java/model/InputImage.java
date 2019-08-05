package model;

public class InputImage {
    private String path;
    private byte[] data;
    private int width;
    private int height;

    public InputImage(String path, byte[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.path = path;
    }

    public InputImage() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
