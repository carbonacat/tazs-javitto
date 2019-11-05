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

package net.ccat.tazs.resources.musics;


/**
 * Gathers all the available musics.
 */
public class Musics
{
    public static final int SILENCE = 0;
    public static final int MUSIC00 = SILENCE + 1;
    public static final int MUSIC01 = MUSIC00 + 1;
    public static final int MUSIC02 = MUSIC01 + 1;
    public static final int MUSIC03 = MUSIC02 + 1;
    public static final int COUNT = MUSIC03 + 1;
    
    
    public static pointer musicPointerForIdentifier(int musicIdentifier)
    {
        if (musicIdentifier == SILENCE)
            return Silence.bin();
        else if (musicIdentifier == MUSIC00)
            return Music00.bin();
        else if (musicIdentifier == MUSIC01)
            return Music01.bin();
        else if (musicIdentifier == MUSIC02)
            return Music02.bin();
        else if (musicIdentifier == MUSIC03)
            return Music03.bin();
        // Something went wrong here!
        return Silence.bin();
    }
}