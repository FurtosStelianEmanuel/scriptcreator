/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sourceforge.tess4j.TesseractException;
import ocr.OcrEngine;
import system.SystemManager;
import system.manager.ActionManager;

/**
 *
 * @author Manel
 */
public class ActionExecutor {

    private Robot executor;
    private OcrEngine ocrEngine;
    String errorMessage = "Eroare la parsuirea actiunii, valori nule in map";
    Map<Object, Object> memory;
    private JFrame rootFrame;
    private ActionManager manager;

    public ActionExecutor(OcrEngine ocrEngine, JFrame rootFrame, ActionManager manager) {
        try {
            executor = new Robot();
            memory = new HashMap<>();
            this.ocrEngine = ocrEngine;
            this.rootFrame = rootFrame;
            this.manager = manager;
        } catch (AWTException ex) {
            Logger.getLogger(ActionExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void highlightScreenshotArea(Rectangle rect) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                JFrame f = new JFrame();
                f.setSize(rect.getSize());
                f.setLocation(rect.getLocation());
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setUndecorated(true);
                f.setOpacity(0.5f);
                f.setType(Window.Type.POPUP);
                f.setVisible(true);
                f.setAlwaysOnTop(true);
                f.getContentPane().setBackground(Color.yellow);
                f.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowOpened(WindowEvent we) {
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                            @Override
                            public void run() {
                                f.setVisible(false);
                                f.dispose();
                            }
                        },
                                300
                        );
                    }
                });
            }
        }).start();
    }

    private void mouseMove(int x, int y) {
        executor.mouseMove(x, y);
    }

    private void mouseClick(int type) {
        executor.mousePress(type);
        executor.mouseRelease(type);
    }

    private void mouseWheel(int direction, int amount) {
        while (amount > 0) {
            executor.mouseWheel(direction);
            amount--;
        }
    }

    private void ocrScreenshot(Rectangle imageRectangle, UUID memoryLocation, boolean confirmRequired) throws TesseractException {
        highlightScreenshotArea(imageRectangle);
        BufferedImage screenshot = executor.createScreenCapture(imageRectangle);
        String result = ocrEngine.process(screenshot).trim();
        if (confirmRequired) {
            String confirmed = JOptionPane.showInputDialog("Confirm OCR result", result);
            if (confirmed == null) {// in caz ca apasa pe cancel
                confirmed = result;
            }
            memory.put(memoryLocation, confirmed);
        } else {
            memory.put(memoryLocation, result);
        }
    }

    //o sa bage in memory folderul pe care il creeaza, deci sa nu faci mai mult de doua foldere in acelasi script 
    //ca ma gandesc ca pusca ceva, n am chef acuma sa repar
    private void createFolder(Path folderPath, Object folderName) {
        String resultFolderName;
        if (folderName instanceof UUID) {
            UUID id = (UUID) folderName;
            resultFolderName = ((String) (memory.get(id))).trim();
        } else {
            resultFolderName = (String) folderName;
        }
        File f = Paths.get(folderPath.toString(), resultFolderName).toFile();
        if (f.exists()) {
            System.out.println("Folder deja existent " + f.getPath());
        } else {
            f.mkdir();
        }
        if (memory.put(ActionKey.FolderPath, folderPath.toString()) != null || memory.put(ActionKey.FolderName, resultFolderName) != null) {
            System.out.println("Ai facut mai multe fodlere intr-un script");
        }
    }

    private void typeSentence(Object sentence) {
        String actualSentence;
        actualSentence = sentence instanceof UUID ? ((String) memory.get(sentence)).trim() : (String) sentence;
        for (int i = 0; i < actualSentence.length(); i++) {
            char c = actualSentence.charAt(i);
            if (Character.isUpperCase(c)) {
                executor.keyPress(KeyEvent.VK_SHIFT);
                executor.keyPress((int) c);
                executor.keyRelease((int) c);
                executor.keyRelease(KeyEvent.VK_SHIFT);
            } else {
                int validKey = c;
                if (Character.isAlphabetic(validKey)) {
                    validKey = validKey - 32;
                }
                executor.keyPress(validKey);
                executor.keyRelease(validKey);
            }
        }
    }

    public void acomplish(Action action) {
        String delayString = (String) action.actionsMap.get(ActionKey.EndDelay);
        //delayu era sus ugaubuga(dont over think it)
        if (delayString == null) {
            System.out.println("DelayString gol");
            return;
        }
        int delay = Integer.valueOf(delayString);
        switch (action.predicate) {
            //<editor-fold desc="MouseClick" defaultstate="collapsed">
            case MouseClick: {
                String clickTypeString = (String) action.actionsMap.get(ActionKey.MouseClickType);
                if (clickTypeString == null) {
                    System.out.println("Click type null");
                } else {
                    int type = Integer.valueOf(clickTypeString);
                    mouseClick(type);
                }
                break;
            }
            //</editor-fold>
            //<editor-fold desc="MouseMove" defaultstate="collapsed">
            case MouseMove: {
                String xString = (String) action.actionsMap.get(ActionKey.XCoordinate);
                String yString = (String) action.actionsMap.get(ActionKey.YCoordinate);
                if (xString == null || yString == null) {
                    System.out.println(errorMessage);
                } else {
                    int x = Integer.valueOf(xString), y = Integer.valueOf(yString);
                    mouseMove(x, y);
                }
                break;
            }
            //</editor-fold>
            //<editor-fold desc="MouseWheel" defaultstate="collapsed">
            case MouseWheel: {
                String directionString = (String) action.actionsMap.get(ActionKey.MouseWheelDirection);
                String amountString = (String) action.actionsMap.get(ActionKey.MouseWheelAmount);
                if (directionString == null || amountString == null) {
                    System.out.println(errorMessage);
                } else {
                    int direction = Integer.valueOf(directionString);
                    int amount = Integer.valueOf(amountString);
                    mouseWheel(direction, amount);
                }
                break;
            }
            //</editor-fold>
            //<editor-fold desc="TakeOcrScreenshot" defaultstate="collapsed">
            case TakeOcrScreenshot: {
                Rectangle rect = (Rectangle) action.actionsMap.get(ActionKey.OcrScreenshotRectangle);
                UUID memoryLocation = (UUID) action.actionsMap.get(ActionKey.StoreOcrValues);
                Object confirmRequiredObject = action.actionsMap.get(ActionKey.ConfirmValueRequired);

                if (rect == null || memoryLocation == null || confirmRequiredObject == null) {
                    System.out.println(errorMessage + " " + rect + " " + memoryLocation + " " + confirmRequiredObject);
                } else {
                    try {
                        boolean confirmRequired = (boolean) confirmRequiredObject;
                        ocrScreenshot(rect, memoryLocation, confirmRequired);
                    } catch (TesseractException ex) {
                        Logger.getLogger(ActionExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            }
            //</editor-fold>
            //<editor-fold desc="CreateFolder" defaultstate="collapsed">
            case CreateFolder: {
                Path folderPath = Paths.get((String) action.actionsMap.get(ActionKey.FolderPath));
                Object folderName = action.actionsMap.get(ActionKey.FolderName);
                if (folderPath == null || folderName == null) {
                    System.out.println(errorMessage);
                } else {
                    createFolder(folderPath, folderName);
                }
                break;
            }
            //</editor-fold>
            //<editor-fold desc="TypeSentence" defaultstate="collapsed">
            case TypeSentence: {
                Object sentence = action.actionsMap.get(ActionKey.SentenceToType);
                if (sentence == null) {
                    System.out.println(errorMessage);
                } else {
                    typeSentence(sentence);
                }
                break;
            }
            //</editor-fold>
            //<editor-fold desc="CustomOradentClickPictogrames" defaultstate="collapsed">
            case CustomOradentClickPictogrames: {
                Rectangle startImage = (Rectangle) action.actionsMap.get(ActionKey.CustomOradentFirstImageRectangle);
                Rectangle horizontalSpacingRectangle = (Rectangle) action.actionsMap.get(ActionKey.CustomOradentHorizontalSeparationRectangle);
                Rectangle lastImage = (Rectangle) action.actionsMap.get(ActionKey.CustomOradentLastImageRectangle);
                Rectangle verticalImage = (Rectangle) action.actionsMap.get(ActionKey.CustomOradentVerticalSeparationRectangle);
                Rectangle firstDate = (Rectangle) action.actionsMap.get(ActionKey.CustomOradentFirstImageDateRectangle);

                Rectangle currentImage = startImage;
                Rectangle currentDate = firstDate;

                PersonEntity mockImages = new PersonEntity();
                int imageCount = 0;
                while (true) {

                    int mx = (int) (currentImage.getX() + currentImage.getWidth() / 2);
                    int my = (int) (currentImage.getY() + currentImage.getHeight() / 2);
                    mouseMove(mx, my);
                    mouseClick(SystemManager.LEFT_CLICK);

                    UUID imageLocation = UUID.randomUUID();
                    try {
                        ocrScreenshot(currentDate, imageLocation, true);
                    } catch (TesseractException ex) {
                        Logger.getLogger(ActionExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    mockImages.addImage((String) memory.get(imageLocation), ++imageCount);

                    int newX = (int) (currentImage.getX() + currentImage.getWidth() + horizontalSpacingRectangle.getWidth());
                    int newY = (int) currentImage.getY();

                    if (newX >= lastImage.getX() + lastImage.getWidth()) {
                        newX = (int) startImage.getX();
                        newY = (int) (currentImage.getY() + currentImage.getHeight() + verticalImage.getHeight());
                    }

                    currentImage = new Rectangle(
                            new Point(
                                    newX,
                                    newY
                            ),
                            currentImage.getSize()
                    );

                    int newDateX = (int) (currentDate.getX() + currentDate.getWidth() + horizontalSpacingRectangle.getWidth());
                    int newDateY = (int) (currentDate.getY());
                    if (newDateX >= lastImage.getX() + lastImage.getWidth()) {
                        newDateY = (int) (newY + currentImage.getHeight());
                        newDateX = (int) firstDate.getX();
                    }

                    currentDate = new Rectangle(
                            new Point(newDateX, newDateY),
                            currentDate.getSize()
                    );
                    int choice = JOptionPane.showConfirmDialog(rootFrame, "Continue checking?");
                    if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.NO_OPTION) {
                        memory.put(ActionKey.CustomOradentPacientImages, mockImages.images);
                        break;
                    }
                }
                break;
            }
            //</editor-fold>
            //<editor-fold desc="CustomOradentAddPacientToIndexer" defaultstate="collapsed">
            case CustomOradentAddPacientToIndexer: {
                Object nameObj = action.actionsMap.get(ActionKey.CustomOradentPacientName);
                Object dateObj = action.actionsMap.get(ActionKey.CustomOradentPacientBirthDate);
                Object sexObj = action.actionsMap.get(ActionKey.CustomOradentPacientSex);
                System.out.println(memory);
                Object directoryObj = Paths.get(((String) memory.get(ActionKey.FolderPath)), (String) memory.get(ActionKey.FolderName));
                Object imagesObj = memory.get(ActionKey.CustomOradentPacientImages);
                if (nameObj == null || dateObj == null || sexObj == null || directoryObj == null || imagesObj == null) {
                    System.out.println(
                            String.format(
                                    "%s - %s %s %s %s %s", errorMessage, nameObj, dateObj, sexObj, directoryObj, imagesObj
                            )
                    );
                } else {
                    String name = (String) memory.get(nameObj);
                    String date = (String) memory.get(dateObj);
                    String sex = (String) memory.get(sexObj);
                    Path directoryPath = (Path) directoryObj;
                    Map<String, String> images = (Map<String, String>) imagesObj;
                    PersonEntity personEntity = new PersonEntity(name, date, sex, directoryPath.toString(), images);
                    try {
                        manager.addToIndex(personEntity, Paths.get("D:","IndexData"), "index.ix");
                    } catch (ClassNotFoundException | IOException ex) {
                        Logger.getLogger(ActionExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            }
            //</editor-fold>
        }
    }

    public void reset() {
        memory.clear();
    }

}
