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

package org.openfaces.component.table;

/**
 * @author Dmitry Pikhulya
 */
public class SumFunction extends SummaryFunction {
    @Override
    protected boolean isApplicableForOrdinalType(OrdinalType ordinalType) {
        return !(ordinalType instanceof DateType);
    }

    @Override
    public Calculator startCalculation() {
        return new Calculator() {
            @Override
            public void addValue(Object value) {
                if (value == null) return;

                OrdinalType type = getOrdinalType(value);
                accumulator = accumulator == null ? value : type.add(accumulator, value);
            }
        };
    }

    @Override
    public String getName() {
        return "Sum";
    }

}
