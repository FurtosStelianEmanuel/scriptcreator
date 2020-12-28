/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import system.SystemManager;

/**
 *
 * @author Manel
 */
public class OcrEngine {

    private final Tesseract tesseract;
    private final int SCALLING_FACTOR = 3;

    public OcrEngine() throws URISyntaxException {
        tesseract = new Tesseract();
        String trainingDataPath = Paths.get(SystemManager.PATH, "trainingset", "tessdata").toString();
        System.out.println("Caut tessdata in" + trainingDataPath);
        tesseract.setDatapath(trainingDataPath);
    }

    class caca extends JPanel {

        Image image;

        public caca(Image image) {
            this.image = image;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
        }
    }

    private BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        /*
        int backgroundColor = new Color(dimg.getRGB(newW - 1, newH - 1)).getRed();
        for (int i = 0; i < dimg.getWidth(); i++) {
            for (int j = 0; j < dimg.getHeight(); j++) {
                int myVal = new Color(dimg.getRGB(i, j)).getRed();
                if (Math.abs(myVal - backgroundColor) <= 100) {
                    dimg.setRGB(i, j, Color.WHITE.getRGB());
                } else {
                    dimg.setRGB(i, j, Color.BLACK.getRGB());
                }
            }
        }
         */
        return dimg;
    }

    int offX, offY;
    Dimension sizeOnPress;

    private BufferedImage preprocessedImage(BufferedImage image) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);

        BufferedImage finalImage = op.filter(resize(image, image.getWidth() * SCALLING_FACTOR, image.getHeight() * SCALLING_FACTOR), null);

        int dim = 3;
        float data[] = new float[dim * dim];

        for (int i = 0; i < data.length; i++) {
            data[i] = 0.1f;
        }
        Kernel kernel = new Kernel(dim, dim, data);
        BufferedImageOp op1 = new ConvolveOp(kernel);
        finalImage = op1.filter(finalImage, null);

        JFrame f = new JFrame();
        f.setUndecorated(true);
        f.setSize(finalImage.getWidth(), finalImage.getHeight());
        f.setLocation((int) (SystemManager.getScreenSize().getWidth() / 2 + 150), (int) (SystemManager.getScreenSize().getHeight() / 2));
        f.add(new caca(finalImage.getScaledInstance(finalImage.getWidth(), finalImage.getHeight(), Image.SCALE_SMOOTH)));
        f.setVisible(true);
        f.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JFrame f = (JFrame) e.getSource();
                offX = e.getX();
                offY = e.getY();
                sizeOnPress = f.getSize();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    f.dispose();
                }
            }
        });
        f.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                JFrame f = (JFrame) e.getSource();
                if (sizeOnPress.equals(f.getSize())) {
                    f.setLocation(f.getX() + e.getX() - offX, f.getY() + e.getY() - offY);
                }
            }
        });

        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        f.dispose();
                    }
                }, 1000);
            }
        });

        return finalImage;
    }

    public String process(BufferedImage image) throws TesseractException {
        return tesseract.doOCR(preprocessedImage(image));
    }
}
