/*
 * OpenFaces - JSF Component Library 2.0
 * Copyright (C) 2007-2012, TeamDev Ltd.
 * licensing@openfaces.org
 * Unless agreed in writing the contents of this file are subject to
 * the GNU Lesser General Public License Version 2.1 (the "LGPL" License).
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * Please visit http://openfaces.org/licensing/ for more details.
 */

package org.openfaces.component.chart;

/**
 * @author Eugene Goncharov
 */
public interface ItemInfo {
    void setKey(Object key);

    Object getKey();

    void setValue(Object value);

    Object getValue();

    public int getIndex();

    public void setIndex(int index);

    SeriesInfo getSeries();

    void setSeries(SeriesInfo info);
}
