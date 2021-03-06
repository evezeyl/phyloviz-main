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

package net.phyloviz.algo.util;

public class DisjointSet {
	private int size;
	private int[] pi;
	private int[] rank;

	public DisjointSet(int n) {
		size = n + 1;
		pi = new int[size];
		rank = new int[size];

		for (int i = 0; i < size; i++) {
			rank[i] = 0;
			pi[i] = i;
		}
	}

	public int findSet(int i) {
		if (i < 0 || i >= size)
			return -1;
		for (; i != pi[i]; i = pi[i])
			pi[i] = pi[pi[i]];
		return i;
	}
		
	public boolean sameSet(int i, int j) {
		return findSet(i) == findSet(j);
	}

	public void unionSet(int i, int j) {
		if (i < 0 || j < 0 || i >= size || j >= size)
			return;
		int iRoot = findSet(i);
		int jRoot = findSet(j);
		if (iRoot == jRoot)
			return;
		if (rank[iRoot] > rank[jRoot])
			pi[jRoot] = iRoot;
		else if (rank[iRoot] < rank[jRoot])
			pi[iRoot] = jRoot;
		else {
			pi[iRoot] = jRoot;
			rank[jRoot] ++;
		}
	}
}
