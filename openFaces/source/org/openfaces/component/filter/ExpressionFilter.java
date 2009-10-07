/*
 * OpenFaces - JSF Component Library 2.0
 * Copyright (C) 2007-2009, TeamDev Ltd.
 * licensing@openfaces.org
 * Unless agreed in writing the contents of this file are subject to
 * the GNU Lesser General Public License Version 2.1 (the "LGPL" License).
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * Please visit http://openfaces.org/licensing/ for more details.
 */
package org.openfaces.component.filter;

import org.openfaces.component.CompoundComponent;
import org.openfaces.component.FilterableComponent;
import org.openfaces.util.ValueBindings;

import javax.el.ValueExpression;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Dmitry Pikhulya
 */
public abstract class ExpressionFilter extends Filter implements CompoundComponent, ValueHolder {
    private static final String DEFAULT_ALL_RECORDS_CRITERION_NAME = "<All>";
    private static final String DEFAULT_EMPTY_RECORDS_CRITERION_NAME = "<Empty>";
    private static final String DEFAULT_NON_EMPTY_RECORDS_CRITERION_NAME = "<Non-empty>";

    private PropertyFilterCriterion value;
    private Object expression;
    private Converter converter;
    private boolean criterionModelUpdateRequired;

    private String predefinedCriterionStyle;
    private String predefinedCriterionClass;
    private Boolean caseSensitive;

    protected String allRecordsText;
    protected String emptyRecordsText;
    protected String nonEmptyRecordsText;

    private String promptText;
    private String promptTextStyle;
    private String promptTextClass;
    private String accesskey;
    private String tabindex;
    private String title;
    private Integer autoFilterDelay;

    @Override
    public Object saveState(FacesContext context) {
        Object superState = super.saveState(context);
        return new Object[]{superState, value, saveAttachedState(context, converter), expression,
                predefinedCriterionStyle, predefinedCriterionClass,
                allRecordsText, emptyRecordsText, nonEmptyRecordsText,
                promptText, promptTextStyle, promptTextClass, caseSensitive, accesskey, tabindex, title, autoFilterDelay
        };
    }

    @Override
    public void restoreState(FacesContext context, Object stateObj) {
        Object[] state = (Object[]) stateObj;
        int i = 0;
        super.restoreState(context, state[i++]);
        value = (PropertyFilterCriterion) state[i++];
        converter = (Converter) restoreAttachedState(context, state[i++]);
        expression = state[i++];
        predefinedCriterionStyle = (String) state[i++];
        predefinedCriterionClass = (String) state[i++];
        allRecordsText = (String) state[i++];
        emptyRecordsText = (String) state[i++];
        nonEmptyRecordsText = (String) state[i++];

        promptText = (String) state[i++];
        promptTextStyle = (String) state[i++];
        promptTextClass = (String) state[i++];
        caseSensitive = (Boolean) state[i++];
        accesskey = (String) state[i++];
        tabindex = (String) state[i++];
        title = (String) state[i++];
        autoFilterDelay = (Integer) state[i++];
    }

