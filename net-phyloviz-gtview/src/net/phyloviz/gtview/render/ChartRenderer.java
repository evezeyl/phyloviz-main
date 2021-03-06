/*-
 * Copyright (c) 2011, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

package net.phyloviz.gtview.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.Iterator;
import java.util.List;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.category.filter.Category;
import net.phyloviz.core.data.Profile;
import net.phyloviz.upgmanjcore.visualization.GView;

import prefuse.render.AbstractShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.VisualItem;

public class ChartRenderer  extends AbstractShapeRenderer {

	private GView gv;
	private CategoryProvider cp;
	
	public ChartRenderer(CategoryProvider cp, GView gv) {
            this.cp = cp;
            this.gv = gv;
	}

	public void drawPie(Graphics2D g, Rectangle area, String stId, int freq, Color fillColor) {
            // Get total value of all slices
            double total = 0;

            List<Category> glst = cp.getCategories(stId);
            Iterator<Category> giter;

            if (glst != null) {
                giter = glst.iterator();
                //System.out.println("grouplist"+glst);
                while (giter.hasNext())
                    total += giter.next().weight();
            }

            total = freq - total;
            if (total < 0)
                total = 0;

            // Draw background pie...
            g.setColor(fillColor);
            g.fillArc(area.x, area.y, area.width, area.height, 0, 360);

            // Draw each pie slice
            int currAngle = 0;

            if (glst != null) {
                giter = glst.iterator();
                while (giter.hasNext()) {
                    // Compute the start and stop angles
                    Category group = giter.next();

                    int arcAngle = Math.round((((float) group.weight()) / freq) * 360);

                    // Set the color and draw a filled arc
                    g.setColor(cp.getCategoryColor(group.getName()));
                    g.fillArc(area.x + 2, area.y + 2, area.width - 4, area.height - 4, 90 - currAngle, - arcAngle);
                    currAngle += arcAngle;
                }
            }

            int arcAngle = Math.round((((float) total) / freq) * 360);
            if (arcAngle > 0) {
                // Ensure that rounding errors do not leave a gap between the first and last slice
                arcAngle = 360 - currAngle;

                // Set color and draw 'others' slice
                g.setColor(Color.LIGHT_GRAY);
                g.fillArc(area.x + 2, area.y + 2, area.width - 4, area.height - 4, 90 - currAngle, - arcAngle);
            }

            int x = (int)area.getCenterX();
            int y = (int)area.getCenterY();
            g.setPaint(new Color(255,255,255));
            Font font = FontLib.getFont("Tahoma", Font.PLAIN, 11  /*+ 5*Math.log(freq)*/);
            g.setFont(font);

            if (gv.showLabel()) {
                String stIdstr = stId;
                int nCaracteres = stIdstr.length();
                g.drawString(stIdstr, x - (2.6f * nCaracteres), y + 4.0f);
            }
	}
	
	@Override
	protected Shape getRawShape(VisualItem item) {
		int x = (int)item.getX();
		int y = (int)item.getY();

		Profile st = (Profile) item.getSourceTuple().get("st_ref");
		
		int offset = 0;

		if (st != null) offset = (int)(gv.getLinearSize() ? 12*st.getFreq() : (12 * Math.log(1 + st.getFreq())));
		int w = 30 + offset;
		int h = 30 + offset;
		
		x = x - w/2;
		y = y - h/2;
		
		Rectangle area = new Rectangle(x, y, w, h);
		return area;
	}

	@Override
	protected void drawShape(Graphics2D g, VisualItem item, Shape shape) {

		//if render type is NONE, then there is nothing to do
		String stId = item.getString("st_id");
		int type = this.getRenderType(item);
		BasicStroke stroke = this.getStroke(item);


		if ( type == AbstractShapeRenderer.RENDER_TYPE_NONE )
			return;

		// set up colors
		Color strokeColor = ColorLib.getColor(item.getStrokeColor());
		Color fillColor = ColorLib.getColor(item.getFillColor());

		boolean sdraw = (type == AbstractShapeRenderer.RENDER_TYPE_DRAW ||
				type == AbstractShapeRenderer.RENDER_TYPE_DRAW_AND_FILL) &&
				strokeColor.getAlpha() != 0;
		boolean fdraw = (type == AbstractShapeRenderer.RENDER_TYPE_FILL ||
				type == AbstractShapeRenderer.RENDER_TYPE_DRAW_AND_FILL) &&
				fillColor.getAlpha() != 0;

		sdraw = fdraw = true;
		if ( !(sdraw || fdraw) ) return;

		Stroke origStroke = null;
		if ( sdraw ) {
			origStroke = g.getStroke();
			g.setStroke(stroke);
		}

		int x, y, w, h;//, aw, ah;
		double xx, yy, ww, hh;

		// see if an optimized (non-shape) rendering call is available for us
		// these can speed things up significantly on the windows JRE
		// it is stupid we have to do this, but we do what we must
		// if we are zoomed in, we have no choice but to use
		// full precision rendering methods.

		if ( shape instanceof RectangularShape ) {
			RectangularShape r = (RectangularShape)shape;


			xx = r.getX(); ww = r.getWidth(); 
			yy = r.getY(); hh = r.getHeight();

			x = (int)xx;
			y = (int)yy;
			w = (int)(ww+xx-x);
			h = (int)(hh+yy-y);


			if ( shape instanceof Rectangle2D ) {

				Rectangle area = (Rectangle) shape;
				int freq = ((Profile) item.get("st_ref")).getFreq();
				drawPie(g, area, stId, freq, fillColor);
			}
		} else if ( shape instanceof Line2D ) {
			if (sdraw) {
				Line2D l = (Line2D)shape;
				x = (int)(l.getX1()+0.5);
				y = (int)(l.getY1()+0.5);
				w = (int)(l.getX2()+0.5);
				h = (int)(l.getY2()+0.5);
				g.setPaint(strokeColor);
				g.drawLine(x, y, w, h);
			}
		} else {
			if (fdraw) { g.setPaint(fillColor);   g.fill(shape); }
			if (sdraw) { g.setPaint(strokeColor); g.draw(shape); }
		}
		if ( sdraw ) {
			g.setStroke(origStroke);
		}
	}
}
