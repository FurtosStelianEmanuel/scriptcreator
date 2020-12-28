/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.manager;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;
import system.SystemManager;
import system.resource.ActionResource;
import tools.Action;
import tools.ActionKey;
import tools.PersonEntity;
import tools.Predicate;

/**
 *
 * @author Manel
 */
public class ActionManagerTest {

    ActionManager actionManager;
    ActionResource actionResource;

    public ActionManagerTest() {
        actionResource = mock(ActionResource.class);
        actionManager = new ActionManager(actionResource);
    }

    @Test
    public void whenSerializeActionsThenActionsSerialized() throws IOException {
        Action a1 = new Action(Predicate.CreateFolder);
        a1.addToMap(ActionKey.FolderName, UUID.randomUUID());
        a1.addToMap(ActionKey.FolderPath, Paths.get("F:", "ScriptData"));

        Action a2 = new Action(Predicate.MouseClick);
        a2.addToMap(ActionKey.MouseClickType, SystemManager.LEFT_CLICK);

        List<Action> script = Arrays.asList(a1, a2);

        actionManager.serializeScript(script, Paths.get("F:", "ScriptData"), "output");
        verify(actionResource, times(1)).serializeScript(script, Paths.get("F:", "ScriptData"), "output");
    }

    @Test
    public void whenGetIndexThenIndexReturned() throws IOException, ClassNotFoundException {
        Action a1 = new Action(Predicate.CreateFolder);
        a1.addToMap(ActionKey.FolderName, UUID.randomUUID());
        a1.addToMap(ActionKey.FolderPath, Paths.get("F:", "ScriptData"));
        Action a2 = new Action(Predicate.MouseClick);
        a2.addToMap(ActionKey.MouseClickType, SystemManager.LEFT_CLICK);
        List<Action> expected = Arrays.asList(a1, a2);

        when(actionResource.getScript(Paths.get("F:", "ScriptData"), "script")).thenReturn(new ArrayList(expected));
        
        List<Action>actual=actionManager.getScript(Paths.get("F:", "ScriptData"), "script");

        verify(actionResource,times(1)).getScript(Paths.get("F:", "ScriptData"), "script");
                
        Assert.assertEquals(expected,actual);
        //nu e ok comparatia aici, chiar daca action resource returneaza o noua instanta a lui expected, 
        //obiectele din lista au acc referinta, daca modificam o proprietate a unui element din copie, ea se reflecta
        //si in expected
        
    }
}
