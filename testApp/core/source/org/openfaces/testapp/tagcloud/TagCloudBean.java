/*
 * OpenFaces - JSF Component Library 2.0
 * Copyright (C) 2007-2013, TeamDev Ltd.
 * licensing@openfaces.org
 * Unless agreed in writing the contents of this file are subject to
 * the GNU Lesser General Public License Version 2.1 (the "LGPL" License).
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * Please visit http://openfaces.org/licensing/ for more details.
 */

package org.openfaces.testapp.tagcloud;


import org.openfaces.component.tagcloud.TagCloud;
import org.openfaces.util.DataUtil;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

/**
 * @author : roman.nikolaienko
 */
public class TagCloudBean implements ActionListener, Serializable {

    private Random random = new Random();
    private Item var = new Item();

    private int actionCounter = 0;
    private int actionListenerCounter = 0;

    private List<Item> itemsList;
    private Item[] itemsArray;

    private List<Item> smallItemsList;

    private Converter numberConverter = new Converter() {
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            if ("".equals(value)) return null;
            return Double.parseDouble(value.substring(1));
        }

        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value == null) return "";
            return "$" + String.valueOf(value);
        }
    };

    private Converter dateConverter = new Converter() {
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            if ("".equals(value))
                return null;
            TimeZone timeZone = TimeZone.getDefault();
            return DataUtil.parseDateFromJs(value, timeZone);
        }

        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value == null) return "";
            try {
                return DataUtil.formatDateForJs((Date) value, TimeZone.getDefault());
            } catch (IllegalArgumentException e) {
                throw new ConverterException("Cannot convert value '" + value + "'");
            }
        }
    };

    public TagCloudBean() {
        initItemsListAndArray();
        initSmallItemList();

    }

    private void initItemsListAndArray() {
        int length = getRandomLength(10, 20);
        itemsList = new ArrayList<Item>(length);
        itemsArray = new Item[length];
        Item curItem;
        for (int i = 0; i < length; i++) {
            curItem = new Item(getRandomText(4, 10), getRandomDate(), getRandomNumber());
            itemsArray[i] = curItem;
            itemsList.add(curItem);
        }
    }

    private void initSmallItemList() {
        int length = getRandomLength(100, 120);
        smallItemsList = new ArrayList<Item>(length);
        Item item;
        for (int i = 0; i < length; i++) {
            item = new Item(getRandomText(1, 2), getRandomDate(), getRandomNumber());
            smallItemsList.add(item);
        }
    }

    private int getRandomLength(int minTextLength, int maxTextLength) {
        return random.nextInt(maxTextLength - minTextLength) + minTextLength;
    }

    private Number getRandomNumber() {
        return random.nextInt(1000);
    }

    private Date getRandomDate() {
        return new Date(random.nextInt() + getRandomLength(32, 40) * 12 * 30 * 24 * 3600 * 1000L);
    }

    private String getRandomText(int minLength, int maxLength) {
        int curLength = getRandomLength(minLength, maxLength);
        StringBuilder buf = new StringBuilder();
        buf.append((char) (random.nextInt('Z' - 'A') + 'A'));
        for (int i = 0; i < curLength; i++) {
            buf.append((char) (random.nextInt('z' - 'a') + 'a'));
        }
        return buf.toString();
    }

    private String getRandomTextWithWhiteSpaces(int minLength, int maxLength) {
        int curLength = getRandomLength(minLength, maxLength);
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < curLength; i++) {
            buf.append(getRandomText(2, 12)).append(" ");
        }
        buf.append(".");
        return buf.toString();
    }

    public List<Item> getItemsList() {
        return itemsList;
    }

    public Item[] getItemsArray() {
        return itemsArray;
    }

    public Converter getNumberConverter() {
        return numberConverter;
    }

    public Converter getDateConverter() {
        return dateConverter;
    }

    public List<Item> getSmallItemsList() {
        return smallItemsList;
    }

    public void setSmallItemsList(List<Item> smallItemsList) {
        this.smallItemsList = smallItemsList;
    }

    public int getActionCounter() {
        return actionCounter;
    }

    public void setActionCounter(int actionCounter) {
        this.actionCounter = actionCounter;
    }

    public int getActionListenerCounter() {
        return actionListenerCounter;
    }

    public Object actionMethod() {
        actionCounter++;
        return "success";
    }

    public void processAction(ActionEvent event) {
        actionListenerCounter++;
        TagCloud cloud = (TagCloud) event.getComponent();
        Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        var = (Item) requestMap.get(cloud.getVar());
    }

    public Item getVar() {
        return var;
    }
}