    public boolean isCaseSensitive() {
        return ValueBindings.get(this, "caseSensitive", caseSensitive, false);
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public Converter getConverter() {
        if (converter != null) return converter;
        ValueExpression ve = getValueExpression("converter");
        return ve != null ? (Converter) ve.getValue(getFacesContext().getELContext()) : null;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }


    public String getPredefinedCriterionStyle() {
        return ValueBindings.get(this, "predefinedCriterionStyle", predefinedCriterionStyle);
    }

    public void setPredefinedCriterionStyle(String style) {
        predefinedCriterionStyle = style;
    }

    public String getPredefinedCriterionClass() {
        return ValueBindings.get(this, "predefinedCriterionClass", predefinedCriterionClass);
    }

    public void setPredefinedCriterionClass(String styleClass) {
        predefinedCriterionClass = styleClass;
    }

    public String getPromptText() {
        return ValueBindings.get(this, "promptText", promptText);
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    public String getPromptTextStyle() {
        return ValueBindings.get(this, "promptTextStyle", promptTextStyle);
    }

    public void setPromptTextStyle(String promptTextStyle) {
        this.promptTextStyle = promptTextStyle;
    }

    public String getPromptTextClass() {
        return ValueBindings.get(this, "promptTextClass", promptTextClass);
    }

    public void setPromptTextClass(String promptTextClass) {
        this.promptTextClass = promptTextClass;
    }

    public Object getExpression() {
        return expression;
    }

    public void setExpression(Object expression) {
        this.expression = expression;
    }

    public String getAllRecordsText() {
        String result = ValueBindings.get(this, "allRecordsCriterionName", allRecordsText);
        if (result == null) {
            FilterableComponent filteredComponent = getFilteredComponent();
            if (filteredComponent != null)
                result = filteredComponent.getAllRecordsFilterText();
        }
        if (result == null)
            result = DEFAULT_ALL_RECORDS_CRITERION_NAME;
        return result;
    }

    public void setAllRecordsText(String allRecordsText) {
        this.allRecordsText = allRecordsText;
    }

    public String getEmptyRecordsText() {
        String result = ValueBindings.get(this, "emptyRecordsCriterionName", emptyRecordsText);
        if (result == null) {
            FilterableComponent filteredComponent = getFilteredComponent();
            if (filteredComponent != null)
                result = filteredComponent.getEmptyRecordsFilterText();
        }
        if (result == null)
            result = DEFAULT_EMPTY_RECORDS_CRITERION_NAME;
        return result;
    }

    public void setEmptyRecordsText(String value) {
        emptyRecordsText = value;
    }

    public String getNonEmptyRecordsText() {
        String result = ValueBindings.get(this, "nonEmptyRecordsCriterionName", nonEmptyRecordsText);
        if (result == null) {
            FilterableComponent filteredComponent = getFilteredComponent();
            if (filteredComponent != null)
                result = filteredComponent.getNonEmptyRecordsFilterText();
        }
        if (result == null)
            result = DEFAULT_NON_EMPTY_RECORDS_CRITERION_NAME;
        return result;
    }

    public void setNonEmptyRecordsText(String value) {
        nonEmptyRecordsText = value;
    }

    public ValueExpression getOptionsExpression() {
        return getValueExpression("options");
    }

    public void setOptionsExpression(ValueExpression optionsExpression) {
        setValueExpression("options", optionsExpression);
    }

    public boolean getWantsRowList() {
        if (!isShowingPredefinedCriterionNames())
            return false;
        ValueExpression filterValuesExpression = getOptionsExpression();
        return filterValuesExpression == null;
    }

    protected boolean isShowingPredefinedCriterionNames() {
        return false;
    }

    public Collection<Object> calculateAllCriterionNames(FacesContext context) {
        ValueExpression valuesExpression = getOptionsExpression();
        if (valuesExpression != null) {
            Iterable values = (Iterable) valuesExpression.getValue(context.getELContext());
            List<Object> result = new ArrayList<Object>();
            if (values != null) {
                for (Object value : values) {
                    result.add(value);
                }
            }
            return result;
        }
        Object expression = getExpression();

        Set<Object> criterionNamesSet = new TreeSet<Object>();
        FilterableComponent filteredComponent = getFilteredComponent();
        List originalRowList = filteredComponent.getRowListForFiltering(this);
        boolean thereAreNullValues = false;
        for (Object data : originalRowList) {
            Object value = filteredComponent.getFilteredValueByData(context, data, expression);
            if (value == null)
                thereAreNullValues = true;
            else
                criterionNamesSet.add(value);
        }
        List<Object> list = new ArrayList<Object>();
        if (thereAreNullValues)
            list.add(null);
        list.addAll(criterionNamesSet);
        return list;
    }

    public void updateValueFromBinding(FacesContext context) {
        ValueExpression valueExpression = getValueExpression("value");
        if (valueExpression != null) {
            value = (PropertyFilterCriterion) valueExpression.getValue(context.getELContext());
            if (value != null) {
                value.process(new FilterCriterionProcessor() {
                    public Object process(PropertyFilterCriterion criterion) {
                        validateCriterion(criterion);
                        return null;
                    }

                    public Object process(AndFilterCriterion criterion) {
                        List<FilterCriterion> criteria = criterion.getCriteria();
                        for (FilterCriterion c : criteria) {
                            c.process(this);
                        }
                        return null;
                    }

                    public Object process(OrFilterCriterion criterion) {
                        List<FilterCriterion> criteria = criterion.getCriteria();
                        for (FilterCriterion c : criteria) {
                            c.process(this);
                        }
                        return null;
                    }
                });
            }
        } else {
            if (value != null)
                validateCriterion(value);
        }
    }

    private void validateCriterion(PropertyFilterCriterion criterion) {
        boolean caseSensitive = isCaseSensitive();
        if (criterion.getPropertyLocator() == null)
            criterion.setPropertyLocator(getPropertyLocator());
        criterion.setCaseSensitive(caseSensitive);
    }

    private PropertyLocator getPropertyLocator() {
        return new PropertyLocator(
                getExpression(),
                getFilteredComponent());
    }

    public Object getLocalValue() {
        return value;
    }

    public int getAutoFilterDelay() {
        FilterableComponent component = getFilteredComponent();
        int defaultValue = component != null ? component.getAutoFilterDelay() : Integer.MIN_VALUE;
        return ValueBindings.get(this, "autoFilterDelay", autoFilterDelay, defaultValue);
    }

    public void setAutoFilterDelay(int autoFilterDelay) {
        this.autoFilterDelay = autoFilterDelay;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = (PropertyFilterCriterion) value;
    }


    /**
     * @param newCriterion new search criterion
     * @return true if the new criterion results in the different filtering behavior as opposed to this filter's previous
     *         criterion
     */
    public boolean changeCriterion(PropertyFilterCriterion newCriterion) {
        FilterCriterion oldCriterion = (FilterCriterion) getValue();
        criterionModelUpdateRequired = true;

        if (isAllRecordsCriterion(newCriterion)) {
            if (isAllRecordsCriterion(oldCriterion))
                return false;
        } else if (newCriterion.equals(oldCriterion))
            return false;
        setValue(newCriterion);
        return true;
    }

    private boolean isAllRecordsCriterion(FilterCriterion criterion) {
        return criterion == null || criterion.acceptsAll();
    }

    @Override
    public void processUpdates(FacesContext context) {
        super.processUpdates(context);
        if (!criterionModelUpdateRequired)
            return;

        ValueExpression criterionExpression = getValueExpression("value");
        if (criterionExpression != null) {
            criterionExpression.setValue(context.getELContext(), value);
            criterionModelUpdateRequired = false;
        }
    }

    public void createSubComponents(FacesContext context) {
    }

}
