//
// Copyright (C) 2019 Carbonacat
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package net.ccat.tazs.battle.handlers;

import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * A Handler for a Unit:
 * - Updates its state
 * - Renders it.
 */
public interface UnitHandler
{
    /***** INFORMATION *****/
    
    /**
     * @return The UnitType of this Unit.
     * @see net.ccat.tazs.battle.UnitTypes.
     */
    public abstract int unitType();
    
    /**
     * @return the usual name.
     */
    public abstract pointer name();
    
    /**
     * @return How many health a Unit of this type should have.
     */
    public abstract int startingHealth();
    
    /**
     * @return How many does a Unit of this type costs.
     */
    public abstract int cost();
    
    /**
     * @return true if this Unit is controlled, false elsewhere.
     */
    public abstract boolean isControlled();
    
    /**
     * A Heavier Unit will move less against a Lighter one when being separated.
     * @return The inverse weight (that is, 1/weight) for this Unit. 0 means it won't budge at all.
     */
    public abstract float inverseWeight();
    
    /**
     * @param system
     * @param unitIdentifier
     * @return true if this unit is ready to attack, false elsewhere.
     */
    public abstract boolean isReadyToAttack(UnitsSystem system, int unitIdentifier);
    
    
    /***** LIFECYCLE *****/
    
    /**
     * Updates a given Unit inside the UnitsSystem.
     * @param system The system the Unit belongs to.
     * @param unitIdentifier Identifies the Unit inside the system.
     */
    public abstract void onTick(UnitsSystem system, int unitIdentifier);
    
    
    /***** EVENTS *****/
    
    /**
     * Called when the Player attempts to control the given Unit.
     * @param control If true, requesting to control this unit. If false, leaving its control.
     * @return true if the Unit will be controlled, false elsewhere.
     */
    public abstract boolean onPlayerControl(UnitsSystem system, int unitIdentifier, boolean control);
    
    /**
     * Called when this Unit was hit by some power.
     * @param system The system the Unit belongs to.
     * @param unitIdentifier Identifies the Unit inside the system.
     * @param powerX the X component of the power.
     * @param powerY the Y component of the power.
     * @param power the power.
     */
    public abstract void onHit(UnitsSystem system, int unitIdentifier,
                               float powerX, float powerY, float power);
    
    
    /***** RENDERING *****/
    
    /**
     * Renders a given Unit inside the UnitsSystem.
     * @param system The system the Unit belongs to.
     * @param unitIdentifier Identifies the Unit inside the system.
     * @param screen The target screen.
     */
    public abstract void draw(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen);
    
    /**
     * Renders a special UI for a Controlled unit.
     * @param system The system the Unit belongs to.
     * @param unitIdentifier Identifies the Unit inside the system.
     * @param screen The target screen.
     */
    public abstract void drawControlUI(UnitsSystem system, int unitIdentifier, AdvancedHiRes16Color screen);
    
    /**
     * Renders the given Unit inside the UnitsSystem.
     * @param system The system the Unit belongs to.
     * @param x
     * @param y
     * @param angle
     * @param team The team.
     * @param timer An increased value, once per frame, reset when the unit is changed.
     * @param unitIdentifier Identifies the Unit inside the system.
     * @param screen The target screen.
     */
    public abstract void drawAsUI(UnitsSystem system,
                                  float x, float y, float angle, int team,
                                  int timer,
                                  AdvancedHiRes16Color screen);
}
