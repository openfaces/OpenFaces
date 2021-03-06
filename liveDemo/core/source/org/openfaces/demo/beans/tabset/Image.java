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

package org.openfaces.demo.beans.tabset;

import java.io.Serializable;

public class Image implements Serializable {
    private Author author;
    private String fileName;
    private String name;

    public Image(Author author, String fileName, String name) {
        this.name = name;
        this.fileName = fileName;
        this.author = author;
    }

    public Author getAuthor() {
        return author;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

}
