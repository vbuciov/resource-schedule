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

import org.joda.time.LocalTime;

/**
 *
 * @author Victor Manuel Bucio Vargas - vbuciov@gmail.com
 */
public class ScheduleModelTimeEvent extends java.util.EventObject
{

    /**
     * Identifies one or more changes in the TIME RANGE contents.
     */
    public static final int START_CHANGED = 0;

    /**
     * Identifies one or more changes in the TIME RANGE contents.
     */
    public static final int END_CHANGED = 1;
    
    /**
     * Identifies the addition of one or more contiguous items to the list
     */
    public static final int TIME_MARKED = 2;
    /**
     * Identifies the removal of one or more contiguous items from the list
     */
    public static final int TIME_REMOVED = 3;

    protected int type;
    protected LocalTime old_value, new_value;

    public ScheduleModelTimeEvent(Object source, int action_type)
    {
        super(source);
        type = action_type;
        old_value = null;
        new_value = null;
    }

    public ScheduleModelTimeEvent(Object source, int action_type, LocalTime value)
    {
        super(source);
        type = action_type;
        old_value = value;
        new_value = value;
    }

    public ScheduleModelTimeEvent(Object source, int action_type, LocalTime old_value, LocalTime new_value)
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

    public LocalTime getOld_value()
    {
        return old_value;
    }

    public LocalTime getNew_value()
    {
        return new_value;
    }
}
