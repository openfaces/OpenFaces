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

package org.openfaces.testapp.datatable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Tatyana Matveyeva
 */
public class TableItem {
    private static int idCounter = 0;

    private String id;
    private String name;
    private String stringField1;
    private String stringField2;
    private int intField1;
    private int intField2;
    private Map dynamicColumns = new HashMap();

    public TableItem(String name, String stringField1, String stringField2, int intField1, int intField2) {
        id = "item" + idCounter++;
        this.name = name;
        this.stringField1 = stringField1;
        this.stringField2 = stringField2;
        this.intField1 = intField1;
        this.intField2 = intField2;
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            String columnName = String.valueOf(i);
            Integer columnValue = Math.abs(random.nextInt(1000));
            dynamicColumns.put(columnName, columnValue);
        }
        String columnName = "Character";
        String columnValue = String.valueOf((char) (Math.abs(random.nextInt(24)) + 97));
        dynamicColumns.put(columnName, columnValue);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStringField1() {
        return stringField1;
    }

    public void setStringField1(String stringField1) {
        this.stringField1 = stringField1;
    }

    public String getStringField2() {
        return stringField2;
    }

    public void setStringField2(String stringField2) {
        this.stringField2 = stringField2;
    }

    public int getIntField1() {
        return intField1;
    }

    public void setIntField1(int intField1) {
        this.intField1 = intField1;
    }

    public int getIntField2() {
        return intField2;
    }

    public void setIntField2(int intField2) {
        this.intField2 = intField2;
    }

    public Map getDynamicColumns() {
        return dynamicColumns;
    }

    public void setDynamicColumns(Map dynamicColumns) {
        this.dynamicColumns = dynamicColumns;
    }

}
