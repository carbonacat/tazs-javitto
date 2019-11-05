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

package net.ccat.tazs.resources.sprites;

import femto.mode.HiRes16Color;


/**
 * Provides the base methods for manipulating a Sprite frame by frame.
 */
interface NonAnimatedSprite
{
    /***** SPRITE *****/
    
    void setPosition(float x, float y);
    void setFlipped(boolean flipped);
    void setMirrored(boolean mirrored);
    void draw(HiRes16Color screen);
    void setStatic(boolean staticState);
    
    
    /***** FRAME MANIPULATION *****/
    
    void selectFrame(int frame);
}