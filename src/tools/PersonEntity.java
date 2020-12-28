/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Manel
 */
public class PersonEntity implements Serializable {

    public String name;
    public String birthDate;
    public String sex;
    /**
     * path-ul complet spre folderul care contine pozele pacientului (E:\ScriptData\Isus)
     */
    public String directory;
    /**
     * ele is salvate din program cu p1,p2,p3.. indexeru o sa faca legatura intre numele ala (p1,p2...) si numele adevarat (data la care a fost facuta radiografia) images.put(dataPoza,p1) images.put(dataPoza2,p2) images.put(9/11/2001-09:11:01)
     */
    public Map<String, String> images;

    public PersonEntity(String name, String birthDate, String sex, String directory, Map<String, String> images) {
        this.name = name;
        this.birthDate = birthDate;
        this.sex = sex;
        this.directory = directory;
        this.images = images;
    }

    public PersonEntity() {
        images = new HashMap<>();
    }

    public void addImage(String ocrDate, int imageCount) {
        String key = ocrDate.replaceAll("/", "-").replaceAll(" ", "_").replaceAll(":", "-");
        if (images.get(String.format("%s.jpg", key)) == null) {
            images.put(String.format("%s.jpg", key), String.format("p%s.jpg", Integer.toString(imageCount)));
        } else {
            int counter = 1;
            while (images.get(String.format("%s(%s).jpg", key, Integer.toString(counter))) != null) {
                counter++;
            }
            images.put(String.format("%s(%s).jpg", key,counter), String.format("p%s.jpg", Integer.toString(imageCount)));
        }
    }

    @Override
    public String toString() {
        return String.format(
                "\nName: %s"
                + "\nBirthDate: %s"
                + "\nSex: %s"
                + "\nDirectory: %s"
                + "\nImages: %s",
                name,
                birthDate,
                sex,
                directory,
                images
        );
    }

}
