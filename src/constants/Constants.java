/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constants;

/**
 *
 * @author Manel
 */
public class Constants {

    public static class Action {

        public static final String[] PREDICATES = new String[]{
            tools.Predicate.MouseMove.toString(),
            tools.Predicate.MouseClick.toString(),
            tools.Predicate.MouseWheel.toString(),
            tools.Predicate.TypeSentence.toString(),
            tools.Predicate.CreateFolder.toString(),
            tools.Predicate.TakeOcrScreenshot.toString(),
            tools.Predicate.CustomOradentClickPictogrames.toString(),
            tools.Predicate.CustomOradentAddPacientToIndexer.toString()
        };
    }
}
