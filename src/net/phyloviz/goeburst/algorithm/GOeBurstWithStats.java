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

package net.phyloviz.goeburst.algorithm;

import net.phyloviz.goeburst.AbstractDistance;
import net.phyloviz.algo.util.DisjointSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.cluster.Edge;
import net.phyloviz.goeburst.cluster.GOeBurstCluster;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;

public class GOeBurstWithStats implements ClusteringMethod<GOeBurstClusterWithStats> {
	
	private int level;
	private int maxStId;
	private TreeMap<Integer,GOeBurstCluster.STLV> stLVs;
	
	public GOeBurstWithStats(int level) {
		this.level = level;
		this.maxStId = 0;
		this.stLVs = new TreeMap<Integer,GOeBurstCluster.STLV>();
	}

	@Override
	public Collection<GOeBurstClusterWithStats> getClustering(TypingData<? extends AbstractProfile> td, AbstractDistance ad) {
		
		ArrayList<Edge> edges = getEdges(td, ad);
		Collection<GOeBurstClusterWithStats> clustering = getGroups(td, edges, ad);
		
		// Update LVs for STs in each group and set group id.
		Iterator<GOeBurstClusterWithStats> gIter = clustering.iterator();
		int gid = 0;
		while (gIter.hasNext()) {
			GOeBurstClusterWithStats g = gIter.next();
			g.setID(gid++);
			g.updateVisibleEdges();
		}
		
		return clustering;
	}
	
	public int getSTxLV(AbstractProfile st, int lv) {
		if (lv > GOeBurstClusterWithStats.MAXLV || lv < 0)
			level = GOeBurstClusterWithStats.MAXLV;
			
		return stLVs.get(st.getUID()).lv[lv];
	}

	private ArrayList<Edge> getEdges(TypingData<? extends AbstractProfile> td, AbstractDistance ad) {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		maxStId = 0;
		
		Iterator<? extends AbstractProfile> uIter = td.iterator();
		while (uIter.hasNext()) {
			AbstractProfile u = uIter.next();
			GOeBurstClusterWithStats.STLV uLV = stLVs.get(u.getUID());
			
			if (uLV == null) {
				uLV = new GOeBurstClusterWithStats.STLV();
				stLVs.put(u.getUID(), uLV);
			}
			
			maxStId = Math.max(maxStId, u.getUID());
			
			Iterator<? extends AbstractProfile> vIter = td.iterator();
			while (vIter.hasNext()) {
				AbstractProfile v = vIter.next();
				
				int diff = ad.compute(u, v);
				
				if (u != v && diff <= GOeBurstClusterWithStats.MAXLV)
					uLV.lv[diff - 1] ++;
				else
					uLV.lv[GOeBurstClusterWithStats.MAXLV] ++;

				if (u.getUID() < v.getUID() && diff <= level)
					edges.add(new Edge(u, v, ad));
			}
		}
		
		return edges;
	}
	
	private Collection<GOeBurstClusterWithStats> getGroups(TypingData<? extends AbstractProfile> td, Collection<Edge> edges, AbstractDistance ad) {
		DisjointSet s = new DisjointSet(maxStId);
		
		Iterator<Edge> eIter = edges.iterator();
		while (eIter.hasNext()) {
			Edge e = eIter.next();
			s.unionSet(e.getU().getUID(), e.getV().getUID());
		}
		
		TreeMap<Integer,GOeBurstClusterWithStats> groups = new TreeMap<Integer,GOeBurstClusterWithStats>();
		eIter = edges.iterator();
		while (eIter.hasNext()) {
			Edge e = eIter.next();
			
			int pi = s.findSet(e.getU().getUID());
			GOeBurstClusterWithStats g = groups.get(pi);
			if ( g == null) {
				g = new GOeBurstClusterWithStats(this, ad);
				groups.put(pi, g);
			}
			
			g.add(e);
		}
		
		// Add singletons.
		Iterator<? extends AbstractProfile> stIter = td.iterator();
		while (stIter.hasNext()) {
			AbstractProfile st = stIter.next();
			
			int pi = s.findSet(st.getUID());
			if (groups.get(pi) == null) {
				GOeBurstClusterWithStats g = new GOeBurstClusterWithStats(this, ad);
				g.add(st);
				groups.put(pi, g);
			}
		}
		
		ArrayList<GOeBurstClusterWithStats> gList = new ArrayList<GOeBurstClusterWithStats>(groups.values());
		Collections.sort(gList);
		
		return gList;
	}
}
