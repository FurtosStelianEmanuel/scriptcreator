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
public class Action implements Serializable {

    int delay;
    Predicate predicate;
    Map<Object, Object> actionsMap;
    private String name = "DefaultActionName";

    public Action(Predicate predicate) {
        this.predicate = predicate;
        delay = 500;
        actionsMap = new HashMap<>();
        addToMap(ActionKey.EndDelay, delay);
    }

    public Action() {
        delay = 2000;
        actionsMap = new HashMap<>();
        addToMap(ActionKey.EndDelay, delay);
    }

    public Map<Object, Object> getMap() {
        return actionsMap;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public final void addToMap(ActionKey key, Object value) {
        if (actionsMap.put(key, value) != null) {
            throw new UnsupportedOperationException("Override la o valoare din map");
        }
    }

    public final void addToMap(ActionKey key, int value) {
        addToMap(key, Integer.toString(value));
    }

}
