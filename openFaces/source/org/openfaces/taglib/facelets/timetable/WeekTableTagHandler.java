/*
 * OpenFaces - JSF Component Library 2.0
 * Copyright (C) 2007-2010, TeamDev Ltd.
 * licensing@openfaces.org
 * Unless agreed in writing the contents of this file are subject to
 * the GNU Lesser General Public License Version 2.1 (the "LGPL" License).
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * Please visit http://openfaces.org/licensing/ for more details.
 */
package org.openfaces.taglib.facelets.timetable;

import com.sun.facelets.tag.jsf.ComponentConfig;
import org.openfaces.taglib.facelets.AbstractFaceletsComponentHandler;
import org.openfaces.taglib.internal.timetable.TimetableViewTag;
import org.openfaces.taglib.internal.timetable.WeekTableTag;

/**
 * @author Roman Porotnikov
 */
public class WeekTableTagHandler extends AbstractFaceletsComponentHandler {
    public WeekTableTagHandler(ComponentConfig componentConfig) {
        super(componentConfig, new WeekTableTag());
    }
}
