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
package org.openfaces.testapp;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;


public class ContextUtil {
    
    private ContextUtil() {
    }

    public static Object resolveVariable(String variableName) {
        Application application = FacesContext.getCurrentInstance().getApplication();
        VariableResolver variableResolver = application.getVariableResolver();
        return variableResolver.resolveVariable(FacesContext.getCurrentInstance(), variableName);
    }
}
