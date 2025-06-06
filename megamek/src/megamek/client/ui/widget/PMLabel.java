/**
 * MegaMek - Copyright (C) 2000-2002 Ben Mazur (bmazur@sev.org)
 * 
 *  This program is free software; you can redistribute it and/or modify it 
 *  under the terms of the GNU General Public License as published by the Free 
 *  Software Foundation; either version 2 of the License, or (at your option) 
 *  any later version.
 * 
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 *  for more details.
 */

package megamek.client.ui.widget;

/**
 * JLabel for PicMap component
 */

import java.awt.Color;
import java.awt.Dimension;

public interface PMLabel extends PMElement {
    public Dimension getSize();

    public int getDescent();

    public void moveTo(int x, int y);

    public void setColor(Color c);

    public void setString(String s);
}
