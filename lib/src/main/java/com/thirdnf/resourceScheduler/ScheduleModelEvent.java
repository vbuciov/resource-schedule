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

/**
 *
 * @author Victor Manuel Bucio Vargas - vbuciov@gmail.com
 */
public class ScheduleModelEvent extends java.util.EventObject
{
    public static final int ALL_RESOURCES = -3;
    public static final int ALL_APPOINTMENTS = -2;
    public static final int ALL_ESTRUCTURE = -1;
    public static final int RESOURCE_EVENT = 0;
    public static final int INSERT_RESOURCE = 1;
    public static final int UPDATE_RESOURCE = 2;
    public static final int DELETE_RESOURCE = 3;

    public static final int APPOINTMENT_EVENT = 4;
    public static final int INSERT_APPOINTMENT = 5;
    public static final int UPDATE_APPOINTMENT = 6;
    public static final int DELETE_APPOINTMENT = 7;
    protected int       type, firtIndex, lastIndex;

    public ScheduleModelEvent(Object source, int action_type)
    {
        super(source);
        type = action_type;
        firtIndex = -1;
        lastIndex = -1;
    }
    
    public ScheduleModelEvent(Object source, int action_type, int element_index)
    {
        super(source);
        type = action_type;
        firtIndex = element_index;
        lastIndex = element_index;
    }
    
        
    public ScheduleModelEvent(Object source, int action_type, int first_index, int last_index)
    {
        super(source);
        type = action_type;
        firtIndex = first_index;
        lastIndex = last_index;
    }

    public int getType()
    {
        return type;
    }

    public int getFirtIndex()
    {
        return firtIndex;
    }

    public int getLastIndex()
    {
        return lastIndex;
    }
}
