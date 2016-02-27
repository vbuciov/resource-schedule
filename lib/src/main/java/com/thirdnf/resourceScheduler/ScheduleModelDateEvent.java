/*
 * Copyright 2016 sis2.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirdnf.resourceScheduler;

import org.joda.time.LocalDate;

/**
 *
 * @author Victor Manuel Bucio Vargas - vbuciov@gmail.com
 */
public class ScheduleModelDateEvent extends java.util.EventObject
{

    /**
     * Identifies one or more changes in the lists contents.
     */
    public static final int CONTENTS_CHANGED = 0;
    /**
     * Identifies the addition of one or more contiguous items to the list
     */
    public static final int DATE_MARKED = 1;
    /**
     * Identifies the removal of one or more contiguous items from the list
     */
    public static final int DATE_REMOVED = 2;

    protected int type;
    protected LocalDate old_value, new_value;

    public ScheduleModelDateEvent(Object source, int action_type)
    {
        super(source);
        type = action_type;
        old_value = null;
        new_value = null;
    }

    public ScheduleModelDateEvent(Object source, int action_type, LocalDate value)
    {
        super(source);
        type = action_type;
        old_value = value;
        new_value = value;
    }

    public ScheduleModelDateEvent(Object source, int action_type, LocalDate old_value, LocalDate new_value)
    {
        super(source);
        type = action_type;
        this.old_value = old_value;
        this.new_value = new_value;
    }

    public int getType()
    {
        return type;
    }

    public LocalDate getOld_value()
    {
        return old_value;
    }

    public LocalDate getNew_value()
    {
        return new_value;
    }

}
