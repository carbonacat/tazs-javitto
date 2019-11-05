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

package net.ccat.tazs.battle.handlers.dasher;

import net.ccat.tazs.tools.MathTools;
import net.ccat.tazs.ui.AdvancedHiRes16Color;


/**
 * Handles the Controlled state of a Dasher.
 * - Reads the PAD
 * - Switch to DasherDead when dead
 */
public class DasherControlledHandler
    extends DasherSeekAndAttackHandler
{
    static final DasherControlledHandler instance = new DasherControlledHandler();
    
    
    /***** INFORMATION *****/
    
    public boolean isControlled()
    {
        return true;
    }
    
    public boolean onPlayerControl(UnitsSystem system, int unitIdentifier, boolean control)
    {
        if (control)
            return false;
        system.unitsHandlers[unitIdentifier] = DasherSeekAndAttackHandler.instance;
        return true;
    }
    
    
    /***** LIFECYCLE *****/
    
    public void onTick(UnitsSystem system, int unitIdentifier) 
    {
        int unitTimer = system.unitsTimers[unitIdentifier];
        
        system.controlledUnitIdentifier = unitIdentifier;
        if (unitTimer <= 0)
            onSeekingTick(system, unitIdentifier, unitTimer);
        else
            onDashingTick(system, unitIdentifier, unitTimer);
    }
    
    
    /***** BEHAVIORS *****/
    
    private static void onSeekingTick(UnitsSystem system, int unitIdentifier, int unitTimer)
    {
        float unitX = system.unitsXs[unitIdentifier];
        float unitY = system.unitsYs[unitIdentifier];
        float unitAngle = system.unitsAngles[unitIdentifier];
        
        unitTimer++;
        if (unitTimer > 0)
            unitTimer = READY_TICKS;
        if (system.playerPadLength > 0)
        {
            float targetAngle = system.playerPadAngle;
            float deltaAngle = MathTools.clamp(MathTools.wrapAngle(targetAngle - unitAngle), -ANGLE_ROTATION_BY_TICK, ANGLE_ROTATION_BY_TICK);
                
            unitAngle = MathTools.wrapAngle(unitAngle + deltaAngle);
            system.unitsAngles[unitIdentifier] = unitAngle;
        }
        
        if ((unitTimer >= READY_TICKS) && (system.playerPrimaryAction))
            unitTimer = DASH_TIMER_INIT;

        // Can't stop!
        unitX += Math.cos(unitAngle) * WALK_SPEED;
        unitY += Math.sin(unitAngle) * WALK_SPEED;
        
        // Updating the changed state.
        system.unitsTimers[unitIdentifier] = unitTimer;
        system.unitsXs[unitIdentifier] = unitX;
        system.unitsYs[unitIdentifier] = unitY;
        system.unitsAngles[unitIdentifier] = unitAngle;
    }
}