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
package org.openfaces.taglib.internal;

import org.openfaces.component.chart.LineStyle;
import org.openfaces.component.chart.impl.PropertiesConverter;
import org.openfaces.component.table.AllNodesCollapsed;
import org.openfaces.component.table.AllNodesExpanded;
import org.openfaces.component.table.ExpansionState;
import org.openfaces.component.table.SeveralLevelsExpanded;
import org.openfaces.renderkit.cssparser.CSSUtil;
import org.openfaces.renderkit.cssparser.StyleObjectModel;
import org.openfaces.util.CalendarUtil;
import org.openfaces.util.Enumerations;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.ActionSource;
import javax.faces.component.ActionSource2;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.event.MethodExpressionValueChangeListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.MethodExpressionValidator;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Pavel Kaplin
 */
public abstract class AbstractComponentTag extends AbstractTag {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static int jsfVersion;
    private static final String LEVELS_EXPANDED_MODE = "levelsExpanded:";
    private static final String ALL_COLLAPSED_MODE = "allCollapsed";
    private static final String ALL_EXPANDED_MODE = "allExpanded";

    private static Map<String, Class> STANDARD_TYPES = new HashMap<String, Class>();
    static {
        STANDARD_TYPES.put("boolean", boolean.class);
        STANDARD_TYPES.put("byte", byte.class);
        STANDARD_TYPES.put("short", short.class);
        STANDARD_TYPES.put("int", int.class);
        STANDARD_TYPES.put("long", long.class);
        STANDARD_TYPES.put("float", float.class);
        STANDARD_TYPES.put("double", double.class);
    }

    private ThreadLocal<FacesContext> facesContext = new ThreadLocal<FacesContext>();

    /**
     * Implementations of this method must not use FacesContext.getCurrentInstance() because it doesn't contain
     * the required value when this method is invoked from the Facelets tag handler (see AbstractFaceletsComponentHandler)
     *
     * @param facesContext current FacesContext
     * @param component    component whose properties should be set up
     */
    public void setComponentProperties(FacesContext facesContext, UIComponent component) {
        ensureSupportedJSFVersion();

        if (component instanceof UIViewRoot) {
            throw new FacesException("All JSF components must be placed inside the <f:view> tag, " +
                    "or any other equivalent view tag. Please check your JSP/XHTML page.");
        }

        String rendererType = getRendererType();
        if (rendererType != null) {
            component.setRendererType(rendererType);
        }
        setBooleanProperty(component, "rendered");

        setStringProperty(component, "style");
        setStringProperty(component, "styleClass");
        setStringProperty(component, "rolloverStyle");
        setStringProperty(component, "rolloverClass");

        setStringProperty(component, "onclick");
        setStringProperty(component, "ondblclick");
        setStringProperty(component, "onmousedown");
        setStringProperty(component, "onmouseover");
        setStringProperty(component, "onmousemove");
        setStringProperty(component, "onmouseout");
        setStringProperty(component, "onmouseup");
        setStringProperty(component, "oncontextmenu");

        setStringProperty(component, "onkeydown");
        setStringProperty(component, "onkeyup");
        setStringProperty(component, "onkeypress");
        setStringProperty(component, "onblur");
        setStringProperty(component, "onfocus");
        setStringProperty(component, "onchange");

        setStringProperty(component, "focusedStyle");
        setStringProperty(component, "focusedClass");
    }

    public abstract String getComponentType();

    public abstract String getRendererType();

    public FacesContext getFacesContext() {
        return facesContext.get();
    }

    public void setFacesContext(FacesContext facesContext) {
        this.facesContext.set(facesContext);
    }
    public void removeFacesContext(){
        this.facesContext.remove();
    }


    protected void setStringProperty(UIComponent component, String propertyName) {
        setStringProperty(component, propertyName, true, false);
    }

    protected void setStringProperty(UIComponent component, String propertyName, boolean supportsEL, boolean required) {
        String stringPropertyValue = getPropertyValue(propertyName);

        if (required) {
            if (stringPropertyValue == null || stringPropertyValue.length() == 0)
                throw new IllegalArgumentException("'" + propertyName + "' attribute must be specified");
        }
        if (stringPropertyValue == null)
            return;

        if (!supportsEL) {
            if (getExpressionCreator().isValueReference(propertyName, stringPropertyValue))
                throw new IllegalArgumentException("'" + propertyName + "' attribute cannot be specified as a value binding expression");
        }

        setStringProperty(component, propertyName, stringPropertyValue);
    }

