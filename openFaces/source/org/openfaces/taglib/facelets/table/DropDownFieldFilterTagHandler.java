/*
 * OpenFaces - JSF Component Library 3.0
 * Copyright (C) 2007-2011, TeamDev Ltd.
 * licensing@openfaces.org
 * Unless agreed in writing the contents of this file are subject to
 * the GNU Lesser General Public License Version 2.1 (the "LGPL" License).
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * Please visit http://openfaces.org/licensing/ for more details.
 */
package org.openfaces.taglib.facelets.table;

import org.openfaces.taglib.facelets.AbstractFaceletsComponentHandler;
import org.openfaces.taglib.internal.filter.DropDownFieldFilterTag;

import javax.faces.view.facelets.ComponentConfig;

/**
 * @author Dmitry Pikhulya
 */
public class DropDownFieldFilterTagHandler extends AbstractFaceletsComponentHandler {
    public DropDownFieldFilterTagHandler(ComponentConfig componentConfig) {
        super(componentConfig, new DropDownFieldFilterTag());
    }
}
