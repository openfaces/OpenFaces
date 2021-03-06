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

package org.openfaces.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import java.util.List;


public class UploadCompletionEvent extends FacesEvent {
    private final List<FileUploadItem> files;

    public UploadCompletionEvent(UIComponent component, List<FileUploadItem> files) {
        super(component);
        if (component == null) throw new IllegalArgumentException("Component " + component + " is null");
        this.files = files;

    }

    public List<FileUploadItem> getFiles() {
        return files;
    }

    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        return listener instanceof UploadCompletionListener;
    }

    @Override
    public void processListener(FacesListener listener) {
        ((UploadCompletionListener) listener).processFiles(this);
    }

}