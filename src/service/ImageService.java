package service;

import java.io.*;
import java.util.InputMismatchException;

public class ImageService {
    private static ImageService instance = null;

    private ImageService() {}

    public static ImageService getInstance() {
        if (instance == null)
            instance = new ImageService();
        return instance;
    }

    public String uploadImage(int userId, String path) throws IOException {
        if (!path.substring(path.length() - 3).equals("jpg"))
            throw new IllegalArgumentException("No image selected");

        String outPath = "resources/" + Integer.toString(userId) + ".jpg";
        try (InputStream in = new FileInputStream(new File(path));
             OutputStream out = new FileOutputStream(new File(outPath))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw e;
        }

        return outPath;
    }

    public static void main(String[] args) throws IOException {
        ImageService imageService = ImageService.getInstance();
        String res = imageService.uploadImage(8, "C:\\Users\\Alexandra\\Pictures\\flower3.jpg");
        System.out.println(res);
    }
}