    protected void setStringProperty(UIComponent component, String propertyName, String value) {
        setObjectProperty(component, propertyName, value);
    }

    protected void setStringProperty(UIComponent component, String propertyName, String value, String tagAttributeName) {
        setObjectProperty(component, propertyName, value, tagAttributeName);
    }

    private void setObjectProperty(UIComponent component, String propertyName, String value, String tagAttributeName) {
        if (value == null) {
            return;
        }
        if (getExpressionCreator().isValueReference(tagAttributeName, value)) {
            FacesContext facesContext = getFacesContext();
            ValueExpression ve = createValueExpression(facesContext, tagAttributeName, value, Object.class);
            component.setValueExpression(propertyName, ve);
        } else {
            component.getAttributes().put(propertyName, value);
        }
    }

    protected void setObjectProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setObjectProperty(component, propertyName, value);
    }

    protected void setObjectProperty(UIComponent component, String propertyName, String value) {
        setObjectProperty(component, propertyName, value, propertyName);
    }

    protected void setBooleanProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setBooleanProperty(component, propertyName, value);
    }

    protected void setBooleanProperty(UIComponent component, String propertyName, String value) {
        if (value == null) {
            return;
        }

        if (getExpressionCreator().isValueReference(propertyName, value)) {
            FacesContext facesContext = getFacesContext();
            ValueExpression ve = createValueExpression(facesContext, propertyName, value, Boolean.class);
            component.setValueExpression(propertyName, ve);
        } else {
            boolean bValue;
            if ("true".equals(value))
                bValue = true;
            else if ("false".equals(value))
                bValue = false;
            else
                throw new IllegalArgumentException("Invalid attribute value. Attribute name: " + propertyName + ". Boolean value expected, but the following was found: " + value);
            component.getAttributes().put(propertyName, bValue);
        }
    }

    protected void setCharProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setCharProperty(component, propertyName, value);
    }

    protected void setCharProperty(UIComponent component, String propertyName, String value) {
        if (value == null) {
            return;
        }

        if (getExpressionCreator().isValueReference(propertyName, value)) {
            FacesContext facesContext = getFacesContext();
            ValueExpression ve = createValueExpression(facesContext, propertyName, value, Boolean.class);
            component.setValueExpression(propertyName, ve);
        } else {
            if (value.length() != 1)
                throw new IllegalArgumentException("Invalid attribute value. Attribute name: " + propertyName + ". Char value (as a string with one character) expected, but the following was found: " + value);
            char c = value.charAt(0);
            component.getAttributes().put(propertyName, c);
        }
    }

    protected void setByteProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setByteProperty(component, propertyName, value);
    }

    protected void setByteProperty(UIComponent component, String propertyName, String value) {
        if (value == null) {
            return;
        }

        if (getExpressionCreator().isValueReference(propertyName, value)) {
            FacesContext facesContext = getFacesContext();
            ValueExpression ve = createValueExpression(facesContext, propertyName, value, Boolean.class);
            component.setValueExpression(propertyName, ve);
        } else {
            byte convertedValue;
            try {
                convertedValue = Byte.parseByte(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid attribute value. Attribute name: " + propertyName + ". Byte value expected, but the following was found: " + value, e);
            }

            component.getAttributes().put(propertyName, convertedValue);
        }
    }

    protected void setShortProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setShortProperty(component, propertyName, value);
    }

    protected void setShortProperty(UIComponent component, String propertyName, String value) {
        if (value == null) {
            return;
        }

        if (getExpressionCreator().isValueReference(propertyName, value)) {
            FacesContext facesContext = getFacesContext();
            ValueExpression ve = createValueExpression(facesContext, propertyName, value, Boolean.class);
            component.setValueExpression(propertyName, ve);
        } else {
            short convertedValue;
            try {
                convertedValue = Short.parseShort(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid attribute value. Attribute name: " + propertyName + ". Short type value expected, but the following was found: " + value, e);
            }

            component.getAttributes().put(propertyName, convertedValue);
        }
    }

    protected void setIntProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setIntProperty(component, propertyName, value);
    }

    protected void setIntProperty(UIComponent component, String propertyName, String value) {
        if (value == null) {
            return;
        }

        if (getExpressionCreator().isValueReference(propertyName, value)) {
            ValueExpression ve = createValueExpression(getFacesContext(), propertyName, value, Integer.class);
            component.setValueExpression(propertyName, ve);
        } else {
            int iValue = Integer.parseInt(value);
            component.getAttributes().put(propertyName, iValue);
        }
    }

    protected void setLongProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setLongProperty(component, propertyName, value);
    }

    protected void setLongProperty(UIComponent component, String propertyName, String value) {
        if (value == null) {
            return;
        }

        if (getExpressionCreator().isValueReference(propertyName, value)) {
            FacesContext facesContext = getFacesContext();
            ValueExpression ve = createValueExpression(facesContext, propertyName, value, Boolean.class);
            component.setValueExpression(propertyName, ve);
        } else {
            long convertedValue;
            try {
                convertedValue = Long.parseLong(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid attribute value. Attribute name: " + propertyName + ". Long type value expected, but the following was found: " + value, e);
            }

            component.getAttributes().put(propertyName, convertedValue);
        }
    }

    protected void setNumberProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setNumberProperty(component, propertyName, value);
    }

    protected void setNumberProperty(UIComponent component, String propertyName, String value) {
        if (value == null) {
            return;
        }
        if (getExpressionCreator().isValueReference(propertyName, value)) {
            ValueExpression ve = createValueExpression(getFacesContext(), propertyName, value, Object.class);
            component.setValueExpression(propertyName, ve);
        } else {
            Number iValue;
            try {
                iValue = Long.parseLong(value);
            } catch (NumberFormatException e) {
                iValue = Double.parseDouble(value);
            }
            component.getAttributes().put(propertyName, iValue);
        }
    }

    protected void setFloatProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setFloatProperty(component, propertyName, value);
    }

    protected void setFloatProperty(UIComponent component, String propertyName, String value) {
        if (value == null) {
            return;
        }

        if (getExpressionCreator().isValueReference(propertyName, value)) {
            ValueExpression ve = createValueExpression(getFacesContext(), propertyName, value, Float.class);
            component.setValueExpression(propertyName, ve);
        } else {
            float fValue = Float.parseFloat(value);
            component.getAttributes().put(propertyName, fValue);
        }
    }

    protected void setDoubleProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setDoubleProperty(component, propertyName, value);
    }

    protected void setDoubleProperty(UIComponent component, String propertyName, String value) {
        if (value == null) {
            return;
        }

        if (getExpressionCreator().isValueReference(propertyName, value)) {
            ValueExpression ve = createValueExpression(getFacesContext(), propertyName, value, Double.class);
            component.setValueExpression(propertyName, ve);
        } else {
            double dValue = Double.parseDouble(value);
            component.getAttributes().put(propertyName, dValue);
        }
    }

    protected void setCollectionProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setCollectionProperty(component, propertyName, value);
    }

    protected void setCollectionProperty(UIComponent component, String propertyName, String value) {
        if (value == null) {
            return;
        }

        if (!setAsValueExpressionIfPossible(component, propertyName, value))
            component.getAttributes().put(propertyName, Collections.singletonList(value));
    }

    protected boolean setLiteralCollectionProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setLiteralCollectionProperty(component, propertyName, value);
        return value != null;
    }

    protected void setLiteralCollectionProperty(UIComponent component, String propertyName, String value) {
        if (value == null) {
            return;
        }
        if (setAsValueExpressionIfPossible(component, propertyName, value)) {
            ValueExpression valueExpressionProxy = new LiteralCollectionValueExpressionProxy(component.getValueExpression(propertyName));
            component.setValueExpression(propertyName, valueExpressionProxy);
        } else {
            Collection<String> collection = parseLiteralCollection(value);
            component.getAttributes().put(propertyName, collection);
        }
    }

    private static Collection<String> parseLiteralCollection(String value) {
        return new ArrayList<String>(Arrays.asList(value.trim().split(" +")));
    }

    protected void setExpansionStateProperty(UIComponent component, String propertyName) {
        String expansionState = getPropertyValue(propertyName);
        if (expansionState == null) return;
        expansionState = expansionState.trim();

        if (expansionState.length() == 0)
            throw new IllegalArgumentException("Invalid value specified for expansionState attribute: the value should not be empty");

        if (isValueReference(expansionState)) {
            FacesContext context = getFacesContext();
            ValueExpression ve = createValueExpression(context, "expansionState", expansionState);
            component.setValueExpression("expansionState", ve);
            return;
        }

        ExpansionState expansionStateObj;
        if (AbstractComponentTag.ALL_EXPANDED_MODE.equals(expansionState))
            expansionStateObj = new AllNodesExpanded();
        else if (ALL_COLLAPSED_MODE.equals(expansionState))
            expansionStateObj = new AllNodesCollapsed();
        else if (expansionState.startsWith(LEVELS_EXPANDED_MODE)) {
            String remainder = expansionState.substring(LEVELS_EXPANDED_MODE.length());
            remainder = remainder.trim();
            int level;
            try {
                level = Integer.parseInt(remainder);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid level specified in expansionState attribute. Integer number expected, but was: " + remainder);
            }
            if (level < 0)
                throw new IllegalArgumentException("Invalid level specified in expansionState attribute. The number should be zero or a positive number: " + level);
            expansionStateObj = new SeveralLevelsExpanded(level);
        } else {
            throw new IllegalArgumentException("Invalid value specified for expansionState attribute: " + expansionState + " . It should be one of the following: " + ALL_EXPANDED_MODE + ", " + ALL_COLLAPSED_MODE + " or " + LEVELS_EXPANDED_MODE + "NUMBER");
        }
        component.getAttributes().put(propertyName, expansionStateObj);
    }

    protected void setClassProperty(UIComponent component, String propertyName) {
        String typeStr = getPropertyValue(propertyName);
        if (setAsValueExpressionIfPossible(component, propertyName, typeStr)) return;

        Class cls = STANDARD_TYPES.get(typeStr);
        if (cls == null)
            try {
                cls = Class.forName(typeStr);
            } catch (ClassNotFoundException e) {
                throw new FacesException("Unknown type: " + typeStr);
            }
        component.getAttributes().put(propertyName, cls);
    }

    private static final class LiteralCollectionValueExpressionProxy extends ValueExpression {

        private ValueExpression delegate;

        private LiteralCollectionValueExpressionProxy(ValueExpression delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object getValue(ELContext elContext) {
            Object value = delegate.getValue(elContext);
            if (value instanceof String) {
                return parseLiteralCollection((String) value);
            }
            return value;
        }

        @Override
        public void setValue(ELContext elContext, Object o) {
            delegate.setValue(elContext, o);
        }

        @Override
        public boolean isReadOnly(ELContext elContext) {
            return delegate.isReadOnly(elContext);
        }

        @Override
        public Class getType(ELContext elContext) {
            return delegate.getType(elContext);
        }

        @Override
        public Class getExpectedType() {
            return delegate.getExpectedType();
        }

        @Override
        public String getExpressionString() {
            return delegate.getExpressionString();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof LiteralCollectionValueExpressionProxy) {
                o = ((LiteralCollectionValueExpressionProxy) o).delegate;
            }
            return delegate.equals(o);
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @Override
        public boolean isLiteralText() {
            return delegate.isLiteralText();
        }
    }

    /**
     * @param component    component
     * @param propertyName property name
     * @return false if explicit setter invokation is required
     */
    protected boolean setAsValueExpressionIfPossible(
            UIComponent component,
            String propertyName) {
        String value = getPropertyValue(propertyName);
        return setAsValueExpressionIfPossible(component, propertyName, value);
    }

    /**
     * @param component        component
     * @param propertyName     property name
     * @param valueDeclaration value declaration
     * @return false if value declaration is not a value expression, so explicit setter invokation is required
     */
    protected boolean setAsValueExpressionIfPossible(
            UIComponent component,
            String propertyName,
            String valueDeclaration) {
        return setAsValueExpressionIfPossible(component, propertyName, valueDeclaration, propertyName);
    }

    /**
     * @param component        component
     * @param propertyName     property name
     * @param valueDeclaration value declaration
     * @return false if value declaration is not a value expression, so explicit setter invokation is required
     */
    protected boolean setAsValueExpressionIfPossible(
            UIComponent component,
            String propertyName,
            String valueDeclaration,
            String attributeName) {
        if (valueDeclaration == null)
            return true;
        if (getExpressionCreator().isValueReference(attributeName, valueDeclaration)) {
            ValueExpression ve = createValueExpression(getFacesContext(), attributeName, valueDeclaration);
            component.setValueExpression(propertyName, ve);
            return true;
        } else
            return false;
    }

    protected void setValueExpressionProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setValueExpressionProperty(component, propertyName, value);
    }

    protected void setValueExpressionProperty(UIComponent component, String propertyName, String propertyValue) {
        setValueExpressionProperty(component, propertyName, propertyValue, propertyName);
    }

    protected void setValueExpressionProperty(UIComponent component, String propertyName, String propertyValue, String attributeName) {
        if (!setAsValueExpressionIfPossible(component, propertyName, propertyValue, attributeName)) {
            throw new IllegalArgumentException(propertyName + " property for " + component.getFamily() + " component " +
                    "should be declared as a value binding expression, but it is declared as follows: \"" + propertyValue + "\"");
        }
    }

    protected void setActionProperty(FacesContext context, ActionSource2 actionSource) {
        String actionPropertyName = "action";
        String actionDeclaration = getPropertyValue(actionPropertyName);
        if (actionDeclaration == null)
            return;

        MethodExpression methodExpression = createMethodExpression(context, actionPropertyName, actionDeclaration, Object.class, new Class[0]);
        actionSource.setActionExpression(methodExpression);
    }

    protected void setActionListener(FacesContext context, ActionSource component) {
        setActionListener(context, component, "actionListener");
    }

    protected void setActionListener(FacesContext context, ActionSource component, String actionListenerPropertyName) {
        setActionListener(context, component, actionListenerPropertyName, new Class[]{ActionEvent.class});
    }

    protected void setActionListener(FacesContext context, ActionSource component, Class[] paramTypes) {
        setActionListener(context, component, "actionListener", paramTypes);
    }

    protected void setActionListener(FacesContext context, ActionSource component, String actionPropertyName, Class[] paramTypes) {
        String actionDeclaration = getPropertyValue(actionPropertyName);
        if (actionDeclaration == null)
            return;

        if (!getExpressionCreator().isValueReference(actionPropertyName, actionDeclaration))
            throw new FacesException("The actionListener attribute should be declared as method expression, but it was declared as follows: " + actionDeclaration);
        String actionListenerAttributeName = "actionListener";
        MethodExpression methodExpression = createMethodExpression(context, actionListenerAttributeName, actionDeclaration, void.class, paramTypes);
        component.addActionListener(new MethodExpressionActionListener(methodExpression));
    }

    protected void setValueChangeListener(FacesContext context, EditableValueHolder component) {
        String actionPropertyName = "valueChangeListener";
        String actionDeclaration = getPropertyValue(actionPropertyName);
        if (actionDeclaration == null)
            return;

        if (!getExpressionCreator().isValueReference(actionPropertyName, actionDeclaration))
            throw new FacesException("The valueChangeListener attribute should be declared as method expression, but it was declared as follows: " + actionDeclaration);
        MethodExpression methodExpression = createMethodExpression(context, actionPropertyName, actionDeclaration, void.class, new Class[]{ValueChangeEvent.class});
        component.addValueChangeListener(new MethodExpressionValueChangeListener(methodExpression));
    }

    protected void setValidator(FacesContext context, EditableValueHolder component) {
        String actionPropertyName = "validator";
        String actionDeclaration = getPropertyValue(actionPropertyName);
        if (actionDeclaration == null)
            return;

        if (!getExpressionCreator().isValueReference(actionPropertyName, actionDeclaration))
            throw new FacesException("The validator attribute should be declared as method expression, but it was declared as follows: " + actionDeclaration);
        MethodExpression methodExpression = createMethodExpression(context, actionPropertyName, actionDeclaration,
                void.class, new Class[]{FacesContext.class, UIComponent.class, Object.class});
        component.addValidator(new MethodExpressionValidator(methodExpression));
    }

    protected void setMethodExpressionProperty(
            FacesContext context,
            UIComponent uiComponent,
            String actionPropertyName,
            Class[] paramTypes,
            Class returnType) {
        String actionDeclaration = getPropertyValue(actionPropertyName);
        if (actionDeclaration == null)
            return;

        if (!getExpressionCreator().isValueReference(actionPropertyName, actionDeclaration))
            throw new FacesException();
        Class[] params = paramTypes != null ? paramTypes : null;
        MethodExpression methodExpression =
                createMethodExpression(context, actionPropertyName, actionDeclaration, returnType, params);
        uiComponent.getAttributes().put(actionPropertyName, methodExpression);
    }

    protected void setLineStyleProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        setLineStyleProperty(component, propertyName, value);
    }

    protected void setLineStyleProperty(UIComponent component, String propertyName, String propertyValue) {
        if (propertyValue != null) {
            if (propertyValue.indexOf(":") != -1 || propertyValue.indexOf(";") != -1)
                throw new IllegalArgumentException("Invalid attribute value. Attribute name: " + propertyName +
                        ". Full CSS declarations are not allowed for this property. " +
                        "The value should be of the following form: \"1px solid black\", but was: \"" + propertyValue + "\"");
        }
        setStringProperty(component, propertyName, propertyValue);
    }

    // todo: shouldn't this method actually delegate to getExpressionCreator().isValueReference ???  Investigate SVN revision 15673 (JSFC-2594 - fixed)

    public static boolean isValueReference(String value) {
        if (value == null) throw new NullPointerException("value");

        int start = value.indexOf("#{");
        if (start < 0) return false;

        int end = value.lastIndexOf('}');
        return (end >= 0 && start < end);
    }

    protected void setLocaleProperty(UIComponent component, String propertyName) {
        String locale = getPropertyValue(propertyName);
        if (setAsValueExpressionIfPossible(component, propertyName, locale))
            return;

        component.getAttributes().put(propertyName, CalendarUtil.getLocaleFromString(locale));
    }

    protected void setTimeZoneProperty(UIComponent component, String propertyName) {
        String timeZone = getPropertyValue(propertyName);
        if (setAsValueExpressionIfPossible(component, propertyName, timeZone)) {
            setValueExpressionProperty(component, propertyName);
            return;
        }

        component.getAttributes().put(propertyName, TimeZone.getTimeZone(timeZone));
    }

    protected void setTimeProperty(UIComponent component, String propertyName) {
        String propertyValue = getPropertyValue(propertyName);
        if (setAsValueExpressionIfPossible(component, propertyName, propertyValue))
            return;

        Date date;
        try {
            date = TIME_FORMAT.parse(propertyValue);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Couldn't parse time attribute '" + propertyName + "'. The value should comply to the 'HH:mm' pattern, but the following value was encountered: \"" + propertyValue + "\"");
        }
        component.getAttributes().put(propertyName, date);
    }

    protected void setTimePropertyAsString(UIComponent component, String propertyName) {
        String propertyValue = getPropertyValue(propertyName);
        if (setAsValueExpressionIfPossible(component, propertyName, propertyValue))
            return;

        try {
            // check the string for validity
            TIME_FORMAT.parse(propertyValue);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Couldn't parse time attribute '" + propertyName + "'. The value should comply to the 'HH:mm' pattern, but the following value was encountered: \"" + propertyValue + "\"");
        }
        component.getAttributes().put(propertyName, propertyValue);
    }

    protected void setColorProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        if (setAsValueExpressionIfPossible(component, propertyName, value))
            return;

        Color color = CSSUtil.parseColor(value);
        component.getAttributes().put(propertyName, color);
    }

    protected void setLineStyleObjectProperty(UIComponent component, String propertyName) {
        String value = getPropertyValue(propertyName);
        if (setAsValueExpressionIfPossible(component, propertyName, value))
            return;

        final StyleObjectModel lineStyleCSSModel = CSSUtil.getLineStyleModel(value);
        final LineStyle lineStyle = PropertiesConverter.toLineStyle(lineStyleCSSModel.getBorder());
        component.getAttributes().put(propertyName, lineStyle);
    }

    protected void setConverterProperty(UIComponent component, String propertyName) {
        Application application = facesContext.get().getApplication();
        String converterValue = getPropertyValue(propertyName);
        if (!setAsValueExpressionIfPossible(component, propertyName)) {
            Converter converter = application.createConverter(converterValue);
            if (component instanceof ValueHolder)
                ((ValueHolder) component).setConverter(converter);
            else
                component.getAttributes().put(propertyName, converter);
        }
    }

    protected <T extends Enum> void setEnumerationProperty(UIComponent component, String propertyName, Class<T> enumerationClass) {
        String attributeValue = getPropertyValue(propertyName);
        if (!setAsValueExpressionIfPossible(component, propertyName, attributeValue)) {
            component.getAttributes().put(
                    propertyName,
                    Enumerations.valueByString(enumerationClass, attributeValue, propertyName));
        }
    }

    private static void ensureSupportedJSFVersion() {
        if (jsfVersion == 0) {
            try {
                Application.class.getMethod("getExpressionFactory");
                jsfVersion = 12;
            } catch (NoSuchMethodException e) {
                jsfVersion = 11;
            }
        }

        if (jsfVersion == 11)
            throw new IllegalStateException("OpenFaces library requires JSF version 1.2 or higher");
    }

}
