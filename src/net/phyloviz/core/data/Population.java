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

/*
 * @(#)Population.java 28/01/11
 *
 * Copyright 2011 Phyloviz. All rights reserved.
 * Use is subject to license terms.
 */
package net.phyloviz.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;
import net.phyloviz.core.explorer.PopulationNode;
import net.phyloviz.core.util.NodeFactory;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * The <code> Population </code> class represents the set of the
 * isolates of a bacterial population.
 *
 * @since   PHILOViZ 1.0
 * @author A. P. Francisco
 *
 */
public class Population implements DataModel, Lookup.Provider, NodeFactory {

	private InstanceContent ic;
	private AbstractLookup lookup;
	/**
	 * The set of the ancillary data names.
	 */
	private ArrayList<String> headers;
	/**
	 * The list of sets of ancillary data values.
	 * Each position of this list contains the corresponding set of ancillary
	 * data values to a specific ancillary data name.
	 */
	private ArrayList<HashSet<String>> domains;
	/**
	 *  The map that to each ancillary data name maps
	 *  an internal index.
	 */
	private HashMap<String, Integer> h2idx;
	private ArrayList<Isolate> collection;
	private TableModel model;
	private DataSaver saver;
	private int key;

	/**
	 * Constructs an empty population.
	 */
	public Population() {
		ic = new InstanceContent();
		lookup = new AbstractLookup(ic);
		headers = new ArrayList<String>();
		domains = new ArrayList<HashSet<String>>();
		h2idx = new HashMap<String, Integer>();
		collection = new ArrayList<Isolate>();
	}

	/**
	 * Adds an isolate to this population with <code>values<code/> as its
	 * ancillary data if they are the same as for the isolates that
	 * belong to this population.
	 *
	 * @param values the ancillary data values of an isolate.
	 * @return <code> true </code> if they are the same as for the isolates
	 * that belong to this population.
	 *
	 */
	public boolean addIsolate(String[] values) {

		// Do we have values for all fields?
		if (values.length != headers.size()) {
			return false;
		}

		Isolate isolate = new Isolate();

		for (int i = 0; i < values.length; i++) {
			isolate.add(values[i]);
			domains.get(i).add(values[i]);
		}

		collection.add(isolate);

		return true;
	}

	/**
	 * Returns the number of isolates in this population.
	 *
	 * @return the number of isolates of this population
	 */
	@Override
	public int size() {
		return collection.size();
	}

	/**
	 *
	 * @param c
	 */
	public void sort(Comparator<Isolate> c) {
		Collections.sort(collection, c);
	}

	@Override
	public PopulationIterator iterator() {
		return new PopulationIterator();
	}

	@Override
	public String toString() {
		return "Isolate Data";
	}

	// LookUp Methods
	@Override
	public Lookup getLookup() {
		return lookup;
	}

	public void add(Object o) {
		ic.add(o);
	}

	public void remove(Object o) {
		ic.remove(o);
	}

	@Override
	public AbstractNode getNode() {
		return new PopulationNode(this);
	}

	@Override
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	@Override
	public String getDomainLabel(int idx) {
		if (idx < 0 || idx >= headers.size()) {
			return null;
		}

		return headers.get(idx);
	}

	@Override
	public Collection<? extends DataItem> getItems() {
		return new ArrayList<Isolate>(collection);
	}

	@Override
	public int weight() {
		return size();
	}

	public class PopulationIterator implements Iterator<Isolate> {

		private Iterator<Isolate> i;
		private Isolate last;
		private int idx;

		public PopulationIterator() {
			i = collection.iterator();
			last = null;
			idx = 0;
		}

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public Isolate next() {
			last = i.next();
			idx++;
			return last;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Not supported!");
		}

		public String get(int idx) {
			if (last == null) {
				return null;
			}

			return last.get(idx);
		}

		public void set(int idx, String value) {
			if (last != null) {
				domains.get(idx).add(value);
				last.set(idx, value);
			}
		}

		public String get(String column) {
			return get(h2idx.get(column));
		}

		public void set(String column, String value) {
			set(h2idx.get(column), value);
		}

		public int index() {
			return idx;
		}
	}

	//Data Model Methods
	public boolean addColumn(String header, ColumnFiller cf) {
		if (h2idx.containsKey(header)) {
			return false;
		}

		h2idx.put(header, headers.size());
		headers.add(header);
		HashSet<String> dom = new HashSet<String>();
		domains.add(dom);

		Iterator<Isolate> i = collection.iterator();
		if (cf != null) {
			while (i.hasNext()) {
				Isolate iso = i.next();
				String val = cf.getValue(iso);
				dom.add(val);
				iso.add(val);
			}
		} else {
			while (i.hasNext()) {
				i.next().add(null);
			}
		}

		return true;
	}

	@Override
	public HashSet<String> getDomain(int idx) {
		if (idx < 0 || idx > domains.size()) {
			return null;
		}

		return domains.get(idx);
	}

	public interface ColumnFiller {

		public String getValue(Isolate i);
	}

	@Override
	public TableModel tableModel() {
		if (model == null) {
			model = new TableModel();
		}

		return model;
	}

	@Override
	public DataSaver getSaver() {
		if (saver == null)
			saver = new DataSaver(this);

		return saver;
	}

	public class TableModel extends AbstractTableModel {

		@Override
		public int getRowCount() {
			return size();
		}

		@Override
		public int getColumnCount() {
			return headers.size();
		}

		@Override
		public String getColumnName(int idx) {
			return getDomainLabel(idx);
		}

		@Override
		public int findColumn(String name) {
			if (h2idx.containsKey(name)) {
				return h2idx.get(name);
			}

			return -1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex < 0 || rowIndex >= collection.size()) {
				return null;
			}

			return collection.get(rowIndex).get(columnIndex);
		}
	}
}
