/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oradentscript;

import java.awt.AWTException;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ocr.OcrEngine;
import system.manager.ActionManager;
import system.manager.IndexEditorService;
import system.resource.ActionResource;
import tools.PersonEntity;
import tools.ScriptCreatorService;

/**
 *
 * @author Manel
 */
public class OradentScript {

    int offX;
    int offY;
    Dimension sizeOnPress;
    final ScriptCreatorService sequenceCreator;
    final OcrEngine ocrEngine;
    ActionManager scriptActionManager;
    ActionResource scriptActionResource;
    IndexEditorService editorService;

    public OradentScript() throws URISyntaxException {
        ocrEngine = new OcrEngine();
        scriptActionResource = new ActionResource();
        scriptActionManager = new ActionManager(scriptActionResource);
        editorService = new IndexEditorService(scriptActionManager);
        sequenceCreator = new ScriptCreatorService(ocrEngine, scriptActionManager, editorService);
        Object monitor = new Object();
        synchronized (monitor) {
            sequenceCreator.startGui(monitor);
        }

    }

    /**
     * @param args the command line arguments
     * @throws java.awt.AWTException
     * @throws java.lang.InterruptedException
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws AWTException, InterruptedException, URISyntaxException, IOException, ClassNotFoundException {
        
        if (args.length == 0) {
            OradentScript oradentScript = new OradentScript();
        } else {
            ActionManager manager = new ActionManager(new ActionResource());
            List<PersonEntity> index = manager.getIndex(Paths.get(args[0]));
            for (int i = 0; i < index.size(); i++) {
                PersonEntity entity = index.get(i);
                System.out.println(String.format("--%s--", Integer.toString(i)));
                System.out.println(entity);
                System.out.println(String.format("--/%s--", Integer.toString(i)));
            }
        }
         

 /*
        ActionManager manager = new ActionManager(new ActionResource());
        List<PersonEntity> entits = manager.getIndex(Paths.get("C:\\Users\\Manel\\Desktop\\index.ix"));
        entits.forEach((PersonEntity person) -> {
            if (!person.sex.equals("male") && !person.sex.equals("female")) {
                person.sex = "male";
            }
        });
        entits.forEach((PersonEntity person) -> {
            if (!person.sex.equals("male") && !person.sex.equals("female")) {
                System.out.println(person.sex);
            }
        });
        Pattern p = Pattern.compile("\\d\\d/\\d\\d/\\d\\d\\d\\d");
        entits.forEach((PersonEntity person) -> {
            person.birthDate = person.birthDate.replaceAll(" ", "");
            Matcher m = p.matcher(person.birthDate);
            if (!m.find()) {
                person.birthDate = "notspecified";
            }
        });

        entits.forEach((PersonEntity person) -> {
            Matcher m = p.matcher(person.birthDate);
            if (!m.find()) {
                System.out.println(person.birthDate);
            }
        });
        manager.setIndex(entits, Paths.get("C:", "Users", "manel", "desktop"), "index.ix");
         */
        /*
        ActionManager manager = new ActionManager(new ActionResource());
        List<PersonEntity> entits = manager.getIndex(Paths.get("C:\\Users\\Manel\\Desktop\\fi.ix"));
        entits.forEach((PersonEntity person) -> {
            if (!Paths.get(person.directory).toFile().exists()) {
                System.out.println(String.format("Nu exista root directory-u %s", person.directory));
            } else {
                File[] files = Paths.get(person.directory).toFile().listFiles();
                int i = 0;
                Map<String, String> images = new HashMap<>();
                for (String key : person.images.keySet()) {
                    if (files.length > 0 && i<files.length) {
                        images.put(key, files[i].getName());
                        i++;
                    }
                }
                person.images = images;
            }
        });
        entits.forEach((PersonEntity person) -> {
            if (!Paths.get(person.directory).toFile().exists()) {
                System.out.println(String.format("Nu exista root directory-u %s", person.directory));
            } else {
                for (String key : person.images.keySet()) {
                    File image = Paths.get(person.directory, person.images.get(key)).toFile();
                    if (!image.exists()) {
                        System.out.println(image.getPath());
                    }
                }
            }
        });
        //manager.setIndex(entits, Paths.get("C:", "Users", "manel", "desktop"), "finalIndex.ix");
 */
    }
}
//<editor-fold desc="dump" defaultstate="collapsed">
/*
List<Action> actions = new ArrayList<>();
        OcrEngine ocrEngine = new OcrEngine();

        ActionExecutor executor = new ActionExecutor(ocrEngine);

        Action action1 = new Action(Predicate.MouseMove);
        action1.addToMap(ActionKey.XCoordinate, 50);
        action1.addToMap(ActionKey.YCoordinate, 50);

        Action action2 = new Action(Predicate.MouseMove);
        action2.addToMap(ActionKey.XCoordinate, 300);
        action2.addToMap(ActionKey.YCoordinate, 300);

        Action action3 = new Action(Predicate.MouseClick);
        action3.addToMap(ActionKey.MouseClickType, SystemManager.LEFT_CLICK);

        Action action4 = new Action(Predicate.MouseClick);
        action4.addToMap(ActionKey.MouseClickType, SystemManager.RIGHT_CLICK);

        Action action5 = new Action(Predicate.MouseClick);
        action5.addToMap(ActionKey.MouseClickType, SystemManager.MIDDLE_CLICK);

        Action action6 = new Action(Predicate.MouseWheel);
        action6.addToMap(ActionKey.MouseWheelDirection, SystemManager.SCROLL_DOWN);
        action6.addToMap(ActionKey.MouseWheelAmount, 5);

        UUID pacientId = UUID.randomUUID();
        UUID otherPacientId = UUID.randomUUID();
        UUID dateId = UUID.randomUUID();

        Action action7 = new Action(Predicate.TakeOcrScreenshoot);
        action7.addToMap(ActionKey.OcrScreenshotRectangle, new Rectangle(new Point(18, 241), new Dimension(114, 29)));
        action7.addToMap(ActionKey.StoreOcrValues, pacientId);

        Action action8 = new Action(Predicate.TakeOcrScreenshoot);
        action8.addToMap(ActionKey.OcrScreenshotRectangle, new Rectangle(new Point(18, 311), new Dimension(144, 29)));
        action8.addToMap(ActionKey.StoreOcrValues, otherPacientId);

        Action action9 = new Action(Predicate.TakeOcrScreenshoot);
        action9.addToMap(ActionKey.OcrScreenshotRectangle, new Rectangle(new Point(716, 484), new Dimension(73, 26)));
        action9.addToMap(ActionKey.StoreOcrValues, dateId);

        Action action10 = new Action(Predicate.CreateFolder);
        action10.addToMap(ActionKey.FolderPath, Paths.get("E:", "ScriptData"));
        action10.addToMap(ActionKey.FolderName, pacientId);

        Action action11 = new Action(Predicate.CreateFolder);
        action11.addToMap(ActionKey.FolderPath, Paths.get("E:", "ScriptData"));
        action11.addToMap(ActionKey.FolderName, otherPacientId);

        Action action12 = new Action(Predicate.TypeSentence);
        action12.addToMap(ActionKey.SentenceToType, pacientId);

//        actions.add(action1);
//        actions.add(action2);
//        actions.add(action3);
//        actions.add(action4);
//        actions.add(action5);
//        actions.add(action6);
        
        ComponentResizer cr = new ComponentResizer();
        JFrame f = new JFrame();
        f.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JFrame f = (JFrame) e.getSource();
                OradentScript.this.offX = e.getX();
                offY = e.getY();
                sizeOnPress = f.getSize();
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

        f.setSize(200, 200);
        f.setUndecorated(true);
        f.setOpacity(0.5f);
        cr.registerComponent(f);
        f.setVisible(true);
        f.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent ce) {
                System.out.println(f.getSize() + " " + f.getLocationOnScreen());
            }

            @Override
            public void componentMoved(ComponentEvent ce) {
                System.out.println(f.getSize() + " " + f.getLocationOnScreen());
            }

            @Override
            public void componentShown(ComponentEvent ce) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void componentHidden(ComponentEvent ce) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
         
        actions.add(action7);
        //actions.add(action8);
        //actions.add(action9);
        actions.add(action10);
        //actions.add(action11);
        //actions.add(action12);
        for (Action action : actions) {
            executor.acomplish(action);
            }
        ActionManager manager = new ActionManager(new ActionResource());
//        try {
//            manager.serializeScript(actions, Paths.get("E:", "ScriptData"), "indexer.script");
//        } catch (IOException ex) {
//            Logger.getLogger(OradentScript.class.getName()).log(Level.SEVERE, null, ex);
//        }
 */
//</editor-fold>
