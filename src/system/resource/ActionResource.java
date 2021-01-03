/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.resource;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.json.simple.JSONArray;
import tools.Action;
import tools.PersonEntity;

/**
 *
 * @author Manel
 */
public class ActionResource implements ActionResourceInterface {

    @Override
    public void serializeScript(List<Action> actions, Path path, String fileName) throws IOException {
        //<editor-fold desc="body" defaultstate="collapsed">
        try (FileOutputStream file
                = new FileOutputStream(
                        Paths.get(path.toString(), fileName).toString()
                ); ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(actions);
        }
        //</editor-fold>
    }

    @Override
    public List<Action> getScript(Path path, String fileName) throws IOException, ClassNotFoundException {
        try (FileInputStream file
                = new FileInputStream(
                        Paths.get(path.toString(), fileName).toString()
                ); ObjectInputStream in = new ObjectInputStream(file)) {
            return (List<Action>) in.readObject();
        }
    }

    @Override
    public void addToIndex(PersonEntity entity, Path path, String fileName) throws IOException, ClassNotFoundException {
        File checkExists = Paths.get(path.toString(), fileName).toFile();
        if (!checkExists.exists()) {
            checkExists.createNewFile();
            try (FileOutputStream outFile
                    = new FileOutputStream(
                            Paths.get(path.toString(), fileName).toString()
                    ); ObjectOutputStream out = new ObjectOutputStream(outFile)) {
                out.writeObject(new ArrayList<>());
                System.out.println("Index nou");
            }
        }
        try (FileInputStream file
                = new FileInputStream(
                        Paths.get(path.toString(), fileName).toString()
                ); ObjectInputStream in = new ObjectInputStream(file)) {
            List<PersonEntity> index = (List<PersonEntity>) in.readObject();
            index.add(entity);
            try (FileOutputStream outFile
                    = new FileOutputStream(
                            Paths.get(path.toString(), fileName).toString()
                    ); ObjectOutputStream out = new ObjectOutputStream(outFile)) {
                out.writeObject(index);
            }
            System.out.println("Adaugat in index " + entity.toString());
        }
    }

    @Override
    public List<PersonEntity> getIndex(Path path, String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        try (FileInputStream file
                = new FileInputStream(
                        Paths.get(path.toString(), fileName).toString()
                ); ObjectInputStream in = new ObjectInputStream(file)) {
            return (List<PersonEntity>) in.readObject();
        }
    }

    public List<PersonEntity> getIndex(Path path) throws FileNotFoundException, IOException, ClassNotFoundException {
        try (FileInputStream file
                = new FileInputStream(
                        Paths.get(path.toString()).toString()
                ); ObjectInputStream in = new ObjectInputStream(file)) {
            return (List<PersonEntity>) in.readObject();
        }
    }

    @Override
    public void setIndex(List<PersonEntity> index, Path path, String fileName) throws IOException, ClassNotFoundException {
        try (FileOutputStream outFile
                = new FileOutputStream(
                        Paths.get(path.toString(), fileName).toString()
                ); ObjectOutputStream out = new ObjectOutputStream(outFile)) {
            out.writeObject(index);
        }
    }

    @Override
    public void exportJsonArray(JSONArray array, Path path, String fileName) throws IOException, ClassNotFoundException {
        try (FileWriter output = new FileWriter(Paths.get(path.toString(), fileName).toFile())) {
            output.write(array.toJSONString());
        }
    }

    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private void writeCompressedImage(File imageFile, double compress, double resize) throws FileNotFoundException, IOException {
        File compressedImageFile = new File(imageFile.getParent(), String.format("%s-min.jpg", imageFile.getName().replace(".jpg", "")));
        OutputStream os;
        ImageWriter writer;
        ImageOutputStream ios;
        try (InputStream is = new FileInputStream(imageFile)) {
            os = new FileOutputStream(compressedImageFile);
            double quality = 1 - compress;
            BufferedImage image = ImageIO.read(is);
            image = toBufferedImage(
                    image.getScaledInstance(
                            (int) (image.getWidth() * resize),
                            (int) (image.getHeight() * resize),
                            Image.SCALE_SMOOTH)
            );

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (!writers.hasNext()) {
                throw new IllegalStateException("No writers found");
            }

            writer = (ImageWriter) writers.next();
            ios = ImageIO.createImageOutputStream(os);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality((float) quality);

            writer.write(null, new IIOImage(image, null, null), param);

            os.close();
            ios.close();
            writer.dispose();
        }

    }

    @Override
    public void minimizeImages(List<File> images, double compression, double resize) {
        try {
            for (File image : images) {
                System.out.println(image.getParent());
                writeCompressedImage(image, 0.6, 0.5);
            }
        } catch (IOException ex) {
            Logger.getLogger(ActionResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
